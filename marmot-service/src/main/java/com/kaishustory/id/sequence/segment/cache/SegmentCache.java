/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.kaishustory.id.sequence.segment.cache;

import com.kaishustory.id.common.constants.IdConstants;
import com.kaishustory.id.common.model.ISegment;
import com.kaishustory.id.common.service.ILoadSegment;
import com.kaishustory.id.common.utils.ThreadPool;
import com.kaishustory.id.sequence.segment.model.Segment;
import com.kaishustory.id.sequence.segment.model.SegmentDefine;
import com.kaishustory.utils.DateUtils;
import com.kaishustory.utils.JsonUtils;
import com.kaishustory.utils.Log;
import com.kaishustory.utils.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 顺序分段加载ID缓存管理
 *
 * @author liguoyang
 * @create 2019-05-15 19:12
 **/
@Component
public class SegmentCache {

    /**
     * Redis
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 分片主键ID定义
     */
    private final String ID_DEF = "ID:DEFINE:SEGMENT:{TAG}";

    /**
     * 分片主键ID定义列表
     */
    private final String ID_DEF_LIST = "ID:DEFINE:SEGMENT:{TYPE}:LIST";

    /**
     * 分片主键ID序列
     */
    private final String ID_SEQ = "ID:SEGMENT:SEQ:{TAG}";

    /**
     * 分片主键ID序列（日重置）
     */
    private final String ID_SEQ_DATE = "ID:SEGMENT:SEQ:{DATE}:{TAG}";

    /**
     * ID缓存 <业务标签，List<ID分片>>
     */
    private static Map<String, List<ISegment>> idCache = new HashMap<>();

    /**
     * ID缓存锁 <业务标签，悲观锁>
     */
    private static Map<String,ReadWriteLock> idCacheLock = new Hashtable<>();

    /**
     * ID缓存加载锁 <业务标签，乐观锁>
     */
    private static Map<String,AtomicBoolean> idCacheLoadLock = new Hashtable<>();

    /**
     * 本地锁
     */
    private static Lock lock = new ReentrantLock();

    /**
     * 分片ID定义
     * @param idDefine ID定义
     * @return 是否成功
     */
    public boolean saveDefine(SegmentDefine idDefine){
        try {
            // 保存定义
            redisTemplate.opsForValue().set(ID_DEF.replace("{TAG}", idDefine.getTag()), JsonUtils.toJson(idDefine));
            // 保存定义列表
            redisTemplate.opsForSet().add(ID_DEF_LIST.replace("{TYPE}", idDefine.getType()), idDefine.getTag());
            Log.info("保存分片ID定义成功！");
            return true;
        }catch (Exception e){
            Log.error("保存分片ID定义失败！", e);
            return false;
        }
    }

    /**
     * 获得分片ID定义
     * @param tag 业务标签
     * @return ID定义
     */
    public SegmentDefine getDefine(String tag){
        return JsonUtils.fromJson(redisTemplate.opsForValue().get(ID_DEF.replace("{TAG}", tag)), SegmentDefine.class);
    }

    /**
     * 读取分片ID定义列表
     */
    public Set<String> getTagList(String type){
        return redisTemplate.opsForSet().members(ID_DEF_LIST.replace("{TYPE}", type));
    }

    /**
     * 获得当前分片（未必可用）
     * @param tag 业务标签
     * @return 当前分片
     */
    public Option<ISegment> getCurrSegment(String tag){
        try {
            // ID分片读锁
            getIdCacheLock(tag).readLock().lock();

            if(idCache.containsKey(tag)
                    && idCache.get(tag).size()>0){
                // 获得当前分片
                return Option.of(idCache.get(tag).get(0));
            }else {
                return Option.empty();
            }
        }finally {
            // ID分片读锁解除
            getIdCacheLock(tag).readLock().unlock();
        }
    }

    /**
     * 读取今日可用分片数量
     * @param tag 业务标签
     * @return 可用分片数量
     */
    private long getTodaySegmentCount(String tag){
        return getSegmentCount(tag, DateUtils.today());
    }

