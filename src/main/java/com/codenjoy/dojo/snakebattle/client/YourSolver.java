package com.codenjoy.dojo.snakebattle.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.snakebattle.model.Elements;


import java.util.Optional;

import static com.codenjoy.dojo.services.Direction.*;
import static com.codenjoy.dojo.snakebattle.model.Elements.*;

/**
 * User: Illia Gavsiievych
 * Это твой алгоритм AI для игры. Реализуй его на свое усмотрение.
 * Обрати внимание на {@see YourSolverTest} - там приготовлен тестовый
 * фреймворк для тебя.
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;

    YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        if (board.isGameOver()) return "";

        Point p = board.getMe();
        Direction[] priority = getPriority(p);

        Optional<Direction> go = tryElements(p, GOLD, priority);
        if (go.isPresent())
            return go.get().toString();

        go = tryElements(p, FURY_PILL, priority);
        if (go.isPresent())
            return go.get().toString();

        go = tryElements(p, APPLE, priority);
        if (go.isPresent())
            return go.get().toString();

        go = tryElements(p, FLYING_PILL, priority);
        if (go.isPresent())
            return go.get().toString();

        go = board.bfs(p, FURY_PILL, board.size() / 2);
        if (go.isPresent())
            return go.get().toString();

        if (board.isAt(p, HEAD_EVIL)) {
            System.out.println("EVIL");
        }

        go = board.bfs(p, APPLE, board.size() / 2);
        if (go.isPresent())
            return go.get().toString();

        go = board.bfs(p, GOLD, board.size() / 2);
        if (go.isPresent())
            return go.get().toString();

        go = avoidBarrierOrStone(p, priority);
        if (go.isPresent())
            return go.get().toString();

        return Direction.STOP.toString();
    }

    private Optional<Direction> tryElements(Point point, Elements elements, Direction[] directions) {
        for (Direction direction: directions) {
            if(board.isAtDirection(point, direction, elements)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    private Optional<Direction> avoidBarrierOrStone(Point point, Direction[] directions) {
        for (Direction direction: directions) {
            if(!board.isBarrierOrStoneOrMeAtDirection(point, direction)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    private Direction[] getPriority(Point p) {
        return new Direction[]{RIGHT, DOWN, LEFT, UP};
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "https://game2.epam-bot-challenge.com.ua/codenjoy-contest/board/player/illya.havsiyevych@gmail.com?code=1617935781189693616",
                new YourSolver(new RandomDice()),
                new Board());
    }
}
