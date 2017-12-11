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
        //System.out.println("We have " + idToLoop.size() + " loops");
        //System.out.println("There is no measurement for " + idsWithoutMeasurement.size() + " loops");


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
