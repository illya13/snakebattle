package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.snakebattle.model.Elements;


import java.util.*;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.Board.*;

public class Solver implements com.codenjoy.dojo.client.Solver<com.codenjoy.dojo.snakebattle.client.Board> {
    private static final String BASE_URL = "https://game2.epam-bot-challenge.com.ua/codenjoy-contest/board/player/";
    private static final String PLAYER_CODE = "?code=1617935781189693616";
    private static final String PLAYER_EMAIL = "illya.havsiyevych@gmail.com";
    private static final String PLAYER_HASH = "bppowg4adbpirr4fm3yirto4krg1cwnwkjeo6gonbixy";

    private static final int SELF_DESTRUCT_STEPS = 301;

    private Learning learning;
    Direction prev;

    private Board board;
    private Point me;
    private Direction[] priority;
    private int step;
    private boolean shortAction;

    boolean fury;
    private boolean fly;

    private boolean pill;
    private int flyCounter;
    int furyCounter;
    int stoneCounter;
    boolean initialized = false;
    private Map<Point, Set<Point>> prediction = new HashMap<>();

    Solver(Dice dice) {
        learning = Learning.Builder.newLearning()
                .withStrategy(new Learning.DefaultStrategy(dice, "./features.json", "average.json"))
                .withPlayer(PLAYER_HASH)
                .build();
        learning.reset(null,0);
    }

    @Override
    public String get(com.codenjoy.dojo.snakebattle.client.Board board) {
        long ts = System.currentTimeMillis();

        this.board = (Board) board;
        if (board.isGameOver()) return "";

        if (this.board.isGameStart()) {
            initialized = false;
            return "";
        }

        if (!initialized) {
            initialized = true;
            initRound();
        }
        initStep();

        if (isSelfDestructMode()) return "ACT(0)";

        if (isPredictMode()) predict();

        prev = nextStep();
        String direction = act(prev);

        System.out.printf("latency: %d ms\n", System.currentTimeMillis() - ts);
        return direction;
    }

    void initRound() {
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
            go = (fury)
                    ? safeAttackTarget(point, join(ENEMY_HEAD_ELEMENTS, ENEMY_BODY_ELEMENTS), priority)
                    : safeAttackTarget(point, join(ENEMY_HEAD_ELEMENTS), priority);
            if (go.isPresent()) {
                System.out.println("=> ATTACK");
                return go;
            }

            if (isPredictMode()) {
                go = safeAttackPrediction(point, priority);
                if (go.isPresent()) {
                    System.out.println("=> PREDICTION ATTACK");
                    return go;
                }
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
        Set<Point> skipped = new HashSet<>();
        BFS.Result go;
        Optional<Direction> goWeight;

        if (isAttackMode()) {
            go = board.bfsAttack(turnAround(prev), point, 9-furyCounter, false,
                    BARRIER_ATTACK,
                    ENEMY_HEAD_DOWN, ENEMY_HEAD_LEFT, ENEMY_HEAD_RIGHT, ENEMY_HEAD_UP,
                    ENEMY_BODY_HORIZONTAL, ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_RIGHT_UP);
            if (go.getDirection().isPresent() && (go.getDistance() > 1) && isSafeAttack(point, go.getDirection().get())) {
                System.out.println("=> BFS: ATTACK");
                return go.getDirection();
            }

            go = board.bfsAttack(turnAround(prev), point, 9-furyCounter, false,
                    BARRIER_ATTACK,
                    ENEMY_HEAD_DOWN, ENEMY_HEAD_LEFT, ENEMY_HEAD_RIGHT, ENEMY_HEAD_UP);
            if (go.getDirection().isPresent() && (go.getDistance() > 1) && isSafeAttack(point, go.getDirection().get())) {
                System.out.println("=> BFS: ATTACK HEAD ONLY");
                return go.getDirection();
            }
        }

        if (isStoneMode() && canEatStoneSoon()) {
            go = board.bfs(turnAround(prev), point, 2 * board.getMySize(), false, BARRIER_NORMAL, STONE);
            if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
                System.out.println("=> BFS: STONE");
                return go.getDirection();
            } else if (stoneCounter > 0) {
                shortAction = false;
                return Optional.empty();
            }
        }

        go = board.bfs(turnAround(prev), point, board.size() / 2, false, BARRIER_NORMAL_STONE, FURY_PILL);
        if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
            if (isPredictMode() && go.getTarget().isPresent() && weAreLate(go.getTarget().get(), go.getDistance())) {
                skipped.add(go.getTarget().get());
                System.out.println("=> BFS: FURY SKIPPED BY PREDICT");
            } else {
                shortAction = false;
                System.out.println("=> BFS: FURY");
                return go.getDirection();
            }
        }

