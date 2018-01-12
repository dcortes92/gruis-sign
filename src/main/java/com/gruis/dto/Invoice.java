package com.gruis.dto;

public class Invoice {
    private int userId;
    private String certificate;
    private String pin;
    private String xml;
    private String key;
    private String user;
    private String password;
    private int mode;

    public Invoice(String certificate, String pin, String xml, String key, int userId, String user, String password, int mode) {
        this.certificate = certificate;
        this.xml = xml;
        this.key = key;
        this.userId = userId;
        this.user = user;
        this.password = password;
        this.mode = mode;
        this.pin = pin;
    }

    public Invoice() { }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCertificate() {
        return this.certificate;
    }

    public String getKey() {
        return key;
    }

    public String getXml() {
        return xml;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "certificate='" + certificate + '\'' +
                ", xml='" + xml + '\'' +
                ", key='" + key + '\'' +
                ", userId='" + userId + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
