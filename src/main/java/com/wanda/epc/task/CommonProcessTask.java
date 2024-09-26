package com.wanda.epc.task;

import com.alibaba.fastjson.JSON;
import com.wanda.epc.common.RedisUtil;
import com.wanda.epc.device.CommonDevice;
import com.wanda.epc.param.DeviceMessage;
import com.wanda.epc.util.ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @program: iot_epc
 * @description: 公共定时任务，定时扫描以data.开头的所有设备的信息，发送到emqx服务器，
 **/
@Configuration
@EnableScheduling
public class CommonProcessTask {

    private final static Logger logger = LoggerFactory.getLogger(CommonProcessTask.class);
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private CommonDevice device;
    @Value("${epc.gcId}")
    private String gcId;
    @Value("${epc.gatewayId}")
    private String gatewayId;


    @Scheduled(cron = "0 0/4 * * * ?")
    public boolean processData() throws Exception {
        Set<String> keys = redisUtil.scan("data." + "Pj" + gcId + "." + gatewayId + ".*");
        if (!CollectionUtils.isEmpty(keys)) {//data.开头的
            logger.info("开始同步全量的redis数据值为");
            for (String key : keys) {
                //不上传data开头的手自动数据，由采集器上传
                if (key.contains("manualAutoSet")) {
                    continue;
                }
                DeviceMessage dm = JSON.parseObject(JSON.toJSONString(redisUtil.get(key)), DeviceMessage.class);
                dm.setUpdateTime(ConvertUtil.getNowDateTime("yyyyMMddHHmmss"));
                device.sendAllMessage(dm);
            }
        }
        return true;
    }

}
