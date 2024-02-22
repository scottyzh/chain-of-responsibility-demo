package com.zh.demo.service.impl;

import com.zh.demo.BO.DeviceBO;
import com.zh.demo.BO.DeviceFilterBO;
import com.zh.demo.DTO.ParmDTO;
import com.zh.demo.common.common.DeviceChainMarkEnum;
import com.zh.demo.designpattern.chain.AbstractChainContext;
import com.zh.demo.service.DemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemoServiceImpl implements DemoService {

    private final AbstractChainContext<DeviceFilterBO, Object> devcieTypeChainContext;

    @Override
    public List<DeviceBO> filterDeviceTypeA(ParmDTO parmDTO) {
        ArrayList<DeviceBO> deviceList = new ArrayList<>();
        // 简化条件
        parmDTO.setCondition(2);
        // 实际情况应该是从数据库读取设备的信息
        for (int i = 0; i < 5; i++) {
            DeviceBO deviceDTO = DeviceBO.builder().condition(new Random().nextInt(100)).build();
            deviceList.add(deviceDTO);
        }
        ArrayList<DeviceBO> deviceRes = new ArrayList<>();
        // 把需要的结果放到threadLocal中,在具体的处理器中对结果List进行处理
        AbstractChainContext.threadLocal.set(deviceRes);
        // 筛选多个设备 对符合的设备加入到deviceRes
        for (DeviceBO deviceBo : deviceList) {
            DeviceFilterBO deviceFilterBO = DeviceFilterBO.builder().condition(parmDTO.getCondition()).deviceBO(deviceBo).build();
            // 以A规则进行处理
            devcieTypeChainContext.handler(DeviceChainMarkEnum.DEVICE_TYPEA_FILTER.getName(), deviceFilterBO);
        }
        AbstractChainContext.threadLocal.remove();
        System.out.println("筛选结果数量：" + deviceRes.size());
        return deviceRes;
    }

    @Override
    public List<DeviceBO> filterDeviceTypeB(ParmDTO parmDTO) {
        ArrayList<DeviceBO> deviceList = new ArrayList<>();
        // 简化条件
        parmDTO.setCondition(2);
        // 实际情况应该是从数据库读取设备的信息
        for (int i = 0; i < 5; i++) {
            DeviceBO deviceDTO = DeviceBO.builder().condition(new Random().nextInt(100)).build();
            deviceList.add(deviceDTO);
        }
        ArrayList<DeviceBO> deviceRes = new ArrayList<>();
        AbstractChainContext.threadLocal.set(deviceRes);
        // 筛选多个设备 对符合的设备加入到deviceRes
        for (DeviceBO deviceBo : deviceList) {
            DeviceFilterBO deviceFilterBO = DeviceFilterBO.builder().condition(parmDTO.getCondition()).deviceBO(deviceBo).build();
            // 以B规则进行处理
            devcieTypeChainContext.handler(DeviceChainMarkEnum.DEVICE_TYPEB_FILTER.getName(), deviceFilterBO);
        }
        AbstractChainContext.threadLocal.remove();
        System.out.println("筛选结果数量：" + deviceRes.size());
        return deviceRes;
    }

}
