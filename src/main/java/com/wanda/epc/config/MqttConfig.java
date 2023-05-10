package com.wanda.epc.config;//package com.wd.iot.config;

import com.wanda.epc.config.emqx.MqttAcceptClient;
import com.wanda.epc.config.emqx.MqttSendClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther liurs
 * @Description mqtt配置类
 * @Date 2022-10-17
 */
@Configuration
public class MqttConfig {

    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);

    @Autowired
    private MqttAcceptClient mqttAcceptClient;

    @Autowired
    private MqttSendClient mqttSendClient;

    /**
     * 订阅mqtt
     *
     * @return
     */
    @Bean
    public MqttSendClient getMqttSendClient(){
        mqttSendClient.connect();
        return mqttSendClient;
    }
    @Bean
    public MqttAcceptClient getMqttAcceptClient(){
        mqttAcceptClient.connect();
        return mqttAcceptClient;
    }
}