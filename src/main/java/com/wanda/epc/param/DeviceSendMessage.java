package com.wanda.epc.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: 孙率众
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
