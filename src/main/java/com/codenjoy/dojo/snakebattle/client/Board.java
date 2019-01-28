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
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.*;

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

    public static final Elements[] ME_ELEMENTS = new Elements[] {TAIL_END_DOWN, TAIL_END_LEFT, TAIL_END_UP, TAIL_END_RIGHT, TAIL_INACTIVE,
            BODY_HORIZONTAL, BODY_VERTICAL, BODY_LEFT_DOWN, BODY_LEFT_UP, BODY_RIGHT_DOWN, BODY_RIGHT_UP};

    public static final Elements[] ENEMY_ELEMENTS = new Elements[] {ENEMY_HEAD_DOWN, ENEMY_HEAD_LEFT, ENEMY_HEAD_RIGHT, ENEMY_HEAD_UP,
            ENEMY_TAIL_END_DOWN, ENEMY_TAIL_END_LEFT, ENEMY_TAIL_END_UP, ENEMY_TAIL_END_RIGHT, ENEMY_TAIL_INACTIVE,
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

    public boolean isSafe(Point point) {
        int count = 0;
        for (Direction direction: new Direction[]{UP, RIGHT, DOWN, LEFT}) {
            if (isAt(direction.change(point), EMPTY_ELEMENTS)) {
                count++;
            }
        }
        return count > 1;
    }

    public Point getMe() {
        return getMyHead().get(0);
    }

    public boolean isGameOver() {
        return getMyHead().isEmpty();
    }

    private List<Point> getMyHead() {
        return get(HEAD_DOWN, HEAD_LEFT, HEAD_RIGHT, HEAD_UP, HEAD_SLEEP, HEAD_EVIL, HEAD_FLY);
    }

    public Optional<Direction> bfs(Point start, int max, Elements[] barrier, Elements... elements) {
        Optional<Direction> result = BFS.bfs(this, start, barrier, elements, max);
        if (result.isPresent()) {
            System.out.println(" " + result.get());
        }
        return result;
    }
}
