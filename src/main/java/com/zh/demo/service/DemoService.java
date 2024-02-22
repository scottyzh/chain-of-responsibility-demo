package com.zh.demo.service;

import com.zh.demo.BO.DeviceBO;
import com.zh.demo.DTO.ParmDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zh
 * @since 2024-01-22
 */
public interface DemoService {

    List<DeviceBO> filterDeviceTypeA(ParmDTO parmDTO);

    List<DeviceBO> filterDeviceTypeB(ParmDTO parmDTO);

}
