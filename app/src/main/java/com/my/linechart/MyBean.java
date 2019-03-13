package com.my.linechart;

import android.graphics.Point;

/**
 * 创建者 ：赵鹏   时间：2019/3/13
 */
public class MyBean {
    private String date;
    private String rate;
    private String num;
    private String nav;
    private Point point;

    public String getNav() {
        return nav;
    }

    public void setNav(String nav) {
        this.nav = nav;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
