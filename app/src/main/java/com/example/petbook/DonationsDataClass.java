package com.example.petbook;

public class DonationsDataClass {
    public String pet_image,pet_name,contact,descriptiom;

    public String getPet_image() {
        return pet_image;
    }

    public void setPet_image(String pet_image) {
        this.pet_image = pet_image;
    }

    public String getPet_name() {
        return pet_name;
    }

    public void setPet_name(String pet_name) {
        this.pet_name = pet_name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDescriptiom() {
        return descriptiom;
    }

    public void setDescriptiom(String descriptiom) {
        this.descriptiom = descriptiom;
    }

    public DonationsDataClass(String pet_image, String pet_name, String contact, String descriptiom) {
        this.pet_image = pet_image;
        this.pet_name = pet_name;
        this.contact = contact;
        this.descriptiom = descriptiom;
    }
}
