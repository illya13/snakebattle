package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.Dice;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientProperties;

import javax.net.ssl.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Learning {
    enum FEATURE {
        STONES, ATTACK, FLY, FOLLOW, DESTRUCT, SHORT, MEDIUM, PREDICT
    }

    public static abstract class Strategy {
        protected Set<FEATURE> features;
        protected Map<String, Double> weights;

        protected Dice dice;

        public Strategy(Dice dice) {
            this.dice = dice;
        }

        public boolean hasFeature(FEATURE feature) {
            return features.contains(feature);
        }

        public abstract void init();
        public abstract void update(double weight, int steps);
    }

    private static final String URL = "https://epam-bot-challenge.com.ua/codenjoy-balancer/rest/score/day/";

    private Board board;
    private Strategy strategy;
    private String date;
    private String player;
    private Optional<Map<String, String>> prev = Optional.empty();

    public Learning() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        date = localDate.format(formatter);
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void reset(Board board, int steps){
        this.board = board;

        Optional<Map<String, String>> stat = getStat(true);
        if ((board != null) && prev.isPresent() && stat.isPresent()) {
            if (steps < this.board.size()+10) {
                System.out.println("dead bots");
            } else {
                int before = Integer.valueOf(prev.get().get("score"));
                int now = Integer.valueOf(stat.get().get("score"));
                double delta = (now - before);
                if (delta > 0) {
                    System.out.printf(" == %s before: %d, now: %d, delta: %.0f, steps: %d => %.3f\n",
                            strategy.toString(), before, now, delta, steps, delta / steps);

                    strategy.update(delta, steps);
                }
            }
        }
        prev = stat;
        strategy.init();
    }

    public Optional<Map<String, String>> getStat(boolean debug) {
        Optional<Client> client = newClient();
        if (client.isPresent()) {
            List<Map<String, String>> table = getStandings(client.get());
            for (Map<String, String> map: table) {
                if (map.get("id").equals(player)) {
                    if (debug) System.out.println(map);
                    return Optional.of(map);
                }
            }
        }
        return Optional.empty();
    }

    private List<Map<String, String>> getStandings(Client client) {
        try {
            WebTarget webTarget = client.target(URL + date);
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.get();
            String stringResponse = response.readEntity(String.class);

            TypeReference<List<Map<String,String>>> typeRef = new TypeReference<List<Map<String,String>>>(){};
            ObjectMapper mapper  = new ObjectMapper();
            return mapper.readValue(stringResponse, typeRef);
        } catch (Exception ignored) {
        }
        return Collections.emptyList();
    }


    private static TrustManager[] getTrustManager() {
        return new TrustManager[] { new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                // Trust all servers
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                // Trust all clients
            }
        } };
    }

    public static Optional<Client> newClient() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, getTrustManager(), new SecureRandom());

            HostnameVerifier verifier = (hostName, sslSession) -> true;

            Client client = ClientBuilder.newBuilder()
                    .sslContext(ctx)
                    .hostnameVerifier(verifier)
                    .build();

            client.property(ClientProperties.CONNECT_TIMEOUT, 300);
            client.property(ClientProperties.READ_TIMEOUT,    300);

            return Optional.of(client);
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }


    public static class DefaultStrategy extends Strategy {
        enum FIELDS {
            CNT, SUM
        }

        private String featuresPath;
        private String averagePath;
        private Map<String, Double> average;

        public DefaultStrategy(Dice dice, String featuresPath, String averagePath) {
            super(dice);
            this.featuresPath = featuresPath;
            this.averagePath = averagePath;

            features = new HashSet<>();
            weights = readJson(featuresPath);
            if (weights == null || weights.isEmpty()) {
                weights = new HashMap<>();

                weights.put(FEATURE.STONES.name(), 50d);
                weights.put(FEATURE.ATTACK.name(), 100d);
                weights.put(FEATURE.DESTRUCT.name(), 100d);
                weights.put(FEATURE.SHORT.name(), 100d);
                weights.put(FEATURE.MEDIUM.name(), 100d);
                weights.put(FEATURE.FLY.name(), 20d);
                weights.put(FEATURE.FOLLOW.name(), 20d);
                weights.put(FEATURE.PREDICT.name(), 100d);
            }
            writeJson(weights, featuresPath);
        }

        public String read(String path) throws Exception {
            return new String(Files.readAllBytes(Paths.get(path)));
        }

        public Map<String, Double> readJson(String path) {
            try {
                String json = read(path);
                TypeReference<Map<String, Double>> typeRef = new TypeReference<Map<String, Double>>() {};
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(json, typeRef);
            } catch (Exception ignored) {
            }
            return null;
        }

        public void write(String path, String content) throws Exception {
            Files.write(Paths.get(path), content.getBytes());
        }

        public void writeJson(Map<String, Double> map, String path) {
            try{
                String json = new ObjectMapper().writeValueAsString(map);
                write(path, json);
            } catch (Exception ignored) {
            }
        }

        public void init() {
            features.clear();
            System.out.println("init features...");
            for(String feature: weights.keySet()) {
                int rnd = dice.next(100);
                System.out.printf("\t%s %d < %.3f", feature, rnd, weights.get(feature));
                if (rnd < weights.get(feature)) {
                    features.add(FEATURE.valueOf(feature));
                    System.out.printf(" ... adding\n");
                } else {
                    System.out.printf(" ... skipping\n");
                }
            }
        }

        public void update(double delta, int steps) {
            updateAverage(delta, steps);
            updateFeatures(delta, steps);
        }

        private void updateFeatures(double delta, int steps) {
            System.out.println("updating features...");
            if ((average.get(FIELDS.CNT.name()) == null) || (average.get(FIELDS.CNT.name()) == 0))
                return;

            double local = delta / steps;
            double global = average.get(FIELDS.SUM.name()) / average.get(FIELDS.CNT.name());
            double relative = (local - global) / global;

            System.out.printf("local: %.3f, global: %.3f, relative: %.3f\n",
                    local, global, relative);

            for(FEATURE feature: features) {
                Double current = weights.get(feature.name());
                System.out.printf("\t%s %.3f", feature.name(), current);

                current += 5 * current * relative / 100;
                System.out.println(" => " + current);
                weights.put(feature.name(), current);
            }
            System.out.println();
            writeJson(weights, featuresPath);
        }

        private void updateAverage(double delta, int steps) {
            average = readJson(averagePath);
            if (average == null) {
                average = new HashMap<>();
            }

            Double sum = average.get(FIELDS.SUM.name());
            if (sum == null) sum = 0d;
            sum += delta;
            average.put(FIELDS.SUM.name(), sum);

            Double cnt = average.get(FIELDS.CNT.name());
            if (cnt == null) cnt = 0d;
            cnt += steps;
            average.put(FIELDS.CNT.name(), cnt);

            for(FEATURE feature: features) {
                Double value = average.get(feature.name());
                if (value == null) value = 0d;
                value++;
                average.put(feature.name(), value);
            }

            writeJson(average, averagePath);
        }

        @Override
        public String toString() {
            return features.toString();
        }
    }


    public static class Builder {
        private Learning learning;

        private Builder() {
            learning = new Learning();
        }

        public static Builder newLearning(){
            return new Builder();
        }

        public Builder withStrategy(Strategy strategy) {
            learning.strategy = strategy;
            return this;
        }

        public Builder withPlayer(String player) {
            learning.player = player;
            return this;
        }

        public Learning build() {
            return learning;
        }
    }
}
