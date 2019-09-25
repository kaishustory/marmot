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

package com.kaishustory.id.sequence.segment.controller;

import com.kaishustory.id.sequence.segment.model.SegmentDefine;
import com.kaishustory.id.sequence.segment.service.SegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 顺序分段加载ID接口
 *
 * @author liguoyang
 * @create 2019-05-15 19:15
 **/
@RestController
@RequestMapping("/segment")
public class SegmentController {

    /**
     * 分片ID管理
     */
    @Autowired
    private SegmentService segmentService;

    /**
     * 随机ID定义
     * @param define 定义信息
     * @return 返回
     */
    @PostMapping("/define")
    public String define(@RequestBody SegmentDefine define){
        segmentService.define(define);
        return "OK";
    }

    /**
     * 获得新ID
     * @param system 系统
     * @param tag 业务标签
     * @return 新ID
     */
    @GetMapping("/get")
    public Long get(String system, String tag){
        return segmentService.get(system, tag);
    }
}
