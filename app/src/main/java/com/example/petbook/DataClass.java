package com.example.petbook;
public class DataClass {
    private String imageURL, caption, contact,status;



    public DataClass(){
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }
    public String getContact() {
        return contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
    public DataClass(String imageURL, String caption, String contact, String status) {
        this.imageURL = imageURL;
        this.caption = caption;
        this.contact = contact;
        this.status = status;
    }
}