package com.example.archivault.model;

public class IncomesModel {

    private String income_by_user;
    private String income_reason;
    private String income_date;
    private String total_income;


    public IncomesModel(String income_by_user, String income_reason, String income_date, String total_income) {
        this.income_by_user = income_by_user;
        this.income_reason = income_reason;
        this.income_date = income_date;
        this.total_income = total_income;
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

    public String getTotal_income() {
        return total_income;
    }
}
