package com.wanda.epc.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @auther liurs
 * @Description 发送到iot-project控制反馈消息类
 * @Date 2022-11-01
 */
@Data
public class DeviceFeedData implements Serializable {

    /**
     * 时间 20220626164101
     */
    private String time;
    /**
     * 仪表号 WXHSWD.DeviceId
     */
    private String meter;
    /**
     * pointId
     */
    private Integer funcid;
    /**
     * 值
     */
    private String value;
    /**
     * 结束时间
     */
    private String endtime;
    /**
     * 反馈结果
     */
    private String result;
}
