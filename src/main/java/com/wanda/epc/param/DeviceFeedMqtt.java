package com.wanda.epc.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @auther liurs
 * @Description 云端mqtt封装消息类
 * @Date 2022-11-01
 */
@Data
public class DeviceFeedMqtt implements Serializable {

    private String sequence_no; //控制指令标识

    private String packet_type; // 消息类型

    private List<DeviceFeedData> content; //数据

}
