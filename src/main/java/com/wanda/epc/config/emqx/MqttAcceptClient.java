package com.wanda.epc.config.emqx;//package com.wd.iot.service.emqx;

import com.wanda.epc.config.MqttProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @auther liurs
 * @Description mqtt接收消息客户端
 * @Date 2022-05-20
 */
@Component
@Slf4j
public class MqttAcceptClient {

    private static final Logger logger = LoggerFactory.getLogger(MqttAcceptClient.class);
    public static MqttClient client;
    @Autowired
    private MqttAcceptCallback mqttAcceptCallback;
    @Autowired
    private MqttProperties mqttProperties;

    private static MqttClient getClient() {
        return client;
    }

    private static void setClient(MqttClient client) {
        MqttAcceptClient.client = client;
    }

    /**
     * 客户端连接
     */
    public void connect() {
        MqttClient client;
        try {
            client = new MqttClient(mqttProperties.getHostUrl(), mqttProperties.getClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttProperties.getUsername());
            options.setPassword(mqttProperties.getPassword().toCharArray());
            options.setConnectionTimeout(mqttProperties.getTimeout());
            options.setKeepAliveInterval(mqttProperties.getKeepAlive());
            options.setAutomaticReconnect(mqttProperties.getReconnect());
            options.setCleanSession(mqttProperties.getCleanSession());
            MqttAcceptClient.setClient(client);
            try {
                // 设置回调
                client.setCallback(mqttAcceptCallback);
                client.connect(options);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 重新连接
     */
    public void reconnection() {
        try {
            client.connect();
        } catch (MqttException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 订阅某个主题
     *
     * @param topic 主题
     * @param qos   连接方式
     */
    public void subscribe(String topic, int qos) {
        try {
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 取消订阅某个主题
     *
     * @param topic
     */
    public void unsubscribe(String topic) {
        logger.info("==============开始取消订阅主题==============" + topic);
        try {
            client.unsubscribe(topic);
        } catch (MqttException e) {
            log.error(e.getMessage(), e);
        }
    }
}

