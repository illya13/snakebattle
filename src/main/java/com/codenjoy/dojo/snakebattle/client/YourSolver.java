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

    private static final int ATTACK_STEPS = 100;
    private Dice dice;
    private Direction prev;

    private Board board;
    private Point me;
    private Direction[] priority;
    private int step;
    private boolean closeAction;

    private boolean fury;
    private boolean fly;

    private boolean pill;
    private int pillCounter;
    private int stoneCounter;

    private boolean stoneOnPrevStep;        // FIXME: remove

    YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        if (board.isGameOver()) return "";

        init();

        prev = nextStep();
        return act(prev);
    }


    private Direction nextStep() {
        Optional<Direction> go;

        go = realTime(me);
        if (go.isPresent())
            return go.get();

        go = midTerm(me);
        if (go.isPresent())
            return go.get();

        return lastCall(me);
    }


    private Optional<Direction> realTime(Point point) {
        Optional<Direction> go;

        if (isAttackMode()) {
            // TODO: implement
        }

        go = safeStepTarget(point, FLYING_PILL, priority);
        if (go.isPresent()) {
            System.out.println("=> FLYING_PILL");
            pillCounter = (fly) ? pillCounter-10 : 0;
            return go;
        }

        go = safeStepTarget(point, FURY_PILL, priority);
        if (go.isPresent()) {
            System.out.println("=> FURY_PILL");
            pillCounter = (fury) ? pillCounter-10 : 0;
            return go;
        }

        if (!fly) {
            go = safeStepTarget(point, STONE, priority);
            if (go.isPresent()) {
                System.out.println("=> STONE");
                stoneCounter++;
                stoneOnPrevStep = true;
                return go;
            }
        }
        stoneOnPrevStep = false;

        go = safeStepTarget(point, GOLD, priority);
        if (go.isPresent()) {
            System.out.println("=> GOLD");
            return go;
        }

        go = safeStepTarget(point, APPLE, priority);
        if (go.isPresent()) {
            System.out.println("=> APPLE");
            return go;
        }

        return go;
    }


    private Optional<Direction> midTerm(Point point) {
        Optional<Direction> go;

        if (isAttackMode()) {
            go = board.bfsAttack(point, board.size() / 6, BARRIER_ATTACK, ENEMY_HEAD_DOWN, ENEMY_HEAD_LEFT, ENEMY_HEAD_RIGHT, ENEMY_HEAD_UP);
            if (go.isPresent() && isSafeAttack(point, go.get())) {
                System.out.println("=> BFS: ATTACK");
                return go;
            }
        }

        if ( canEatStoneSoon() && (!fly || pillCounter > 6) ) {
            go = board.bfs(point, board.size() / 6, BARRIER_NORMAL, STONE);
            if (go.isPresent() && isSafeStep(point, go.get())) {
                System.out.println("=> BFS: STONE CLOSE");
                return go;
            }
        }

        go = (canFly())
                ? board.bfsFly(point, board.size() / 6, BARRIER_FLY, FURY_PILL, FLYING_PILL, GOLD, APPLE)
                : board.bfs(point, board.size() / 6, BARRIER_NORMAL_STONE, FURY_PILL, FLYING_PILL, GOLD, APPLE);
        if (go.isPresent() && isSafeStep(point, go.get())) {
            System.out.println("=> BFS: ANY CLOSE");
            return go;
        }

        if (board.getMySize() > 4) {
            go = board.bfs(point, board.size() / 2, BARRIER_NORMAL, STONE);
            if (go.isPresent() && isSafeStep(point, go.get())) {
                closeAction = false;
                System.out.println("=> BFS: STONE FAR");
                return go;
            }
        }

        go = (canFly())
                ? board.bfsFly(point, board.size() * 2, BARRIER_FLY, GOLD, APPLE, FURY_PILL, FLYING_PILL)
                : board.bfs(point, board.size() * 2, BARRIER_NORMAL_STONE, GOLD, APPLE, FURY_PILL, FLYING_PILL);
        if (go.isPresent() && isSafeStep(point, go.get())) {
            closeAction = false;
            System.out.println("=> BFS: ANY FAR");
            return go;
        }

        return Optional.empty();
    }

    private boolean isAttackMode() {
        return /*(board.getEnemySnakes() == 1) && */ (step > ATTACK_STEPS);
    }


    private Direction lastCall(Point point) {
        Optional<Direction> go = safeStepAvoid(point, BARRIER_NORMAL_STONE, priority);
        if (go.isPresent())
            return go.get();

        go = unsafeStepAvoid(point, BARRIER_NORMAL, priority);
        if (go.isPresent())
            return go.get();

        go = unsafeStepAvoid(point, BARRIER_CUT_MYSELF, priority);
        if (go.isPresent())
            return go.get();

        go = unsafeStepAvoid(point, BARRIER_NO_WAY, priority);
        if (go.isPresent())
            return go.get();

        return priority[0];
    }


    private String act(Direction direction) {
        if ( (enemyCloseToTail() || (!closeAction && canEatStoneSoon())) && (stoneCounter > 0) ) {
            System.out.println("ACT");
            stoneCounter--;
            return "(" + direction.toString() + ", ACT)";
        }
        return direction.toString();
    }


    private void init() {
        if (board.isGameStart()) {
            step = 0;
            stoneCounter = 0;
            pill = false;
            stoneOnPrevStep = false;
        }
        step++;
        closeAction = true;

        board.traceSnakes();
        System.out.println("[" + step + "] me: " + board.getMySize() + ", enemies[" + board.getEnemySnakes()+ "]: " + board.getEnemySize());

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
            if (board.isAt(p, elements) &&
                    (isAttackMode() ? isSafeAttack(point, direction) : isSafeStep(point, direction)) )
                return Optional.of(direction);
        }
        return Optional.empty();
    }

    private boolean isSafeStep(Point point, Direction direction) {
        Point p = direction.change(point);

        return ( canFly() ? board.isSafeFly(p) : board.isSafe(p) ) &&
                !isStepBack(direction) && canEatStoneAt(p) && canAttack(p);
    }

    private boolean isStepBack(Direction direction) {
        if (prev != null) {
            switch (prev) {
                case UP:
                    return direction.equals(Direction.DOWN);
                case DOWN:
                    return direction.equals(Direction.UP);
                case RIGHT:
                    return direction.equals(Direction.LEFT);
                case LEFT:
                    return direction.equals(Direction.RIGHT);
            }
        }
        return false;
    }

    private boolean isSafeAttack(Point point, Direction direction) {
        Point p = direction.change(point);

        return ( canFly() ? board.isSafeFly(p) : board.isSafeAttack(p) ) &&
                !isStepBack(direction) && canEatStoneAt(p) && canAttack(p);
    }

    private Optional<Direction> safeStepAvoid(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(!board.isAt(p, elements) && isSafeStep(point, direction)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    private Optional<Direction> unsafeStepAvoid(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(!board.isAt(p, elements) && !isStepBack(direction)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    private boolean canAttack(Point point) {
        return board.countNear(point, ENEMY_HEAD_ELEMENTS) == 0 ||
                ((fury && pillCounter < 9) && !board.isNear(point, ENEMY_HEAD_EVIL) ) ||
                ( board.getMySize() > board.getEnemySize() );
    }

    private boolean canEatStoneAt(Point p) {
        return !board.isAt(p, STONE) || canEatStoneNow();
    }

    private boolean canEatStoneNow() {
        return ( (stoneOnPrevStep) ? (board.getMySize() > 7) : (board.getMySize() > 4) )
                || (fury && pillCounter < 9);
    }

    private boolean canEatStoneSoon() {
        return (board.getMySize() > 4) || (fury && pillCounter < 8);
    }

    private boolean canFly() {
        return fly && (pillCounter < 9);
    }

    private boolean enemyCloseToTail() {
        return board.countNear(board.getMyTail(), ENEMY_HEAD_ELEMENTS) > 0;
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
        System.out.print("stones: " + stoneCounter);
        if (stoneOnPrevStep) {
            System.out.print("+");
        }
        if (fury) {
            System.out.println(", fury[" + pillCounter + "]");
        } else if (fly) {
            System.out.println(", fly[" + pillCounter + "]");
        } else {
            System.out.println();
        }
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "https://game2.epam-bot-challenge.com.ua/codenjoy-contest/board/player/illya.havsiyevych@gmail.com?code=1617935781189693616",
                new YourSolver(new RandomDice()),
                new Board());
    }
}
