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

import static com.codenjoy.dojo.snakebattle.client.Board.*;
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
    private Direction[] priority;

    private boolean fury;
    private boolean fly;

    private boolean pill;
    private int pillCounter;
    private int stoneCounter = 0;

    private static final Elements[] BARRIER_ENEMY = join(BARRIER_ELEMENTS, STONE_ELEMENTS, ME_ELEMENTS, ENEMY_ELEMENTS);
    private static final Elements[] BARRIER = join(BARRIER_ELEMENTS, STONE_ELEMENTS, ME_ELEMENTS);
    private static final Elements[] NORMAL = join(BARRIER_ELEMENTS, ME_ELEMENTS, ENEMY_TAIL_ELEMENTS);
    private static final Elements[] CUT_MYSELF = join(BARRIER_ELEMENTS, ENEMY_TAIL_ELEMENTS);
    private static final Elements[] NO_WAY = join(ENEMY_TAIL_ELEMENTS);

    YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        if (board.isGameOver()) {
            stoneCounter = 0;
            return "";
        }

        board.traceSnakes();
        System.out.println("me [" + board.getMySize() + "], enemies [" + board.getEnemySnakes()+ "]: " + board.getEnemySize());

        board.traceSafe();

        Point me = board.getMe();
        checkPills(me);

        priority = board.getPriority(me);

        Optional<Direction> go = realTime(me);
        if (go.isPresent())
            return go.get().toString();

        go = midTerm(me);
        if (go.isPresent())
            return go.get().toString();

        return lastCall(me);
    }

    private void checkPills(Point point) {
        if (board.isAt(point, HEAD_EVIL, HEAD_FLY)) {
            fury = board.isAt(point, HEAD_EVIL);
            fly = board.isAt(point, HEAD_FLY);
            if (!pill) {
                pill = true;
            } else {
                ++pillCounter;
            }
        } else {
            pill = false;
            fury = false;
            fly = false;
            pillCounter = 0;
        }
        System.out.println("stones[" + stoneCounter + "], pill[" + pillCounter + "]: " + pill + ", fury: " + fury + ", fly: " + fly);
    }

    private Optional<Direction> realTime(Point point) {
        Optional<Direction> go;

        if (board.getMySize() > 4) {
            go = tryToGo(point, STONE, priority);
            if (go.isPresent()) {
                stoneCounter++;
                return go;
            }
        }

        go = tryToGo(point, FLYING_PILL, priority);
        if (go.isPresent()) {
            pillCounter = 0;
        }

        go = tryToGo(point, FURY_PILL, priority);
        if (go.isPresent()) {
            pillCounter = 0;
            return go;
        }

        go = tryToGo(point, GOLD, priority);
        if (go.isPresent())
            return go;

        go = tryToGo(point, APPLE, priority);
        if (go.isPresent())
            return go;

        return go;
    }

    private Optional<Direction> midTerm(Point point) {
        Optional<Direction> go;

        if (board.getMySize() > 4) {
            go = board.bfs(point, board.size() / 4, BARRIER_ENEMY, STONE);
            if (go.isPresent())
                return go;
        }

        go = board.bfs(point, board.size() / 4, BARRIER_ENEMY, FURY_PILL, FLYING_PILL);
        if (go.isPresent())
            return go;

        go = board.bfs(point, board.size(), BARRIER_ENEMY, GOLD, APPLE);
        if (go.isPresent())
            return go;

        if (fury || board.iAmTheBoss()) {
            go = board.bfs(point, board.size() / 4, BARRIER, ENEMY_HEAD_ELEMENTS);
            if (go.isPresent()) {
                Point p = go.get().change(point);
                if (board.isSafeToGo(p)) {
                    System.out.println("AGGRESSIVE");
                    return go;
                } else {
                    System.out.println("CLOSE");
                }
            }
        }

        return board.bfs(point, board.size() * 2, BARRIER_ENEMY, GOLD, APPLE, FURY_PILL, FLYING_PILL);
    }

    private String lastCall(Point point) {
        Optional<Direction> go = avoid(point, priority);
        if (go.isPresent())
            return go.get().toString();

        go = lastCall(point, NORMAL, priority);
        if (go.isPresent())
            return go.get().toString();

        go = lastCall(point, CUT_MYSELF, priority);
        if (go.isPresent())
            return go.get().toString();

        go = lastCall(point, NO_WAY, priority);
        if (go.isPresent())
            return go.get().toString();

        return priority[0].toString();
    }

    private Optional<Direction> tryToGo(Point point, Elements elements, Direction[] directions) {
        return tryToGo(point, new Elements[]{elements}, directions);
    }

    private Optional<Direction> tryToGo(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(board.isSafeToGo(p) && board.isAt(p, elements)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    private Optional<Direction> avoid(Point point, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(board.isSafeToGo(p) && !board.isAt(p, NORMAL)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    private Optional<Direction> lastCall(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(!board.isAt(p, elements)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "https://game2.epam-bot-challenge.com.ua/codenjoy-contest/board/player/illya.havsiyevych@gmail.com?code=1617935781189693616",
                new YourSolver(new RandomDice()),
                new Board());
    }
}
