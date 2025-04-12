package com.cloudsim.cloudsim.model;

import java.util.List;

public class SimulationRequest {
    private int numberOfUsers;
    private int numberOfVms;
    private int numberOfCloudlets;
    private int vmRam;
    private double slaThreshold;
    private List<String> strategies;

    // Getters and Setters
    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public int getNumberOfVms() {
        return numberOfVms;
    }

    public void setNumberOfVms(int numberOfVms) {
        this.numberOfVms = numberOfVms;
    }

    public int getNumberOfCloudlets() {
        return numberOfCloudlets;
    }

    public void setNumberOfCloudlets(int numberOfCloudlets) {
        this.numberOfCloudlets = numberOfCloudlets;
    }

    public int getVmRam() {
        return vmRam;
    }

    public void setVmRam(int vmRam) {
        this.vmRam = vmRam;
    }

    public double getSlaThreshold() {
        return slaThreshold;
    }

    public void setSlaThreshold(double slaThreshold) {
        this.slaThreshold = slaThreshold;
    }

    public List<String> getStrategies() {
        return strategies;
    }

    public void setStrategies(List<String> strategies) {
        this.strategies = strategies;
    }
}
