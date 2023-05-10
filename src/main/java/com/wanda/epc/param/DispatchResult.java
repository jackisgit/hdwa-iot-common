package com.wanda.epc.param;

/**
 * 下发结果  由具体设备的控制指令下发方法设值
 *
 * @author Bo
 */
public class DispatchResult {

    private boolean isSuccess;
    private String result;

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
	
