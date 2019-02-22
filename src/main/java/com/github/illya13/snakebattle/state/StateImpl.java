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

import java.util.*;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.Board.*;


public class StateImpl implements State {
    Board board;
    LevelImpl level;
    Field field;
    BFS bfs;

    int step;

    MeImpl me;
    Map<Point, Enemy> enemies;

    private StateImpl() {}

    private StateImpl(Board board) {
        this.board = board;

        level = new LevelImpl(board.boardAsString().replaceAll("\n", ""));
        field = createGame(level);

        bfs = new BFS(board);

        me = new MeImpl(board.getMe());

        enemies = new HashMap<>();
        for(Point point: board.getEnemies()) {
            EnemyImpl enemy = new EnemyImpl(point);
            enemies.put(enemy.head(), enemy);
        }
    }

    public static StateImpl fromBoard(Board board) {
        StateImpl state = new StateImpl(board);
        state.step = 1;
        return state;
    }

    public static StateImpl fromState(StateImpl old, Direction from, Board board) {
        StateImpl state = new StateImpl(board);
        state.step = old.step+1;

        state.me.copyFrom(old.me);
        state.me.direction = from;
        for (Enemy enemy : state.enemies()) {
            copyFrom((EnemyImpl)enemy, old.enemies);
        }
        return state;
    }

    private static void copyFrom(EnemyImpl enemy, Map<Point, Enemy> enemies) {
        EnemyImpl old = (EnemyImpl) enemies.get(enemy.direction().inverted().change(enemy.head()));
        enemy.copyFrom(old);
    }

    public void update() {
        me.update();
        for (Enemy enemy : enemies()) {
            ((EnemyImpl)enemy).update();
        }
    }

