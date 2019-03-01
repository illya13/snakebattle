package com.github.illya13.snakebattle.solver;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snakebattle.model.Elements;
import com.github.illya13.snakebattle.board.Board;
import com.github.illya13.snakebattle.Solver;
import com.github.illya13.snakebattle.State;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.board.Board.*;

public class BFSSolver implements Solver {
    @Override
    public void init() {}

    @Override
    public Direction next(State state) {
        Direction direction = findClosest(state, APPLE, GOLD, FURY_PILL, FLYING_PILL);
        return (direction != null) ? direction : Direction.RIGHT;
    }

    @Override
    public void done(int reward) {}

    private Direction findClosest(State state, Elements... elements) {
        int min = Integer.MAX_VALUE;
        Direction direction = null;

        for (Action action: getActions(state)) {
            Point point = action.direction().change(state.me().head());
            if (unsafeBorder(state.board(), point))
                continue;

            if (unsafeStone(state, point))
                continue;

            if (unsafeEnemy(state, point))
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
                (state.me().size() < 5) && !state.me().isFly() && !state.me().isFury();
    }

    private boolean unsafeBorder(Board board, Point point) {
        return board.isAt(point, join(BARRIER_ELEMENTS, ENEMY_ELEMENTS));
    }

    private boolean unsafeEnemy(State state, Point point) {
        return state.board().isAt(point, ENEMY_ELEMENTS) && !state.me().isFly() && !state.me().isFury();
    }

    private List<Action> getActions(State state) {
        Elements[] barrier = BARRIER_ELEMENTS;
        if (!state.me().isFly())
            barrier = join(barrier, MY_ELEMENTS);
        if (!state.me().isFly() && !state.me().isFury())
            barrier = join(barrier, ENEMY_ELEMENTS);

        Elements[] target = join(new Elements[] {APPLE, GOLD, FURY_PILL, FLYING_PILL});

        Direction inverted = state.me().direction().inverted();
        List<Action> actions = new LinkedList<>();
        for(Direction d: all) {
            if (d.equals(inverted))
                continue;

            if (state.board().isAt(d.change(state.me().head()), barrier))
                continue;

            actions.add(new Action(state.board(), d,state.me(), barrier, target));
        }
        return actions;
    }


    private class Action {
        private Board board;
        private Direction direction;
        private Map<Point, Integer> items;

        public Action(Board board, Direction direction, State.Me snake, Elements[] barrier, Elements[] target) {
            this.board = board;
            Point point = direction.change(snake.head());

            this.direction = direction;

            if (!board.isAt(point, NONE)) {
                items = new LinkedHashMap<>();
                items.put(point, 0);
                items.putAll(board.bfs(snake, point, barrier, target));
            } else {
                items = board.bfs(snake, point, barrier, target);
            }
        }

        public Direction direction() {
            return direction;
        }

        public Map<Point, Integer> items(Elements... elements) {
            return items.entrySet().stream()
                    .filter(map -> board.isAt(map.getKey(), elements))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
        }

        public String toString() {
            return direction + "[" + items.size() + ']';
        }
    }
}
