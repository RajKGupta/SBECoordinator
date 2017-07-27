package com.example.rajk.leasingmanagers.model;

/**
 * Created by RajK on 03-06-2017.
 */

public class Quotation {
    private String approvedByCust;
    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Quotation() {

    }

    public Quotation(String approvedByCust, String url) {

        this.approvedByCust = approvedByCust;
        this.url = url;
    }

    public String getApprovedByCust() {
        return approvedByCust;
    }

    public void setApprovedByCust(String approvedByCust) {
        this.approvedByCust = approvedByCust;
    }


}
