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
import static com.codenjoy.dojo.services.Direction.*;


public class BFS {
    public static Optional<Direction> bfs(Board board, Point start, Elements[] barrier, Elements[] target, int size, MODE mode) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        Map<Point, Path> visited = new HashMap<>(size*2);
        visited.put(start, new Path(null, null, 0));

        Optional<Point> found = bfs(board, queue, visited, barrier, target, size, mode);
        if (!found.isPresent())
            return Optional.empty();

        Point point = found.get();
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
        while (!visited.get(point).getFrom().equals(start)) {
            point = visited.get(point).getFrom();
            if (point == null)
                return Optional.empty();
        }
        return Optional.of(visited.get(point).getDirection());
    }

    private static Optional<Point> bfs(Board board, Queue<Point> queue, Map<Point, Path> visited, Elements[] barrier, Elements[] target, int max, MODE mode) {
        while (!queue.isEmpty()) {
            Point point = queue.poll();

            if (board.isAt(point, target))
                return Optional.of(point);

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
        return Optional.empty();
    }

    private static boolean isSafe(Board board, Point p, MODE mode) {
        switch (mode) {
            case NORMAL: return board.isSafe(p);
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

}
