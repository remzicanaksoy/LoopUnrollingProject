package com.company;

import java.io.*;
import java.util.*;

/**
 * Created by remzi on 12/7/17.
 */
public class Main {
    static HashSet<String> idsWithoutMeasurement = new HashSet<>();
    final static String outputFile = "mergedData.txt";
    final static String outputFile2 = "mergedDataMultipleLine.txt";

    public static void main(String[] args) {
        //read each execution file and keep the average for each loop and for each unroll factor
        HashMap<String, Loop> idToLoop = new HashMap<>();
        String directoryPath = args[0];
        File[] files = new File(directoryPath).listFiles();
        for (File file : files) {
            if(file.getName().contains("Exec")) {
                //Read the file and update the loops
                updateLoopInfoWithExecTime(idToLoop, file);
            }
        }

        //iterate through unrool factors and keep the best one
        Iterator it = idToLoop.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String id = (String) pair.getKey();
            Loop loop = (Loop) pair.getValue();
            if(!id.equals(loop.loopId)) {
                System.err.println("inconsisteny for loop id's");
                System.exit(1);
            }
            loop.calculateAverageRuntimes();
        }

        for (File file : files) {
            if(file.getName().contains("Feature")) {
                //Read the file and update the loop features
                updateLoopInfoWithFeatures(idToLoop, file);
            }
        }

        printLoopsToNewFile(idToLoop);
        printMultipleLinesPerLoopToNewFile(idToLoop);

        HashMap<String, Integer> perfectDecisions = constructTheLoopListWithDecisons("/Users/remzi/Desktop/results/knn.txt", 18, idToLoop);
        HashMap<String, Double> bestExecutionTimes = calculateTimesForGivenDecisions(perfectDecisions, idToLoop);

        HashMap<String, Integer> decisionsOfKNN = constructTheLoopListWithDecisons("/Users/remzi/Desktop/results/knn.txt", -1, idToLoop);
        HashMap<String, Double> executionTimesForKNN = calculateTimesForGivenDecisions(decisionsOfKNN, idToLoop);
        HashMap<String, Integer> decisionsOfDecisionTree = constructTheLoopListWithDecisons("/Users/remzi/Desktop/results/DecisionTree.txt", -1, idToLoop);
        HashMap<String, Double> executionTimesForDecisionTree = calculateTimesForGivenDecisions(decisionsOfDecisionTree, idToLoop);
        HashMap<String, Integer> decisionsOfMLPClassifier = constructTheLoopListWithDecisons("/Users/remzi/Desktop/results/MLPClassifier.txt", -1, idToLoop);
        HashMap<String, Double> executionTimesForMLP = calculateTimesForGivenDecisions(decisionsOfMLPClassifier, idToLoop);
        HashMap<String, Integer> decisionsOfRandomForest = constructTheLoopListWithDecisons("/Users/remzi/Desktop/results/Random-forest.txt", -1, idToLoop);
        HashMap<String, Double> executionTimesForRandomForest = calculateTimesForGivenDecisions(decisionsOfRandomForest, idToLoop);
        HashMap<String, Integer> decisionsOfLLVM = constructTheLoopListWithDecisons("/Users/remzi/Desktop/results/Random-forest.txt", 8, idToLoop);
        HashMap<String, Double> executionTimesForLLVM = calculateTimesForGivenDecisions(decisionsOfLLVM, idToLoop);
        HashMap<String, Integer> randomDecisons = constructTheLoopListWithDecisons("/Users/remzi/Desktop/results/Random-forest.txt", 17, idToLoop);
        HashMap<String, Double> executionTimesForRandomDecisons = calculateTimesForGivenDecisions(randomDecisons, idToLoop);
        HashMap<String, Integer> decisionsOfAsIs = constructTheLoopListWithDecisons("/Users/remzi/Desktop/results/Random-forest.txt", 1, idToLoop);
        HashMap<String, Double> executionTimesForAsIs = calculateTimesForGivenDecisions(decisionsOfAsIs, idToLoop);

        normalizeTheExecutionTimes(bestExecutionTimes, executionTimesForAsIs);
        normalizeTheExecutionTimes(executionTimesForKNN, executionTimesForAsIs);
        normalizeTheExecutionTimes(executionTimesForDecisionTree, executionTimesForAsIs);
        normalizeTheExecutionTimes(executionTimesForMLP, executionTimesForAsIs);
        normalizeTheExecutionTimes(executionTimesForRandomForest, executionTimesForAsIs);
        normalizeTheExecutionTimes(executionTimesForLLVM, executionTimesForAsIs);
        normalizeTheExecutionTimes(executionTimesForRandomDecisons, executionTimesForAsIs);
        normalizeTheExecutionTimes(executionTimesForAsIs, executionTimesForAsIs);

        printNormalizedTimesAndTotalTime(bestExecutionTimes, "/Users/remzi/Desktop/results/BestExecutionNormalizedTime");
        printNormalizedTimesAndTotalTime(executionTimesForKNN, "/Users/remzi/Desktop/results/KNNNormalizedTime");
        printNormalizedTimesAndTotalTime(executionTimesForDecisionTree, "/Users/remzi/Desktop/results/DecisionTreeNormalizedTime");
        printNormalizedTimesAndTotalTime(executionTimesForMLP, "/Users/remzi/Desktop/results/MLPNormalizedTime");
        printNormalizedTimesAndTotalTime(executionTimesForRandomForest, "/Users/remzi/Desktop/results/RandomForestNormalizedTime");
        printNormalizedTimesAndTotalTime(executionTimesForLLVM, "/Users/remzi/Desktop/results/LLVMNormalizedTime");
        printNormalizedTimesAndTotalTime(executionTimesForRandomDecisons, "/Users/remzi/Desktop/results/RandomNormalizedTime");
        printNormalizedTimesAndTotalTime(executionTimesForAsIs, "/Users/remzi/Desktop/results/ASISNormalizedTime");

