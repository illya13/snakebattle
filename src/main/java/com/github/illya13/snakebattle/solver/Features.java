package com.github.illya13.snakebattle.solver;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.model.Elements;
import com.github.illya13.snakebattle.State;
import com.github.illya13.snakebattle.board.Board;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.board.Board.*;

public class Features {
    enum FEATURE {
        LIVENESS, BARRIER, ENEMY, STONE, BODY,
        APPLE, GOLD, FURY, FLY, AVERAGE,
        STONE_N_FURY, STONE_N_SIZE, ENEMY_N_FURY, ENEMY_N_SIZE,
        ESCAPE_FURY, ESCAPE_TRAFFIC
    }

    private State state;
    private Point point;
    private Map<Point, Integer> items;
    private int[][] liveness;

    private Map<FEATURE, Reward> all;

    public Features(State state, Point point) {
        this.state = state;
        this.point = point;
        liveness = state.board().liveness();

        initItems();
        initFeatures();
    }

    private void initItems(){
        items = new LinkedHashMap<>();

        if (!state.board().isAt(point, NONE)) {
            items.put(point, 0);
        }

        Elements[] barrier = BARRIER_ELEMENTS;
        if (!state.me().isFly())
            barrier = join(barrier, MY_ELEMENTS);
        if (!state.me().isFly() && !state.me().isFury())
            barrier = join(barrier, ENEMY_ELEMENTS);
        Elements[] target = join(new Elements[] {APPLE, GOLD, STONE, FURY_PILL, FLYING_PILL});

        items.putAll(state.board().bfs(state.me(), point, barrier, target));
        items.putAll(state.board().bfs(state.me(), point, BARRIER_ELEMENTS, ENEMY_HEAD_ELEMENTS));
    }

    private void initFeatures() {
        all = new LinkedHashMap<>();

        all.put(FEATURE.LIVENESS, new Features.Liveness());
        all.put(FEATURE.BARRIER, new Features.Barrier());
        all.put(FEATURE.ENEMY, new Features.Enemy());
        all.put(FEATURE.STONE, new Features.Stone());
        all.put(FEATURE.BODY, new Features.MyBody());

        all.put(FEATURE.APPLE, new Features.ClosestFeature(APPLE));
        all.put(FEATURE.GOLD, new Features.ClosestFeature(GOLD));
        all.put(FEATURE.FURY, new Features.ClosestFeature(FURY_PILL));
        all.put(FEATURE.FLY, new Features.ClosestFeature(FLYING_PILL));
        all.put(FEATURE.AVERAGE, new Features.AverageItemsFeature(APPLE, GOLD, FURY_PILL));

        all.put(FEATURE.STONE_N_FURY, new Features.ClosestItemInFuryFeature(STONE));
        all.put(FEATURE.STONE_N_SIZE, new Features.ClosestStoneWithSizeFeature());
        all.put(FEATURE.ENEMY_N_FURY, new Features.ClosestItemInFuryFeature(ENEMY_HEAD_ELEMENTS));
        all.put(FEATURE.ENEMY_N_SIZE, new Features.ClosestEnemyWithSizeFeature());

        all.put(FEATURE.ESCAPE_FURY, new Features.EscapeFuryFeature());
        all.put(FEATURE.ESCAPE_TRAFFIC, new Features.EscapeTraffic());
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
            if (normalized < 0) return 0d;
            if (normalized > 1) return 1d;
            return normalized;
        }

        double closestItemFeature(Elements... elements) {
            double value = itemMinDistance(elements);
            return 1-normalize(value, 0d, 2 * state.board().size());
        }

        double closestItemInFuryFeature(Elements... elements) {
            double value = state.me().fury() * closestItemFeature(elements);
            return normalize(value, 0d, 19);
        }

