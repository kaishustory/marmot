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

package com.kaishustory.utils

import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * Controller 返回结果
 */
open class Result {
    // 错误码
    var errcode: Int = 0
    // 错误信息
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    var errmsg: String? = null
    // 返回结果
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    var result: Any? = null


    /**
     * 返回错误信息
     * @param errcode 错误码
     * @param errmsg 错误信息
     */
    constructor (errcode: Int, errmsg: String){
        this.errcode = errcode
        this.errmsg = errmsg
    }
    /**
     * 返回结果信息
     * @param errcode 错误码
     * @param result 返回结果
     */
    constructor(errcode: Int, result: Any){
        this.errcode = errcode
        this.result = result
    }

    override fun toString(): String {
        return JsonUtils.toJson(this)
    }

    companion object {
        /**
         * 处理成功
         */
        val success = 0
        /**
         * 处理失败
         */
        val fail = -1
    }

}
