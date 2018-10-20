package Util;

import java.util.Date;

public class BillItem {
    private Date date;
    private double amount;
    private String mainType;
    private String SubType;
    private String description;

    @Override
    public String toString() {
        return "BillItem{" +
                "date=" + date +
                ", amount=" + amount +
                ", mainType='" + mainType + '\'' +
                ", SubType='" + SubType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public BillItem(Date date, double amount, String mainType, String subType, String description) {
        this.date = date;
        this.amount = amount;
        this.mainType = mainType;
        SubType = subType;
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public Date getDate() {

        return date;
    }

    public double getAmount() {
        return amount;
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
}
