package com.cloudsim.cloudsim.policy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import com.cloudsim.cloudsim.model.SimulationRequest;
import com.cloudsim.cloudsim.model.SimulationResult;
public class BestFitSimulation {

    public SimulationResult run(SimulationRequest request) {
        try {
            CloudSim.init(request.getNumberOfUsers(), Calendar.getInstance(), false);
            System.out.println("CloudSim initialized");

            // Create the Datacenter and Broker
            Datacenter datacenter = createDatacenter("Datacenter_0");
            DatacenterBroker broker = new DatacenterBroker("Broker");

            int brokerId = broker.getId();

            // VM Configuration - Dynamic MIPS and RAM based on the request
            int vmMips = 1000;  // Base MIPS value
            if (request.getStrategies().contains("BestFit")) {
                vmMips = 1000;
            } else if (request.getStrategies().contains("RoundRobin")) {
                vmMips = 1500;
            } else if (request.getStrategies().contains("TimeShared")) {
                vmMips = 2000;
            }

            // Create multiple VMs based on the number of VMs in the request
            List<Vm> vmList = new ArrayList<>();
            for (int i = 0; i < request.getNumberOfVms(); i++) {
                Vm vm = new Vm(
                    i,
                    brokerId,
                    vmMips,
                    1,
                    request.getVmRam(),  // Dynamic RAM from the request
                    1000,
                    10000,
                    "Xen",
                    new CloudletSchedulerTimeShared()
                );
                vmList.add(vm);
            }
            broker.submitVmList(vmList);

            // Cloudlet Configuration - Based on the number of cloudlets and users
            double cloudletLength = request.getNumberOfCloudlets() * 1000;  // Adjust cloudlet length
            Cloudlet cloudlet = new Cloudlet(
                0,
                (long) cloudletLength,
                1,
                300,
                300,
                null, null, null, false
            );
            cloudlet.setUserId(brokerId);
            broker.submitCloudletList(Collections.singletonList(cloudlet));

            CloudSim.startSimulation();
            System.out.println("Simulation started");

            CloudSim.stopSimulation();
            System.out.println("Simulation stopped");

            // Calculate execution time based on cloudlet length, VM MIPS, and the number of VMs
            double execTime = cloudlet.getCloudletLength() / (vmMips * request.getNumberOfVms());  // More VMs reduce execution time

            // Factor in the number of users - More users increase load and execution time
            execTime *= (1 + (request.getNumberOfUsers() / 1000.0)); // Additional time for more users

            // Cost Calculation - Influenced by the number of users, VMs, and RAM
            double costPerSec = 3.0;
            double cost = execTime * costPerSec * request.getNumberOfVms();  // More VMs lead to higher cost

            // Energy consumption calculation - Based on VM's resources and execution time
            double energyPerSec = 0.5;
            double energy = execTime * energyPerSec * request.getNumberOfVms();  // More VMs lead to higher energy consumption

            // SLA violation based on execution time (threshold defined by SLA)
            double slaViolation = (execTime > request.getSlaThreshold()) ? (execTime - request.getSlaThreshold()) / request.getSlaThreshold() : 0.0;

            return new SimulationResult("BestFit", execTime, cost, energy, slaViolation);

        } catch (Exception e) {
            e.printStackTrace();
            return new SimulationResult("BestFit", 0, 0, 0, 0);
        }
    }

    private Datacenter createDatacenter(String name) throws Exception {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(1000))); // MIPS for the PE

        int ram = 2048;  // Default RAM
        long storage = 1000000;
        int bw = 10000;

        hostList.add(new Host(
            0,
            new RamProvisionerSimple(ram),
            new BwProvisionerSimple(bw),
            storage,
            peList,
            new VmSchedulerTimeShared(peList)
        ));

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double timeZone = 10.0;
        double costPerSec = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            arch, os, vmm, hostList, timeZone, costPerSec, costPerMem, costPerStorage, costPerBw
        );

        return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
    }
}
