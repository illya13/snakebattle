package com.github.illya13.snakebattle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Statistics {
    private enum FIELDS {
        CNT, SUM
    }

    private String filename;
    private Map<String, Double> stat;

    public Statistics(String filename) {
        this.filename = filename;

        stat = readJson(filename);
        if (stat == null) {
            stat = new HashMap<>();
        }
    }

    public double count() {
        Double cnt = stat.get(FIELDS.CNT.name());
        if (cnt == null) cnt = 0d;
        return cnt;
    }

    public double total() {
        Double sum = stat.get(FIELDS.SUM.name());
        if (sum == null) sum = 0d;
        return sum;
    }


    public void update(double rewards) {
        Double sum = total();
        sum += rewards;
        stat.put(FIELDS.SUM.name(), sum);

        Double cnt = count();
        cnt++;
        stat.put(FIELDS.CNT.name(), cnt);

        writeJson(stat, filename);
    }

    private String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    private Map<String, Double> readJson(String path) {
        try {
            String json = read(path);
            TypeReference<Map<String, Double>> typeRef = new TypeReference<Map<String, Double>>() {};
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, typeRef);
        } catch (Exception ignored) {
        }
        return null;
    }

    private void write(String path, String content) throws Exception {
        Files.write(Paths.get(path), content.getBytes());
    }

    private void writeJson(Map<String, Double> map, String path) {
        try{
            String json = new ObjectMapper().writeValueAsString(map);
            write(path, json);
        } catch (Exception ignored) {
        }
    }
}
