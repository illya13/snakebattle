package com.github.illya13.snakebattle.state;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import com.codenjoy.dojo.snakebattle.model.Elements;
import com.codenjoy.dojo.snakebattle.model.board.Field;
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
    BFS bfs;

    int step;

    MeImpl me;
    Map<Point, Enemy> enemies;

    private StateImpl() {}

    private StateImpl(Board board) {
        this.board = board;

        level = new LevelImpl(board.boardAsString().replaceAll("\n", ""));
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

            initSnake(head, level, this);
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
            // updateReward(prev);
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
            if (endOfRoundWin()) {
                return 50;
            } else if (prev.isAt(head(), APPLE)) {
                return 1;
            } else if (prev.isAt(head(), GOLD)) {
                return 10;
            } else if (prev.isAt(head(), STONE)) {
                return 5;
            } else if (prev.isAt(head(), ENEMY_ELEMENTS)) {
                return 10 * oppositeSize(direction().inverted().change(head()), prev);
                // find snake size on old board
/*
                for (Enemy enemy: enemies()) {
                    int i = 0;
                    for (Point p : enemy.body()) {
                        if (p.equals(point)) {
                            if (!flyCase(enemy) && (fightVictory(enemy, i) || eatEnemy(enemy, i))) {
                                return (enemy.size() - i) * 10;
                            }
                            return 0;
                        }
                        i++;
                    }
                }
*/
            }
            return 0;
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

/*


        private void handleCurrentReward() {
            int dx = getCurrentReward();
            if (dx > 0) {
                reward += dx;
                System.out.println("+" + dx);
            }
        }
*/

/*
        private int getCurrentReward() {
            for (Point point: board.get(Elements.ENEMY_HEAD_DEAD)) {
                EnemyImpl enemy = new EnemyImpl();
                enemy.reset();
                enemy.init(point);

                for (Point p: me.body()) {
                    if (p.equals(enemy.head())) {
                        return enemy.size();
                    }
                }
            }
            return 0;
        }
*/

/*

*/

/*

        private boolean flyCase(Enemy enemy) {
            return isFly() || enemy.isFly();
        }

        private boolean fightVictory(Enemy enemy, int i) {
            return ((isFury() && enemy.isFury()) || (!isFury() && !enemy.isFury())) && (size() - enemy.size() > 1) && (i == 0);
        }

        private boolean eatEnemy(Enemy enemy, int i) {
            return isFury() && !enemy.isFury();
        }
*/

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

    private SnakeImpl newSnake_() {
        return new SnakeImpl();
    }

    private static SnakeImpl newSnake() {
        return new StateImpl().newSnake_();
    }

    private static void initSnake(Point head, LevelImpl level, SnakeImpl snake) {
        snake.head = head;
        try {
            Hero hero = (Hero) parseSnake.invoke(level, head, null);
            snake.direction = (Direction) getDirection.invoke(hero);

            for (Tail tail : hero.body()) {
                snake.body.addFirst(tail);
            }

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    private static int oppositeSize(Point winner, Board prev) {
        LevelImpl oldLevel = new LevelImpl(prev.boardAsString().replaceAll("\n", ""));

        List<SnakeImpl> all = new LinkedList<>();

        SnakeImpl snake = newSnake();
        initSnake(prev.getMe(), oldLevel, snake);
        all.add(snake);

        for(Point p: prev.getEnemies()) {
            snake = newSnake();
            initSnake(p, oldLevel, snake);
            all.add(snake);
        }
        return 0;
    }
}