    public void stepFrom(Board prev) {
        me.stepFrom(prev);
        for (Enemy enemy : enemies()) {
            ((EnemyImpl)enemy).stepFrom(prev);
        }
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
    public Collection<Enemy> enemies() {
        return enemies.values();
    }

    @Override
    public String toString() {
        return "[" + step + "]\tme: " + me + "\n\tenemies: " + enemies;
    }


    private class SnakeImpl implements Snake {
        private static final int MAX_DURATION = 10;

        Direction direction;
        Point head;
        LinkedList<Point> body;

        int furyCounter;
        int flyCounter;
        int reward;

        private List<Action> actions;

        private SnakeImpl() {
            body = new LinkedList<>();
            actions = new LinkedList<>();

            furyCounter = 0;
            flyCounter = 0;
            reward = 0;
        }

        public SnakeImpl(Point head) {
            this();

            initSnake(head, level, field, this);
            initActions();
        }

        public void copyFrom(SnakeImpl other) {
            furyCounter = other.furyCounter;
            flyCounter = other.flyCounter;

            reward = other.reward;
        }

        public void update() {
            checkAndDecPills();
        }

        public void stepFrom(Board prev) {
            if (prev.isAt(head(), Elements.FURY_PILL)) {
                incFury();
            } else if (prev.isAt(head(), Elements.FLYING_PILL)) {
                incFly();
            }
            updateReward(prev);
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
        public Collection<Point> body() {
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

        @Override
        public boolean isFly() {
            return flyCounter > 0;
        }

        @Override
        public int fly() {
            return flyCounter;
        }

        void incFly() {
            flyCounter += MAX_DURATION;
        }

        @Override
        public Collection<Action> actions() {
            return actions;
        }

        @Override
        public int reward() {
            return reward;
        }

        @Override
        public String toString() {
            return "{" + reward + "} " + direction + "[" + size() + "]" + pillsToString();
        }

        private void initActions() {
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

                actions.add(new ActionImpl(d,this, barrier, target));
            }
        }

        private void updateReward(Board prev) {
            int dx = detectReward(prev);
            if (dx > 0) {
                reward += dx;
                System.out.println("+" + dx);
            }
        }

        private int detectReward(Board prev) {
            int result = 0;
            if (prev.isAt(head(), APPLE)) {
                result += 1;
            }
            if (prev.isAt(head(), GOLD)) {
                result += 10;
            }
            if (prev.isAt(head(), STONE)) {
                result += 5;
            }
            if (board.isAt(head(), ENEMY_HEAD_DEAD)) {
                result += 10 * deadSize(head(), board);
            }
            if (!isFly() && prev.isAt(head(), ENEMY_ELEMENTS)) {
                result += 10 * eatSize(head(), direction().inverted().change(head()), prev);
            }
            if (endOfRoundWin()) {
                result += 50;
            }
            return result;
        }

        private boolean endOfRoundWin() {
            if (enemies().isEmpty() && board.get(ENEMY_HEAD_DEAD).isEmpty())
                return true;

            int max = 0;
            for(Enemy enemy: enemies()) {
                if (enemy.size() > max) {
                    max = enemy.size();
                }
            }
            return (step() == 300) && (size() > max);
        }

        private void checkAndDecPills() {
            if (isFury()) furyCounter--;
            if (isFly()) flyCounter--;
        }

        private void incFury() {
            furyCounter += MAX_DURATION;
        }

        private String pillsToString() {
            return (isFury() ? ", fury[" + fury() + "]" : "") +
                    (isFly() ? ", fly[" + fly()+ "]" : "");
        }
    }


    private class EnemyImpl extends SnakeImpl implements Enemy {
        public EnemyImpl(Point head) {
            super(head);
        }
    }


    private class MeImpl extends SnakeImpl implements Me {
        public MeImpl(Point head) {
            super(head);
        }
    }


    private class ActionImpl implements Action {
        private Direction direction;
        private Map<Point, Integer> items;

        public ActionImpl(Direction direction, Snake snake, Elements[] barrier, Elements[] target) {
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

    // HELPERS

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

    private SnakeImpl newSnake_() {
        return new SnakeImpl();
    }

    private static SnakeImpl newSnake() {
        return new StateImpl().newSnake_();
    }

    private static void initSnake(Point head, LevelImpl level, Field field, SnakeImpl snake) {
        snake.head = head;
        try {
            Hero hero = (Hero) parseSnake.invoke(level, head, field);
            snake.direction = (Direction) getDirection.invoke(hero);

            for (Tail tail : hero.body()) {
                snake.body.addFirst(tail);
            }

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    private static void initPills(Board prev, SnakeImpl snake) {
        snake.furyCounter = (prev.isAt(snake.head(), HEAD_EVIL, ENEMY_HEAD_EVIL)) ? 2 : 0;
        snake.flyCounter = (prev.isAt(snake.head(), HEAD_FLY, ENEMY_HEAD_FLY)) ? 2 : 0;
    }

    private static int eatSize(Point target, Point winner, Board prev) {
        LevelImpl oldLevel = new LevelImpl(prev.boardAsString().replaceAll("\n", ""));
        Field oldField = createGame(oldLevel);

        List<SnakeImpl> all = new LinkedList<>();
        if (!winner.equals(prev.getMe())) {
            SnakeImpl snake = newSnake();
            initSnake(prev.getMe(), oldLevel, oldField, snake);
            initPills(prev, snake);
            all.add(snake);
        }

        for(Point p: prev.getEnemies()) {
            if (!winner.equals(p)) {
                SnakeImpl snake = newSnake();
                initSnake(p, oldLevel, oldField, snake);
                initPills(prev, snake);
                all.add(snake);
            }
        }

        for (SnakeImpl snake: all) {
            if (snake.isFly())
                continue;

            int i = 0;
            for (Point p : snake.body()) {
                if (p.equals(target)) {
                    return snake.size() - i;
                }
                i++;
            }
        }

        return 0;
    }

    private static int deadSize(Point target, Board board) {
        LevelImpl oldLevel = new LevelImpl(board.boardAsString().replaceAll("\n", ""));
        Field oldField = createGame(oldLevel);

        for (Point point: board.get(Elements.ENEMY_HEAD_DEAD)) {
            SnakeImpl snake = newSnake();
            initSnake(point, oldLevel, oldField, snake);
            if (target.equals(snake.head())) {
                return snake.size();
            }
        }
        return 0;
    }
}
