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
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.board.Board.*;

public class GASolver implements Solver {
    @Override
    public Direction next(State state) {
        Direction direction = findBest(state);
        return (direction != null) ? direction : Direction.RIGHT;
    }

    private Direction findBest(State state) {
        double max = 0;
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
        state.board().liveness();

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
        private State state;
        private Direction direction;
        private Point point;
        private Map<Point, Integer> items;

        public Action(State state, Direction direction) {
            this.state = state;
            this.direction = direction;
            this.point = direction.change(state.me().head());
            items = new LinkedHashMap<>();

            if (!state.board().isAt(point, NONE)) {
                items.put(point, 0);
            }
            initItems(point);
            initEnemies(point);
        }

        private void initItems(Point point) {
            Elements[] barrier = BARRIER_ELEMENTS;
            if (!state.me().isFly())
                barrier = join(barrier, MY_ELEMENTS);
            if (!state.me().isFly() && !state.me().isFury())
                barrier = join(barrier, ENEMY_ELEMENTS);

            Elements[] target = join(new Elements[] {APPLE, GOLD, STONE, FURY_PILL, FLYING_PILL});

            items.putAll(state.board().bfs(state.me(), point, barrier, target));
        }

        private void initEnemies(Point point) {
            items.putAll(state.board().bfs(state.me(), point, BARRIER_ELEMENTS, ENEMY_HEAD_ELEMENTS));
        }

        public Direction direction() {
            return direction;
        }

        public String rewardsAsString() {
            double closestApple = closestItemFeature(APPLE);
            double closestGold = closestItemFeature(GOLD);
            double closestStone1 = closestStoneAndFuryFeature();
            double closestStone2 = closestStoneAndSize();
            double closestEnemy1 = closestEnemyAndFuryFeature();
            double closestEnemy2 = closestEnemyAndSize();
            double average = averageItemFeature(APPLE, GOLD, FURY_PILL);

            return String.format("gold: %.3f, apple: %.3f, stone: %.3f %.3f, enemy: %.3f %.3f, avg: %.3f",
                    closestGold, closestApple,
                    closestStone1, closestStone2,
                    closestEnemy1, closestEnemy2, average);

        }

        public double rewards(){
            double closestApple = closestItemFeature(APPLE);
            double closestGold = closestItemFeature(GOLD);
            double closestStone1 = closestStoneAndFuryFeature();
            double closestStone2 = closestStoneAndSize();
            double closestEnemy1 = closestEnemyAndFuryFeature();
            double closestEnemy2 = closestEnemyAndSize();
            double average = averageItemFeature(APPLE, GOLD, FURY_PILL);

            return closestGold + closestApple + closestStone1 + closestStone2 + closestEnemy1 + closestEnemy2 + average;
        }

        private Map<Point, Integer> items(Elements... elements) {
            return items.entrySet().stream()
                    .filter(map -> state.board().isAt(map.getKey(), elements))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
        }

        private int itemMinDistance(Elements... elements) {
            Map<Point, Integer> filtered = items(elements);
            if (filtered.isEmpty())
                return Integer.MAX_VALUE;

            return filtered.get(filtered.keySet().iterator().next());
        }

        private double itemAverageDistance(Elements... elements) {
            Map<Point, Integer> filtered = items(elements);
            if (filtered.isEmpty())
                return Integer.MAX_VALUE;

            double sum = 0d;
            for (Point p: filtered.keySet()) {
                sum += filtered.get(p);
            }
            return sum / filtered.size();
        }

        private double closestItemFeature(Elements... elements) {
            double value = 1d / (1 + itemMinDistance(elements));
            return normalizePath(value);
        }

        private double averageItemFeature(Elements... elements) {
            double value = 1d / (1 + itemAverageDistance(elements));
            return normalizePath(value);
        }

        private double closestStoneAndFuryFeature() {
            double value = 1d * state.me().fury() / (1 + itemMinDistance(STONE));
            return normalizePathWithPill(value);
        }

        private double closestStoneAndSize() {
            double value = 1d * (state.me().size()-5) / (1 + itemMinDistance(STONE));
            return normalizePathWithSize(value);
        }

        private double closestEnemyAndFuryFeature() {
            double value = 1d * state.me().fury() / (1 + itemMinDistance(ENEMY_ELEMENTS));
            return normalizePathWithPill(value);
        }

        private double closestEnemyAndSize() {
            if (state.board().isAt(point, ENEMY_BODY_ELEMENTS))
                return 0;

            Map<Point, Integer> heads = items(ENEMY_HEAD_ELEMENTS);

            for (Point p: heads.keySet()) {
                for (State.Enemy enemy: state.enemies()) {
                    if (enemy.head().equals(p)) {
                        double value = 1d * (state.me().size()-enemy.size()) / (1 + heads.get(p));
                        return normalizeDistanceWithDiff(value);
                    }
                }
            }
            return 0;
        }

        private double normalize(double value, double min, double max) {
            double normalized = (value - min) / (max - min);
            if (normalized < min) return min;
            if (normalized > max) return max;
            return normalized;
        }

        private double normalizePath(double value) {
            return normalize(value, 1d / (2 * state.board().size()), 1d);
        }

        private double normalizePathWithPill(double value) {
            return normalize(value, 0, 19d);
        }

        private double normalizePathWithSize(double value) {
            return normalize(value, 0, state.board().size());
        }

        private double normalizeDistanceWithDiff(double value) {
            return -0.5d + normalize(value, -3, 3);
        }


        public String toString() {
            return direction + "[" + String.format("%.3f", rewards())+ "] {" + rewardsAsString() + "}";
        }
    }
}