    /**
     * 读取当前可用分片数量
     * @param tag 业务标签
     * @param date 使用日期
     * @return 可用分片数量
     */
    private long getSegmentCount(String tag, String date){
        try {
            // ID分片读锁
            getIdCacheLock(tag).readLock().lock();

            if (!idCache.containsKey(tag)) {
                return 0;
            }
            // 读取可用分片数量
            return idCache.get(tag).stream().filter(segment -> segment.validAll(date)).count();
        }finally {
            // ID分片读锁解除
            getIdCacheLock(tag).readLock().unlock();
        }
    }

    /**
     * 清除无效分片
     * @param tag 业务标签
     */
    private void clearNovalidSegment(String tag){
        try {
            // ID分片写锁
            getIdCacheLock(tag).writeLock().lock();

            if(idCache.containsKey(tag)) {
                // 获得无用分片
                List<ISegment> expireSegment = idCache.get(tag).stream().filter(seg -> !seg.validAll()).collect(Collectors.toList());
                // 删除无用分片
                if (expireSegment != null && expireSegment.size() > 0) {
                    idCache.get(tag).removeAll(expireSegment);
                }
            }

        }finally {
            // ID分片写锁解除
            getIdCacheLock(tag).writeLock().unlock();
        }
    }

    /**
     * 加载ID分片
     * @param tag 业务标签
     * @return
     */
    public void loadSegment(String tag){

        loadSegment(IdConstants.TYPE_SEGMENT, tag, new ILoadSegment() {

            @Override
            public void init(String tag) {

            }

            /**
             * 保存默认ID定义
             * @param tag ID定义
             * @return
             */
            @Override
            public SegmentDefine saveDefaultDefine(String tag) {
                SegmentDefine define = new SegmentDefine();
                define.setType(IdConstants.TYPE_SEGMENT);
                define.setTag(tag);
                define.setStep(1000);
                define.setDayReset(false);
                saveDefine(define);
                return define;
            }

            /**
             * 获得最大ID
             * @param define ID定义
             * @param date 日期
             * @return 最大ID
             */
            @Override
            public Long getMaxId(SegmentDefine define, String date) {

                Long id = redisTemplate.opsForValue().increment((define.isDayReset() ? ID_SEQ_DATE.replace("{DATE}", date) : ID_SEQ).replace("{TAG}", tag), define.getStep());
                // 设置日重置队列过期时间
                if(define.isDayReset() && id == (long)define.getStep()) {
                    redisTemplate.expireAt(ID_SEQ_DATE.replace("{DATE}", date).replace("{TAG}", tag), DateUtils.toDate(DateUtils.addDate(date, 2)));
                }
                return id;
            }

            /**
             * 创建新分片
             * @param define ID定义
             * @param date 日期
             * @param maxId 最大ID
             * @return 新分片
             */
            @Override
            public ISegment createSegment(SegmentDefine define, String date, Long maxId) {
                Segment segment = new Segment();
                // 当前值
                segment.setCurr(new AtomicLong(maxId - define.getStep()));
                // 最大值
                segment.setMax(maxId);
                // 有效日期
                segment.setEffectiveTime(date);
                return segment;
            }

            /**
             * 更新ID定义
             * @param define ID定义
             * @param segment 新生村分片
             * @return ID定义
             */
            @Override
            public SegmentDefine updateDefine(SegmentDefine define, ISegment segment) {

                // 更新最大主键
                define.setMaxId(segment.getMax());
                // 更新时间
                define.setUpdateTime(new Date());
                return define;
            }

            @Override
            public void destroy(String tag) {

            }
        });
    }


