package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.*;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.services.Direction.*;
import static com.codenjoy.dojo.snakebattle.model.Elements.*;


public class Board extends com.codenjoy.dojo.snakebattle.client.Board {
    public static final Elements[] STONE_ELEMENTS = new Elements[]{STONE};

    public static final Elements[] EMPTY_ELEMENTS = new Elements[]{NONE, APPLE, FLYING_PILL, FURY_PILL, GOLD};

    public static final Elements[] BARRIER_ELEMENTS = new Elements[]{WALL, START_FLOOR, ENEMY_HEAD_SLEEP, ENEMY_TAIL_INACTIVE};


    public static final Elements[] ME_HEAD_ELEMENTS = new Elements[]{HEAD_DOWN, HEAD_LEFT, HEAD_RIGHT, HEAD_UP, HEAD_SLEEP, HEAD_EVIL, HEAD_FLY};

    public static final Elements[] ME_TAIL_ELEMENTS = new Elements[]{TAIL_END_DOWN, TAIL_END_LEFT, TAIL_END_UP, TAIL_END_RIGHT, TAIL_INACTIVE};

    public static final Elements[] ME_BODY_ELEMENTS = new Elements[]{BODY_HORIZONTAL, BODY_VERTICAL, BODY_LEFT_DOWN, BODY_LEFT_UP, BODY_RIGHT_DOWN, BODY_RIGHT_UP};


    public static final Elements[] ENEMY_HEAD_ELEMENTS = new Elements[]{ENEMY_HEAD_DOWN, ENEMY_HEAD_LEFT, ENEMY_HEAD_RIGHT, ENEMY_HEAD_UP, ENEMY_HEAD_FLY, ENEMY_HEAD_EVIL};

    public static final Elements[] ENEMY_BODY_ELEMENTS = new Elements[]{ENEMY_BODY_HORIZONTAL, ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_RIGHT_UP};

    public static final Elements[] ENEMY_TAIL_ELEMENTS = new Elements[]{ENEMY_TAIL_END_DOWN, ENEMY_TAIL_END_LEFT, ENEMY_TAIL_END_UP, ENEMY_TAIL_END_RIGHT};

    public static final Elements[] ENEMY_ELEMENTS = join(ENEMY_HEAD_ELEMENTS, ENEMY_BODY_ELEMENTS, ENEMY_TAIL_ELEMENTS);


    public static final Elements[] ME_BODY_TAIL_ELEMENTS = join(ME_BODY_ELEMENTS, ME_TAIL_ELEMENTS);

    public static final Elements[] SAFE_ELEMENTS = join(EMPTY_ELEMENTS, STONE_ELEMENTS, ME_HEAD_ELEMENTS, ME_BODY_ELEMENTS, ME_TAIL_ELEMENTS);
    public static final Elements[] SAFE_FLY_ELEMENTS = join(EMPTY_ELEMENTS, STONE_ELEMENTS, ME_HEAD_ELEMENTS, ME_BODY_ELEMENTS, ME_TAIL_ELEMENTS, ENEMY_ELEMENTS);
    public static final Elements[] SAFE_ATTACK_ELEMENTS = join(EMPTY_ELEMENTS, STONE_ELEMENTS, ME_HEAD_ELEMENTS, ME_BODY_ELEMENTS, ME_TAIL_ELEMENTS, ENEMY_ELEMENTS);

    public static final Elements[] BARRIER_NORMAL = join(BARRIER_ELEMENTS, ME_BODY_ELEMENTS, ENEMY_HEAD_ELEMENTS, ENEMY_BODY_ELEMENTS, ENEMY_TAIL_ELEMENTS);
    public static final Elements[] BARRIER_FLY = join(BARRIER_ELEMENTS);
    public static final Elements[] BARRIER_ATTACK = join(BARRIER_ELEMENTS, ME_BODY_ELEMENTS);
    public static final Elements[] BARRIER_NORMAL_STONE = join(BARRIER_ELEMENTS, STONE_ELEMENTS, ME_BODY_ELEMENTS, ENEMY_HEAD_ELEMENTS, ENEMY_BODY_ELEMENTS, ENEMY_TAIL_ELEMENTS);
    public static final Elements[] BARRIER_CUT_MYSELF = join(BARRIER_ELEMENTS, ENEMY_BODY_ELEMENTS, ENEMY_TAIL_ELEMENTS);
    public static final Elements[] BARRIER_NO_WAY = join(ENEMY_BODY_ELEMENTS, ENEMY_TAIL_ELEMENTS);


