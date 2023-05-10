package com.wanda.epc.config.emqx;//package com.wd.iot.service.emqx;

import com.wanda.epc.config.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @auther liurs
 * @Description mqtt发送消息客户端
 * @Date 2022-05-20
 */
@Component
@Primary
public class MqttSendClient {

    private static final Logger logger = LoggerFactory.getLogger( MqttSendClient.class);

    @Autowired
    private MqttSendCallBack mqttSendCallBack;

    @Autowired
    private MqttProperties mqttProperties;

    public static MqttClient client;


    public MqttClient getClient() {
        return client;
    }

    private static void setClient(MqttClient client) {
         MqttAcceptClient.client = client;
    }

    public MqttClient connect() {
//        MqttClient client = null;
        try {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            client = new MqttClient(mqttProperties.getHostUrl(), uuid, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttProperties.getUsername());
            options.setPassword(mqttProperties.getPassword().toCharArray());
//            options.setConnectionTimeout(mqttProperties.getTimeout());
//            options.setKeepAliveInterval(mqttProperties.getKeepAlive());
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setMaxInflight(1048576);

            try {
                // 设置回调
                client.setCallback(mqttSendCallBack);
                client.connect(options);
                MqttSendClient.setClient(client);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }

    /**
     * 发布消息
     * 主题格式： 前缀/编码/packet_type
     *
     * @param retained    是否保留
     * @param topic       主题
     * @param pushMessage 消息体
     */
    public void publish(boolean retained, String topic, String pushMessage) {
        MqttMessage message = new MqttMessage();
        message.setQos(mqttProperties.getQos());
        message.setRetained(retained);
        message.setPayload(pushMessage.getBytes());
//        MqttClient mqttClient = connect();
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        } finally {
//            disconnect(client);
//            close(client);
        }
    }

    /**
     * 发布消息
     * 主题格式： 前缀/编码/packet_type
     *
     * @param topic       主题
     * @param pushMessage 消息体
     */
    public void publish( String topic, String pushMessage) {
       this.publish(true,topic,pushMessage);
    }

    /**
     * 关闭连接
     *
     * @param mqttClient
     */
    public static void disconnect(MqttClient mqttClient) {
        try {
            if (mqttClient != null) mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     *
     * @param mqttClient
     */
    public static void close(MqttClient mqttClient) {
        try {
            if (mqttClient != null) mqttClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
