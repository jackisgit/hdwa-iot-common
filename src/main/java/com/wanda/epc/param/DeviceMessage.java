package com.wanda.epc.param;

/**
 * 设备消息类
 *
 * @author liurs
 */
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


    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCollectCode() {
        return collectCode;
    }

    public void setCollectCode(String collectCode) {
        this.collectCode = collectCode;
    }

    public String getEqId() {
        return eqId;
    }

    public void setEqId(String eqId) {
        this.eqId = eqId;
    }

    public Integer getParamId() {
        return paramId;
    }

    public void setParamId(Integer paramId) {
        this.paramId = paramId;
    }

    public String getOutParamId() {
        return outParamId;
    }

    public void setOutParamId(String outParamId) {
        this.outParamId = outParamId;
    }

    public boolean isCalc() {
        return isCalc;
    }

    public void setCalc(boolean calc) {
        isCalc = calc;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOutParam() {
        return outParam;
    }

    public void setOutParam(String outParam) {
        this.outParam = outParam;
    }

    public String getOpratorControlString() {
        return opratorControlString;
    }

    public void setOpratorControlString(String opratorControlString) {
        this.opratorControlString = opratorControlString;
    }

    public String getOpratorStatusString() {
        return opratorStatusString;
    }

    public void setOpratorStatusString(String opratorStatusString) {
        this.opratorStatusString = opratorStatusString;
    }

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