        //System.out.println("We have " + idToLoop.size() + " loops");
        //System.out.println("There is no measurement for " + idsWithoutMeasurement.size() + " loops");


    }

    public static void printNormalizedTimesAndTotalTime(HashMap<String, Double> list, String outputFile) {
        Iterator it = list.entrySet().iterator();
        double sum = 0;
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String id = (String) pair.getKey();
            double executionTime = (double) pair.getValue();
            sum += executionTime;
        }

        writer.println("Total normalized execution time = " + sum);
        System.out.println("total normalized execution time = " + sum + " for " + outputFile.substring(outputFile.lastIndexOf("/") + 1));
        it = list.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String id = (String) pair.getKey();
            double executionTime = (double) pair.getValue();
            writer.println(id + "," + executionTime);

        }

        writer.close();

    }

    public static void normalizeTheExecutionTimes(HashMap<String, Double> listToNormalize, HashMap<String, Double> baseList) {
        Iterator it = listToNormalize.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String id = (String) pair.getKey();
            double normalExecutionTime = (double) pair.getValue();
            double baseExecutionTime = baseList.get(id);
            listToNormalize.put(id, normalExecutionTime/baseExecutionTime);
        }
    }

    public static HashMap<String, Double> calculateTimesForGivenDecisions(HashMap<String, Integer> decisions, HashMap<String, Loop> idToLoop) {
        HashMap<String, Double> executionTimesForList = new HashMap<>();
        Iterator it = decisions.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String id = (String) pair.getKey();
            int factorDecision = (int) pair.getValue();
            Loop loop = idToLoop.get(id);
            double executionTime = loop.calculateAverageForFactor(factorDecision);
            executionTimesForList.put(id, executionTime);
        }

        return executionTimesForList;
    }

    public static HashMap<String, Integer> constructTheLoopListWithDecisons(String filename, int constantFactor, HashMap<String, Loop> idToLoop) {
        HashMap<String, Integer> loopList = new HashMap<>();
        int[] possibleFactors = {1,2,3,4,5,8,10,16};
        Random random = new Random();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String loopId = values[0];
                int factor = Integer.parseInt(values[2]);
                if(constantFactor >= 0) {
                    if(constantFactor <= 16) {
                        factor = constantFactor;
                    }
                    else if(constantFactor == 17)
                    {
                        int index = random.nextInt(possibleFactors.length);
                        factor = possibleFactors[index];
                    }
                    else if(constantFactor == 18)
                    {
                        factor = idToLoop.get(loopId).bestUnrollFactor;
                    }
                }

                loopList.put(loopId, factor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return loopList;
    }

    private static void printMultipleLinesPerLoopToNewFile(HashMap<String, Loop> idToLoop) {
        Iterator it = idToLoop.entrySet().iterator();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputFile2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int count = 0;

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String id = (String) pair.getKey();
            Loop loop = (Loop) pair.getValue();
            if(loop.features == null) {
                //System.out.println("no features for loop " + loop.loopId);
                //System.out.println(loop);
                count++;
            }
            else {
                writer.print(loop.toString2());
            }
        }

        writer.close();
        //System.out.println("no features for " + count + " loops");

    }

    private static void printLoopsToNewFile(HashMap<String, Loop> idToLoop) {
        Iterator it = idToLoop.entrySet().iterator();
        int count = 0;
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String id = (String) pair.getKey();
            Loop loop = (Loop) pair.getValue();
            if(loop.features == null) {
                count++;
                //System.out.println("no features for loop " + loop.loopId);
                //System.out.println(loop);

            }
            else {
                writer.println(loop);
            }
        }

        writer.close();
        //System.out.println("no features for " + count + " loops");
    }

    private static void updateLoopInfoWithFeatures(HashMap<String, Loop> idToLoop, File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                String[] values = line.replaceAll("\\s+","").split(",");
                String loopId = values[0];
                String[] features = Arrays.copyOfRange(values, 1, values.length);
                Loop loop = idToLoop.get(loopId);
                if(loopId.equals("30080006")) {
                    System.out.println("WTF");
                }
                if(loop == null) {
                    idsWithoutMeasurement.add(loopId);
                    //System.out.println("there is no measurements for the loop: " + loopId);
                }
                else {
                    String featureString = convertFeatureArrayToString(features);
                    if (loop.features == null) {
                        loop.features = featureString;
                    } else if (!loop.features.equals(featureString)) {
                        //System.out.println("detected different features for loop " + loopId + ": " + featureString + " vs " + loop.features);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertFeatureArrayToString(String[] features) {
        String s = "";
        for(String feature : features) {
            if(!s.equals("")) {
                s = s + "," + feature;
            }
            else {
                s = feature;
            }
        }

        return s;
    }

    public static void updateLoopInfoWithExecTime(HashMap<String, Loop> idToLoop, File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                String[] values = line.replaceAll("\\s+","").split(",");
                String loopId = values[0];
                int unrollFactor = Integer.parseInt(values[1]);
                double duration = Double.parseDouble(values[2]);

                if(unrollFactor < 1 || duration <= 0) {
                    //System.out.println("zero unroll factor or negative duration");
                }
                else {
                    Loop loop = idToLoop.get(loopId);

                    if(loop == null) {
                        loop = new Loop(loopId);
                        idToLoop.put(loopId, loop);
                    }

                    loop.addRuntime(unrollFactor, duration);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
