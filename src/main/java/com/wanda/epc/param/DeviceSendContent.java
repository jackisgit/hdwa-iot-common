package com.wanda.epc.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: iot_epc
 * @description: emq消息发送conetent对象
 * @author: 孙率众
 * @create: 2022-09-17 14:08
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSendContent {

    /**
     * 点位
     */
    private Integer funcid;

    /**
     * 设备ID
     */
    private String meter;

    /**
     * 时间
     */
    private String time;

    /**
     * 采集数据值
     */
    private String value;

    /**
     * 点位code
     */
    private String paramName;
}