    /**
     * 加载ID分片
     * @param type 类型
     * @param tag 业务标签
     * @return
     */
    protected void loadSegment(String type, String tag, ILoadSegment loadSegment){

        // 获得ID缓存加载乐观锁
        if(lockIdLoad(tag)){

            // 清理无用分片
            clearNovalidSegment(tag);

            // 获得今日可用分片数量
            long validTodaySegmentCount = getTodaySegmentCount(tag);

            // 异步加载ID缓存分片
            Future future = ThreadPool.async(() -> {
                try {
                    // 初始化
                    loadSegment.init(tag);

                    // 获取主键定义
                    SegmentDefine define = getDefine(tag);

                    if(define==null){
                        Log.error("主键未定义，自动生成默认ID定义 TAG：{}", tag);
                        // 自动补充默认ID定义
                        define = loadSegment.saveDefaultDefine(tag);
                    }

                    // 检查日期列表
                    List<String> dateList =  define.isDayReset() ?
                            // 按日重置ID，加载今明两天
                            Arrays.asList(DateUtils.today(), DateUtils.addDate(DateUtils.today(),1)):
                            // 不重置ID，加载今天
                            Collections.singletonList(DateUtils.today());

                    // 日期列表
                    for (String date : dateList) {

                        // 获取指定日期分片数量
                        long validSegmentCount = getSegmentCount(tag, date);

                        // 保证分片缓存列表中，始终有两个可用分片
                        for (int i = 0; i < 3 - validSegmentCount; i++) {

                            /** 获得新ID段 **/
                            // 获取一段主键
                            Long maxId = loadSegment.getMaxId(define, date);

                            /** 生产新ID段 **/
                            ISegment segment = loadSegment.createSegment(define, date, maxId);

                            /** 更新ID信息 **/
                            saveDefine(loadSegment.updateDefine(define, segment));

                            /** ID分段，加载入缓存 **/
                            try {
                                // ID分片写锁
                                getIdCacheLock(tag).writeLock().lock();

                                if (!idCache.containsKey(tag)) {
                                    idCache.put(tag, new ArrayList<>(5));
                                }
                                // 加入缓存
                                idCache.get(tag).add(segment);

                            } finally {
                                // ID分片写锁解除
                                getIdCacheLock(tag).writeLock().unlock();
                            }

                            Log.info("加载ID分片成功！Tag：{}", tag);
                        }
                    }
                    return Option.of("OK");

                }catch (Exception e){
                    Log.error("加载ID分片失败！Tag：{}", tag, e);
                    return Option.error(e.getMessage());
                }finally {
                    // 解除ID缓存加载乐观锁
                    unlockIdLoad(tag);
                    // 销毁
                    loadSegment.destroy(tag);
                }
            });

            // 如果可用分片为0，则等待ID分片加载完成后，继续执行程序
            if(validTodaySegmentCount==0){
                try {
                    Log.info("同步加载ID分片！");
                    Option result = (Option)future.get();
                    if(result.error()){
                        throw new RuntimeException(result.getErrmsg());
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

        }else {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 加锁 - ID分片加载
     * @param tag 业务标签
     * @return 是否成功
     */
    private boolean lockIdLoad(String tag){
        if(!idCacheLoadLock.containsKey(tag)){
            try {
                lock.lock();
                if(!idCacheLoadLock.containsKey(tag)) {
                    idCacheLoadLock.put(tag, new AtomicBoolean(false));
                }
            }finally {
                lock.unlock();
            }
        }
        return idCacheLoadLock.get(tag).compareAndSet(false, true);
    }

    /**
     * 解锁 - ID分片加载
     * @param tag 业务标签
     * @return 是否成功
     */
    private void unlockIdLoad(String tag){
        if(!idCacheLoadLock.containsKey(tag)){
            try {
                lock.lock();
                if(!idCacheLoadLock.containsKey(tag)) {
                    idCacheLoadLock.put(tag, new AtomicBoolean(false));
                }
            }finally {
                lock.unlock();
            }
        }else {
            idCacheLoadLock.get(tag).set(false);
        }
    }

    /**
     * 获得ID缓存锁
     * @param tag 业务标签
     * @return 是否成功
     */
    private ReadWriteLock getIdCacheLock(String tag){
        if(!idCacheLock.containsKey(tag)){
            try {
                lock.lock();
                if(!idCacheLock.containsKey(tag)) {
                    idCacheLock.put(tag, new ReentrantReadWriteLock());
                }
            }finally {
                lock.unlock();
            }
        }
        return idCacheLock.get(tag);
    }
}