    public static Elements[] join(Elements[]... arrays) {
        int i = 0;
        for (Elements[] array : arrays) {
            i += array.length;
        }
        Elements[] result = new Elements[i];

        i = 0;
        for (Elements[] array : arrays) {
            for (Elements element : array) {
                result[i++] = element;
            }
        }
        return result;
    }

    public BFS.Result bfs(Direction prev, Point start, int max, boolean weight, Elements[] barrier, Elements... elements) {
        BFS bfs = BFS.Builder.newBFS(this, prev, start)
                .barrier(barrier).target(elements).weight(weight)
                .build();
        return bfs.bfs(max);
    }

    public BFS.Result bfsAttack(Direction prev, Point start, int max, boolean weight, Elements[] barrier, Elements... elements) {
        BFS bfs = BFS.Builder.newBFS(this, prev, start)
                .barrier(barrier).target(elements).weight(weight)
                .attack().build();
        return bfs.bfs(max);
    }

    public BFS.Result bfsFly(Direction prev, Point start, int max, boolean weight, Elements[] barrier, Elements... elements) {
        BFS bfs = BFS.Builder.newBFS(this, prev, start)
                .barrier(barrier).target(elements).weight(weight)
                .fly().build();
        return bfs.bfs(max);
    }

    public Optional<Direction> bfsWeight(Direction prev, Point start, int max, Set<Point> skipped, Elements[] barrier, Elements... elements) {
        BFS bfs = BFS.Builder.newBFS(this, prev, start)
                .barrier(barrier).target(elements)
                .weight(true).skipped(skipped)
                .build();
        return bfs.bfs(max).getDirection();
    }

    public Optional<Direction> bfsWeightFly(Direction prev, Point start, int max, Set<Point> skipped, Elements[] barrier, Elements... elements) {
        BFS bfs = BFS.Builder.newBFS(this, prev, start)
                .barrier(barrier).target(elements)
                .weight(true).skipped(skipped)
                .fly().build();
        return bfs.bfs(max).getDirection();
    }

    private static final int SAFE_TRACE_ROUNDS = 3;
    private boolean[][] safeGo;
    private boolean[][] safeFly;
    private boolean[][] safeAttack;

    public void traceSafe() {
        safeGo = new boolean[size()][size()];
        safeFly = new boolean[size()][size()];
        safeAttack = new boolean[size()][size()];

        for (int x = 0; x < size(); ++x) {
            for (int y = 0; y < size(); ++y) {
                safeGo[x][y] = isAt(x, y, SAFE_ELEMENTS);
                safeFly[x][y] = isAt(x, y, SAFE_FLY_ELEMENTS);
                safeAttack[x][y] = isAt(x, y, SAFE_ATTACK_ELEMENTS);
            }
        }

        for (int i = 0; i < SAFE_TRACE_ROUNDS; ++i) {
            for (int x = 0; x < size(); ++x) {
                for (int y = 0; y < size(); ++y) {
                    int goCount = 0;
                    int flyCount = 0;
                    int attackCount = 0;
                    for (Direction direction : new Direction[]{UP, RIGHT, DOWN, LEFT}) {
                        Point p = direction.change(PointImpl.pt(x, y));
                        if (p.isOutOf(size()))
                            continue;

                        if (safeGo[p.getX()][p.getY()] && isAt(p, SAFE_ELEMENTS)) {
                            goCount++;
                        }
                        if (safeFly[p.getX()][p.getY()] && isAt(p, SAFE_FLY_ELEMENTS)) {
                            flyCount++;
                        }
                        if (safeAttack[p.getX()][p.getY()] && isAt(p, SAFE_ATTACK_ELEMENTS)) {
                            attackCount++;
                        }
                    }
                    safeGo[x][y] = safeGo[x][y] && (goCount > 1);
                    safeFly[x][y] = safeFly[x][y] && (flyCount > 1);
                    safeAttack[x][y] = safeAttack[x][y] && (attackCount > 1);
                }
            }
        }

        // debugSafe(safeGo);
        // debugSafe(safeFly);
        // debugSafe(safeAttack);
    }

