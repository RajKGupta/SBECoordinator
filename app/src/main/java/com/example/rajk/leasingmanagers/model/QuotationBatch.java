package com.example.rajk.leasingmanagers.model;

/**
 * Created by RajK on 06-07-2017.
 */

public class QuotationBatch {
    private String dateassigned;
    private String datecompleted;
    private String note;

    public QuotationBatch(String dateassigned, String datecompleted, String note) {
        this.dateassigned = dateassigned;
        this.datecompleted = datecompleted;
        this.note = note;
    }

    public QuotationBatch() {
    }
}
