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

package com.kaishustory.id.sequence.segment.model;

import com.kaishustory.id.common.model.Define;
import lombok.Data;

/**
 * 随机ID定义
 *
 * @author liguoyang
 * @create 2019-05-13 15:10
 **/
@Data
public class SegmentDefine extends Define {

    /**
     * 最大主键
     */
    private Long maxId;

    /**
     * 主键数量（规划ID必填）
     */
    private Long maxNum;

    /**
     * 加载步长
     */
    private Integer step = 1000;

    /**
     * 每日重置（仅分片ID支持）
     */
    private boolean dayReset = false;

}
