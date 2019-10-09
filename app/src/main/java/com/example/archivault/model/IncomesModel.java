package com.example.archivault.model;

public class IncomesModel {

    private String income_id;
    private String income_by_user;
    private String income_reason;
    private String income_date;
    private String total_amount;


    public IncomesModel(String income_id, String income_by_user, String income_reason, String income_date, String total_amount) {

        this.income_id = income_id;
        this.income_by_user = income_by_user;
        this.income_reason = income_reason;
        this.income_date = income_date;
        this.total_amount = total_amount;
    }

    public String getIncome_id() {
        return income_id;
    }

    public String getIncome_by_user() {
        return income_by_user;
    }

    public String getIncome_reason() {
        return income_reason;
    }

    public String getIncome_date() {
        return income_date;
    }

    public String getTotal_amount() {
        return total_amount;
    }
}
