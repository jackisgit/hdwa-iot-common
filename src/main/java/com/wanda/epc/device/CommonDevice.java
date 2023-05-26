package com.wanda.epc.device;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanda.epc.common.RedisUtil;
import com.wanda.epc.config.emqx.MqttSendClient;
import com.wanda.epc.constant.IotEpaConstant;
import com.wanda.epc.device.feed.FeedbackTask;
import com.wanda.epc.param.DeviceMessage;
import com.wanda.epc.param.DeviceSendContent;
import com.wanda.epc.param.DeviceSendMessage;
import com.wanda.epc.util.ConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
@Service
public class CommonDevice extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(CommonDevice.class);

    @Autowired
    MqttSendClient sendClient;

    @Autowired
    RedisUtil redisUtil;

    @Value("${epc.gcId}")
    private String gcId;

    @Value("${epc.gatewayId}")
    private String gatewayId;

    @Value("${epc.subsystem}")
    private String subsystem;

    private Queue<DeviceSendMessage> deviceMsgQueue = new ConcurrentLinkedQueue<DeviceSendMessage>();

    private String clientId = "IOT-EPC-"+gcId+"-"+ gatewayId;

    private int processInterval = 1;

    private String statusKey = "subsystem_connect_status_";

    public synchronized void sendMessage(DeviceMessage dm) {
        DeviceSendMessage dsm = convert(dm);
        //发送所在子系统状态
        subsystemStatus();
        //数据变化发送emq,不变化不更新
        if (isUpdate(dm)){
            deviceMsgQueue.add(dsm);
        }
    }

    public synchronized void sendAllMessage(DeviceMessage dm) {
        DeviceSendMessage dsm = syncConvert(dm);
        //发送所在子系统状态
        deviceMsgQueue.add(dsm);
    }

    @Override
    public void run() {
        while (true) {
            try {
                DeviceSendMessage pMessage = deviceMsgQueue.poll();
                if (pMessage != null) {
                    sendClient.publish(true,
                            IotEpaConstant.mqtt_topic_prefix_project+gcId+ IotEpaConstant.REPORT,
                            JSON.toJSONString(pMessage));
                }
                Thread.sleep(processInterval);
            } catch(Exception e){
                logger.error(e.getMessage());
            }
        }
    }

    public void UpdateRedis(DeviceSendMessage dsm) {
    }


    public void feedback(String message)  {
        FeedbackTask.addControlPool(message);
    }

    public void controlMessage(String message)  {
        FeedbackTask.addControlPool(message);
    }

    /**
     * @Description 采集数据转换
     * @param dm
     */
    private DeviceSendMessage convert(DeviceMessage dm) {
        DeviceSendContent dsc = new DeviceSendContent();
        DeviceSendMessage dsm = new DeviceSendMessage();
        List<DeviceSendContent> list = new ArrayList<>();
        list.add(dsc);
        dsc.setParamName(dm.getParamName());
        dsc.setMeter(dm.getEqId());
        dsc.setFuncid(dm.getParamId());
        dsc.setTime(ConvertUtil.getNowDateTime("yyyyMMddHHmmss"));
        String value = dm.getValue();
        //如果采集上来的值位空默认至0，当作离线处理
        if (StringUtils.isNotEmpty(value)) {
            if (value.equals("true")) {
                value = "1";
            } else if (value.equals("false")) {
                value = "0";
            }
        } else {
            value = "0";
        }
        //是否转换
        value = operatorString(dm,value);
        //是否计算
        value = calculate(dm, value);
        dsc.setValue(value);
        dsm.setMqtt_client_id(clientId);
        dsm.setSequence_no(1L);
        dsm.setContent(list);
        dm.setValue(value);
        dm.setUpdateTime(ConvertUtil.getNowDateTime("yyyyMMddHHmmss"));
        return dsm;
    }

    /**
     * @Description 只封装 不计算
     * @param dm
     */
    private DeviceSendMessage syncConvert(DeviceMessage dm) {
        DeviceSendContent dsc = new DeviceSendContent();
        DeviceSendMessage dsm = new DeviceSendMessage();
        List<DeviceSendContent> list = new ArrayList<>();
        dsc.setParamName(dm.getParamName());
        dsc.setMeter(dm.getEqId());
        dsc.setFuncid(dm.getParamId());
        dsc.setTime(ConvertUtil.getNowDateTime("yyyyMMddHHmmss"));
        dsc.setValue(dm.getValue());
        list.add(dsc);
        dsm.setMqtt_client_id(clientId);
        dsm.setSequence_no(1L);
        dsm.setContent(list);
        return dsm;
    }

    /***
     * @Description 进行加减乘除计算
     * @param dm
     * @param value
     * @return
     */
    private String calculate(DeviceMessage dm, String value){
        String formula= dm.getFormula();
        if (StringUtils.isNotEmpty(formula) && !formula.equals("null")){
            formula = value + formula;
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
            try {
                return new DecimalFormat("0.00").format(engine.eval(formula));
            }catch (Exception e){
                e.printStackTrace();
                logger.info("{},{}点位计算式转换错误", dm.getEqId(),dm.getParamId());
            }
        }
        return value;
    }

    /***
     * @Description 采集值转换
     * @param dm
     * @param value
     * @return
     */
    public String operatorString(DeviceMessage dm, String value){
        String operatorStatus = dm.getOpratorStatusString();
        if (StringUtils.isNotEmpty(operatorStatus)){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(operatorStatus);
                if (StringUtils.isNotEmpty(jsonNode.get(value).asText())){
                    return jsonNode.get(value).asText();
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /***
     * @Description 控制值转换
     * @param dm
     * @param value
     * @return
     */
    public String controlString(DeviceMessage dm, String value){
        String controlStatus = dm.getOpratorControlString();
        if (StringUtils.isNotEmpty(controlStatus)){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(controlStatus);
                if (StringUtils.isNotEmpty(jsonNode.get(value).asText())){
                    return jsonNode.get(value).asText();
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return value;
    }




    /**
     * @Description 判断数据是否变化
     * @param dsm
     */
    private Boolean isUpdate(DeviceMessage dsm) {
        Boolean flag = true;
        String key = "data."+"Pj" + gcId + "." + gatewayId + "." + dsm.getEqId() + "-" +dsm.getParamId();
        DeviceMessage redisDm = JSON.parseObject(JSON.toJSONString(redisUtil.get(key)), DeviceMessage.class);
        if (redisDm != null && redisDm.getValue()!=null && dsm.getValue()!=null && dsm.getValue().equals(redisDm.getValue())){
            flag = false;
        }
        dsm.setUpdateTime(ConvertUtil.getNowDateTime("yyyyMMddHHmmss"));
        redisUtil.set(key,dsm);
        return flag;
    }

    /**
     * @Description 子系统状态
     */
    private void subsystemStatus(){
        if (StringUtils.isNotEmpty(subsystem)){
            String[] subsystemSplit = subsystem.split("/");
            if (subsystemSplit.length >0){
                for (String systemName: subsystemSplit){
                    String redisKey = statusKey + systemName;
                    redisUtil.set(redisKey, ConvertUtil.getNewDateTime());
                }
            }
        }
    }
}
