package com.wanda.epc.device;

import com.alibaba.fastjson.JSON;
import com.wanda.epc.common.RedisUtil;
import com.wanda.epc.config.emqx.MqttSendClient;
import com.wanda.epc.param.DeviceMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: iot_dapc
 * @description: 设备采集及指令下发基础类
 * @author: 孙率众
 * @create: 2022-09-13 11:35
 **/
public abstract class BaseDevice {

    protected Map<String, List<DeviceMessage>> deviceParamListMap = new HashMap<String, List<DeviceMessage>>();

    public static Map<String, DeviceMessage> controlParamMap = new HashMap<String, DeviceMessage>();

    private static final Logger logger = LoggerFactory.getLogger(BaseDevice.class);

    @Value("${epc.gcId}")
    private String gcId;

    @Value("${epc.gatewayId}")
    private String gatewayId;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    MqttSendClient mqttSendClient;

    @Autowired
    CommonDevice commonDevice;

    /***
     * 线程数
     */
    @Value("${epc.threadNum}")
    private int threadNum;

    @PostConstruct
    public void run() {
        Set<String> keys = redisUtil.scan("Pj" + gcId + "." + gatewayId + ".*");
        Set<String> dataKeys = redisUtil.scan("data.Pj" + gcId + "." + gatewayId + ".*");
        if (!CollectionUtils.isEmpty(dataKeys)) {
            Long count = redisUtil.removeBatch(dataKeys);
            logger.info("==============初始化删除data数据{}条================", count);
        }
        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                DeviceMessage deviceMessage = JSON.parseObject(JSON.toJSONString(redisUtil.get(key)), DeviceMessage.class);
                String[] splitKey = key.split("\\.");
                String controlKey = splitKey[2];
                String outParamId = deviceMessage.getOutParamId();
                if (StringUtils.isNotEmpty(outParamId)) {
                    boolean result = deviceParamListMap.containsKey(outParamId);
                    if (result) {
                        List<DeviceMessage> deviceMessageList = deviceParamListMap.get(outParamId);
                        deviceMessageList.add(deviceMessage);
                        deviceParamListMap.put(outParamId, deviceMessageList);

                    } else {
                        List<DeviceMessage> deviceMessageList = new ArrayList<>();
                        deviceMessageList.add(deviceMessage);
                        deviceParamListMap.put(outParamId, deviceMessageList);
                    }
                    controlParamMap.put(controlKey, deviceMessage);
                }
            }
        }
        logger.info("==============初始化{}广场、{}子系统的redis数据{}条================", gcId, gatewayId, deviceParamListMap.size());
//        启动发送队列线程
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            executor.execute(commonDevice);
        }
    }

    /**
     * 数据采集发送
     *
     * @param dm
     */
    public abstract void sendMessage(DeviceMessage dm);


    /**
     * 数据采集
     *
     * @return
     * @throws Exception
     */
    public abstract boolean processData() throws Exception;

    /**
     * 控制指令下发
     *
     * @param meter
     * @param funcid
     * @param value
     * @param message
     * @return
     */
    public abstract void dispatchCommand(String meter, Integer funcid, String value, String message) throws Exception;


    /**
     * 数据采集-带有参数
     *
     * @return
     * @param obj
     * @throws Exception
     */
    public abstract boolean processData(String... obj) throws Exception;

}
