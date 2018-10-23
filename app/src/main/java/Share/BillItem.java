package Share;

import java.io.Serializable;

public class BillItem implements Serializable{
    private int id;
    private int year;
    private int month;
    private int day;
    private double amount;
    private String mainType;
    private String SubType;
    private String description;

    @Override
    public String toString() {
        return "BillItem{" +
                "id=" + id +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", amount=" + amount +
                ", mainType='" + mainType + '\'' +
                ", SubType='" + SubType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
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

    public int getId() {

        return id;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
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

    public BillItem(int id, int year, int month, int day, double amount, String mainType, String subType, String description) {

        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.amount = amount;
        this.mainType = mainType;
        SubType = subType;
        this.description = description;
    }
}
