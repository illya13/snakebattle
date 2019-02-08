package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.*;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.Board.*;

public abstract class SolverBaseImpl extends AbstractSolverBase {
    Map<Point, Set<Point>> prediction = new HashMap<>();

    SolverBaseImpl(Dice dice) {
        super(dice);
    }

    protected BFS.Result getBFSDirection(Point point, int max) {
        return (canFly())
                ? board.bfsFly(turnAround(prev), point, max, false, BARRIER_FLY, GOLD, APPLE)
                : board.bfs(turnAround(prev), point, max, false, BARRIER_NORMAL_STONE, GOLD, APPLE);
    }

    protected Optional<Direction> getBFSWeightDirection(Point point, int max, Set<Point> skipped) {
        return (canFly())
                ? board.bfsWeightFly(turnAround(prev), point, max, skipped, BARRIER_FLY, GOLD, APPLE, FURY_PILL)
                : board.bfsWeight(turnAround(prev), point, max, skipped, BARRIER_NORMAL_STONE, GOLD, APPLE, FURY_PILL);
    }

    protected boolean areWeLate(Point target, int distance) {
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

    protected void initPredict() {
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

    protected void debugPrediction() {
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

    protected void getEnemyTargetByBFS(Set<Point> targets, Point enemy, Elements... elements) {
        BFS.Result go = board.bfs(turnAround(getEnemyDirectionByHead(enemy)), enemy, board.size(), false, BARRIER_ATTACK, elements);
        if (go.getDirection().isPresent()) {
            targets.add(go.getDirection().get().change(enemy));
        }
    }

    protected boolean isEnemyPredicted(Point point) {
        for (Set<Point> set: prediction.values()) {
            if (set.contains(point))
                return true;
        }
        return false;
    }

    protected Optional<Direction> avoidBorder(Point point, Direction[] directions) {
        for (Direction direction: directions) {
            if ( (point.getX() < 3) || (point.getY() < 2) || (point.getX() == board.size() - 2) || (point.getY() == board.size() - 2) ) {
                if (isSafeStep(point, direction))
                    return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    protected Optional<Direction> safeStepTarget(Point point, Elements elements, Direction[] directions) {
        return safeStepTarget(point, new Elements[]{elements}, directions);
    }

    protected Optional<Direction> safeStepTarget(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if (board.isAt(p, elements) && isSafeStep(point, direction))
                return Optional.of(direction);
        }
        return Optional.empty();
    }

    protected Optional<Direction> safeAttackTarget(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if (board.isAt(p, elements) && isSafeAttack(point, direction))
                return Optional.of(direction);
        }
        return Optional.empty();
    }

    protected Optional<Direction> safeAttackPrediction(Point point, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(isEnemyPredicted(p) && isSafeAttack(point, direction))
                return Optional.of(direction);
        }
        return Optional.empty();
    }

    protected boolean isSafeStep(Point point, Direction direction) {
        Point p = direction.change(point);

        return ( canFly() ? board.isSafeFly(p) : board.isSafe(p) ) &&
                !isStepBack(direction) && canEatStoneAt(p) && avoidAttack(p);
    }

    protected boolean isSafeAttack(Point point, Direction direction) {
        Point p = direction.change(point);

        return ( canFly() ? board.isSafeFly(p) : board.isSafeAttack(p) ) &&
                !isStepBack(direction) && avoidTail(p);
    }

    protected Optional<Direction> safeStepAvoid(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(!board.isAt(p, elements) && isSafeStep(point, direction)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    protected Optional<Direction> unsafeStepAvoid(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(!board.isAt(p, elements) && !isStepBack(direction)) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    protected Optional<Direction> unsafeStepPredictionAvoid(Point point, Elements[] elements, Direction[] directions) {
        for (Direction direction: directions) {
            Point p = direction.change(point);
            if(!board.isAt(p, elements) && !isStepBack(direction) && (!isPredictMode() || !isEnemyPredicted(p))) {
                return Optional.of(direction);
            }
        }
        return Optional.empty();
    }

    protected boolean avoidAttack(Point point) {
        return board.countNear(point, ENEMY_HEAD_ELEMENTS, 2) == 0 ||
                // (isPredictMode() && !isEnemyPredicted(point)) ||     // FIXME: move to FOLLOW ?
                ((fury && furyCounter < 9) && !board.isNear(point, ENEMY_HEAD_EVIL) ) ||
                ((board.countNear(point, ENEMY_HEAD_ELEMENTS, 1) == 0) && board.isAt(point, FURY_PILL) ) ||
                ( board.getMySize() - board.getEnemySize() > 1 );
    }
}
