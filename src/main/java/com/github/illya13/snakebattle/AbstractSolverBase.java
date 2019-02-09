package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import java.util.*;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.Board.*;

public abstract class AbstractSolverBase implements com.codenjoy.dojo.client.Solver<com.codenjoy.dojo.snakebattle.client.Board> {
    static final String BASE_URL = "https://game2.epam-bot-challenge.com.ua/codenjoy-contest/board/player/";
    static final String PLAYER_CODE = "?code=1617935781189693616";
    static final String PLAYER_EMAIL = "illya.havsiyevych@gmail.com";
    static final String PLAYER_HASH = "bppowg4adbpirr4fm3yirto4krg1cwnwkjeo6gonbixy";

    static final int SELF_DESTRUCT_STEPS = 301;

    Learning learning;
    Direction prev;

    Board board;
    Point me;
    Direction[] priority;
    int step;
    boolean shortAction;

    boolean fury;
    boolean fly;

    boolean pill;
    int flyCounter;
    int furyCounter;
    int stoneCounter;

    boolean initialized = false;


    AbstractSolverBase(Dice dice) {
        learning = Learning.Builder.newLearning()
                .withStrategy(new Learning.DefaultStrategy(dice, "./features.json", "average.json"))
                .withPlayer(PLAYER_HASH)
                .build();
        learning.reset(null,0);
    }

    void initRound() {
        learning.reset(board, step);
        step = 0;
        stoneCounter = 0;
        pill = false;
    }

    protected void initStep() {
        System.out.printf(" => %s\n", learning.getStrategy());

        step++;
        shortAction = true;

        me = board.getMe();
        priority = board.getPriority(me, true);

        board.traceSnakes();
        board.traceSafe();

        checkPills(me);

        System.out.printf("%s me[%d]: %d, enemies[%d]: %d\n",
                ((prev == null) ? "" : prev.toString()),
                step, board.getMySize(), board.getEnemySnakes(), board.getEnemySize());

        System.out.print("stones: " + stoneCounter);
        if (fury) System.out.print(", fury[" + furyCounter + "]");
        if (fly) System.out.print(", fly[" + flyCounter + "]");
        System.out.println();
    }

    protected void checkPills(Point point) {
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

    protected boolean isSlideInsideMode() {
        return learning.getStrategy().hasFeature(Learning.FEATURE.INSIDE);
    }

    protected boolean isShortMode() {
        return learning.getStrategy().hasFeature(Learning.FEATURE.SHORT);
    }

    protected boolean isMediumMode() {
        return learning.getStrategy().hasFeature(Learning.FEATURE.MEDIUM);
    }

    protected boolean isAttackMode() {
        return ((fury && (furyCounter < 9)) || (board.getMySize() - board.getEnemySize() > 1))
                && learning.getStrategy().hasFeature(Learning.FEATURE.ATTACK);
    }

    protected boolean isFlyMode() {
        return (!fury || (furyCounter > 7)) && learning.getStrategy().hasFeature(Learning.FEATURE.FLY);
    }

    protected boolean isFollowMode() {
        return (board.getEnemySnakes() == 1) && learning.getStrategy().hasFeature(Learning.FEATURE.FOLLOW);
    }

    protected boolean isStoneMode() {
        return !fly && learning.getStrategy().hasFeature(Learning.FEATURE.STONES);
    }

    protected boolean isSelfDestructMode() {
        return (step > SELF_DESTRUCT_STEPS) && learning.getStrategy().hasFeature(Learning.FEATURE.DESTRUCT);
    }

    protected boolean isPredictMode() {
        return learning.getStrategy().hasFeature(Learning.FEATURE.PREDICT);
    }

    protected boolean avoidTail(Point point) {
        return !board.isAt(point, ENEMY_TAIL_ELEMENTS);
    }

    protected boolean canEatStoneAt(Point p) {
        return !board.isAt(p, STONE) || canEatStoneNow();
    }

    protected boolean canEatStoneNow() {
        return (board.getMySize() > 4) || (fury && furyCounter < 9);
    }

    protected boolean canEatStoneSoon() {
        return ((board.getMySize() > 4) && (!fly || flyCounter > 7)) || (fury && furyCounter < 9);
    }

    protected boolean canFly() {
        return fly && (flyCounter < 9);
    }

    protected boolean enemyCloseToTail() {
        return board.countNear(board.getMyTail(), ENEMY_HEAD_ELEMENTS, 1) > 0;
    }

    protected boolean isStepBack(Direction direction) {
        return direction.equals(turnAround(prev));
    }

    protected Direction turnAround(Direction direction) {
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

    protected Direction getEnemyDirectionByHead(Point enemy) {
        switch (board.getAt(enemy)) {
            case ENEMY_HEAD_UP:
                return Direction.UP;
            case ENEMY_HEAD_DOWN:
                return Direction.DOWN;
            case ENEMY_HEAD_LEFT:
                return Direction.LEFT;
            case ENEMY_HEAD_RIGHT:
                return Direction.RIGHT;
        }
        return null;
    }

    protected Set<Point> getEnemyTargetByHead(Point enemy) {
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
}
