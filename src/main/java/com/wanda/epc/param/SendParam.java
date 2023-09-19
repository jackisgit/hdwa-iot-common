package com.wanda.epc.param;

import lombok.Data;

/**
 * 发送消息的参数
 * @Author 孙率众
 * @Date 下午3:40 2022/9/5
 * @param  * @param null
 * @return null
 **/
@Data
public class SendParam {
    /***
     * 消息内容
     */
    private String messageContent;
    /**
     * 客户端发消息的主题主题
     */
    private String topic;
}
