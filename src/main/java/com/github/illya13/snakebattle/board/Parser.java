package com.github.illya13.snakebattle.board;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.board.Board.*;


public class Parser {
    private Board board;

    public Parser(Board board) {
        this.board = board;
    }

    public ParsedSnake parseSnake(Point head) {
        ParsedSnake parsed = new ParsedSnake(head);
        initPills(parsed);

        parsed.direction = getDirection(head);

        Elements headElement = getAt(head);
        if (Arrays.asList(HEAD_FLY, ENEMY_HEAD_FLY).contains(headElement)) {
            parsed.direction = getHeadDirectionWithMod(head, parsed.direction);
        }

        if (Arrays.asList(HEAD_EVIL).contains(headElement)) {
            parsed.direction = getHeadDirectionWithMod(head, parsed.direction);
        }

        if (Arrays.asList(HEAD_DEAD, ENEMY_HEAD_DEAD).contains(headElement)) {
            parsed.direction = getHeadDirectionWithMod(head, parsed.direction);
        }

        Direction direction = parsed.direction.inverted();

        Point point = head;
        Set<Point> visited = new HashSet<>();
        while (direction != null) {
            if (visited.contains(point))
                break;
            visited.add(point);

            point = direction.change(point);
            parsed.body.addLast(point);
            direction = next(point, direction);
        }

        return parsed;
    }

    public ParsedSnake parseEnemy(Point head) {
        ParsedSnake parsed = new ParsedSnake(head);
        initEnemyPills(parsed);

        parsed.direction = getEnemyDirection(head);

        Elements headElement = getAt(head);
        if (Arrays.asList(ENEMY_HEAD_FLY).contains(headElement)) {
            parsed.direction = getEnemyHeadDirectionWithMod(head, parsed.direction);
        }

        if (Arrays.asList(ENEMY_HEAD_EVIL).contains(headElement)) {
            parsed.direction = getEnemyHeadDirectionWithMod(head, parsed.direction);
        }

        if (Arrays.asList(ENEMY_HEAD_DEAD).contains(headElement)) {
            parsed.direction = getEnemyHeadDirectionWithMod(head, parsed.direction);
        }

        Direction direction = parsed.direction.inverted();

        Point point = head;
        Set<Point> visited = new HashSet<>();
        while (direction != null) {
            if (visited.contains(point))
                break;
            visited.add(point);

            point = direction.change(point);
            parsed.body.addLast(point);
            direction = enemyNext(point, direction);
        }

        return parsed;
    }

    private Direction getHeadDirectionWithMod(Point head, Direction defaultDirection) {
        Elements atLeft = getAt(Direction.LEFT.change(head));
        if (Arrays.asList(Elements.BODY_HORIZONTAL,
                Elements.BODY_RIGHT_DOWN,
                Elements.BODY_RIGHT_UP,
                Elements.TAIL_END_LEFT).contains(atLeft)) {
            return Direction.RIGHT;
        }

        Elements atRight = getAt(Direction.RIGHT.change(head));
        if (Arrays.asList(Elements.BODY_HORIZONTAL,
                Elements.BODY_LEFT_DOWN,
                Elements.BODY_LEFT_UP,
                Elements.TAIL_END_RIGHT).contains(atRight)) {
            return Direction.LEFT;
        }

        Elements atDown = getAt(Direction.DOWN.change(head));
        if (Arrays.asList(Elements.BODY_VERTICAL,
                Elements.BODY_LEFT_UP,
                Elements.BODY_RIGHT_UP,
                Elements.TAIL_END_DOWN).contains(atDown)) {
            return Direction.UP;
        }

        Elements atUp = getAt(Direction.UP.change(head));
        if (Arrays.asList(Elements.BODY_VERTICAL,
                Elements.BODY_LEFT_DOWN,
                Elements.BODY_RIGHT_DOWN,
                Elements.TAIL_END_UP).contains(atUp)) {
            return Direction.DOWN;
        }

        for (Direction direction : all) {
            if (Arrays.asList(MY_ELEMENTS).contains(getAt(direction.change(head))))
                return direction.inverted();
        }

        return defaultDirection;
    }

