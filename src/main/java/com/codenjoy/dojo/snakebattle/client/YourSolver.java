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
    private Point me;
    private Direction[] priority;

    private boolean fury;
    private boolean fly;

    private boolean pill;
    private int pillCounter;
    private int stoneCounter = 0;

    private static final Elements[] BARRIER_NORMAL = join(BARRIER_ELEMENTS, ME_ELEMENTS, ENEMY_ELEMENTS);
    private static final Elements[] BARRIER_CUT_MYSELF = join(BARRIER_ELEMENTS, ENEMY_TAIL_ELEMENTS);
    private static final Elements[] BARRIER_NO_WAY = join(ENEMY_TAIL_ELEMENTS);

    YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        if (board.isGameOver()) return "";

        init(board);

        Optional<Direction> go;

        go = realTime(me);
        if (go.isPresent())
            return go.get().toString();

        go = midTerm(me);
        if (go.isPresent())
            return go.get().toString();

        return lastCall(me);
    }


    private Optional<Direction> realTime(Point point) {
        Optional<Direction> go;

        go = safeStepTarget(point, FLYING_PILL, priority);
        if (go.isPresent()) {
            pillCounter = 0;
        }

        go = safeStepTarget(point, FURY_PILL, priority);
        if (go.isPresent()) {
            pillCounter = 0;
            return go;
        }

        if (board.getMySize() > 4  && !fly) {
            go = safeStepTarget(point, STONE, priority);
            if (go.isPresent()) {
                stoneCounter++;
                return go;
            }
        }

        go = safeStepTarget(point, GOLD, priority);
        if (go.isPresent())
            return go;

        go = safeStepTarget(point, APPLE, priority);
        if (go.isPresent())
            return go;

        return go;
    }


    private Optional<Direction> midTerm(Point point) {
        Optional<Direction> go;

        go = board.bfs(point, board.size() / 5, BARRIER_NORMAL, FURY_PILL, FLYING_PILL);
        if (go.isPresent()) {
            System.out.println("=> PILL");
            return go;
        }

        if (board.getMySize() > 4 && (!fly || pillCounter > 5)) {
            go = board.bfs(point, board.size() / 3, BARRIER_NORMAL, STONE);
            if (go.isPresent()) {
                System.out.println("=> STONE");
                return go;
            }
        }

        go = board.bfs(point, board.size() / 2, BARRIER_NORMAL, GOLD, APPLE);
        if (go.isPresent()) {
            System.out.println("=> GOLD, APPLE");
            return go;
        }

        go = board.bfs(point, board.size() * 2, BARRIER_NORMAL, GOLD, APPLE, FURY_PILL, FLYING_PILL);
        if (go.isPresent()) {
            System.out.println("=> GOLD, APPLE, FURY_PILL, FLYING_PILL");
        }
        return go;
    }


    private String lastCall(Point point) {
        Optional<Direction> go = safeStepAvoid(point, BARRIER_NORMAL, priority);
        if (go.isPresent())
            return go.get().toString();

        go = unsafeStepAvoid(point, BARRIER_NORMAL, priority);
        if (go.isPresent())
            return go.get().toString();

        if (fly) {
            return priority[0].toString();
        }

        go = unsafeStepAvoid(point, BARRIER_CUT_MYSELF, priority);
        if (go.isPresent())
            return go.get().toString();

        go = unsafeStepAvoid(point, BARRIER_NO_WAY, priority);
        if (go.isPresent())
            return go.get().toString();

        return priority[0].toString();
    }


    private void init(Board board) {
        if (board.isGameStart()) {
            stoneCounter = 0;
            pill = false;
        }

        board.traceSnakes();
        System.out.println("me [" + board.getMySize() + "], enemies [" + board.getEnemySnakes()+ "]: " + board.getEnemySize());

        board.traceSafe();

        me = board.getMe();
        checkPills(me);

        priority = board.getPriority(me);
    }


    private Optional<Direction> safeStepTarget(Point point, Elements elements, Direction[] directions) {
        return safeStepTarget(point, new Elements[]{elements}, directions);
    }

    private Optional<Direction> safeStepTarget(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(board.isSafe(p) && board.isAt(p, elements)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    private Optional<Direction> safeStepAvoid(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(board.isSafe(p) && !board.isAt(p, elements)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    private Optional<Direction> unsafeStepAvoid(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(!board.isAt(p, elements)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
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

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "https://game2.epam-bot-challenge.com.ua/codenjoy-contest/board/player/illya.havsiyevych@gmail.com?code=1617935781189693616",
                new YourSolver(new RandomDice()),
                new Board());
    }
}
