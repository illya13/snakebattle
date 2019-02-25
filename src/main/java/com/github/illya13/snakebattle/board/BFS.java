package com.github.illya13.snakebattle.board;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snakebattle.model.Elements;
import com.github.illya13.snakebattle.State;

import java.util.*;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.board.Board.*;

public class BFS {
    private Board board;

    public BFS(Board board) {
        this.board = board;
    }

    public Map<Point, Integer> bfs(State.Snake snake, Point start, Elements[] barrier, Elements[] targets) {
        Direction prev = (snake.head().equals(start)) ? snake.direction() : null;

        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        Map<Point, Path> visited = new HashMap<>(board.size() * 2);
        visited.put(start, new Path(null, null, 0));

        return bfs(queue, visited, start, prev, barrier, targets);
    }


    private LinkedHashMap<Point, Integer> bfs(Queue<Point> queue, Map<Point, Path> visited, Point start, Direction prev, Elements[] barrier, Elements[] targets) {
        LinkedHashMap<Point, Integer> found = new LinkedHashMap<>();
        while (!queue.isEmpty()) {
            Point point = queue.poll();

            if (board.isAt(point, targets)) {
                found.put(point, visited.get(point).getDistance());
            }

            for (Direction direction : all) {
                if (start.equals(point) && direction.equals(prev))
                    continue;

                Point p = direction.change(point);
                if (!visited.containsKey(p) && !board.isAt(p, barrier) && !p.isOutOf(board.size())) {
                    int distance = visited.get(point).getDistance();

                    queue.add(p);
                    visited.put(p, new Path(point, direction, distance + 1));
                }
            }
        }
        return found;
    }

    private Direction traceBack(Point start, Point point, Map<Point, Path> visited) {
        while (!visited.get(point).getFrom().equals(start)) {
            point = visited.get(point).getFrom();
        }
        return visited.get(point).getDirection();
    }


    private void debug(Map<Point, Path> visited) {
        for (int y = board.size() - 1; y >= 0; --y) {
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