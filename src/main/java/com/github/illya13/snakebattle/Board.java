package com.github.illya13.snakebattle;


import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.List;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;

public class Board extends AbstractBoard<Elements> {
    public static final Direction[] all = new Direction[]{Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN};

    public static final Elements[] BARRIER_ELEMENTS = new Elements[]{WALL, START_FLOOR, ENEMY_HEAD_SLEEP, ENEMY_TAIL_INACTIVE};

    public static final Elements[] MY_HEAD_ELEMENTS = new Elements[]{HEAD_DOWN, HEAD_LEFT, HEAD_RIGHT, HEAD_UP, HEAD_SLEEP, HEAD_EVIL, HEAD_FLY};
    public static final Elements[] MY_TAIL_ELEMENTS = new Elements[]{TAIL_END_DOWN, TAIL_END_LEFT, TAIL_END_UP, TAIL_END_RIGHT, TAIL_INACTIVE};
    public static final Elements[] MY_BODY_ELEMENTS = new Elements[]{BODY_HORIZONTAL, BODY_VERTICAL, BODY_LEFT_DOWN, BODY_LEFT_UP, BODY_RIGHT_DOWN, BODY_RIGHT_UP};
    public static final Elements[] MY_ELEMENTS = join(MY_HEAD_ELEMENTS, MY_BODY_ELEMENTS, MY_TAIL_ELEMENTS);

    public static final Elements[] ENEMY_HEAD_ELEMENTS = new Elements[]{ENEMY_HEAD_DOWN, ENEMY_HEAD_LEFT, ENEMY_HEAD_RIGHT, ENEMY_HEAD_UP, ENEMY_HEAD_FLY, ENEMY_HEAD_EVIL};
    public static final Elements[] ENEMY_BODY_ELEMENTS = new Elements[]{ENEMY_BODY_HORIZONTAL, ENEMY_BODY_VERTICAL, ENEMY_BODY_LEFT_DOWN, ENEMY_BODY_LEFT_UP, ENEMY_BODY_RIGHT_DOWN, ENEMY_BODY_RIGHT_UP};
    public static final Elements[] ENEMY_TAIL_ELEMENTS = new Elements[]{ENEMY_TAIL_END_DOWN, ENEMY_TAIL_END_LEFT, ENEMY_TAIL_END_UP, ENEMY_TAIL_END_RIGHT};
    public static final Elements[] ENEMY_ELEMENTS = join(ENEMY_HEAD_ELEMENTS, ENEMY_BODY_ELEMENTS, ENEMY_TAIL_ELEMENTS);

    public static Elements[] joinI(Elements[] array, Elements... items) {
        return join(array, items);
    }

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

    @Override
    public Elements valueOf(char ch) {
        return Elements.valueOf(ch);
    }

    @Override
    protected int inversionY(int y) {
        return size - 1 - y;
    }

    public Point getMe() {
        return (!get(MY_HEAD_ELEMENTS).isEmpty())
                ? get(MY_HEAD_ELEMENTS).get(0)
                : get(ENEMY_HEAD_DEAD).get(0);
    }

    public boolean isGameStart() {
        return !get(HEAD_SLEEP).isEmpty();
    }

    public boolean isGameOver() {
        return !get(HEAD_DEAD).isEmpty();
    }

    public List<Point> getEnemies() {
        return get(ENEMY_HEAD_ELEMENTS);
    }
}
