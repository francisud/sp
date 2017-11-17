package com.example.fud.spnew;

public class Class_ResultRow {
    private String species;
    private String percentage;

    public Class_ResultRow(String species, String percentage){
        this.species = species;
        this.percentage = percentage;
    }

    public String getSpecies(){
        return species;
    }

    public String getPercentage(){
        return percentage;
    }
}
