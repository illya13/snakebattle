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

    @Override
    public Elements valueOf(char ch) {
        return Elements.valueOf(ch);
    }

    public boolean isBarrierAt(Point point) {
        return isAt(point, WALL, START_FLOOR, ENEMY_HEAD_SLEEP, ENEMY_TAIL_INACTIVE);
    }

    public boolean isStoneAt(Point point) {
        return isAt(point, STONE);
    }

    public boolean isMeAt(Point point) {
        return isAt(point, TAIL_END_DOWN, TAIL_END_LEFT, TAIL_END_UP, TAIL_END_RIGHT, TAIL_INACTIVE,
                BODY_HORIZONTAL, BODY_VERTICAL, BODY_LEFT_DOWN, BODY_LEFT_UP, BODY_RIGHT_DOWN, BODY_RIGHT_UP);
    }

    public boolean isBarrierOrStoneOrMeAt(Point point) {
        return isBarrierAt(point) || isStoneAt(point) || isMeAt(point);
    }

    public boolean isBarrierOrStoneOrMeAtDirection(Point point, Direction direction) {
        return isBarrierOrStoneOrMeAt(direction.change(point));
    }

    public boolean isAtDirection(Point point, Direction direction, Elements elements) {
        return isAt(direction.change(point), elements);
    }

    @Override
    protected int inversionY(int y) {
        return size - 1 - y;
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

    public Optional<Direction> bfs(Point start, int max, Elements... elements) {
        Optional<Direction> result = BFS.bfs(this, start, elements, max);
        if (result.isPresent()) {
            System.out.println("BFS: " + result.get());
        }
        return result;
    }
}
