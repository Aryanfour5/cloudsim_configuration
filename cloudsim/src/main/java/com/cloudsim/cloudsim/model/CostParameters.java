package com.cloudsim.cloudsim.model;

public class CostParameters {
    public double costPerSecond;
    public double costPerMem;
    public double costPerStorage;
    public double costPerBw;

    public CostParameters(double costPerSecond, double costPerMem, 
                        double costPerStorage, double costPerBw) {
        this.costPerSecond = costPerSecond;
        this.costPerMem = costPerMem;
        this.costPerStorage = costPerStorage;
        this.costPerBw = costPerBw;
    }
}