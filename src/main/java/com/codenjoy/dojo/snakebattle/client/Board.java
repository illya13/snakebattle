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


import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.*;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.services.Direction.*;
import static com.codenjoy.dojo.snakebattle.model.Elements.*;

/**
 * Класс, обрабатывающий строковое представление доски.
 * Содержит ряд унаследованных методов {@see AbstractBoard},
 * но ты можешь добавить сюда любые свои методы на их основе.
 */
public class Board extends AbstractBoard<Elements> {
    public static final Elements[] STONE_ELEMENTS = new Elements[] {STONE};
    public static final Elements[] EMPTY_ELEMENTS = new Elements[] {NONE, APPLE, FLYING_PILL, FURY_PILL, GOLD};

    public static final Elements[] BARRIER_ELEMENTS = new Elements[] {WALL, START_FLOOR, ENEMY_HEAD_SLEEP, ENEMY_TAIL_INACTIVE};

    public static final Elements[] ME_HEAD_ELEMENTS = new Elements[] {HEAD_DOWN, HEAD_LEFT, HEAD_RIGHT, HEAD_UP, HEAD_SLEEP, HEAD_EVIL, HEAD_FLY};

    public static final Elements[] ME_ELEMENTS = new Elements[] {TAIL_END_DOWN, TAIL_END_LEFT, TAIL_END_UP, TAIL_END_RIGHT, TAIL_INACTIVE,
            BODY_HORIZONTAL, BODY_VERTICAL, BODY_LEFT_DOWN, BODY_LEFT_UP, BODY_RIGHT_DOWN, BODY_RIGHT_UP};

    public static final Elements[] ME_BODY_ELEMENTS = new Elements[] {BODY_HORIZONTAL, BODY_VERTICAL, BODY_LEFT_DOWN, BODY_LEFT_UP, BODY_RIGHT_DOWN, BODY_RIGHT_UP};

    public static final Elements[] ENEMY_ELEMENTS = new Elements[] {ENEMY_HEAD_DOWN, ENEMY_HEAD_LEFT, ENEMY_HEAD_RIGHT, ENEMY_HEAD_UP, ENEMY_HEAD_FLY, ENEMY_HEAD_EVIL,
            ENEMY_TAIL_END_DOWN, ENEMY_TAIL_END_LEFT, ENEMY_TAIL_END_UP, ENEMY_TAIL_END_RIGHT,
            ENEMY_BODY_HORIZONTAL, ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_RIGHT_UP};

    public static final Elements[] ENEMY_HEAD_ELEMENTS = new Elements[] {ENEMY_HEAD_DOWN, ENEMY_HEAD_LEFT, ENEMY_HEAD_RIGHT, ENEMY_HEAD_UP, ENEMY_HEAD_FLY, ENEMY_HEAD_EVIL};

    public static final Elements[] ENEMY_TAIL_ELEMENTS = new Elements[] {ENEMY_TAIL_END_DOWN, ENEMY_TAIL_END_LEFT, ENEMY_TAIL_END_UP, ENEMY_TAIL_END_RIGHT, ENEMY_TAIL_INACTIVE,
            ENEMY_BODY_HORIZONTAL, ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_RIGHT_UP};


    public static Elements[] join(Elements[]... arrays) {
        int i = 0;
        for (Elements[] array: arrays) {
            i += array.length;
        }
        Elements[] result = new Elements[i];

        i = 0;
        for (Elements[] array: arrays) {
            for (Elements element: array) {
                result[i++] = element;
            }
        }
        return result;
    }

    @Override
    public Elements valueOf(char ch) {
        return Elements.valueOf(ch);
    }

    @Override
    protected int inversionY(int y) {
        return size - 1 - y;
    }

    private static final int SAFE_TRACE_ROUNDS = 3;

    private boolean[][] safe;

    private Elements[] SAFE_ELEMENTS = join(EMPTY_ELEMENTS, STONE_ELEMENTS, ME_HEAD_ELEMENTS);

