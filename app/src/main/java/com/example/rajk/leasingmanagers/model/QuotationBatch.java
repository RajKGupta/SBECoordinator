package com.example.rajk.leasingmanagers.model;

public class QuotationBatch {

    String endDate, startDate, coordnote, id, custName;
    private int color = -1;

    public QuotationBatch() {
    }

    public QuotationBatch(String id, String coordnote, String startDate, String endDate, int color, String custName) {
        this.endDate = endDate;
        this.id = id;
        this.coordnote = coordnote;
        this.custName = custName;
        this.startDate = startDate;
        this.color = color;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoordnote() {
        return coordnote;
    }

    public void setCoordnote(String coordnote) {
        this.coordnote = coordnote;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
