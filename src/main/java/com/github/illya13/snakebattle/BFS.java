package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.*;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.model.Elements.NONE;


public class BFS {
    public enum MODE {
        NORMAL, FLY, ATTACK
    }

    private Board board;
    private Direction prev;
    private Point start;
    private boolean weight;
    private Set<Point> skipped;
    private MODE mode;
    private Elements[] barrier;
    private Elements[] target;

    private BFS(Board board, Direction prev, Point start) {
        this.prev = prev;
        this.board = board;
        this.start = start;
        weight = false;
        mode = MODE.NORMAL;
    }

    public Result bfs(int size) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        Map<Point, Path> visited = new HashMap<>(size*2);
        visited.put(start, new Path(null, null, 0));

        LinkedHashSet<Point> found = bfs(queue, visited, size);
        if (found.isEmpty())
            return Result.empty();

        // debug(visited);

        Point point = found.iterator().next();
        return (weight)
                ? calcWeights(visited, found)
                : new Result(traceBack(start, point, visited), point, visited.get(point).distance);
    }


    private LinkedHashSet<Point> bfs(Queue<Point> queue, Map<Point, Path> visited, int max) {
        LinkedHashSet<Point> found = new LinkedHashSet<>();
        while (!queue.isEmpty()) {
            Point point = queue.poll();

            if (board.isAt(point, target)) {
                found.add(point);
                if (!weight) {
                    break;
                }
            }

            for (Direction direction: board.getPriority(point, false)) {
                if (start.equals(point) && direction.equals(prev))
                    continue;

                Point p = direction.change(point);
                if (!visited.containsKey(p) && !board.isAt(p, barrier) && !p.isOutOf(board.size())) {
                    int distance = visited.get(point).getDistance();
                    if (distance > max)
                        continue;

                    if (!isSafe(p)) {
                        continue;
                    }

                    queue.add(p);
                    visited.put(p, new Path(point, direction,distance+1));
                }
            }
        }
        return found;
    }


    private Result calcWeights(Map<Point, Path> visited, LinkedHashSet<Point> found) {
        System.out.println("BFS weights:");
        Map<Direction, Double> weightMap = new HashMap<>();
        for (Point point: found) {
            if (skipped.contains(point))
                continue;

            Direction direction = traceBack(start, point, visited);
            double score = SCORES.getScore(board.getAllAt(point));
            double dx = score / visited.get(point).distance;
            System.out.printf("\t%s %s %3.0f %d", direction, board.getAllAt(point), score, visited.get(point).distance);

            if (board.getEnemySnakes() > 0) {
                double distance = 0d;
                for (Point enemy : board.getEnemies()) {
                    distance += enemy.distance(point);
                }
                distance /= board.getEnemySnakes();
                dx *= distance;
                System.out.printf(" %.3f ", distance);
            }

            Double value = weightMap.get(direction);
            if (value == null)
                value = 0d;
            value += dx;

            System.out.printf("%.3f\n", dx);
            weightMap.put(direction, value);
        }

        if (weightMap.keySet().isEmpty()) {
            return Result.empty();
        }

        List<Direction> sorted = weightMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> e.getKey())
                .collect(Collectors.toList());

        System.out.println(weightMap);
        // System.out.println(sorted);
        return new Result(sorted.get(0));
    }


    private Direction traceBack(Point start, Point point, Map<Point, Path> visited ) {
        while (!visited.get(point).getFrom().equals(start)) {
            point = visited.get(point).getFrom();
        }
        return visited.get(point).getDirection();
    }


    private void debug(Map<Point, Path> visited) {
        for(int y = board.size()-1; y >= 0; --y) {
            for (int x = 0; x < board.size(); ++x) {
                Point p = PointImpl.pt(x, y);
                if (!visited.containsKey(p)) {
                    System.out.print(board.isAt(p, NONE) ? "   " : board.getAllAt(x, y));
                } else {
                    System.out.printf("%3d", visited.get(p).distance);
                }
            }
            System.out.println();
        }
    }


    private boolean isSafe(Point point) {
        switch (mode) {
            case NORMAL: return board.isSafe(point);
            case FLY: return board.isSafeFly(point);
            case ATTACK: return board.isSafeAttack(point);
        }
        return true;
    }


    public static class Result {
        private static final Result empty = new Result(null, null, 0);

        private Direction direction;
        private Point target;
        private int distance;

        private Result(Direction direction) {
            this.target = null;
            this.direction = direction;
        }

        private Result(Direction direction, Point target, int distance) {
            this.target = target;
            this.direction = direction;
            this.distance = distance;
        }

        public Optional<Direction> getDirection() {
            return (direction == null) ? Optional.empty() : Optional.of(direction);
        }

        public Optional<Point> getTarget() {
            return (target == null) ? Optional.empty() : Optional.of(target);
        }

        public int getDistance() {
            return distance;
        }

        public static Result empty() {
            return empty;
        }
    }

    public static class Builder {
        private BFS bfs;

        public static Builder newBFS(Board board, Direction prev, Point start) {
            Builder builder = new Builder();
            builder.bfs = new BFS(board, prev, start);
            return builder;
        }

        public Builder fly() {
            bfs.mode = MODE.FLY;
            return this;
        }

        public Builder attack() {
            bfs.mode = MODE.ATTACK;
            return this;
        }

        public Builder barrier(Elements[] barrier) {
            bfs.barrier = barrier;
            return this;
        }

        public Builder target(Elements[] target) {
            bfs.target = target;
            return this;
        }

        public Builder weight(boolean weight) {
            bfs.weight = weight;
            return this;
        }

        public Builder skipped(Set<Point> skipped) {
            bfs.skipped = skipped;
            return this;
        }

        public BFS build() {
            return bfs;
        }
    }


    private static class Path {
        private Point from;
        private Direction direction;
        private int distance;

        public Path(Point from, Direction direction, int distance) {
            this.from = from;
            this.direction = direction;
            this.distance = distance;
        }

        public Point getFrom() {
            return from;
        }

        public Direction getDirection() {
            return direction;
        }

        public int getDistance() {
            return distance;
        }
    }


    private enum SCORES {
        APPLE(Elements.APPLE, 1), GOLD(Elements.GOLD, 10), STONE(Elements.STONE, 5), FURY(Elements.FURY_PILL, 20);

        private Elements elements;
        private int score;

        SCORES(Elements elements, int score) {
            this.elements = elements;
            this.score = score;
        }

        public static int getScore(List<Elements> elements) {
            int sum = 0;
            for(Elements e: elements) {
                sum += getScore(e);
            }
            return sum;
        }

        public static int getScore(Elements elements) {
            for (SCORES p: values()) {
                if (p.elements.equals(elements)) {
                    return p.score;
                }
            }
            return 0;
        }
    }
}
