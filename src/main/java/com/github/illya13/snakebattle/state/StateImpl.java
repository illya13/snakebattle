package com.github.illya13.snakebattle.state;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import com.codenjoy.dojo.snakebattle.model.Elements;
import com.codenjoy.dojo.snakebattle.model.board.Field;
import com.codenjoy.dojo.snakebattle.model.board.SnakeBoard;
import com.codenjoy.dojo.snakebattle.model.board.Timer;
import com.codenjoy.dojo.snakebattle.model.hero.Hero;
import com.codenjoy.dojo.snakebattle.model.hero.Tail;
import com.codenjoy.dojo.snakebattle.model.level.LevelImpl;

import com.github.illya13.snakebattle.Board;
import com.github.illya13.snakebattle.State;

import java.lang.reflect.Method;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.Board.*;


public class StateImpl implements State {
    Board board;
    int step;
    MeImpl me;
    List<Enemy> enemies;

    public StateImpl() {
        me = new MeImpl();
        enemies = new LinkedList<>();
    }

    public void reset() {
        step = 0;
        me.reset();
        enemies.clear();
    }

    public void initStep(Board board) {
        this.board = board;
        LevelImpl level = new LevelImpl(board.boardAsString().replaceAll("\n", ""));

        BFS bfs = new BFS(board);

        step++;
        me.initStep(board, board.getMe(), level, bfs);

        enemies.clear();
        for(Point point: board.getEnemies()) {
            EnemyImpl enemy = new EnemyImpl();
            enemy.reset();
            enemy.initStep(board, point, level, bfs);
            enemies.add(enemy);
        }
    }

    public void stepTo(Board board, Direction direction) {
        me.stepTo(board, direction);
    }

    @Override
    public Board board() {
        return board;
    }

    @Override
    public int liveness(Point point) {
        return 0;
    }

    @Override
    public int step() {
        return step;
    }

    @Override
    public Me me() {
        return me;
    }

    @Override
    public List<Enemy> enemies() {
        return enemies;
    }

    @Override
    public String toString() {
        return "[" + step + "]\tme: " + me + "\n\tenemies: " + enemies;
    }


    private static class SnakeImpl implements Snake {
        private static final int MAX_DURATION = 10;

        static Method parseSnake;
        static Method getDirection;

        static {
            try {
                parseSnake = LevelImpl.class.getDeclaredMethod("parseSnake", Point.class, Field.class);
                parseSnake.setAccessible(true);
                getDirection = Hero.class.getDeclaredMethod("getDirection");
                getDirection.setAccessible(true);
            } catch (NoSuchMethodException ignored) {
                // no op
            }
        }

        Direction direction;
        Point head;
        LinkedList<Point> body;
        int furyCounter;
        int flyCounter;

        private List<Action> actions;

        public SnakeImpl() {
            body = new LinkedList<>();
            actions = new LinkedList<>();
        }

        public void reset() {
            furyCounter = 0;
            flyCounter = 0;
        }

        public void initStep(Board board, Point head, LevelImpl level, BFS bfs) {
            initSnake(head, level);
            initActions(board, bfs);
        }

        private void initSnake(Point head, LevelImpl level) {
            Field field = createGame(level);

            this.head = head;
            try {
                Hero hero = (Hero) parseSnake.invoke(level, head, field);
                direction = (Direction) getDirection.invoke(hero);
                if (hero.isFury()) checkAndSetFury();
                if (hero.isFlying()) checkAndSetFly();

                body.clear();
                for (Tail tail : hero.body()) {
                    body.addFirst(tail);
                }

            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        private void initActions(Board board, BFS bfs) {
            Elements[] barrier = (isFly() || isFury())
                    ? BARRIER_ELEMENTS
                    : join(BARRIER_ELEMENTS, MY_ELEMENTS, ENEMY_ELEMENTS);

            Elements[] target = new Elements[] {APPLE, GOLD, STONE, FURY_PILL, FLYING_PILL};

            Direction inverted = direction().inverted();
            actions.clear();
            for(Direction d: all) {
                if (d.equals(inverted))
                    continue;

                if (board.isAt(d.change(head()), barrier))
                    continue;

                actions.add(new ActionImpl(board, d, this, bfs, barrier, target));
            }
        }


        @Override
        public Direction direction() {
            return direction;
        }

        @Override
        public Point head() {
            return head;
        }

        @Override
        public List<Point> body() {
            return body;
        }

        @Override
        public int size() {
            return body.size();
        }

        @Override
        public boolean isFury() {
            return furyCounter > 0;
        }

        @Override
        public int fury() {
            return furyCounter;
        }

        void checkAndSetFury() {
            if (isFury()) {
                furyCounter--;
            } else {
                furyCounter = MAX_DURATION-1;
            }
        }

        void incFury() {
            furyCounter += MAX_DURATION;
        }

        @Override
        public boolean isFly() {
            return flyCounter > 0;
        }

        @Override
        public int fly() {
            return flyCounter;
        }

        void checkAndSetFly() {
            if (isFly()) {
                flyCounter--;
            } else {
                flyCounter = MAX_DURATION-1;
            }
        }

        void incFly() {
            flyCounter += MAX_DURATION;
        }

        @Override
        public List<Action> actions() {
            return actions;
        }

        @Override
        public String toString() {
            return direction + "[" + size() + "]" + pillsToString() + ", actions: " + actions();
        }

        String pillsToString() {
            return (isFury() ? ", fury" : "") +
                    (isFly() ? ", fly" : "");
        }
    }


    private static class EnemyImpl extends SnakeImpl implements Enemy {
    }


    private static class MeImpl extends SnakeImpl implements Me {
        public void stepTo(Board board, Direction next) {
            Point point = next.change(head());
            if (board.isAt(point, Elements.FURY_PILL)) {
                incFury();
            } else if (board.isAt(point, Elements.FLYING_PILL)) {
                incFly();
            }
        }

        @Override
        String pillsToString() {
            return (isFury() ? ", fury[" + fury() + "]" : "") +
                    (isFly() ? ", fly[" + fly()+ "]" : "");
        }
    }


    private static class ActionImpl implements Action {
        private Board board;
        private Direction direction;
        private Map<Point, Integer> items;

        public ActionImpl(Board board, Direction direction, Snake snake, BFS bfs, Elements[] barrier, Elements[] target) {
            this.board = board;
            this.direction = direction;
            items = bfs.bfs(snake, direction.change(snake.head()), barrier, target);
        }

        @Override
        public Direction direction() {
            return direction;
        }

        @Override
        public Map<Point, Integer> items(Elements... elements) {
            return items.entrySet().stream()
                    .filter(map -> board.isAt(map.getKey(), elements))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
        }

        @Override
        public String toString() {
            return direction + "[" + items.size() + ']';
        }
    }

    private static Field createGame(LevelImpl level) {
        return new SnakeBoard(level,null,
                new Timer(new com.codenjoy.dojo.services.settings.SimpleParameter<>(5)),
                new Timer(new com.codenjoy.dojo.services.settings.SimpleParameter<>(300)),
                new Timer(new com.codenjoy.dojo.services.settings.SimpleParameter<>(1)),
                new com.codenjoy.dojo.services.settings.SimpleParameter<>(1),
                new com.codenjoy.dojo.services.settings.SimpleParameter<>(10),
                new com.codenjoy.dojo.services.settings.SimpleParameter<>(10),
                new com.codenjoy.dojo.services.settings.SimpleParameter<>(3),
                new com.codenjoy.dojo.services.settings.SimpleParameter<>(40)
        );
    }
}
