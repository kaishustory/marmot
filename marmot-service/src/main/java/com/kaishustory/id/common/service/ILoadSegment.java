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

import com.kaishustory.id.common.model.ISegment;
import com.kaishustory.id.sequence.segment.model.SegmentDefine;

/**
 * 分片加载ID接口
 *
 * @author liguoyang
 * @create 2019-05-15 19:46
 **/
public interface ILoadSegment {

    /**
     * 初始化
     */
    void init(String tag);

    /**
     * 自动补充默认定义
     * @param tag ID定义
     */
    SegmentDefine saveDefaultDefine(String tag);

    /**
     * 获得最大ID
     * @param define ID定义
     * @param date 日期
     * @return 最大ID
     */
    Long getMaxId(SegmentDefine define, String date);

    /**
     * 生成分片
     * @param define ID定义
     * @param date 日期
     * @param maxId 最大ID
     * @return 分片
     */
    ISegment createSegment(SegmentDefine define, String date, Long maxId);

    /**
     * 更新ID定义信息
     * @param define ID定义
     * @param segment 新生村分片
     * @return 更新定义
     */
    SegmentDefine updateDefine(SegmentDefine define, ISegment segment);

    /**
     * 销毁
     */
    void destroy(String tag);
}
