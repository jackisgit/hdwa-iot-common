package com.wanda.epc.device.feed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanda.epc.config.emqx.MqttSendClient;
import com.wanda.epc.constant.IotEpaConstant;
import com.wanda.epc.param.DeviceFeedData;
import com.wanda.epc.param.DeviceFeedMqtt;
import com.wanda.epc.param.DeviceMessageRevice;
import com.wanda.epc.param.DeviceSendContent;
import com.wanda.epc.util.ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @auther liurs
 * @Description 监听任务，反馈结果
 * @Date 2022-07-04
 */
@Service
public class FeedbackTask {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackTask.class);

    //控制收到的控制包
    private static ArrayBlockingQueue<String> ControlPool = new ArrayBlockingQueue<>(10*1000);;

    private static long sleepCount =1;  //轮询时间



    /***
     * 监听任务
     */
    public static void task(MqttSendClient mqttSendClient, String gcId){
        while (true) {
            try {
                String controlMessage = ControlPool.take();
                logger.warn("send sendDownAck " + controlMessage);
                sendFeedMessage(controlMessage, mqttSendClient, gcId);
                Thread.sleep(sleepCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 添加消息到队列
     */
    public static void addControlPool(String message)  {
        try {
            ControlPool.put(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 发送反馈mqtt
     */
    public static void sendFeedMessage(String controlMessage, MqttSendClient mqttSendClient, String gcId)  {
        DeviceMessageRevice deviceMessage = JSON.parseObject(controlMessage, DeviceMessageRevice.class);
        if (deviceMessage !=null){
            DeviceFeedMqtt deviceFeedCloudMqtt = new DeviceFeedMqtt();
            deviceFeedCloudMqtt.setPacket_type(IotEpaConstant.POINTSETACK);
            deviceFeedCloudMqtt.setSequence_no(deviceMessage.getSequence_no());
            List<DeviceFeedData> feed = new ArrayList<>();
            if (deviceMessage.getContent() !=null){
                for (DeviceSendContent deviceData:deviceMessage.getContent()){
                    DeviceFeedData feedData = new DeviceFeedData();
                    feedData.setMeter(deviceData.getMeter());
                    feedData.setFuncid(deviceData.getFuncid());
                    feedData.setTime(deviceData.getTime());
                    feedData.setValue(deviceData.getValue());
                    feedData.setEndtime(ConvertUtil.getNowDateTime("yyyyMMddHHmmss"));
                    feedData.setResult("success");
                    feed.add(feedData);
                }
                deviceFeedCloudMqtt.setContent(feed);
                mqttSendClient.publish(IotEpaConstant.mqtt_topic_prefix_project +gcId+ IotEpaConstant.SETUP, JSONObject.toJSONString(deviceFeedCloudMqtt));
            }
        }
    }
}