    private void debugSafe(boolean[][] safeGo) {
        for (int y = size() - 1; y >= 0; --y) {
            for (int x = 0; x < size(); ++x) {
                if (!safeGo[x][y]) {
                    System.out.print(getAllAt(x, y));
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }

    public boolean isSafe(Point point) {
        return safeGo[point.getX()][point.getY()];
    }

    public boolean isSafeFly(Point point) {
        return safeGo[point.getX()][point.getY()];
    }

    public boolean isSafeAttack(Point point) {
        return safeAttack[point.getX()][point.getY()];
    }

    public Direction[] getPriority(Point point, boolean debug) {
        Map<Direction, Integer> map = new HashMap<>();
        for (Direction direction : new Direction[]{RIGHT, DOWN, LEFT, UP}) {
            Point p = direction.change(point);
            int count = countNear(p, SAFE_ELEMENTS, 1);
            map.put(direction, count);
        }

        Point middle = PointImpl.pt(size()/2, size()/2);
        Map<Direction, Integer> sorted = map.entrySet().stream()
                .sorted((o1, o2) -> (o1.getValue().equals(o2.getValue()))
                        ? Double.compare(middle.distance(o1.getKey().change(point)), middle.distance(o2.getKey().change(point)))
                        : -Integer.compare(o1.getValue(), o2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        if (debug) System.out.println("priority: " + sorted);
        return sorted.keySet().toArray(new Direction[4]);
    }

    public int countNear(Point point, Elements[] elements, int radius) {
        int result = 0;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius; dy <= radius; ++dy) {
                if (Math.abs(dx) + Math.abs(dy) <= radius) {
                    if (!PointImpl.pt(point.getX() + dx, point.getY() + dy).isOutOf(this.size)) {
                        if (isAt(point.getX() + dx, point.getY() + dy, elements)) {
                            result++;
                        }
                    }
                }
            }
        }
        return result;
    }

    private int mySize;
    private int enemySnakes;
    private int enemySize;

    public void traceSnakes() {
        mySize = 0;
        enemySnakes = 0;
        enemySize = 0;

        for (int x = 0; x < size(); ++x) {
            for (int y = 0; y < size(); ++y) {
                if (isAt(x, y, join(ME_HEAD_ELEMENTS, ME_BODY_TAIL_ELEMENTS)))
                    mySize++;
                if (isAt(x, y, ENEMY_ELEMENTS))
                    enemySize++;
                if (isAt(x, y, ENEMY_HEAD_ELEMENTS))
                    enemySnakes++;
            }
        }
    }

    public int getMySize() {
        return mySize;
    }

    public int getEnemySnakes() {
        return enemySnakes;
    }

    public int getEnemySize() {
        return enemySize;
    }

    public boolean isGameStart() {
        return !get(HEAD_SLEEP).isEmpty();
    }

    private List<Point> getMyHead() {
        return get(ME_HEAD_ELEMENTS);
    }

    public Point getMyTail() {
        List<Point> tail = get(ME_TAIL_ELEMENTS);
        return (!tail.isEmpty()) ? get(ME_TAIL_ELEMENTS).get(0) : getMe();
    }

    public List<Point> getEnemies() {
        return get(ENEMY_HEAD_ELEMENTS);
    }
}
