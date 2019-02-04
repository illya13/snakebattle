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
    private Point start;
    private boolean weight;
    private MODE mode;
    private Elements[] barrier;
    private Elements[] target;

    private BFS(Board board, Point start) {
        this.board = board;
        this.start = start;
        weight = false;
        mode = MODE.NORMAL;
    }

    public Optional<Direction> bfs(int size) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        Map<Point, Path> visited = new HashMap<>(size*2);
        visited.put(start, new Path(null, null, 0));

        LinkedHashSet<Point> found = bfs(queue, visited, size);
        if (found.isEmpty())
            return Optional.empty();

        // debug(visited);

        return (weight)
                ? calcWeights(visited, found)
                : Optional.of(traceBack(start, found.iterator().next(), visited));
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


    private Optional<Direction> calcWeights(Map<Point, Path> visited, LinkedHashSet<Point> found) {
        Map<Direction, Double> weightMap = new HashMap<>();
        for (Point point: found) {
            Direction direction = traceBack(start, point, visited);
            double points = POINTS.getPoints(board.getAllAt(point));
            double dx = points / visited.get(point).distance;

            Double value = weightMap.get(direction);
            if (value == null)
                value = 0d;
            value += dx;

            System.out.printf("\t%s %s %3.0f %d %.3f\n", direction, board.getAllAt(point), points,
                    visited.get(point).distance, dx);

            weightMap.put(direction, value);
        }

        List<Direction> sorted = weightMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> e.getKey())
                .collect(Collectors.toList());

        System.out.println(weightMap);
        // System.out.println(sorted);
        return Optional.of(sorted.get(0));
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


    public static class Builder {
        private BFS bfs;

        public static Builder newBFS(Board board, Point start) {
            Builder builder = new Builder();
            builder.bfs = new BFS(board, start);
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


    public enum POINTS {
        APPLE(Elements.APPLE, 1), GOLD(Elements.GOLD, 5), STONE(Elements.STONE, 10), FURY(Elements.FURY_PILL, 20);

        private Elements elements;
        private int points;

        POINTS(Elements elements, int points) {
            this.elements = elements;
            this.points = points;
        }

        public static int getPoints(List<Elements> elements) {
            int sum = 0;
            for(Elements e: elements) {
                sum += getPoints(e);
            }
            return sum;
        }

        public static int getPoints(Elements elements) {
            for (POINTS p: values()) {
                if (p.elements.equals(elements)) {
                    return p.points;
                }
            }
            return 0;
        }
    }
}
