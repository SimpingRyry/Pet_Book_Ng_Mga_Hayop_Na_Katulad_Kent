package com.example.petbook;

public class AdoptionDataClass {
    String pet_image,petname,petage,contact,owner;

    public String getPetname() {
        return petname;
    }

    public void setPetname(String petname) {
        this.petname = petname;
    }

    public String getPet_image() {
        return pet_image;
    }

    public void setPet_image(String pet_image) {
        this.pet_image = pet_image;
    }

    public String getPetage() {
        return petage;
    }

    public void setPetage(String petage) {
        this.petage = petage;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public AdoptionDataClass(String pet_image,String petname, String petage, String contact, String owner) {
        this.petname = petname;
        this.petage = petage;
        this.contact = contact;
        this.owner = owner;
        this.pet_image = pet_image;
    }
}
