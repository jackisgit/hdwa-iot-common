package com.wanda.epc.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @auther liurs
 * @Description mqtt指令下发封装消息类
 * @Date 2022-10-31
 */
@Data
public class DeviceMessageRevice implements Serializable {

    private String mqtt_client_id; // clientId

    private String sequence_no; //

    private String packet_type; // 消息类型

    private List<DeviceSendContent> content; //数据

}
