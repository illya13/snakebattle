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


import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.snakebattle.model.Elements;


import java.util.*;

import static com.codenjoy.dojo.snakebattle.client.Board.*;
import static com.codenjoy.dojo.snakebattle.model.Elements.*;

/**
 * User: Illia Gavsiievych
 * Это твой алгоритм AI для игры. Реализуй его на свое усмотрение.
 * Обрати внимание на {@see YourSolverTest} - там приготовлен тестовый
 * фреймворк для тебя.
 */
public class YourSolver implements Solver<Board> {
    private static final String BASE_URL = "https://game2.epam-bot-challenge.com.ua/codenjoy-contest/board/player/";
    private static final String PLAYER_CODE = "?code=1617935781189693616";
    private static final String PLAYER_HASH = "bppowg4adbpirr4fm3yirto4krg1cwnwkjeo6gonbixy";

    private static final int SELF_DESTRUCT_STEPS = 300;

    private Learning learning;
    private Direction prev;

    private Board board;
    private Point me;
    private Direction[] priority;
    private int step;

    private boolean fury;
    private boolean fly;

    private boolean pill;
    private int flyCounter;
    private int furyCounter;
    private int stoneCounter;
    private boolean initialized = false;
    private Map<Point, Set<Point>> prediction = new HashMap<>();

    YourSolver(Dice dice) {
        learning = Learning.Builder.newLearning()
                .withStrategy(new Learning.DefaultStrategy(dice, "./features.json", "average.json"))
                .withPlayer(PLAYER_HASH)
                .build();
        learning.reset(null,0);
    }

    @Override
    public String get(Board board) {
        long ts = System.currentTimeMillis();

        this.board = board;
        if (board.isGameOver()) return "";

        if (board.isGameStart()) {
            initialized = false;
            return "";
        }

        if (!initialized) {
            initialized = true;
            initRound();
        }
        initStep();

        if (isSelfDestructMode()) return "ACT(0)";

        if (isPredictMode())predict();

        prev = nextStep();
        String direction = act(prev);

        System.out.printf("latency: %d ms\n", System.currentTimeMillis() - ts);
        return direction;
    }

    private void initRound() {
        learning.reset(board, step);
        step = 0;
        stoneCounter = 0;
        pill = false;
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
            go = safeAttackTarget(point, ENEMY_ELEMENTS, priority);
            if (go.isPresent()) {
                System.out.println("=> ATTACK");
                return go;
            }
        }

        if (isFollowMode()) {
            // can we do smth ?
        }

        go = safeStepTarget(point, FURY_PILL, priority);
        if (go.isPresent()) {
            System.out.println("=> FURY_PILL");
            furyCounter = (fury) ? furyCounter-10 : 0;
            if (!fury) fury = true;
            return go;
        }

        if (isStoneMode()) {
            go = safeStepTarget(point, STONE, priority);
            if (go.isPresent()) {
                System.out.println("=> STONE");
                stoneCounter++;
                return go;
            }
        }

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

        if (isFlyMode()) {
            go = safeStepTarget(point, FLYING_PILL, priority);
            if (go.isPresent()) {
                System.out.println("=> FLYING_PILL");
                flyCounter = (fly) ? flyCounter - 10 : 0;
                if (!fly) fly = true;
                return go;
            }
        }

