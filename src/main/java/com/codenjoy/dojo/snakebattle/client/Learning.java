package com.codenjoy.dojo.snakebattle.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.services.Dice;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        STONES, ATTACK, DESTRUCT, SHORT, MEDIUM
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
        public abstract void update(double weight);
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
            if (steps < this.board.size()) {
                System.out.println("dead bots");
            } else {
                int before = Integer.valueOf(prev.get().get("score"));
                int now = Integer.valueOf(stat.get().get("score"));
                double delta = (now - before);
                System.out.printf(" == %s before: %d, now: %d, delta: %.0f, steps: %d => %.5f\n",
                        strategy.toString(), before, now, delta, steps, delta/steps);

                strategy.update(delta/steps - 1);
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
        WebTarget webTarget = client.target(URL + date);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        String stringResponse = response.readEntity(String.class);

        try {
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

            return Optional.of(
                    ClientBuilder.newBuilder()
                            .sslContext(ctx)
                            .hostnameVerifier(verifier)
                            .build()
            );
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }


    public static class DefaultStrategy extends Strategy {
        private String path;

        public DefaultStrategy(Dice dice, String path) {
            super(dice);
            this.path = path;

            features = new HashSet<>();

            readFeatures();
            if (weights == null || weights.isEmpty()) {
                weights = new HashMap<>();

                weights.put(FEATURE.STONES.name(), 100d);
                weights.put(FEATURE.ATTACK.name(), 100d);
                weights.put(FEATURE.DESTRUCT.name(), 100d);
                weights.put(FEATURE.SHORT.name(), 70d);
                weights.put(FEATURE.MEDIUM.name(), 80d);
            }
            writeFeatures();
        }

        public String read(String path) throws Exception {
            return new String(Files.readAllBytes(Paths.get(path)));
        }

        public void readFeatures() {
            try {
                String json = read(path);
                TypeReference<Map<String, Double>> typeRef = new TypeReference<Map<String, Double>>() {};
                ObjectMapper mapper = new ObjectMapper();
                weights = mapper.readValue(json, typeRef);
            } catch (Exception ignored) {
            }
        }

        public void write(String path, String content) throws Exception {
            Files.write(Paths.get(path), content.getBytes());
        }

        public void writeFeatures() {
            try{
                String json = new ObjectMapper().writeValueAsString(weights);
                write(path, json);
            } catch (Exception ignored) {
            }
        }

        public void init() {
            features.clear();
            for(String feature: weights.keySet()) {
                int rnd = dice.next(100);
                System.out.printf("%s %d < %.2f", feature, rnd, weights.get(feature));
                if (rnd < weights.get(feature)) {
                    features.add(FEATURE.valueOf(feature));
                    System.out.printf(" ... adding\n");
                } else {
                    System.out.printf(" ... skipping\n");
                }
            }
        }

        public void update(double delta) {
            for(FEATURE feature: features) {
                double current  = weights.get(feature.name());
                if ((feature == FEATURE.SHORT) || (feature == FEATURE.MEDIUM)) {
                    weights.put(feature.name(), current+delta);
                    System.out.printf("%s %.5f %.5f %.5f\n",
                            feature.name(), current, delta, current+delta);
                }
            }
            writeFeatures();
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
