package com.github.illya13.snakebattle.solver;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.github.illya13.snakebattle.Solver;
import com.github.illya13.snakebattle.State;
import io.jenetics.Genotype;
import io.jenetics.IntegerGene;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.Exchanger;

import static com.github.illya13.snakebattle.board.Board.*;

public class GASolver implements Solver {
    public static final String FILENAME = "features.csv";
    private static final int MAX_RUN = 3;

    private GAEngine engine;
    private Genotype<IntegerGene> genotype;
    private Exchanger<Integer> exchanger;
    private int run;
    private int total;

    public GASolver(GAEngine engine) {
        this.engine = engine;
    }

    @Override
    public void init() {
        if (genotype == null) {
            GAEngine.Request request = engine.next();
            genotype = request.getGenotype();
            exchanger = request.getExchanger();
            run = 0;
            total = 0;
        }
    }

    @Override
    public Direction next(State state) {
        Direction direction = findBest(state);
        return (direction != null) ? direction : Direction.RIGHT;
    }

    @Override
    public void done(int reward) {
        total += reward;
        run++;
        if (run < MAX_RUN)
            return;

        try {
            exchanger.exchange(total / MAX_RUN);
            genotype = null;
            exchanger = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        engine.shutdown();
    }

    @Override
    public String status() {
        return "GA: " + engine.toString() + ": " + genotype;
    }

    private Direction findBest(State state) {
        double max = Double.NEGATIVE_INFINITY;
        Direction direction = null;

        System.out.println(genotype);
        for (Action action: getActions(state)) {
            System.out.println(action);
            if (action.rewards() > max ) {
                max  = action.rewards();
                direction = action.direction();
            }
        }
        return direction;
    }

    private List<Action> getActions(State state) {
        Direction inverted = state.me().direction().inverted();
        List<Action> actions = new LinkedList<>();
        for(Direction direction: all) {
            if (direction.equals(inverted))
                continue;

            actions.add(new Action(state, direction));
        }
        return actions;
    }

    private class Action {
        private Direction direction;
        private Point point;
        private Map<Features.FEATURE, Features.Reward> features;

        public Action(State state, Direction direction) {
            this.direction = direction;
            this.point = direction.change(state.me().head());

            features = new Features(state, point).all();
        }

        public Direction direction() {
            return direction;
        }

        public String rewardsAsString() {
            StringBuilder sb = new StringBuilder();
            for (Features.FEATURE feature: features.keySet()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(String.format("%s: %.3f", feature, features.get(feature).reward()));
            }
            return sb.toString();
        }

        public double rewards(){
            // saveFeatures();
            double total = 0;
            int i = 0;
            for (Features.FEATURE feature: features.keySet()) {
                total += genotype.getChromosome().getGene(i).intValue() * features.get(feature).reward();
                i++;
            }
            return total;
        }

        public String toString() {
            return direction + "[" + String.format("%.3f", rewards())+ "] {" + rewardsAsString() + "}";
        }

        private synchronized void saveFeatures() {
            Path p = Paths.get(FILENAME);

            try {
                if (!Files.exists(p)) {
                    BufferedWriter writer = Files.newBufferedWriter(p, StandardOpenOption.CREATE_NEW);
                    for (Features.FEATURE feature: features.keySet()) {
                        writer.write(feature.toString());
                        writer.write(',');
                    }
                    writer.write('\n');
                    writer.close();
                }
                BufferedWriter writer = Files.newBufferedWriter(p, StandardOpenOption.APPEND);
                for (Features.FEATURE feature: features.keySet()) {
                    writer.write(String.valueOf(features.get(feature).reward()));
                    writer.write(',');
                }
                writer.write('\n');
                writer.close();
            } catch (IOException ioe) {
                throw new IllegalStateException(ioe);
            }
        }
    }
}
