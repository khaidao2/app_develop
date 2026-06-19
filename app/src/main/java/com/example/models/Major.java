package com.example.models;

import java.io.Serializable;

public class Major implements Serializable {
    private String name;
    private String ologyId;
    private String departmentId;
    private String graduateLevelId;
    private String studyTypeId;

    public Major() {
    }

    public Major(String name, String ologyId, String departmentId, String graduateLevelId, String studyTypeId) {
        this.name = name;
        this.ologyId = ologyId;
        this.departmentId = departmentId;
        this.graduateLevelId = graduateLevelId;
        this.studyTypeId = studyTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOlogyId() {
        return ologyId;
    }

    public void setOlogyId(String ologyId) {
        this.ologyId = ologyId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getGraduateLevelId() {
        return graduateLevelId;
    }

    public void setGraduateLevelId(String graduateLevelId) {
        this.graduateLevelId = graduateLevelId;
    }

    public String getStudyTypeId() {
        return studyTypeId;
    }

    public void setStudyTypeId(String studyTypeId) {
        this.studyTypeId = studyTypeId;
    }
}
