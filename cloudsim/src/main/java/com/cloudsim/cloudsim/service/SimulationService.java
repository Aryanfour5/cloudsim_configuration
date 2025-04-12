package com.cloudsim.cloudsim.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cloudsim.cloudsim.model.SimulationRequest;
import com.cloudsim.cloudsim.model.SimulationResult;
import com.cloudsim.cloudsim.policy.BestFitSimulation;
import com.cloudsim.cloudsim.policy.RoundRobinSimulation;
import com.cloudsim.cloudsim.policy.TimeSharedSimulation;

@Service
public class SimulationService {

    public List<SimulationResult> runSimulations(SimulationRequest request) {
        List<SimulationResult> results = new ArrayList<>();

        // Loop through each strategy and run the corresponding simulation
        for (String strategy : request.getStrategies()) {
            switch (strategy) {
                case "BestFit":
                    results.add(new BestFitSimulation().run(request));
                    break;
                case "RoundRobin":
                    results.add(new RoundRobinSimulation().run(request));
                    break;
                case "TimeShared":
                    results.add(new TimeSharedSimulation().run(request));
                    break;
                // Add more strategies here if needed
                default:
                    throw new IllegalArgumentException("Unknown strategy: " + strategy);
            }
        }

        return results;
    }
}
