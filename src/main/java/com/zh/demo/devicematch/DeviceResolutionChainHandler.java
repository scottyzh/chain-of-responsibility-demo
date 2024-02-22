/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zh.demo.devicematch;

import com.zh.demo.BO.DeviceFilterBO;
import com.zh.demo.designpattern.chain.type.DeviceTypeAChainFilter;
import com.zh.demo.designpattern.chain.type.DeviceTypeBChainFilter;
import org.springframework.stereotype.Component;

@Component
public class DeviceResolutionChainHandler implements DeviceTypeAChainFilter, DeviceTypeBChainFilter {

    @Override
    public boolean handler(DeviceFilterBO deviceFilterBO) {
        // 分辨率支持
        System.out.println("处理器B：分辨率支持");
        return true;
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
