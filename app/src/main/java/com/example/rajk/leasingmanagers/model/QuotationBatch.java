package com.example.rajk.leasingmanagers.model;

public class QuotationBatch {

    String endDate;
    String startDate;
    String note;
    String id;

    String empId;
    private int color = -1;

    public QuotationBatch() {
    }

    public QuotationBatch(String id, String note, String startDate, String endDate, String empId, int color) {
        this.endDate = endDate;
        this.id = id;
        this.note = note;
        this.startDate = startDate;
        this.empId = empId;
        this.color = color;
    }


    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
