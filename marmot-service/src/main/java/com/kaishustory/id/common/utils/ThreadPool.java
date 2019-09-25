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

package com.kaishustory.id.common.utils;

import com.kaishustory.utils.Option;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

/**
 * 线程池
 *
 * @author liguoyang
 * @create 2019-05-07 19:27
 **/
public class ThreadPool {

    private static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60,TimeUnit.SECONDS, new SynchronousQueue<>(), new CustomizableThreadFactory("exec-id-load-"));

    public static Future async(Callable<Option> callable){
        return poolExecutor.submit(callable);
    }
}
