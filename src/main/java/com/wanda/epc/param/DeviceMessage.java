package com.wanda.epc.param;

import lombok.Data;

import java.util.Map;

/**
 * 设备消息类
 *
 * @author liurs
 */
@Data
public class DeviceMessage {

    private String outParam;

    private String projectId; // 项目id

    private String collectCode; //采集器编号

    private String eqId; // 设备id

    private Integer paramId; // 参数id

    private String outParamId; // 外部参数id

    private boolean isCalc; // 是否需要计算(1计算0不计算)

    private String formula; // 计算公式

    private String value; // 值

    private String updateTime; //  采集时间 （yyyy-MM-dd HH24:mm:ss）

    /**
     * 点位code
     */
    private String paramName;

    //控制计算转换
    private String opratorControlString;

    //状态计算转换
    private String opratorStatusString;

    private Map<String, String> variable;


    @Override
    public String toString() {
        return "DeviceMessage{" +
                "outParam='" + outParam + '\'' +
                ", projectId='" + projectId + '\'' +
                ", collectCode='" + collectCode + '\'' +
                ", eqId='" + eqId + '\'' +
                ", paramId=" + paramId +
                ", outParamId='" + outParamId + '\'' +
                ", isCalc=" + isCalc +
                ", formula='" + formula + '\'' +
                ", value='" + value + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", paramName='" + paramName + '\'' +
                ", opratorControlString='" + opratorControlString + '\'' +
                ", opratorStatusString='" + opratorStatusString + '\'' +
                '}';
    }
}
