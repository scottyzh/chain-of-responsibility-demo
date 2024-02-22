package com.zh.demo.BO;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class DeviceFilterBO {

    private int condition;

    private DeviceBO deviceBO;
}
