package com.wanda.epc.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: iot_epc
 * @description: 设备发送消息对象
 * @author: 孙率众
 * @create: 2022-09-17 11:05
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSendMessage {

    private String mqtt_client_id;

    private String packet_type = "report";

    private Long sequence_no;

    private List<DeviceSendContent> content;
}
