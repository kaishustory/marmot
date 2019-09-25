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

package com.kaishustory.id.common.service;

import com.kaishustory.id.random.plan.service.PlanService;
import com.kaishustory.id.sequence.segment.service.SegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 系统启动初始化
 *
 * @author liguoyang
 * @create 2019-05-16 15:38
 **/
@Component
public class InitService {

    /**
     * 顺序分片ID管理
     */
    @Autowired
    private SegmentService segmentService;

    /**
     * 随机规划ID管理
     */
    @Autowired
    private PlanService planService;

    /**
     * 启动初始化
     */
    @PostConstruct
    public void init(){
        // 顺序分片ID加载
        segmentService.init();
        // 随机规划ID加载
        planService.init();
    }
}
