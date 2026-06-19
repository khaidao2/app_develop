package com.example.models;

import java.io.Serializable;
import java.util.ArrayList;

public class StudyTypeGroup implements Serializable {
    private String studyType;
    private ArrayList<Major> majors;

    public StudyTypeGroup() {
        this.majors = new ArrayList<>();
    }

    public StudyTypeGroup(String studyType, ArrayList<Major> majors) {
        this.studyType = studyType;
        this.majors = majors;
    }

    public String getStudyType() {
        return studyType;
    }

    public void setStudyType(String studyType) {
        this.studyType = studyType;
    }

    public ArrayList<Major> getMajors() {
        return majors;
    }

    public void setMajors(ArrayList<Major> majors) {
        this.majors = majors;
    }
}