        double averageItemFeature(Elements... elements) {
            double value = itemAverageDistance(elements);
            return normalize(value, 0, 2 * state.board().size());
        }
    }

    public class Liveness extends FeatureBase {
        @Override
        public double reward() {
            double value = liveness[point.getX()][point.getY()];
            return normalize(value, 0d, state.board().size() / 5d);
        }
    }

    public class Barrier extends FeatureBase {
        @Override
        public double reward() {
            if (state.board().isAt(point, BARRIER_ELEMENTS))
                return 0d;
            return 1d;
        }
    }

    public class Enemy extends FeatureBase {
        @Override
        public double reward() {
            if (state.board().isAt(point, ENEMY_HEAD_ELEMENTS)) {
                for (State.Enemy enemy: state.enemies()) {
                    if (enemy.head().equals(point)) {
                        boolean attack = (state.me().isFury() && !enemy.isFury()) ||
                                (!enemy.isFury() && (state.me().size() - enemy.size() > 1));
                        return attack ? 1d : 0d;
                    }
                }
            }
            if (state.board().isAt(point, ENEMY_ELEMENTS) && !state.me().isFly() && !state.me().isFury())
                return 0d;
            return 0.5d;
        }
    }

    public class Stone extends FeatureBase {
        @Override
        public double reward() {
            if (state.board().isAt(point, Elements.STONE) && state.me().isFury())
                return 1d;

            if (state.board().isAt(point, Elements.STONE) &&
                    (state.me().size() < 5) && !state.me().isFly() && !state.me().isFury())
                return 0d;
            return 0.5d;
        }
    }

    public class MyBody extends FeatureBase {
        @Override
        public double reward() {
            return (state.board().isAt(point, MY_BODY_ELEMENTS)) ? 0d : 1d;
        }
    }

    public class ClosestFeature extends FeatureBase {
        private Elements[] elements;

        public ClosestFeature(Elements ...elements) {
            this.elements = elements;
        }

        @Override
        public double reward() {
            return closestItemFeature(elements);
        }
    }

    public class AverageItemsFeature extends FeatureBase {
        private Elements[] elements;

        public AverageItemsFeature(Elements ...elements) {
            this.elements = elements;
        }

        @Override
        public double reward() {
            return averageItemFeature(elements);
        }
    }

    public class ClosestItemInFuryFeature extends FeatureBase {
        private Elements[] elements;

        public ClosestItemInFuryFeature(Elements ...elements) {
            this.elements = elements;
        }

        @Override
        public double reward() {
            return closestItemInFuryFeature(elements);
        }
    }

    public class ClosestStoneWithSizeFeature extends FeatureBase {
        @Override
        public double reward() {
            double value = state.me().size() * closestItemFeature(STONE);
            return normalize(value, 0d, state.board().size());
        }
    }

    public class ClosestEnemyWithSizeFeature extends FeatureBase {
        @Override
        public double reward() {
            Map<Point, Integer> heads = filterItems(ENEMY_HEAD_ELEMENTS);

            for (Point p: heads.keySet()) {
                for (State.Enemy enemy: state.enemies()) {
                    if (enemy.head().equals(p)) {
                        double value = 1d * state.me().size() * heads.get(p) / enemy.size();
                        return normalize(value, 0d, state.board().size() * state.board().size() / 5d);
                    }
                }
            }
            return 0;
        }
    }

    public class EscapeFuryFeature extends FeatureBase {
        @Override
        public double reward() {
            Map<Point, Integer> heads = filterItems(ENEMY_HEAD_ELEMENTS);

            for (Point p: heads.keySet()) {
                for (State.Enemy enemy: state.enemies()) {
                    if (enemy.head().equals(p)) {
                        double value = heads.get(p) * enemy.fury();
                        return 1 - normalize(value, 0d, 9 * 2 * state.board().size());
                    }
                }
            }
            return 0;
        }
    }

    public class EscapeTraffic extends FeatureBase {
        @Override
        public double reward() {
            return 1  - averageItemFeature(ENEMY_ELEMENTS);
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
