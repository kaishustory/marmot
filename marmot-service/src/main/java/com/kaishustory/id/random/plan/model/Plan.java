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

package com.kaishustory.id.random.plan.model;

import com.kaishustory.id.common.model.ISegment;
import lombok.Data;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 规划ID
 *
 * @author liguoyang
 * @create 2019-05-13 15:52
 **/
@Data
public class Plan implements ISegment {

    /**
     * 列表
     */
    private Deque<Long> curr = new ConcurrentLinkedDeque<>();

    /**
     * 有效时间
     */
    private String effectiveTime;


    /**
     * 生成主键
     */
    @Override
    public Long newID() {
        return curr.pollFirst();
    }

    /**
     * 获得最大ID
     */
    @Override
    public Long getMax(){
        return curr.getLast();
    }

    /**
     * 验证分片是否可用
     */
    @Override
    public boolean validAll(){
        return
                // 检查是否过期
                validExpire()
                // 检查是否已用尽
                && validMax();
    }

    /**
     * 验证分片是否可用
     * @param date 验证日期
     */
    @Override
    public boolean validAll(String date){
        return
                // 检查是否过期
                validExpire(date)
                // 检查是否已用尽
                && validMax();
    }

    /**
     * 验证分片是否过期
     */
    @Override
    public boolean validExpire() {
        // 检查是否过期
        return true;
    }

    /**
     * 验证分片是否过期
     * @param date 验证日期
     */
    @Override
    public boolean validExpire(String date) {
        return true;
    }

    /**
     * 验证分片是否有剩余
     */
    @Override
    public boolean validMax() {
        // 检查是否过期
        return this.getCurr().size()>0;
    }

    /**
     * 今日有效ID分段
     */
    @Override
    public boolean today(){
        return true;
    }

}