    private Direction getEnemyHeadDirectionWithMod(Point head, Direction defaultDirection) {
        Elements atLeft = getAt(Direction.LEFT.change(head));
        if (Arrays.asList(Elements.ENEMY_BODY_HORIZONTAL,
                Elements.ENEMY_BODY_RIGHT_DOWN,
                Elements.ENEMY_BODY_RIGHT_UP,
                Elements.ENEMY_TAIL_END_LEFT).contains(atLeft)) {
            return Direction.RIGHT;
        }

        Elements atRight = getAt(Direction.RIGHT.change(head));
        if (Arrays.asList(Elements.ENEMY_BODY_HORIZONTAL,
                Elements.ENEMY_BODY_LEFT_DOWN,
                Elements.ENEMY_BODY_LEFT_UP,
                Elements.ENEMY_TAIL_END_RIGHT).contains(atRight)) {
            return Direction.LEFT;
        }

        Elements atDown = getAt(Direction.DOWN.change(head));
        if (Arrays.asList(Elements.ENEMY_BODY_VERTICAL,
                Elements.ENEMY_BODY_LEFT_UP,
                Elements.ENEMY_BODY_RIGHT_UP,
                Elements.ENEMY_TAIL_END_DOWN).contains(atDown)) {
            return Direction.UP;
        }

        Elements atUp = getAt(Direction.UP.change(head));
        if (Arrays.asList(Elements.ENEMY_BODY_VERTICAL,
                Elements.ENEMY_BODY_LEFT_DOWN,
                Elements.ENEMY_BODY_RIGHT_DOWN,
                Elements.ENEMY_TAIL_END_UP).contains(atUp)) {
            return Direction.DOWN;
        }

        for (Direction direction : all) {
            if (Arrays.asList(ENEMY_ELEMENTS).contains(getAt(direction.change(head))))
                return direction.inverted();
        }

        return defaultDirection;
    }

    private Direction next(Point point, Direction direction) {
        switch (getAt(point)) {
            case BODY_HORIZONTAL:
                return direction;
            case BODY_VERTICAL:
                return direction;
            case BODY_LEFT_DOWN:
                return ((direction == Direction.RIGHT) ? Direction.DOWN : Direction.LEFT);
            case BODY_RIGHT_DOWN:
                return ((direction == Direction.LEFT) ? Direction.DOWN : Direction.RIGHT);
            case BODY_LEFT_UP:
                return ((direction == Direction.RIGHT) ? Direction.UP : Direction.LEFT);
            case BODY_RIGHT_UP:
                return ((direction == Direction.LEFT) ? Direction.UP : Direction.RIGHT);
        }
        return null;
    }

    private Direction enemyNext(Point point, Direction direction) {
        switch (getAt(point)) {
            case ENEMY_BODY_HORIZONTAL:
                return direction;
            case ENEMY_BODY_VERTICAL:
                return direction;
            case ENEMY_BODY_LEFT_DOWN:
                return ((direction == Direction.RIGHT) ? Direction.DOWN : Direction.LEFT);
            case ENEMY_BODY_RIGHT_DOWN:
                return ((direction == Direction.LEFT) ? Direction.DOWN : Direction.RIGHT);
            case ENEMY_BODY_LEFT_UP:
                return ((direction == Direction.RIGHT) ? Direction.UP : Direction.LEFT);
            case ENEMY_BODY_RIGHT_UP:
                return ((direction == Direction.LEFT) ? Direction.UP : Direction.RIGHT);
        }
        return null;
    }

    private Direction getDirection(Point point) {
        switch (getAt(point)) {
            case HEAD_DOWN:
                return Direction.DOWN;
            case HEAD_UP:
                return Direction.UP;
            case HEAD_LEFT:
                return Direction.LEFT;
            default:
                return Direction.RIGHT;
        }
    }

    private Direction getEnemyDirection(Point point) {
        switch (getAt(point)) {
            case ENEMY_HEAD_DOWN:
                return Direction.DOWN;
            case ENEMY_HEAD_UP:
                return Direction.UP;
            case ENEMY_HEAD_LEFT:
                return Direction.LEFT;
            default:
                return Direction.RIGHT;
        }
    }

    private Elements getAt(Point pt) {
        return (pt.isOutOf(board.size())) ? null : board.getAt(pt);
    }


    private void initPills(ParsedSnake snake) {
        snake.isFury = (board.isAt(snake.head, HEAD_EVIL));
        snake.isFly = (board.isAt(snake.head, HEAD_FLY));
    }

    private void initEnemyPills(ParsedSnake snake) {
        snake.isFury = (board.isAt(snake.head, ENEMY_HEAD_EVIL));
        snake.isFly = (board.isAt(snake.head, ENEMY_HEAD_FLY));
    }


    public static class ParsedSnake {
        protected Direction direction;
        private Point head;
        private LinkedList<Point> body;

        private boolean isFury;
        private boolean isFly;


        private ParsedSnake(Point head) {
            this.head = head;
            body = new LinkedList<>();
            body.addLast(head);
        }

        public ParsedSnake(ParsedSnake other) {
            this.head = other.head();
            this.body = other.body();
            this.direction = other.direction();
            this.isFury = other.isFury();
            this.isFly = other.isFly();
        }

        public Direction direction() {
            return direction;
        }

        public Point head() {
            return head;
        }

        public LinkedList<Point> body() {
            return body;
        }

        public int size() {
            return body.size();
        }

        public boolean isFury() {
            return isFury;
        }

        public boolean isFly() {
            return isFly;
        }

        public int inSnake(Point target) {
            int i = 0;
            for (Point p : body()) {
                if (p.equals(target)) {
                    return i;
                }
                i++;
            }
            return -1;
        }
    }
}


