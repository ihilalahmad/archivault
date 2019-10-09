package com.example.archivault.model;

public class ExpensesModel {

    private String id;
    private String title_summary;
    private String username_summary;
    private String date_summary;
    private String total_amount_summary;
    private String item_quantity;


    public ExpensesModel(String id, String title_summary, String username_summary, String date_summary, String total_amount_summary, String item_quantity) {
        this.id = id;
        this.title_summary = title_summary;
        this.username_summary = username_summary;
        this.date_summary = date_summary;
        this.total_amount_summary = total_amount_summary;
        this.item_quantity = item_quantity;
    }

    public String getId() {
        return id;
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

    public String getItem_quantity(){

        return item_quantity;
    }
}
