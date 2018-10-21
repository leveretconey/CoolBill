package Util;

import java.util.Calendar;
import java.util.Date;

public class BillItem {
    private Calendar date;
    private double amount;
    private String mainType;
    private String SubType;
    private String description;

    @Override
    public String toString() {
        return "BillItem{" +
                "amount=" + amount +
                ", date=" + date +
                ", mainType='" + mainType + '\'' +
                ", SubType='" + SubType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
    }

    public void setSubType(String subType) {
        SubType = subType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {

        return amount;
    }

    public Calendar getDate() {
        return date;
    }

    public String getMainType() {
        return mainType;
    }

    public String getSubType() {
        return SubType;
    }

    public String getDescription() {
        return description;
    }

    public BillItem(Calendar date, double amount, String mainType, String subType, String description) {
        this.date = date;
        this.amount = amount;
        this.mainType = mainType;
        SubType = subType;
        this.description = description;
    }
}
