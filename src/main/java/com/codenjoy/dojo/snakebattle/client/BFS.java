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

import static com.codenjoy.dojo.services.Direction.*;
import static com.codenjoy.dojo.snakebattle.model.Elements.NONE;


public class BFS {
    public static Optional<Direction> bfs(Board board, Point start, boolean weight, Elements[] barrier, Elements[] target, int size, MODE mode) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        Map<Point, Path> visited = new HashMap<>(size*2);
        visited.put(start, new Path(null, null, 0));

        LinkedHashSet<Point> found = bfs(board, queue, visited, barrier, target, size, mode, weight);
        if (found.isEmpty())
            return Optional.empty();
/*
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
*/
        if (!weight) {
            return Optional.of(traceBack(start, found.iterator().next(), visited));
        }

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

    private static Direction traceBack(Point start, Point point, Map<Point, Path> visited ) {
        while (!visited.get(point).getFrom().equals(start)) {
            point = visited.get(point).getFrom();
        }
        return visited.get(point).getDirection();
    }

    private static LinkedHashSet<Point> bfs(Board board, Queue<Point> queue, Map<Point, Path> visited, Elements[] barrier, Elements[] target, int max, MODE mode, boolean all) {
        LinkedHashSet<Point> found = new LinkedHashSet<>();
        while (!queue.isEmpty()) {
            Point point = queue.poll();

            if (board.isAt(point, target)) {
                found.add(point);
                if (!all) {
                    break;
                }
            }

            for (Direction direction: new Direction[]{RIGHT, DOWN, LEFT, UP}) {
                Point p = direction.change(point);
                if (!visited.containsKey(p) && !board.isAt(p, barrier) && !p.isOutOf(board.size())) {
                    int distance = visited.get(point).getDistance();
                    if (distance > max)
                        continue;

                    if (!isSafe(board, p, mode)) {
                        continue;
                    }

                    queue.add(p);
                    visited.put(p, new Path(point, direction,distance+1));
                }
            }
        }
        return found;
    }

    private static boolean isSafe(Board board, Point p, MODE mode) {
        switch (mode) {
            case NORMAL: return board.isSafe(p);
            case FLY: return board.isSafeFly(p);
            case ATTACK: return board.isSafeAttack(p);
        }
        return true;
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

    public enum MODE {
        NORMAL, FLY, ATTACK
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
