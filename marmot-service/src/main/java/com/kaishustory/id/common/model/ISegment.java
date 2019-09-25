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

package com.kaishustory.id.common.model;

/**
 * 片段接口
 *
 * @author liguoyang
 * @create 2019-05-15 19:30
 **/
public interface ISegment {

    /**
     * 生成主键
     */
    Long newID();

    /**
     * 获得最大ID
     */
    Long getMax();

    /**
     * 验证分片是否可用
     */
    boolean validAll();

    /**
     * 验证分片是否可用
     * @param date 验证日期
     */
    boolean validAll(String date);

    /**
     * 验证分片是否过期
     */
    boolean validExpire();

    /**
     * 验证分片是否过期
     * @param date 验证日期
     */
    boolean validExpire(String date);

    /**
     * 验证分片是否有剩余
     */
    boolean validMax();

    /**
     * 今日有效ID分段
     */
    boolean today();
}
