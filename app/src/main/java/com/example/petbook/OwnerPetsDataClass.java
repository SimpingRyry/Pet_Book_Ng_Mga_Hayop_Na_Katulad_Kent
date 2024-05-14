package com.example.petbook;

public class OwnerPetsDataClass {
    public OwnerPetsDataClass(String image_url, String pet_name) {
        this.image_url = image_url;
        this.pet_name = pet_name;
    }

    String image_url, pet_name;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPet_name() {
        return pet_name;
    }

    public void setPet_name(String pet_name) {
        this.pet_name = pet_name;
    }
}
