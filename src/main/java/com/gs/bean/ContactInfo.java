package com.gs.bean;

public class ContactInfo {
    private String date;
    private String name;
    private String subject;
    private String email;
    private String content;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ContactInfo(String date, String name, String subject, String email, String content) {
        this.date = date;
        this.name = name;
        this.subject = subject;
        this.email = email;
        this.content = content;
    }

    public ContactInfo() {
    }
}
