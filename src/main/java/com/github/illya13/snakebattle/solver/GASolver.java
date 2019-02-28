package com.github.illya13.snakebattle.solver;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.model.Elements;
import com.github.illya13.snakebattle.Solver;
import com.github.illya13.snakebattle.State;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.board.Board.*;

public class GASolver implements Solver {
    @Override
    public Direction next(State state) {
        Direction direction = findBest(state);
        return (direction != null) ? direction : Direction.RIGHT;
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
        private State state;
        private Direction direction;
        private Point point;
        private Map<Point, Integer> items;
        private int[][] liveness;
        private Map<Features.FEATURE, Features.Reward> features;

        public Action(State state, Direction direction) {
            this.state = state;
            this.direction = direction;
            this.point = direction.change(state.me().head());
            liveness = state.board().liveness();
            items = new LinkedHashMap<>();

            if (!state.board().isAt(point, NONE)) {
                items.put(point, 0);
            }
            initItems();
            initEnemies();
            initFeatures();
        }

        public Direction direction() {
            return direction;
        }


        private void initItems() {
            Elements[] barrier = BARRIER_ELEMENTS;
            if (!state.me().isFly())
                barrier = join(barrier, MY_ELEMENTS);
            if (!state.me().isFly() && !state.me().isFury())
                barrier = join(barrier, ENEMY_ELEMENTS);

            Elements[] target = join(new Elements[] {APPLE, GOLD, STONE, FURY_PILL, FLYING_PILL});

            items.putAll(state.board().bfs(state.me(), point, barrier, target));
        }

        private void initEnemies() {
            items.putAll(state.board().bfs(state.me(), point, BARRIER_ELEMENTS, ENEMY_HEAD_ELEMENTS));
        }

        private void initFeatures() {
            features = new Features(state, point, items, liveness).all();
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
