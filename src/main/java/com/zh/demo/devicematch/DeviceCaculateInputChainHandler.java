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

import com.zh.demo.BO.DeviceBO;
import com.zh.demo.BO.DeviceFilterBO;
import com.zh.demo.designpattern.chain.AbstractChainContext;
import com.zh.demo.designpattern.chain.type.DeviceTypeAChainFilter;
import com.zh.demo.designpattern.chain.type.DeviceTypeBChainFilter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DeviceCaculateInputChainHandler implements DeviceTypeAChainFilter, DeviceTypeBChainFilter {

    @Override
    public boolean handler(DeviceFilterBO deviceFilterBO) {
        if (deviceFilterBO.getDeviceBO().getCondition() % deviceFilterBO.getCondition() == 0) {
            System.out.println("处理器D：输入接口不支持");
            return false;
        }
        ArrayList<DeviceBO> deviceRes = (ArrayList<DeviceBO>) AbstractChainContext.threadLocal.get();
        deviceRes.add(deviceFilterBO.getDeviceBO());
        // 接口支持
        System.out.println("处理器D：输入接口支持");
        // 计算设备数量满足要求
        System.out.println("处理器D：根据输入接口计算的设备数量满足要求");
        return true;
    }

    @Override
    public int getOrder() {
        return 40;
    }
}
