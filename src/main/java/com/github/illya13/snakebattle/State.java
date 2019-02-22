package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.*;


public interface State {
    Board board();
    int liveness(Point point);

    int step();

    Me me();
    Collection<Enemy> enemies();

    interface Snake {
        Direction direction();
        Point head();
        Collection<Point> body();
        int size();

        boolean isFury();
        int fury();

        boolean isFly();
        int fly();

        Collection<Action> actions();

        int reward();
    }

    interface Enemy extends Snake {
    }

    interface Me extends Snake {
    }

    interface Action {
        Direction direction();
        Map<Point, Integer> items(Elements... elements);
    }
}