        if (isFollowMode()) {
            go = board.bfsAttack(turnAround(prev), point, board.size() / 2, false,
                    BARRIER_ATTACK,
                    ENEMY_HEAD_DOWN, ENEMY_HEAD_LEFT, ENEMY_HEAD_RIGHT, ENEMY_HEAD_UP,
                    ENEMY_BODY_HORIZONTAL, ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_RIGHT_UP);
            if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
                System.out.println("=> BFS: FOLLOW");
                return go.getDirection();
            }
        }

        if (isShortMode()) {
            go = getBFSDirection(point, board.size() / 6);
            if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
                if (isPredictMode() && go.getTarget().isPresent() && weAreLate(go.getTarget().get(), go.getDistance())) {
                    skipped.add(go.getTarget().get());
                    System.out.println("=> BFS: ANY SHORT SKIPPED BY PREDICT");
                } else {
                    System.out.println("=> BFS: ANY SHORT");
                    return go.getDirection();
                }
            }
        }

        if (isMediumMode()) {
            goWeight = getBFSWeightDirection(point, board.size() / 2, skipped);
            if (goWeight.isPresent() && isSafeStep(point, goWeight.get())) {
                shortAction = false;
                System.out.println("=> BFS: ANY MEDIUM");
                return goWeight;
            }
        }

        goWeight = getBFSWeightDirection(point, board.size() * 2, skipped);
        if (goWeight.isPresent() && isSafeStep(point, goWeight.get())) {
            shortAction = false;
            System.out.println("=> BFS: ANY LONG");
            return goWeight;
        }

        return Optional.empty();
    }


    private Direction lastCall(Point point) {
        Optional<Direction> go = safeStepAvoid(point, BARRIER_NORMAL_STONE, priority);
        if (go.isPresent()) {
            System.out.println("=> SAFE STEP");
            return go.get();
        }

        System.out.println("=> LAST CALL");
        go = unsafeStepPredictionAvoid(point, BARRIER_NORMAL_STONE, priority);
        if (go.isPresent())
            return go.get();

        go = unsafeStepAvoid(point, BARRIER_NORMAL_STONE, priority);
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
        String result = direction.toString();

        if ( (enemyCloseToTail() || (!shortAction && canEatStoneSoon()) || (shortAction && fury && (furyCounter <= 10 - board.getMySize())))
                && (stoneCounter > 0) ) {
            System.out.println("ACT");
            stoneCounter--;
            result = "(" + direction.toString() + ", ACT)";
        }

        if (board.isAt(direction.change(me), STONE))
            stoneCounter++;

        return result;
    }


    private BFS.Result getBFSDirection(Point point, int max) {
        return (canFly())
                ? board.bfsFly(turnAround(prev), point, max, false, BARRIER_FLY, GOLD, APPLE)
                : board.bfs(turnAround(prev), point, max, false, BARRIER_NORMAL_STONE, GOLD, APPLE);
    }

    private Optional<Direction> getBFSWeightDirection(Point point, int max, Set<Point> skipped) {
        return (canFly())
                ? board.bfsWeightFly(turnAround(prev), point, max, skipped, BARRIER_FLY, GOLD, APPLE, FURY_PILL)
                : board.bfsWeight(turnAround(prev), point, max, skipped, BARRIER_NORMAL_STONE, GOLD, APPLE, FURY_PILL);
    }

    private boolean isShortMode() {
        return learning.getStrategy().hasFeature(Learning.FEATURE.SHORT);
    }

    private boolean isMediumMode() {
        return learning.getStrategy().hasFeature(Learning.FEATURE.MEDIUM);
    }

    private boolean isAttackMode() {
        return ((fury && (furyCounter < 9)) || (board.getMySize() - board.getEnemySize() > 1))
                && learning.getStrategy().hasFeature(Learning.FEATURE.ATTACK);
    }

    private boolean isFlyMode() {
        return (!fury || (furyCounter > 7)) && learning.getStrategy().hasFeature(Learning.FEATURE.FLY);
    }

    private boolean isFollowMode() {
        return (board.getEnemySnakes() == 1) && learning.getStrategy().hasFeature(Learning.FEATURE.FOLLOW);
    }

    private boolean isStoneMode() {
        return !fly && (SELF_DESTRUCT_STEPS - step > 30) && learning.getStrategy().hasFeature(Learning.FEATURE.STONES);
    }

    private boolean isSelfDestructMode() {
        return (step > SELF_DESTRUCT_STEPS) && learning.getStrategy().hasFeature(Learning.FEATURE.DESTRUCT);
    }

    private boolean isPredictMode() {
        return learning.getStrategy().hasFeature(Learning.FEATURE.PREDICT);
    }

    private boolean weAreLate(Point target, int distance) {
        System.out.println("are we late?");

        BFS.Result go = board.bfsAttack(null, target, distance, false, BARRIER_ATTACK, ENEMY_HEAD_ELEMENTS);
        if (go.getDirection().isPresent() && (go.getDistance() <= distance)) {
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

            BFS.Result go = board.bfsFly(turnAround(getEnemyDirectionByHead(enemy)), enemy, board.size(), false, BARRIER_FLY, join(ME_HEAD_ELEMENTS, ME_BODY_ELEMENTS));
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
        BFS.Result go = board.bfs(turnAround(getEnemyDirectionByHead(enemy)), enemy, board.size(), false, BARRIER_ATTACK, elements);
        if (go.getDirection().isPresent()) {
            targets.add(go.getDirection().get().change(enemy));
        }
    }

    private Direction getEnemyDirectionByHead(Point enemy) {
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
        shortAction = true;

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

    private Optional<Direction> safeAttackPrediction(Point point, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(isEnemyPredicted(p) && isSafeAttack(point, direction))
                return Optional.of(direction);
        }
        return Optional.empty();
    }

    private boolean isSafeStep(Point point, Direction direction) {
        Point p = direction.change(point);

        return ( canFly() ? board.isSafeFly(p) : board.isSafe(p) ) &&
                !isStepBack(direction) && canEatStoneAt(p) && avoidAttack(p);
    }

    private boolean isSafeAttack(Point point, Direction direction) {
        Point p = direction.change(point);

        return ( canFly() ? board.isSafeFly(p) : board.isSafeAttack(p) ) &&
                !isStepBack(direction) && avoidTail(p);
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

    private Optional<Direction> unsafeStepPredictionAvoid(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(!board.isAt(p, elements) && !isStepBack(direction) && (!isPredictMode() || !isEnemyPredicted(p))) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }


    private boolean avoidAttack(Point point) {
        return board.countNear(point, ENEMY_HEAD_ELEMENTS, 2) == 0 ||
                // (isPredictMode() && !isEnemyPredicted(point)) ||     // FIXME: move to FOLLOW ?
                ((fury && furyCounter < 9) && !board.isNear(point, ENEMY_HEAD_EVIL) ) ||
                ( board.getMySize() - board.getEnemySize() > 1 );
    }

    private boolean avoidTail(Point point) {
        return !board.isAt(point, ENEMY_TAIL_ELEMENTS);
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
        return board.countNear(board.getMyTail(), ENEMY_HEAD_ELEMENTS, 1) > 0;
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
                BASE_URL + PLAYER_EMAIL + PLAYER_CODE,
                new Solver(new RandomDice()),
                new Board());
    }
}
