    package com.cloudsim.cloudsim.model;

    public class SimulationResult {

        private String strategy;
        private double executionTime;
        private double cost;
        private double energyConsumption;
        private double slaViolation;

        public SimulationResult() {}

        public SimulationResult(String strategy, double executionTime, double cost, double energyConsumption, double slaViolation) {
            this.strategy = strategy;
            this.executionTime = executionTime;
            this.cost = cost;
            this.energyConsumption = energyConsumption;
            this.slaViolation = slaViolation;
        }
        private int totalCloudletsProcessed;
        private double averageExecutionTime;
        
        public int getTotalCloudletsProcessed() {
            return totalCloudletsProcessed;
        }
        public void setTotalCloudletsProcessed(int totalCloudletsProcessed) {
            this.totalCloudletsProcessed = totalCloudletsProcessed;
        }
        
        public double getAverageExecutionTime() {
            return averageExecutionTime;
        }
        public void setAverageExecutionTime(double averageExecutionTime) {
            this.averageExecutionTime = averageExecutionTime;
        }
        
        public String getStrategy() { return strategy; }
        public void setStrategy(String strategy) { this.strategy = strategy; }

        public double getExecutionTime() { return executionTime; }
        public void setExecutionTime(double executionTime) { this.executionTime = executionTime; }

        public double getCost() { return cost; }
        public void setCost(double cost) { this.cost = cost; }

        public double getEnergyConsumption() { return energyConsumption; }
        public void setEnergyConsumption(double energyConsumption) { this.energyConsumption = energyConsumption; }

        public double getSlaViolation() { return slaViolation; }
        public void setSlaViolation(double slaViolation) { this.slaViolation = slaViolation; }
    }
