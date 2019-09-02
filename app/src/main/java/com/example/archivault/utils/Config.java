package com.example.archivault.utils;

public class Config {

    public static final String BASE_URL = "https://dev.mraheelkhan.com/archivault/api/";


    public static final String LOGIN_URL = BASE_URL +"login";
    public static final String PROJECTS_URL = BASE_URL +"projects";
    public static final String PROJECTS_SUMMARY_URL = BASE_URL +"summary?project_id=";
    public static final String INCOMES_URL = BASE_URL +"incomes?project_id=";
    public static final String EXPENSES_URL = BASE_URL +"expenses?project_id=";
    public static final String ADD_NEW_INCOME = BASE_URL +"income/store";
    public static final String ADD_NEW_EXPENSE = BASE_URL +"expense/store";
}
