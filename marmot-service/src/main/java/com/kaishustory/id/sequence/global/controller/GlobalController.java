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

package com.kaishustory.id.sequence.global.controller;

import com.kaishustory.id.sequence.global.service.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 顺序全局ID接口
 *
 * @author liguoyang
 * @create 2019-05-16 16:32
 **/
@RestController
@RequestMapping("/global")
public class GlobalController {

    /**
     * 全局ID管理
     */
    @Autowired
    private GlobalService globalService;

    /**
     * 获得新ID
     * @param system 系统
     * @param tag 业务标签
     * @param dayReset 按日重置ID
     * @return 新ID
     */
    @GetMapping("/get")
    public Long get(String system, String tag, Boolean dayReset){
        return globalService.get(system, tag, dayReset);
    }
}
