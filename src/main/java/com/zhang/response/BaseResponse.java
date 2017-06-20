package com.zhang.response;

/**
 * Created by 致远 on 2017/6/15 0015.
 */
public class BaseResponse {

    private boolean success = false;
    private int total = 0;
    private String preUrl = "http://tnfs.tngou.net/image";
    private Object data = "查询失败";

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getPreUrl() {
        return preUrl;
    }

    public void setPreUrl(String preUrl) {
        this.preUrl = preUrl;
    }
}