        return go;
    }


    private Optional<Direction> midTerm(Point point) {
        BFS.Result go;

        if (isAttackMode()) {
            go = board.bfsAttack(point, board.size() / 6, false,
                    BARRIER_ATTACK,
                    ENEMY_HEAD_DOWN, ENEMY_HEAD_LEFT, ENEMY_HEAD_RIGHT, ENEMY_HEAD_UP,
                    ENEMY_BODY_HORIZONTAL, ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_RIGHT_UP);
            if (go.getDirection().isPresent() && isSafeAttack(point, go.getDirection().get())) {
                System.out.println("=> BFS: ATTACK");
                return go.getDirection();
            }
        }

        if (isShortMode()) {
            if (isStoneMode() && canEatStoneSoon()) {
                go = board.bfs(point, board.size() / 6, false, BARRIER_NORMAL, STONE);
                if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
                    System.out.println("=> BFS: STONE SHORT");
                    return go.getDirection();
                }
            }

            go = getBFSDirection(point, board.size() / 6, false);
            if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
                if (isPredictMode() && go.getTarget().isPresent() && weAreLate(go.getTarget().get(), go.getDistance())) {
                    System.out.println("=> BFS: SKIPPED BY PREDICT");
                } else {
                    System.out.println("=> BFS: ANY SHORT");
                    return go.getDirection();
                }
            }
        }

        if (isFollowMode()) {
            go = board.bfsAttack(point, board.size() * 2, false,
                    BARRIER_ATTACK,
                    ENEMY_HEAD_DOWN, ENEMY_HEAD_LEFT, ENEMY_HEAD_RIGHT, ENEMY_HEAD_UP,
                    ENEMY_BODY_HORIZONTAL, ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_RIGHT_UP);
            if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
                System.out.println("=> BFS: FOLLOW");
                return go.getDirection();
            }
        }

        if (isMediumMode()) {
            go = getBFSDirection(point, board.size() / 2, true);
            if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
                System.out.println("=> BFS: ANY MEDIUM");
                return go.getDirection();
            }

            if (board.getMySize() > 4) {
                go = board.bfs(point, board.size() / 2, false, BARRIER_NORMAL, STONE);
                if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
                    System.out.println("=> BFS: STONE MEDIUM");
                    return go.getDirection();
                }
            }
        }

        go = getBFSDirection(point, board.size() * 2, true);
        if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
            System.out.println("=> BFS: ANY LONG");
            return go.getDirection();
        }

        return Optional.empty();
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
        if ( (enemyCloseToTail() || canEatStoneSoon()) && (stoneCounter > 0) ) {
            System.out.println("ACT");
            stoneCounter--;
            return "(" + direction.toString() + ", ACT)";
        }
        return direction.toString();
    }


    private BFS.Result getBFSDirection(Point point, int max, boolean weight) {
        return (canFly())
                ? board.bfsFly(point, max, weight, BARRIER_FLY, GOLD, APPLE, FURY_PILL/*, FLYING_PILL*/)
                : board.bfs(point, max, weight, BARRIER_NORMAL_STONE, GOLD, APPLE, FURY_PILL/*, FLYING_PILL*/);
    }

    private boolean isShortMode() {
        return learning.getStrategy().hasFeature(Learning.FEATURE.SHORT);
    }

    private boolean isMediumMode() {
        return learning.getStrategy().hasFeature(Learning.FEATURE.MEDIUM);
    }

    private boolean isAttackMode() {
        return fury && (furyCounter < 9) && learning.getStrategy().hasFeature(Learning.FEATURE.ATTACK);
    }

    private boolean isFlyMode() {
        return (!fury || (furyCounter > 7)) && learning.getStrategy().hasFeature(Learning.FEATURE.FLY);
    }

    private boolean isFollowMode() {
        return (board.getEnemySnakes() == 1) && learning.getStrategy().hasFeature(Learning.FEATURE.FOLLOW);
    }

    private boolean isStoneMode() {
        return !fly && learning.getStrategy().hasFeature(Learning.FEATURE.STONES);
    }

    private boolean isSelfDestructMode() {
        return (step > SELF_DESTRUCT_STEPS) && learning.getStrategy().hasFeature(Learning.FEATURE.DESTRUCT);
    }

    private boolean isPredictMode() {
        return learning.getStrategy().hasFeature(Learning.FEATURE.PREDICT);
    }

    private boolean weAreLate(Point target, int distance) {
        System.out.println("are we late?");
        BFS.Result go = board.bfsAttack(target, distance,false, BARRIER_ATTACK, ENEMY_HEAD_ELEMENTS);
        if (go.getDirection().isPresent()) {
            System.out.printf("\t %s %s %d\n",
                    board.getAt(target), board.getAt(go.getTarget().get()), go.getDistance());
            return true;
        }
        System.out.println("\tno");
        return false;
    }

    private void predict() {
        prediction.clear();
        System.out.println("predictions:");
        for (Point enemy: board.getEnemies()) {
            Set<Point> targets = getEnemyTargetByHead(enemy);

            getEnemyTargetByBFS(targets, enemy, GOLD, APPLE);
            getEnemyTargetByBFS(targets, enemy, FURY_PILL);
            getEnemyTargetByBFS(targets, enemy, STONE);
            BFS.Result go = board.bfsFly(enemy, board.size(),false, BARRIER_FLY, join(ME_HEAD_ELEMENTS, ME_BODY_ELEMENTS));
            if (go.getDirection().isPresent()) {
                targets.add(go.getDirection().get().change(enemy));
            }
            prediction.put(enemy, targets);
            System.out.printf("\t %s %s\n", board.getAt(enemy), targets);
        }

        // debugPrediction();
    }

    private void debugPrediction() {
        for (int y = board.size() - 1; y >= 0; --y) {
            for (int x = 0; x < board.size(); ++x) {
                Point p = PointImpl.pt(x, y);
                if (!isEnemyPredicted(p)) {
                    System.out.print(board.isAt(p, NONE) ? "   " : board.getAllAt(x, y));
                } else {
                    System.out.printf(" %s ", HEAD_DEAD);
                }
            }
            System.out.println();
        }
    }

    private void getEnemyTargetByBFS(Set<Point> targets, Point enemy, Elements... elements) {
        BFS.Result go = board.bfs(enemy, board.size(),false, BARRIER_ATTACK, elements);
        if (go.getDirection().isPresent()) {
            targets.add(go.getDirection().get().change(enemy));
        }
    }

    private Set<Point> getEnemyTargetByHead(Point enemy) {
        Set<Point> targets = new HashSet<>();
        switch (board.getAt(enemy)) {
            case ENEMY_HEAD_UP:
                targets.add(Direction.UP.change(enemy));
                break;
            case ENEMY_HEAD_DOWN:
                targets.add(Direction.DOWN.change(enemy));
                break;
            case ENEMY_HEAD_LEFT:
                targets.add(Direction.LEFT.change(enemy));
                break;
            case ENEMY_HEAD_RIGHT:
                targets.add(Direction.RIGHT.change(enemy));
                break;
        }
        return targets;
    }

    private boolean isEnemyPredicted(Point point) {
        for (Set<Point> set: prediction.values()) {
            if (set.contains(point))
                return true;
        }
        return false;
    }

    private void initStep() {
        System.out.printf(" => %s\n", learning.getStrategy());

        step++;

        me = board.getMe();
        priority = board.getPriority(me, true);

        board.traceSnakes();
        board.traceSafe();

        checkPills(me);

        System.out.printf("me[%d]: %d, enemies[%d]: %d\n",
                step, board.getMySize(), board.getEnemySnakes(), board.getEnemySize());

        System.out.print("stones: " + stoneCounter);
        if (fury) System.out.print(", fury[" + furyCounter + "]");
        if (fly) System.out.print(", fly[" + flyCounter + "]");
        System.out.println();
    }


    private Optional<Direction> safeStepTarget(Point point, Elements elements, Direction[] directions) {
        return safeStepTarget(point, new Elements[]{elements}, directions);
    }

    private Optional<Direction> safeStepTarget(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if (board.isAt(p, elements) && isSafeStep(point, direction))
                return Optional.of(direction);
        }
        return Optional.empty();
    }

    private Optional<Direction> safeAttackTarget(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if (board.isAt(p, elements) && isSafeAttack(point, direction))
                return Optional.of(direction);
        }
        return Optional.empty();
    }

    private boolean isSafeStep(Point point, Direction direction) {
        Point p = direction.change(point);

        return ( canFly() ? board.isSafeFly(p) : board.isSafe(p) ) &&
                !isStepBack(direction) && canEatStoneAt(p) && canAttack(p);
    }

    private boolean isSafeAttack(Point point, Direction direction) {
        Point p = direction.change(point);

        return ( canFly() ? board.isSafeFly(p) : board.isSafeAttack(p) ) &&
                !isStepBack(direction);
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
                (isPredictMode() && !isEnemyPredicted(point)) ||
                ((fury && furyCounter < 9) && !board.isNear(point, ENEMY_HEAD_EVIL) ) ||
                ( board.getMySize() - board.getEnemySize() > 1 );
    }

    private boolean canEatStoneAt(Point p) {
        return !board.isAt(p, STONE) || canEatStoneNow();
    }

    private boolean canEatStoneNow() {
        return (board.getMySize() > 4) || (fury && furyCounter < 9);
    }

    private boolean canEatStoneSoon() {
        return ((board.getMySize() > 4) && (!fly || flyCounter > 7)) || (fury && furyCounter < 9);
    }

    private boolean canFly() {
        return fly && (flyCounter < 9);
    }

    private boolean enemyCloseToTail() {
        return board.countNear(board.getMyTail(), ENEMY_HEAD_ELEMENTS) > 0;
    }

    private boolean isStepBack(Direction direction) {
        return direction.equals(turnAround(prev));
    }

    private Direction turnAround(Direction direction) {
        if (direction == null)
            return null;

        switch (direction) {
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            case RIGHT:
                return Direction.LEFT;
            case LEFT:
                return Direction.RIGHT;
            default:
                return null;
        }
    }

    private void checkPills(Point point) {
        if (board.isAt(point, HEAD_EVIL, HEAD_FLY)) {
            fury = fury || board.isAt(point, HEAD_EVIL);
            fly = fly || board.isAt(point, HEAD_FLY);
            if (!pill) {
                pill = true;
            } else {
                flyCounter++;
                if (flyCounter == 10) fly = false;

                furyCounter++;
                if (furyCounter == 10) fury = false;
            }
        } else {
            pill = false;
            fury = false;
            fly = false;
            flyCounter = 0;
            furyCounter = 0;
        }
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                BASE_URL + PLAYER_HASH + PLAYER_CODE,
                new YourSolver(new RandomDice()),
                new Board());
    }
}
