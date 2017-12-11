package com.company;

import java.util.*;

/**
 * Created by remzi on 12/7/17.
 */
public class Loop {

    String loopId;
    String features;
    int bestUnrollFactor;
    HashMap<Integer, HashSet<Double>> factorToRunTimes;
    double bestRunTime;

    public Loop(String loopId){
        this.loopId = loopId;
        factorToRunTimes = new HashMap<>();
        features = null;
    }

    public void addRuntime(int factor, double duration) {
        if(duration <= 0) {
            System.err.println("duration should be positive");
            System.exit(1);
        }

        HashSet<Double> runTimes = factorToRunTimes.get(factor);
        if(runTimes == null) {
            runTimes = new HashSet<>();
            factorToRunTimes.put(factor, runTimes);
        }

        runTimes.add(duration);
    }

    public void calculateAverageRuntimes() {
        if(factorToRunTimes.size() == 0) {
            System.out.println("No positive entry for id: " + loopId);
            return;
        }
        int bestFactor = -1;
        double minAverage = Double.MAX_VALUE;

        Iterator it = factorToRunTimes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            int factor = (Integer) pair.getKey();
            HashSet<Double> runTimes = (HashSet<Double>) pair.getValue();

            double average = calculateAverageForFactor(runTimes);
            if(average < minAverage) {
                minAverage = average;
                bestFactor = factor;
            }
        }

        bestUnrollFactor = bestFactor;
        bestRunTime = minAverage;
    }

    private double calculateAverageForFactor(HashSet<Double> runTimes) {
        int count = 0;
        double sum = 0;
        if( runTimes.size() <= 0) {
            System.err.println("no entry for this factor");
            System.exit(1);
        }

        for(double d : runTimes) {
            if( d <= 0) {
                System.err.println("found negative entry");
                System.exit(1);
            }

            count++;
            sum += d;
        }

        return sum / (double) count;
    }

    public String toString() {
        String s = features + "," + bestUnrollFactor;
        return s;
    }

    public String toString2() {
        String s = "";
        Iterator it = factorToRunTimes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            int factor = (Integer) pair.getKey();
            HashSet<Double> runTimes = (HashSet<Double>) pair.getValue();

            double average = calculateAverageForFactor(runTimes);
            String line = loopId + "," + features + "," + factor + "," + average;
            s = s + line + "\n";
        }

        return s;
    }
}
