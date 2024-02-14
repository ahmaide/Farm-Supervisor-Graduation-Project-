package com.example.graduationproject;

public class Crops {

    private String cropName;

    public Crops(){

    }

    public Crops(String cropName) {
        this.cropName = cropName;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    @Override
    public String toString() {
        return "Crops{" +
                "cropName='" + cropName + '\'' +
                '}';
    }
}
