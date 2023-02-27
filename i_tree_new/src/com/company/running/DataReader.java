package com.company.running;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataReader {
    String initDomainFileDir;
    String recordsFileDir;

    public DataReader(String initDomainFileDir, String recordsFileDir) {
        this.initDomainFileDir = initDomainFileDir;
        this.recordsFileDir = recordsFileDir;
    }

    public double[][] coefficientSet() throws IOException {
        Map<String, ArrayList<Map<String, Integer>>> records = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(this.recordsFileDir);
        records = objectMapper.readValue(file, new TypeReference<Map<String, ArrayList<Map<String, Integer>>>>(){});
        double[][] coefficients = new double[records.get("records").size()][];
//        double[] coefficient = new double[3]; // need to be changed

        for (int i = 0; i < records.get("records").size(); i++){
            double[] coefficient = new double[3]; // need to be changed
            coefficient[0] = records.get("records").get(i).get("coefficient_x1");
            coefficient[1] = records.get("records").get(i).get("coefficient_x2");
//            coefficient[2] = records.get("records").get(i).get("coefficient_x3");
//            System.out.println(coefficient[0]);
//            System.out.println(coefficient[1]);
            coefficient[2] = records.get("records").get(i).get("constant");
            coefficients[i] = coefficient;
        }
        return coefficients;
    }
}
