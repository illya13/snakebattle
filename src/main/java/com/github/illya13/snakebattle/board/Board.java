package com.github.illya13.snakebattle.board;


import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snakebattle.model.Elements;
import com.github.illya13.snakebattle.State;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private Parser parser;
    private BFS bfs;

    public Board() {
        parser = new Parser(this);
        bfs = new BFS(this);
    }

    @Override
    public Elements valueOf(char ch) {
        return Elements.valueOf(ch);
    }

    @Override
    protected int inversionY(int y) {
        return size - 1 - y;
    }

    public Parser.ParsedSnake getMe() {
        Point head = (!get(MY_HEAD_ELEMENTS).isEmpty())
                ? get(MY_HEAD_ELEMENTS).get(0)
                : get(ENEMY_HEAD_FLY, ENEMY_HEAD_DEAD).get(0);
        return parser.parseSnake(head);
    }

    public boolean isGameStart() {
        return !get(HEAD_SLEEP).isEmpty();
    }

    public boolean isGameOver() {
        return !get(HEAD_DEAD).isEmpty();
    }

    public boolean isLastStep() {
        return getEnemies().isEmpty() && getDeadSnakes().isEmpty();
    }

    public List<Parser.ParsedSnake> getEnemies() {
        return get(ENEMY_HEAD_ELEMENTS).stream()
                .map(parser::parseEnemy).collect(Collectors.toList());
    }

    public List<Parser.ParsedSnake> getDeadSnakes() {
        return get(ENEMY_HEAD_DEAD).stream()
                .map(parser::parseEnemy).collect(Collectors.toList());
    }

    public List<Parser.ParsedSnake> allSnakes() {
        return Stream.concat(
                Stream.of(getMe()),
                get(ENEMY_HEAD_ELEMENTS).stream().map(parser::parseEnemy)
        ).collect(Collectors.toList());
    }

    public Map<Point, Integer> bfs(State.Snake snake, Point start, Elements[] barrier, Elements[] targets) {
        return bfs.bfs(snake, start, barrier, targets);
    }

    // HELPERS

    public int eatSize(Point target, Point winner) {
        for (Parser.ParsedSnake snake: allSnakes()) {
            if (snake.isFly() || winner.equals(snake.head()))
                continue;

            int i = snake.inSnake(target);
            if (i != -1) return snake.size() - i;
        }

        return 0;
    }

    public int deadSize(Parser.ParsedSnake alive) {
        for (Parser.ParsedSnake dead: getDeadSnakes()) {
            int i = alive.inSnake(dead.head());
            if ((i == 0) && !alive.isFury() && (alive.size() - dead.size() < 2))
                return 0;

            if (i == -1) continue;

            return dead.size();
        }
        return 0;
    }

    public int maxOtherSnakeSize(Parser.ParsedSnake first) {
        int max = 0;
        for(Parser.ParsedSnake snake: allSnakes()) {
            if (snake.head().equals(first.head()))
                continue;

            if (snake.size() > max) {
                max = snake.size();
            }
        }
        return max;
    }


    private int[][] liveness;

    public int[][] liveness() {
        if (liveness != null)
            return liveness;

        liveness = new int[size()][size()];

        for (int x = 0; x < size(); ++x) {
            for (int y = 0; y < size(); ++y) {
                liveness[x][y] = (isAt(x, y, BARRIER_ELEMENTS) ? 0 : 1);
            }
        }

        for (int i = 0; i < size() / 2 ; ++i) {
            for (int x = 0; x < size(); ++x) {
                for (int y = 0; y < size(); ++y) {
                    int min = Integer.MAX_VALUE;
                    for (Direction direction : all) {
                        Point p = direction.change(PointImpl.pt(x, y));
                        if (p.isOutOf(size()))
                            continue;

                        if (liveness[p.getX()][p.getY()] < min) {
                            min = liveness[p.getX()][p.getY()];
                        }
                    }
                    liveness[x][y] = isAt(x, y, BARRIER_ELEMENTS) ? 0 : min + 1;
                }
            }
        }

        // debugSafe(liveness);
        return liveness;
    }

    private void debugSafe(int[][] safeGo) {
        for (int y = size() - 1; y >= 0; --y) {
            for (int x = 0; x < size(); ++x) {
                if (isAt(x, y, BARRIER_ELEMENTS)) {
                    System.out.printf("%s", getAllAt(x, y));
                } else {
                    System.out.printf("%3d", safeGo[x][y]);
                }
            }
            System.out.println();
        }
    }
}
