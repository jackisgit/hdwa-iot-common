package com.wanda.epc.config.emqx;//package com.wd.iot.service.emqx;

import com.alibaba.fastjson.JSON;
import com.wanda.epc.constant.IotEpaConstant;
import com.wanda.epc.device.BaseDevice;
import com.wanda.epc.device.CommonDevice;
import com.wanda.epc.param.DeviceMessage;
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
    private CommonDevice commonDevice;


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
        try {
            String message = new String(mqttMessage.getPayload(), "gb2312");
            DeviceMessageRevice deviceMessageRevice = JSON.parseObject(message, DeviceMessageRevice.class);
            DeviceSendContent deviceReceiveContent = deviceMessageRevice.getContent().get(0);
            Integer funcid = deviceReceiveContent.getFuncid();
            String meter = deviceReceiveContent.getMeter();
            String value = deviceReceiveContent.getValue();
            DeviceMessage dm = BaseDevice.controlParamMap.get(meter + "-" + funcid);
            if (null == dm) {
                logger.info("该采集器不存在设备点位 : {}-{}", meter, funcid);
                return;
            }
            logger.info("接收控制消息内容 : " + message);
            //如果控制点为防盗或者门禁采集器时，发送控制值
            if ("FD".equals(dm.getCollectCode()) || "MJ".equals(dm.getCollectCode())) {
                dm.setValue(value);
                commonDevice.sendMessage(dm);
            }
            //控制值映射转换
            value = commonDevice.controlString(dm, value);
            BaseDevice bean = (BaseDevice) ApplicationContextUtils.getBean(beanName, BaseDevice.class);
            bean.dispatchCommand(meter, funcid, value, message);
        } catch (Exception e) {
            logger.error("处理数据失败", e);
        }
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
                + gcId
                + IotEpaConstant.SEDOWN);
        mqttAcceptClient.subscribe(IotEpaConstant.mqtt_topic_prefix_project
                + gcId
                + IotEpaConstant.SEDOWN, 0);
    }

}

