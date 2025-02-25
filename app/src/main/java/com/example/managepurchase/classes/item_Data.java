package com.example.managepurchase.classes;

public class item_Data {
    String costumerName;
    String costumerPhone;
    String date;
    String time;

    public item_Data(String costumerName, String costumerPhone, String date, String time) {
        this.costumerName = costumerName;
        this.costumerPhone = costumerPhone;
        this.date = date;
        this.time = time;

    }
    public String getCostumerName() {
        return costumerName;
    }

    public String getCostumerPhone() {
        return costumerPhone;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
    public void setCostumerName(String costumerName) {
        this.costumerName = costumerName;
    }

    public void setCostumerPhone(String costumerPhone) {
        this.costumerPhone = costumerPhone;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
