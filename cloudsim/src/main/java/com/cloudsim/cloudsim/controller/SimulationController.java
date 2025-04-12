package com.cloudsim.cloudsim.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudsim.cloudsim.model.SimulationRequest;
import com.cloudsim.cloudsim.model.SimulationResult;
import com.cloudsim.cloudsim.service.SimulationService;

@RestController
@RequestMapping("/api/simulate")
@CrossOrigin(origins = "http://localhost:5173") 
public class SimulationController {

    @Autowired
    private SimulationService simulationService;

    @PostMapping
    public List<SimulationResult> simulate(@RequestBody SimulationRequest request) {
        return simulationService.runSimulations(request);
    }
}
