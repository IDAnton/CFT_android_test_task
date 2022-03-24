package com.example.task;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Valute {
    private String id;
    private String numCode;
    private String charCode;
    private Integer nominal;
    private String name;
    private double value;
    private double previous;

    public Valute(JSONObject json) {
        try {
            this.id = (String) json.get("ID");

        this.numCode = (String)  json.get("NumCode");
        this.charCode = (String)  json.get("CharCode");
        this.nominal = (Integer)  json.get("Nominal");
        this.name = (String)  json.get("Name");
        this.value = (double) json.get("Value");
        this.previous = (double) json.get("Previous");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumCode() {
        return numCode;
    }

    public void setNumCode(String numCode) {
        this.numCode = numCode;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public Integer getNominal() {
        return nominal;
    }

    public void setNominal(Integer nominal) {
        this.nominal = nominal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getPrevious() {
        return previous;
    }

    public void setPrevious(double previous) {
        this.previous = previous;
    }

    @NonNull
    @Override
    public String toString() {
        return charCode;
    }
}
