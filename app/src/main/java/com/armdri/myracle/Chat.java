package com.armdri.myracle;

public class Chat {
    private String name;
    private String msg;
    private int viewType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getViewType() { return viewType; }

    public void setViewType(int viewType) { this.viewType = viewType; }
}
