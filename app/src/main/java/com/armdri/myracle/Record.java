package com.armdri.myracle;

public class Record{
    DateUtil.Format dateFormat;
    int color;

    public Record(){
        this.dateFormat = null;
        this.color = -1;
    }

    public Record(DateUtil.Format dateFormat, int color){
        this.dateFormat = dateFormat;
        this.color = color;
    }

    public DateUtil.Format getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(DateUtil.Format dateFormat) {
        this.dateFormat = dateFormat;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}