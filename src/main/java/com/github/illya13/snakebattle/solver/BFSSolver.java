package com.github.illya13.snakebattle.solver;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.model.Elements;
import com.github.illya13.snakebattle.Board;
import com.github.illya13.snakebattle.Solver;
import com.github.illya13.snakebattle.State;

import java.util.Map;

import static com.github.illya13.snakebattle.Board.*;

public class BFSSolver implements Solver {
    @Override
    public Direction next(State state) {
        Direction direction = findFirst(state, Elements.FURY_PILL, Elements.APPLE, Elements.GOLD, Elements.FLYING_PILL);
        return (direction != null) ? direction : Direction.RIGHT;
    }

    private Direction findFirst(State state, Elements... elements) {
        int min = Integer.MAX_VALUE;
        Direction direction = null;

        for (State.Action action: state.me().actions()) {
            Point point = action.direction().change(state.me().head());
            if (unsafeBorder(state.board(), point))
                continue;

            if (unsafeStone(state, point))
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

    private boolean unsafeStone(State state, Point point) {
        return state.board().isAt(point, Elements.STONE) &&
                ( (state.me().size() < 5) && !state.me().isFly() && !state.me().isFury());
    }

    private boolean unsafeBorder(Board board, Point point) {
        return board.isAt(point, join(BARRIER_ELEMENTS, ENEMY_ELEMENTS));
    }
}
