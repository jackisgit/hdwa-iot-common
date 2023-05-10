package com.wanda.epc.config.emqx;//package com.wd.iot.service.emqx;

import com.alibaba.fastjson.JSON;
import com.wanda.epc.constant.IotEpaConstant;
import com.wanda.epc.device.BaseDevice;
import com.wanda.epc.param.DeviceMessageRevice;
import com.wanda.epc.param.DeviceSendContent;
import com.wanda.epc.util.ApplicationContextUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;


/**
 * @auther liurs
 * @Description mqtt接收回调类
 * @Date 2022-05-20
 */
@Component
public class MqttAcceptCallback implements MqttCallbackExtended {

    private static final Logger logger = LoggerFactory.getLogger(MqttAcceptCallback.class);

    @Autowired
    @Lazy
    MqttAcceptClient mqttAcceptClient;

    @Autowired
    private ApplicationContextUtils applicationContext;


    @Value("${beanName}")
    private String beanName;


    @Value("${epc.gcId}")
    private String gcId;
    /**
     * 客户端断开后触发
     *
     * @param throwable
     */
    @Override
    public void connectionLost(Throwable throwable) {

    }

    /**
     * 客户端收到消息触发
     *
     * @param topic       主题
     * @param mqttMessage 消息
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        logger.info("接收消息主题 : " + topic);
        logger.info("接收消息Qos : " + mqttMessage.getQos());
        //不知道为啥utf-8不行只能用gb2312
        String message = new String(mqttMessage.getPayload(), "gb2312");
        logger.info("接收消息内容 : " + message);
        DeviceMessageRevice deviceMessageRevice = JSON.parseObject(message, DeviceMessageRevice.class);
        DeviceSendContent deviceReceiveContent = deviceMessageRevice.getContent().get(0);
        Integer funcid = deviceReceiveContent.getFuncid();
        String meter = deviceReceiveContent.getMeter();
        String value = deviceReceiveContent.getValue();
        BaseDevice bean = (BaseDevice) applicationContext.getBean(beanName, BaseDevice.class);
        bean.dispatchCommand(meter, funcid, value, message);
//        控制topic: project/广场ID/setdown type: pointset
    }

    /**
     * 发布消息成功
     *
     * @param token token
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        String[] topics = token.getTopics();
        for (String topic : topics) {
            logger.info("向主题：" + topic + "发送消息成功！");
        }
        try {
            MqttMessage message = token.getMessage();
            byte[] payload = message.getPayload();
            String s = new String(payload, "UTF-8");
            logger.info("消息的内容是：" + s);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接emq服务器后触发
     *
     * @param b
     * @param s
     */
    @Override
    public void connectComplete(boolean b, String s) {
        logger.info("订阅主题{}", IotEpaConstant.mqtt_topic_prefix_project
                +  gcId
                + IotEpaConstant.SEDOWN);
        mqttAcceptClient.subscribe(IotEpaConstant.mqtt_topic_prefix_project
                +  gcId
                + IotEpaConstant.SEDOWN , 0);
    }
}

