package com.github.illya13.snakebattle.solver;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.github.illya13.snakebattle.Solver;
import com.github.illya13.snakebattle.State;
import io.jenetics.Genotype;
import io.jenetics.IntegerGene;


import java.util.*;

import static com.github.illya13.snakebattle.board.Board.*;

public class GASolver implements Solver {
    private GAEngine engine;
    Genotype<IntegerGene> genotype;

    public GASolver() {
        engine = new GAEngine();
    }

    @Override
    public void init() {
        if (genotype == null)
            genotype = engine.next();
    }

    @Override
    public Direction next(State state) {
        Direction direction = findBest(state);
        return (direction != null) ? direction : Direction.RIGHT;
    }

    @Override
    public void done(int reward) {
        engine.evaluate(genotype, reward);
        genotype = null;
    }

    @Override
    public void shutdown() {
        engine.shutdown();
    }

    @Override
    public String status() {
        return engine.toString() + ": " + genotype;
    }

    private Direction findBest(State state) {
        double max = Double.NEGATIVE_INFINITY;
        Direction direction = null;

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

    private static class Action {
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
            double total = 0;
            for (Features.FEATURE feature: features.keySet()) {
                total += features.get(feature).reward();
            }
            return total;
        }

        public String toString() {
            return direction + "[" + String.format("%.3f", rewards())+ "] {" + rewardsAsString() + "}";
        }
    }
}
