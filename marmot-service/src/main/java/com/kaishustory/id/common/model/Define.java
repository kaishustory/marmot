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

import lombok.Data;

import java.util.Date;

/**
 * ID定义
 *
 * @author liguoyang
 * @create 2019-05-16 15:19
 **/
@Data
public class Define {

    /**
     * 业务标签（必填）
     */
    private String tag;

    /**
     * 类型：顺序分片：SEGMENT，随机规划：PLAN
     */
    private String type;

    /**
     * 更新时间
     */
    private Date updateTime;
}
