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

package com.kaishustory.id.sequence.global.cache;

import com.kaishustory.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 顺序全局ID缓存管理
 *
 * @author liguoyang
 * @create 2019-05-16 16:18
 **/
@Component
public class GlobalCache {

    /**
     * Redis
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 全局主键ID序列
     */
    private final String ID_SEQ = "ID:GLOBAL:SEQ:{TAG}";

    /**
     * 全局主键ID序列（按日重置）
     */
    private final String ID_SEQ_DAYREST = "ID:GLOBAL:SEQ:{DATE}:{TAG}";

    /**
     * 获得ID
     * @param tag 业务标签
     * @param dayReset 是否重置
     * @return ID
     */
    public long get(String tag, boolean dayReset){
        if(!dayReset) {
            return redisTemplate.opsForValue().increment(ID_SEQ.replace("{TAG}", tag), 1);
        }else {
            Long id = redisTemplate.opsForValue().increment(ID_SEQ_DAYREST.replace("{DATE}", DateUtils.today()).replace("{TAG}", tag), 1);
            // 设置过期时间
            if(Objects.equals(id, 1L)){
                redisTemplate.expireAt(ID_SEQ_DAYREST.replace("{DATE}", DateUtils.today()).replace("{TAG}", tag), DateUtils.toDate(DateUtils.addDate(DateUtils.today(), 2)));
            }
            return id;
        }
    }
}
