package com.github.illya13.snakebattle.solver;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.model.Elements;
import com.github.illya13.snakebattle.Board;
import com.github.illya13.snakebattle.Solver;
import com.github.illya13.snakebattle.State;

import java.util.Map;

import static com.github.illya13.snakebattle.Board.BARRIER_ELEMENTS;

public class BFSSolver implements Solver {
    @Override
    public Direction next(State state) {
        Direction direction = findFirst(state, state.board(), Elements.FURY_PILL, Elements.APPLE, Elements.GOLD, Elements.FLYING_PILL);
        return (direction != null) ? direction : Direction.RIGHT;
    }

    private Direction findFirst(State state, Board board, Elements... elements) {
        int min = Integer.MAX_VALUE;
        Direction direction = null;

        for (State.Action action: state.me().actions()) {
            if (board.isAt(action.direction().change(state.me().head()), BARRIER_ELEMENTS))
                continue;

            Map<Point, Integer> items = action.items(elements);
            if (items.isEmpty())
                continue;

            for (Point p: items.keySet()) {
                if (items.get(p) >= min)
                    break;

                min = items.get(p);
                direction = action.direction();
            }
        }
        return direction;
    }
}