    public void traceSafe() {
        safe = new boolean[size()][size()];

        for(int x = 0; x < size(); ++x) {
            for(int y = 0; y < size(); ++y) {
                safe[x][y] = isAt(x, y, SAFE_ELEMENTS);
            }
        }

        for(int i = 0; i < SAFE_TRACE_ROUNDS; ++i) {
            for (int x = 0; x < size(); ++x) {
                for (int y = 0; y < size(); ++y) {
                    int goCount = 0;
                    for (Direction direction: new Direction[]{UP, RIGHT, DOWN, LEFT}) {
                        Point p = direction.change(PointImpl.pt(x, y));
                        if (p.isOutOf(size()))
                            continue;

                        if (safe[p.getX()][p.getY()] && isAt(p, SAFE_ELEMENTS)) {
                            goCount++;
                        }
                    }
                    safe[x][y] = safe[x][y] && (goCount > 1);
                }
            }
        }

        for(int y = size()-1; y >= 0; --y) {
            for (int x = 0; x < size(); ++x) {
                if (!safe[x][y]) {
                    System.out.print(getAllAt(x, y));
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }

    public boolean isSafe(Point point) {
        return safe[point.getX()][point.getY()];
    }

    public Direction[] getPriority(Point point) {
        Map<Direction, Integer> map = new HashMap<>();
        for (Direction direction:  new Direction[]{RIGHT, DOWN, LEFT, UP}) {
            Point p = direction.change(point);
            int count = countNear(p, SAFE_TRACE_ROUNDS-1, SAFE_ELEMENTS);
            map.put(direction, count);
        }

        Map<Direction, Integer> sorted = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        System.out.println("priority: " + sorted);
        return sorted.keySet().toArray(new Direction[4]);
    }

    public int countNear(Point point, int radius, Elements[] elements) {
        int result = 0;
        for(int dx = -radius; dx <= radius; ++dx) {
            for(int dy = -radius; dy <= radius; ++dy) {
                if (!PointImpl.pt(point.getX() + dx, point.getY() + dy).isOutOf(this.size) && (dx != 0 || dy != 0) && (!this.withoutCorners() || dx == 0 || dy == 0)) {
                    if (isAt(point.getX() + dx, point.getY() + dy, elements)) {
                        result++;
                    }
                }
            }
        }
        return result;
    }

    private int mySize;
    private int enemySnakes;
    private int enemySize;
    private int enemyFury;

    public void traceSnakes() {
        mySize = 0;
        enemySnakes = 0;
        enemySize = 0;
        enemyFury = 0;

        for (int x = 0; x < size(); ++x) {
            for (int y = 0; y < size(); ++y) {
                if (isAt(x, y, join(ME_HEAD_ELEMENTS, ME_ELEMENTS)))
                    mySize++;
                if (isAt(x, y, ENEMY_ELEMENTS))
                    enemySize++;
                if (isAt(x, y, ENEMY_HEAD_ELEMENTS))
                    enemySnakes++;
                if (isAt(x, y, ENEMY_HEAD_EVIL))
                    enemyFury++;
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

    public boolean iAmTheBoss() {
        return (enemyFury == 0) && (mySize > enemySize);
    }


    public Point getMe() {
        return getMyHead().get(0);
    }

    public boolean isGameOver() {
        return getMyHead().isEmpty();
    }

    public boolean isGameStart() {
        return !get(HEAD_SLEEP).isEmpty();
    }

    private List<Point> getMyHead() {
        return get(ME_HEAD_ELEMENTS);
    }

    public Optional<Direction> bfs(Point start, int max, Elements[] barrier, Elements... elements) {
        Optional<Direction> result = BFS.bfs(this, start, barrier, elements, max);
        if (result.isPresent()) {
            System.out.println(" " + result.get());
        }
        return result;
    }
}
