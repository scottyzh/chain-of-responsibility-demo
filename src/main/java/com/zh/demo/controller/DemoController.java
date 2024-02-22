package com.zh.demo.controller;

import com.zh.demo.DTO.ParmDTO;
import com.zh.demo.common.common.AjaxResult;
import com.zh.demo.service.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zh
 * @since 2024-01-23
 */
@RestController
@Api(tags = "demo接口")
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @ApiOperation(value = "筛选A设备接口",notes = "传入参数筛选")
    @PostMapping(value = "/filterDeviceTypeA")
    public AjaxResult filterDeviceTypeA() {
        ParmDTO parmDTO = new ParmDTO();
        return AjaxResult.success(demoService.filterDeviceTypeA(parmDTO));
    }

    @ApiOperation(value = "筛选B设备接口",notes = "传入参数筛选")
    @PostMapping(value = "/filterDeviceTypeB")
    public AjaxResult filterDeviceTypeB()  {
        ParmDTO parmDTO = new ParmDTO();
        return AjaxResult.success(demoService.filterDeviceTypeB(parmDTO));
    }

}
