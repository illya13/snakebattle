package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.client.WebSocketRunner;



import java.util.*;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.Board.*;

public class Solver extends SolverBaseImpl {

    Solver(Dice dice) {
        super(dice);
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

        if (isPredictMode()) initPredict();

        prev = nextStep();
        String direction = act(prev);

        System.out.printf("latency: %d ms\n", System.currentTimeMillis() - ts);
        return direction;
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

        if (isSlideInsideMode()) {
            go = avoidBorder(point, priority);
            if (go.isPresent()) {
                System.out.println("=> SLIDE INSIDE");
                return go;
            }
        }

        go = lockProtection(point);
        if (go.isPresent()) {
            System.out.println("=> LOCK PROTECTION");
            return go;
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
            if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
                System.out.println("=> BFS: ATTACK HEAD ONLY");
                return go.getDirection();
            }
        }

        if (isStoneMode() && canEatStoneSoon()) {
            go = board.bfs(turnAround(prev), point, board.size() / 4, false, BARRIER_NORMAL, STONE);
            if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
                System.out.println("=> BFS: STONE");
                return go.getDirection();
            } else if (stoneCounter > 0) {
                shortAction = false;
                return Optional.empty();
            }
        }

        go = board.bfs(turnAround(prev), point, board.size() / 4, false, BARRIER_NORMAL_STONE, FURY_PILL);
        if (go.getDirection().isPresent() && isSafeStep(point, go.getDirection().get())) {
            if (isPredictMode() && go.getTarget().isPresent() && areWeLate(go.getTarget().get(), go.getDistance())) {
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
                if (isPredictMode() && go.getTarget().isPresent() && areWeLate(go.getTarget().get(), go.getDistance())) {
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

        if ( (enemyCloseToTail() /*|| (!shortAction && canEatStoneSoon())*/ || (shortAction && fury && (furyCounter <= 10 - board.getMySize())))
                && (stoneCounter > 0) ) {
            System.out.println("ACT");
            stoneCounter--;
            result = "(" + direction.toString() + ", ACT)";
        }

        if (board.isAt(direction.change(me), STONE))
            stoneCounter++;

        return result;
    }


    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                BASE_URL + PLAYER_EMAIL + PLAYER_CODE,
                new Solver(new RandomDice()),
                new Board());
    }
}
