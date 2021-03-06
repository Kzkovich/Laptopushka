package ru.kzkovich.laptopushka.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "laptop_characteristics")
public class LaptopCharacteristics {

    @PrimaryKey
    private String articul;
    private String brand;
    private String model;
    private String cpu;
    private String screen;
    private String ram;
    private String hdd;
    private String graphics;
    private String resolution;
    private String matrixType;
    private Double priceInDollars;

    public String getArticul() {
        return articul;
    }

    public void setArticul(String articul) {
        this.articul = articul;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getHdd() {
        return hdd;
    }

    public void setHdd(String hdd) {
        this.hdd = hdd;
    }

    public String getGraphics() {
        return graphics;
    }

    public void setGraphics(String graphics) {
        this.graphics = graphics;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getMatrixType() {
        return matrixType;
    }

    public void setMatrixType(String matrixType) {
        this.matrixType = matrixType;
    }

    public Double getPriceInDollars() {
        return priceInDollars;
    }

    public void setPriceInDollars(Double priceInDollars) {
        this.priceInDollars = priceInDollars;
    }

    public LaptopCharacteristics(String articul, String brand, String model, String cpu, String screen, String ram, String hdd, String graphics, String resolution, String matrixType, Double priceInDollars) {
        this.articul = articul;
        this.brand = brand;
        this.model = model;
        this.cpu = cpu;
        this.screen = screen;
        this.ram = ram;
        this.hdd = hdd;
        this.graphics = graphics;
        this.resolution = resolution;
        this.matrixType = matrixType;
        this.priceInDollars = priceInDollars;
    }
    public LaptopCharacteristics() {

    }

    @Override
    public String toString() {
        return "LaptopCharacteristics{" +
                "articul='" + articul + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", cpu='" + cpu + '\'' +
                ", screen='" + screen + '\'' +
                ", ram='" + ram + '\'' +
                ", hdd='" + hdd + '\'' +
                ", graphics='" + graphics + '\'' +
                ", resolution='" + resolution + '\'' +
                ", matrixType='" + matrixType + '\'' +
                ", priceInDollars=" + priceInDollars +
                '}';
    }
}
