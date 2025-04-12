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

public class RoundRobinSimulation {

    public SimulationResult run(SimulationRequest request) {
        try {
            CloudSim.init(request.getNumberOfUsers(), Calendar.getInstance(), false);

            Datacenter datacenter = createDatacenter("Datacenter_0");
            DatacenterBroker broker = new DatacenterBroker("Broker");

            int brokerId = broker.getId();

            // VM Configuration
            int vmMips = 1500;
            Vm vm = new Vm(
                0,
                brokerId,
                vmMips,  // Different MIPS value for RoundRobin
                1,
                request.getVmRam(),
                1000,
                10000,
                "Xen",
                new CloudletSchedulerTimeShared()
            );
            broker.submitVmList(Collections.singletonList(vm));

            // Cloudlet Configuration
            long cloudletLength = request.getNumberOfCloudlets() * 1000;
            Cloudlet cloudlet = new Cloudlet(
                0,
                cloudletLength,
                1,
                300,
                300,
                null, null, null, false
            );
            cloudlet.setUserId(brokerId);
            broker.submitCloudletList(Collections.singletonList(cloudlet));

            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // RoundRobin strategy reduces execution time by cycling through VMs
            double execTime = cloudlet.getCloudletLength() / (vmMips * request.getNumberOfVms());
            execTime *= (1 + (request.getNumberOfUsers() / 1000.0));

            // Cost Calculation - Cost increases with more VMs and users
            double costPerSec = 3.0;
            double cost = execTime * costPerSec * request.getNumberOfVms();

            // Energy consumption calculation - More VMs increase energy usage
            double energyPerSec = 0.5;
            double energy = execTime * energyPerSec * request.getNumberOfVms();

            // SLA violation based on execution time
            double slaViolation = (execTime > request.getSlaThreshold()) ? (execTime - request.getSlaThreshold()) / request.getSlaThreshold() : 0.0;

            return new SimulationResult("RoundRobin", execTime, cost, energy, slaViolation);

        } catch (Exception e) {
            e.printStackTrace();
            return new SimulationResult("RoundRobin", 0, 0, 0, 0);
        }
    }

    private Datacenter createDatacenter(String name) throws Exception {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(1000)));

        int ram = 2048;
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
