package com.codenjoy.dojo.snakebattle.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.*;
import static com.codenjoy.dojo.services.Direction.*;


public class BFS {
    public static Optional<Direction> bfs(Board board, Point start, Elements[] elements, int size) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        Map<Point, Path> visited = new HashMap<>();
        visited.put(start, new Path(null, null, 0));

        Optional<Point> found = bfs(board, queue, visited, elements, size);
        if (!found.isPresent())
            return Optional.empty();

        Point point = found.get();
        while (!visited.get(point).getFrom().equals(start)) {
            point = visited.get(point).getFrom();
            if (point == null)
                return Optional.empty();
        }
        return Optional.of(visited.get(point).getDirection());
    }

    private static Optional<Point> bfs(Board board, Queue<Point> queue, Map<Point, Path> visited, Elements[] elements, int max) {
        while (!queue.isEmpty()) {
            Point point = queue.poll();

            if (board.isAt(point, elements))
                return Optional.of(point);

            for (Direction direction: new Direction[]{RIGHT, DOWN, LEFT, UP}) {
                Point p = direction.change(point);
                if (!visited.containsKey(p) && !board.isBarrierOrStoneOrEnemyOrMeAt(p) && !p.isOutOf(board.size())) {
                    int distance = visited.get(point).getDistance();
                    if (distance < max) {
                        queue.add(p);
                        visited.put(p, new Path(point, direction, distance+1));
                    }
                }
            }
        }
        return Optional.empty();
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

}
