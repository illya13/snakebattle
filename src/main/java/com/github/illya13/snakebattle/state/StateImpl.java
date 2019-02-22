package com.github.illya13.snakebattle.state;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import com.codenjoy.dojo.snakebattle.model.Elements;

import com.github.illya13.snakebattle.Board;
import com.github.illya13.snakebattle.State;

import java.util.*;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.Board.*;


public class StateImpl implements State {
    Board board;
    BFS bfs;

    int step;

    MeImpl me;
    Map<Point, Enemy> enemies;

    private StateImpl(Board board) {
        this.board = board;

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
        EnemyImpl old = (EnemyImpl) enemies.remove(enemy.direction().inverted().change(enemy.head()));
        if (old != null) {
            enemy.copyFrom(old);
            return;
        }

        for(Direction direction: all) {
            old = (EnemyImpl) enemies.remove(direction.change(enemy.head()));
            if (old != null) {
                enemy.copyFrom(old);
                return;
            }
        }
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
    public List<Enemy> enemies() {
        return new LinkedList<>(enemies.values());
    }

    private List<Snake> snakes() {
        List<Snake> all = new LinkedList<>();
        all.add(me());
        all.addAll(enemies());
        return all;
    }

    @Override
    public String toString() {
        return "[" + step + "]\tme: " + me + "\n\tenemies: " + enemies;
    }


    private abstract class SnakeImpl implements Snake {
        private static final int MAX_DURATION = 10;

        Direction direction;
        Point head;
        LinkedList<Point> body;

        int furyCounter;
        int flyCounter;
        int reward;

        private List<Action> actions;

        SnakeImpl() {
            body = new LinkedList<>();
            actions = new LinkedList<>();

            furyCounter = 0;
            flyCounter = 0;
            reward = 0;
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
        public List<Action> actions() {
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

        void initActions() {
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
            // seems only 1 possible reward
            if (endOfRoundWin()) {
                return 50;
            }

            int result = 0;
            if (prev.isAt(head(), APPLE)) {
                result += 1;
            }
            if (prev.isAt(head(), GOLD)) {
                result += 10;
            }
            if (!isFly() && prev.isAt(head(), STONE)) {
                result += 5;
            }
            if (!isFly() && prev.isAt(head(), join(MY_ELEMENTS, ENEMY_ELEMENTS))) {
                result += 10 * eatSize(head(), direction().inverted().change(head()), prev);
            }
            if (!board.get(Elements.ENEMY_HEAD_DEAD).isEmpty()) {
                result += 10 * deadSize(this, board);
            }
            return result;
        }

        private boolean endOfRoundWin() {
            if (enemies().isEmpty() && board.get(ENEMY_HEAD_DEAD).isEmpty())
                return true;

            int max = 0;
            for(Snake snake: snakes()) {
                if (snake.head().equals(head()))
                    continue;

                if (snake.size() > max) {
                    max = snake.size();
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
        EnemyImpl() {}

        EnemyImpl(Point head) {
            super();

            initEnemy(head, board, this);
            initActions();
        }
    }


    private class MeImpl extends SnakeImpl implements Me {
        MeImpl() {}

        MeImpl(Point head) {
            super();

            initMe(head, board, this);
            initActions();
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

    private StateImpl() {}

    private EnemyImpl newEnemy_() {
        return new EnemyImpl();
    }

    private static EnemyImpl newEnemy() {
        return new StateImpl().newEnemy_();
    }

    private MeImpl newMe_() {
        return new MeImpl();
    }

    private static MeImpl newMe() {
        return new StateImpl().newMe_();
    }

    private static void initMe(Point head, Board aBoard, MeImpl snake) {
        Parser.ParsedSnake parsed = new Parser(aBoard).parseSnake(head);

        snake.head = parsed.head();
        snake.direction = parsed.direction();
        snake.body = parsed.body();
    }

    private static void initEnemy(Point head, Board aBoard, EnemyImpl snake) {
        Parser.ParsedSnake parsed = new Parser(aBoard).parseEnemy(head);

        snake.head = parsed.head();
        snake.direction = parsed.direction();
        snake.body = parsed.body();
    }

    private static void initPills(Board prev, SnakeImpl snake) {
        snake.furyCounter = (prev.isAt(snake.head(), HEAD_EVIL, ENEMY_HEAD_EVIL)) ? 2 : 0;
        snake.flyCounter = (prev.isAt(snake.head(), HEAD_FLY, ENEMY_HEAD_FLY)) ? 2 : 0;
    }

    private static int eatSize(Point target, Point winner, Board prev) {
        List<Snake> snakes = boardSnakes(prev);

        for (Snake snake: snakes) {
            if (snake.isFly() || winner.equals(snake.head()))
                continue;

            int i = inSnake(target, snake);
            if (i != -1) return snake.size() - i;
        }

        return 0;
    }

    private static Integer inSnake(Point target, Snake snake) {
        int i = 0;
        for (Point p : snake.body()) {
            if (p.equals(target)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private static int deadSize(Snake alive, Board board) {
        for (Point point: board.get(Elements.ENEMY_HEAD_DEAD)) {
            EnemyImpl dead = newEnemy();
            initEnemy(point, board, dead);

            int i = inSnake(dead.head(), alive);
            if (i == -1) continue;

            return dead.size();
        }
        return 0;
    }

    private static List<Snake> boardSnakes(Board board) {
        List<Snake> snakes = new LinkedList<>();
        MeImpl me = newMe();
        initMe(board.getMe(), board, me);
        initPills(board, me);
        snakes.add(me);

        for(Point p: board.getEnemies()) {
            EnemyImpl enemy = newEnemy();
            initEnemy(p, board, enemy);
            initPills(board, enemy);
            snakes.add(enemy);
        }
        return snakes;
    }
}
