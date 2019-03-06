package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.github.illya13.snakebattle.board.Board;

import java.util.*;


public interface State {
    Board board();

    int step();

    Me me();
    List<Enemy> enemies();

    interface Snake {
        Direction direction();
        Point head();
        List<Point> body();
        int size();

        boolean isFury();
        int fury();

        boolean isFly();
        int fly();

        int reward();
    }

    interface Enemy extends Snake {
    }

    interface Me extends Snake {
    }
}
