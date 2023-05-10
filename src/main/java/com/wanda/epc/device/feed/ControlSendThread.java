package com.wanda.epc.device.feed;

import com.wanda.epc.config.emqx.MqttSendClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liurs
 * @description: 阻塞线程
 * @date 2022-06-29 14:06
 */

public class ControlSendThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ControlSendThread.class);

    private MqttSendClient mqttSendClient;

    private String gcId;

    public ControlSendThread(MqttSendClient mqttSendClient, String gcId) {
        this.mqttSendClient=mqttSendClient;
        this.gcId=gcId;
    }

    /**
     * @param
     * @author
     * @description 监听控制消息
     * @date 2022-06-29 13:40
     * @return:
     */
    @Override
    public void run() {
        try {
            logger.info("开始监听控制消息");
            FeedbackTask.task(mqttSendClient,gcId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
}
