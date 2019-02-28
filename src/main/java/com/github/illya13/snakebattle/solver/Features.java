package com.github.illya13.snakebattle.solver;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.model.Elements;
import com.github.illya13.snakebattle.State;
import com.github.illya13.snakebattle.board.Board;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.codenjoy.dojo.snakebattle.model.Elements.STONE;
import static com.github.illya13.snakebattle.board.Board.*;

public class Features {
    enum FEATURE {
        LIVENESS, APPLE, GOLD, AVERAGE,
        STONE_N_FURY, STONE_N_SIZE,
        ENEMY_N_FURY, ENEMY_N_SIZE
    }

    private State state;
    private Point point;
    private Map<Point, Integer> items;

    private Map<FEATURE, Reward> all;

    public Features(State state, Point point, Map<Point, Integer> items) {
        this.state = state;
        this.point = point;
        this.items = items;

        init();
    }

    private void init() {
        all = new LinkedHashMap<>();

        all.put(FEATURE.LIVENESS, new Features.Liveness());
        all.put(FEATURE.APPLE, new Features.ClosestAppleFeature());
        all.put(FEATURE.GOLD, new Features.ClosestGoldFeature());
        all.put(FEATURE.AVERAGE, new Features.AverageItemsFeature());
        all.put(FEATURE.STONE_N_FURY, new Features.ClosestStoneInFuryFeature());
        all.put(FEATURE.STONE_N_SIZE, new Features.ClosestStoneWithSizeFeature());
        all.put(FEATURE.ENEMY_N_FURY, new Features.ClosestEnemyInFuryFeature());
        all.put(FEATURE.ENEMY_N_SIZE, new Features.ClosestEnemyWithSizeFeature());
    }

    public interface Reward {
        double reward();
    }


    public Map<FEATURE, Reward> all() {
        return all;
    }


    abstract class FeatureBase implements Reward {
        double normalize(double value, double min, double max) {
            double normalized = (value - min) / (max - min);
            if (normalized < min) return min;
            if (normalized > max) return max;
            return normalized;
        }

        double normalizePath(double value) {
            return normalize(value, 1d / (2 * state.board().size()), 1d);
        }

        double normalizePathWithPill(double value) {
            return normalize(value, 0d, 19d);
        }

        double normalizeDistanceWithDiff(double value) {
            return -0.5d + normalize(value, -3, 3);
        }

        double closestItemFeature(Elements... elements) {
            double value = 1d / (1 + itemMinDistance(elements));
            return normalizePath(value);
        }

        double averageItemFeature(Elements... elements) {
            double value = 1d / (1 + itemAverageDistance(elements));
            return normalizePath(value);
        }
    }

    public class Liveness extends FeatureBase {
        @Override
        public double reward() {
            Board board = state.board();
            double value = board.liveness(point) / (board.size() / 2d);
            return normalize(value, 0d, 1d);
        }
    }

    public class ClosestAppleFeature extends FeatureBase {
        @Override
        public double reward() {
            return closestItemFeature(Elements.APPLE);
        }
    }

    public class ClosestGoldFeature extends FeatureBase {
        @Override
        public double reward() {
            return closestItemFeature(Elements.GOLD);
        }
    }

    public class AverageItemsFeature extends FeatureBase {
        @Override
        public double reward() {
            return averageItemFeature(Elements.APPLE, Elements.GOLD, FURY_PILL);
        }
    }

    public class ClosestStoneInFuryFeature extends FeatureBase {
        @Override
        public double reward() {
            double value = 1d * state.me().fury() / (1 + itemMinDistance(STONE));
            return normalizePathWithPill(value);
        }
    }

    public class ClosestStoneWithSizeFeature extends FeatureBase {
        @Override
        public double reward() {
            double value = 1d * (state.me().size()-5) / (1 + itemMinDistance(STONE));
            return normalizeDistanceWithDiff(value);
        }
    }

    public class ClosestEnemyInFuryFeature extends FeatureBase {
        @Override
        public double reward() {
            double value = 1d * state.me().fury() / (1 + itemMinDistance(ENEMY_ELEMENTS));
            return normalizePathWithPill(value);
        }
    }

    public class ClosestEnemyWithSizeFeature extends FeatureBase {
        @Override
        public double reward() {
            if (state.board().isAt(point, ENEMY_BODY_ELEMENTS))
                return -0.5d;

            Map<Point, Integer> heads = filterItems(ENEMY_HEAD_ELEMENTS);

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
    }

    // HELPERS

    private Map<Point, Integer> filterItems(Elements... elements) {
        return items.entrySet().stream()
                .filter(map -> state.board().isAt(map.getKey(), elements))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
    }

    private int itemMinDistance(Elements... elements) {
        Map<Point, Integer> filtered = filterItems(elements);
        if (filtered.isEmpty())
            return Integer.MAX_VALUE;

        return filtered.get(filtered.keySet().iterator().next());
    }

    private double itemAverageDistance(Elements... elements) {
        Map<Point, Integer> filtered = filterItems(elements);
        if (filtered.isEmpty())
            return Integer.MAX_VALUE;

        double sum = 0d;
        for (Point p: filtered.keySet()) {
            sum += filtered.get(p);
        }
        return sum / filtered.size();
    }
}
