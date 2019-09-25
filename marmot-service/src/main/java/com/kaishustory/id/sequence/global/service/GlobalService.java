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

package com.kaishustory.id.sequence.global.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.kaishustory.id.sequence.global.cache.GlobalCache;
import com.kaishustory.id.service.IGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 顺序全局ID管理
 *
 * @author liguoyang
 * @create 2019-05-16 16:25
 **/
@Component
@Service(interfaceClass = IGlobalService.class)
public class GlobalService implements IGlobalService {

    /**
     * 全局ID缓存
     */
    @Autowired
    private GlobalCache globalCache;

    /**
     * 获得ID
     * @param system 系统
     * @param tag 业务标签
     * @param dayReset 是否按日重置
     * @return ID
     */
    @Override
    public long get(String system, String tag, boolean dayReset){
        return globalCache.get(system+"-"+tag, dayReset);
    }
}
