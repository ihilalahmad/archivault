package com.example.archivault.model;

public class SummaryModel {

    private String title_summary;
    private String username_summary;
    private String date_summary;
    private String total_amount_summary;


    public SummaryModel(String title_summary, String username_summary, String date_summary, String total_amount_summary) {
        this.title_summary = title_summary;
        this.username_summary = username_summary;
        this.date_summary = date_summary;
        this.total_amount_summary = total_amount_summary;
    }

    public String getTitle_summary() {
        return title_summary;
    }

    public String getUsername_summary() {
        return username_summary;
    }

    public String getDate_summary() {
        return date_summary;
    }

    public String getTotal_amount_summary() {
        return total_amount_summary;
    }
}
