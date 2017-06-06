package com.example.rajk.leasingmanagers.model;

/**
 * Created by RajK on 03-06-2017.
 */

public class Quotation {
    private String quotationId,quotationUrl,approvedByCust,approvedByAdmin;

    public Quotation() {

    }

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(String quotationId) {
        this.quotationId = quotationId;
    }

    public String getQuotationUrl() {
        return quotationUrl;
    }

    public void setQuotationUrl(String quotationUrl) {
        this.quotationUrl = quotationUrl;
    }

    public String getApprovedByCust() {
        return approvedByCust;
    }

    public void setApprovedByCust(String approvedByCust) {
        this.approvedByCust = approvedByCust;
    }

    public String getApprovedByAdmin() {
        return approvedByAdmin;
    }

    public void setApprovedByAdmin(String approvedByAdmin) {
        this.approvedByAdmin = approvedByAdmin;
    }
}
