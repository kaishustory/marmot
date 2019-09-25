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

package com.kaishustory.id.random.plan.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.kaishustory.id.common.constants.IdConstants;
import com.kaishustory.id.random.plan.cache.PlanCache;
import com.kaishustory.id.sequence.segment.cache.SegmentCache;
import com.kaishustory.id.sequence.segment.model.SegmentDefine;
import com.kaishustory.id.sequence.segment.service.SegmentService;
import com.kaishustory.id.service.IPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 随机规划ID管理
 *
 * @author liguoyang
 * @create 2019-05-15 19:19
 **/
@Component
@Service(interfaceClass = IPlanService.class)
public class PlanService extends SegmentService implements IPlanService {

    /**
     * 随机ID缓存
     */
    @Autowired
    private PlanCache planCache;

    /**
     * 获得ID缓存
     */
    @Override
    protected SegmentCache getSegmentCache(){
        return planCache;
    }

    /**
     * 获得ID
     * @param system 系统
     * @param tag 主键Tag
     * @return ID
     */
    @Override
    public long get(String system, String tag) {
        return super.get(system, tag);
    }

    /**
     * 随机ID定义
     * @param idDefine ID定义
     * @return 是否成功
     */
    @Override
    public boolean define(SegmentDefine idDefine){
        // 类型
        idDefine.setType(IdConstants.TYPE_PLAN);
        // ID定义
        return super.define(idDefine);
    }

    /**
     * 初始化主键ID
     */
    @Override
    public void init() {
        // 检查任务列表，并加载缓存
        planCache.getTagList(IdConstants.TYPE_PLAN).forEach(this::checkSegment);
    }
}
