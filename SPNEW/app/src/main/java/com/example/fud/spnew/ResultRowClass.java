package com.example.fud.spnew;

public class ResultRowClass {
    private String species;
    private String percentage;

    public ResultRowClass(String species, String percentage){
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
