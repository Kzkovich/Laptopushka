package ru.kzkovich.laptopushka.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "config")
public class CharacteristicsConfig {

    @PrimaryKey
    private String articul;
    private Double rate;

    CharacteristicsConfig(){};

    CharacteristicsConfig(String articul, Double rate) {
        this.articul = articul;
        this.rate = rate;
    }

    public String getArticul() {
        return articul;
    }

    public void setArticul(String articul) {
        this.articul = articul;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
