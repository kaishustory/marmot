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

package com.kaishustory.id.sequence.segment.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.kaishustory.id.common.constants.IdConstants;
import com.kaishustory.id.common.model.ISegment;
import com.kaishustory.id.common.service.IDService;
import com.kaishustory.id.sequence.segment.cache.SegmentCache;
import com.kaishustory.id.sequence.segment.model.SegmentDefine;
import com.kaishustory.id.service.ISegmentService;
import com.kaishustory.utils.Log;
import com.kaishustory.utils.Option;
import com.kaishustory.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 顺序分段加载ID管理
 *
 * @author liguoyang
 * @create 2019-05-15 19:06
 **/
@Component
@Service(interfaceClass = ISegmentService.class)
public class SegmentService implements IDService, ISegmentService {

    /**
     * 顺序ID缓存
     */
    @Autowired
    private SegmentCache segmentCache;

    /**
     * 获得ID缓存
     */
    protected SegmentCache getSegmentCache(){
        return segmentCache;
    }

    /**
     * 获得ID
     * @param system 系统
     * @param tag 主键Tag
     * @return ID
     */
    @Override
    public long get(String system, String tag) {
        return getId(system+"-"+tag);
    }

    /**
     * 初始化主键ID
     */
    @Override
    public void init() {
        // 检查任务列表，并加载缓存
        segmentCache.getTagList(IdConstants.TYPE_SEGMENT).forEach(this::checkSegment);
    }

    /**
     * 随机ID定义
     * @param idDefine ID定义
     * @return 是否成功
     */
    @Override
    public boolean define(SegmentDefine idDefine){
        // 类型
        if(StringUtils.isNull(idDefine.getType())) {
            idDefine.setType(IdConstants.TYPE_SEGMENT);
        }
        // 保存ID定义
        if(getSegmentCache().saveDefine(idDefine)){
            // 检查分片，并加载缓存
            checkSegment(idDefine.getTag());
            return true;
        }else {
            return false;
        }
    }

    /**
     * 获得主键ID
     * @param tag
     * @return
     */
    private long getId(String tag){
        // 获得当前分段
        ISegment segment = getSegment(tag);
        if(segment==null){
            Log.errorThrow("分段为空");
        }
        // ID +1
        Long newID = segment.newID();

        // 如果获得ID为空，重新获得ID
        if(newID==null){
            return getId(tag);
        }
        return newID;
    }

    /**
     * 获取当前可用分片
     * @param tag 业务标签
     * @return 当前分片
     */
    private ISegment getSegment(String tag){

        // 获得当前分片
        Option<ISegment> segment = getSegmentCache().getCurrSegment(tag);

        // 分片无可用ID 或 分片过期
        if( segment.nil() || !segment.get().validAll()) {

            // 加载新分片
            getSegmentCache().loadSegment(tag);
            // 重新获取分片ID
            return getSegment(tag);
        }

        // 获取当前分片
        return segment.get();
    }

    /**
     * 检查主键分片
     * @param tag 业务标签
     */
    protected void checkSegment(String tag){
        getSegment(tag);
    }


}
