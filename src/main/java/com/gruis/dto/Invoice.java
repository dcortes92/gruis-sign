package com.gruis.dto;

public class Invoice {
    private String certificate;
    private String xml;
    private String key;

    public Invoice(String certificate, String xml, String key) {
        this.certificate = certificate;
        this.xml = xml;
        this.key = key;
    }

    public Invoice() { }

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
                '}';
    }
}
