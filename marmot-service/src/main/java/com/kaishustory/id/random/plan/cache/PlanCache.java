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

package com.kaishustory.id.random.plan.cache;

import com.kaishustory.id.common.constants.IdConstants;
import com.kaishustory.id.common.model.ISegment;
import com.kaishustory.id.common.service.ILoadSegment;
import com.kaishustory.id.random.plan.model.Plan;
import com.kaishustory.id.random.plan.utils.PlanAlgorithm;
import com.kaishustory.id.sequence.segment.cache.SegmentCache;
import com.kaishustory.id.sequence.segment.model.SegmentDefine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 随机规划ID缓存管理
 *
 * @author liguoyang
 * @create 2019-05-16 10:45
 **/
@Component
public class PlanCache extends SegmentCache {

    /**
     * Redis
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 规划算法全局锁
     */
    private final String PLAN_LOCK = "ID:PLAN:LOCK:{TAG}";

    /**
     * 规划算法本地锁
     */
    private Lock planLocalLock = new ReentrantLock();

    /**
     * 加载ID分片
     * @param tag 业务标签
     * @return
     */
    @Override
    public void loadSegment(String tag){

        // 加载分片ID
        loadSegment(IdConstants.TYPE_PLAN, tag, new ILoadSegment() {

            /**
             * 初始化
             * @param tag 业务标签
             */
            @Override
            public void init(String tag) {
                // 获得全局锁
                planLock(tag);
            }

            /**
             * 保存默认ID定义
             * @param tag ID定义
             * @return
             */
            @Override
            public SegmentDefine saveDefaultDefine(String tag) {
                SegmentDefine define = new SegmentDefine();
                define.setTag(tag);
                define.setType(IdConstants.TYPE_PLAN);
                define.setStep(1000);
                define.setMaxNum(100000000L*5);
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
                return define.getMaxId();
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

                Plan plan = new Plan();
                // 生成ID列表
                plan.getCurr().addAll(genID(define.getMaxNum(), define.getMaxId(), define.getStep()));
                // 设置有效日期
                plan.setEffectiveTime(date);
                return plan;
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

            /**
             * 销毁
             * @param tag 业务标签
             */
            @Override
            public void destroy(String tag) {
                // 解除全局锁
                planUnlock(tag);
            }
        });

    }

    /**
     * 生成ID列表
     * @param maxNum 最大数量
     * @param maxId 当前最大ID
     * @param num 生成ID数量
     * @return 新生成ID列表
     */
    private List<Long> genID(Long maxNum, Long maxId, int num){
        PlanAlgorithm planAlgorithm = new PlanAlgorithm(maxNum);
        List<Long> ids = new ArrayList<>();
        Long last = maxId;
        for (int i = 0; i < num; i++) {
            last = planAlgorithm.get(last);
            ids.add(last);
        }
        return ids;
    }

    /**
     * 获得规划算法全局锁
     * @param tag 业务标签
     */
    private void planLock(String tag) {
        // 获得本地锁
        planLocalLock.lock();
        // 尝试全局锁
        while (redisTemplate.opsForValue().increment(PLAN_LOCK.replace("{TAG}", tag),1) != 1) {
            try {
                // 获得锁失败时，等待150毫秒后重试
                Thread.sleep(150);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 设置锁时间1分钟
        redisTemplate.expire(PLAN_LOCK.replace("{TAG}", tag), 1, TimeUnit.MINUTES);
    }

    /**
     * 解除规划算法全局锁
     * @param tag 业务标签
     */
    private void planUnlock(String tag) {
        // 解除全局锁
        redisTemplate.delete(PLAN_LOCK.replace("{TAG}", tag));
        // 解除本地锁
        planLocalLock.unlock();
    }

}
