package com.example.managepurchase.classes;

public class BusinessHours {
    private int openingHour;
    private int openingMinute;
    private int closingHour;
    private int closingMinute;

    // בונה ברירת מחדל נדרש ל-Firebase
    public BusinessHours() { }

    public BusinessHours(int openingHour, int openingMinute, int closingHour, int closingMinute) {
        this.openingHour = openingHour;
        this.openingMinute = openingMinute;
        this.closingHour = closingHour;
        this.closingMinute = closingMinute;
    }

    public int getOpeningHour() {
        return openingHour;
    }

    public void setOpeningHour(int openingHour) {
        this.openingHour = openingHour;
    }

    public int getOpeningMinute() {
        return openingMinute;
    }

    public void setOpeningMinute(int openingMinute) {
        this.openingMinute = openingMinute;
    }

    public int getClosingHour() {
        return closingHour;
    }

    public void setClosingHour(int closingHour) {
        this.closingHour = closingHour;
    }

    public int getClosingMinute() {
        return closingMinute;
    }

    public void setClosingMinute(int closingMinute) {
        this.closingMinute = closingMinute;
    }
}
