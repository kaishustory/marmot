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

package com.kaishustory.id.service;

/**
 * 全局主键服务
 *
 * @author liguoyang
 * @create 2019-05-16 16:28
 **/
public interface IGlobalService {

    /**
     * 获得ID
     * @param system 系统
     * @param tag 业务标签
     * @param dayReset 是否按日重置
     * @return ID
     */
    long get(String system, String tag, boolean dayReset);
}
