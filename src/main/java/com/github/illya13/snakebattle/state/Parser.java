package com.github.illya13.snakebattle.state;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.model.Elements;
import com.github.illya13.snakebattle.Board;

import java.util.Arrays;
import java.util.LinkedList;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.codenjoy.dojo.snakebattle.model.Elements.ENEMY_HEAD_EVIL;

public class Parser {
    Board board;

    public Parser(Board board) {
        this.board = board;
    }

    private Elements getAt(Point pt) {
        return board.getAt(pt);
    }

    public ParsedSnake parseSnake(Point head) {
        ParsedSnake parsed = new ParsedSnake(head);

        Direction direction = getDirection(head);
        parsed.direction = direction;

        Elements headElement = getAt(head);
        if (Arrays.asList(HEAD_FLY, ENEMY_HEAD_FLY).contains(headElement)) {
            direction = getHeadDirectionWithMod(head);
            parsed.direction = direction;
        }

        if (Arrays.asList(HEAD_EVIL, ENEMY_HEAD_EVIL).contains(headElement)) {
            direction = getHeadDirectionWithMod(head);
            parsed.direction = direction;
        }

        if (Arrays.asList(HEAD_DEAD, ENEMY_HEAD_DEAD).contains(headElement)) {
            direction = getHeadDirectionWithMod(head);
            parsed.direction = direction;
        }

        direction = direction.inverted();

        Point point = head;
        while (direction != null) {
            point = direction.change(point);
            parsed.body.addLast(point);
            direction = next(point, direction);
        }

        return parsed;
    }

    private Direction getHeadDirectionWithMod(Point head) {
        Elements atLeft = getAt(Direction.LEFT.change(head));
        if (Arrays.asList(Elements.BODY_HORIZONTAL,
                Elements.BODY_RIGHT_DOWN,
                Elements.BODY_RIGHT_UP,
                Elements.TAIL_END_LEFT,
                Elements.ENEMY_BODY_HORIZONTAL,
                Elements.ENEMY_BODY_RIGHT_DOWN,
                Elements.ENEMY_BODY_RIGHT_UP,
                Elements.ENEMY_TAIL_END_LEFT).contains(atLeft))
        {
            return Direction.RIGHT;
        }

        Elements atRight = getAt(Direction.RIGHT.change(head));
        if (Arrays.asList(Elements.BODY_HORIZONTAL,
                Elements.BODY_LEFT_DOWN,
                Elements.BODY_LEFT_UP,
                Elements.TAIL_END_RIGHT,
                Elements.ENEMY_BODY_HORIZONTAL,
                Elements.ENEMY_BODY_LEFT_DOWN,
                Elements.ENEMY_BODY_LEFT_UP,
                Elements.ENEMY_TAIL_END_RIGHT).contains(atRight))
        {
            return Direction.LEFT;
        }

        Elements atDown = getAt(Direction.DOWN.change(head));
        if (Arrays.asList(Elements.BODY_VERTICAL,
                Elements.BODY_LEFT_UP,
                Elements.BODY_RIGHT_UP,
                Elements.TAIL_END_DOWN,
                Elements.ENEMY_BODY_VERTICAL,
                Elements.ENEMY_BODY_LEFT_UP,
                Elements.ENEMY_BODY_RIGHT_UP,
                Elements.ENEMY_TAIL_END_DOWN).contains(atDown))
        {
            return Direction.UP;
        }

        Elements atUp = getAt(Direction.UP.change(head));
        if (Arrays.asList(Elements.BODY_VERTICAL,
                Elements.BODY_LEFT_DOWN,
                Elements.BODY_RIGHT_DOWN,
                Elements.TAIL_END_UP,
                Elements.ENEMY_BODY_VERTICAL,
                Elements.ENEMY_BODY_LEFT_DOWN,
                Elements.ENEMY_BODY_RIGHT_DOWN,
                Elements.ENEMY_TAIL_END_UP).contains(atUp))
        {
            return Direction.DOWN;
        }

        throw new RuntimeException("Smth wrong with head");
    }

    private Direction next(Point point, Direction direction) {
        switch (getAt(point)) {
            case BODY_HORIZONTAL:
            case ENEMY_BODY_HORIZONTAL:
                return direction;
            case BODY_VERTICAL:
            case ENEMY_BODY_VERTICAL:
                return direction;
            case BODY_LEFT_DOWN:
            case ENEMY_BODY_LEFT_DOWN:
                return ((direction == Direction.RIGHT) ? Direction.DOWN : Direction.LEFT);
            case BODY_RIGHT_DOWN:
            case ENEMY_BODY_RIGHT_DOWN:
                return ((direction == Direction.LEFT) ? Direction.DOWN : Direction.RIGHT);
            case BODY_LEFT_UP:
            case ENEMY_BODY_LEFT_UP:
                return ((direction == Direction.RIGHT) ? Direction.UP : Direction.LEFT);
            case BODY_RIGHT_UP:
            case ENEMY_BODY_RIGHT_UP:
                return ((direction == Direction.LEFT) ? Direction.UP : Direction.RIGHT);
        }
        return null;
    }

    private Direction getDirection(Point point) {
        switch (getAt(point)) {
            case HEAD_DOWN : return Direction.DOWN;
            case ENEMY_HEAD_DOWN : return Direction.DOWN;
            case HEAD_UP : return Direction.UP;
            case ENEMY_HEAD_UP : return Direction.UP;
            case HEAD_LEFT : return Direction.LEFT;
            case ENEMY_HEAD_LEFT : return Direction.LEFT;
            default : return Direction.RIGHT;
        }
    }

    public static class ParsedSnake {
        Direction direction;
        Point head;
        LinkedList<Point> body;

        public ParsedSnake(Point head) {
            this.head = head;
            body = new LinkedList<>();
            body.addLast(head);
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
    }
}
