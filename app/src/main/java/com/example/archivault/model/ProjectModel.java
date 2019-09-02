package com.example.archivault.model;

public class ProjectModel {

    private String project_id;
    private String project_name;

    public ProjectModel(String project_id, String project_name) {
        this.project_id = project_id;
        this.project_name = project_name;
    }

    public String getProject_id() {
        return project_id;
    }

    public String getProject_name() {
        return project_name;
    }
}
