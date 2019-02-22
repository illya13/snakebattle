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
    LevelImpl level;
    BFS bfs;

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
        level = new LevelImpl(board.boardAsString().replaceAll("\n", ""));
        bfs = new BFS(board);

        step++;
        me.initStep(board.getMe());

        enemies.clear();
        for(Point point: board.getEnemies()) {
            EnemyImpl enemy = new EnemyImpl();
            enemy.reset();
            enemy.initStep(point);
            enemies.add(enemy);
        }
    }

    public void stepTo(Direction direction) {
        me.stepTo(direction);
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


    private class SnakeImpl implements Snake {
        private static final int MAX_DURATION = 10;

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

        public void initStep(Point head) {
            initSnake(head);
            initActions();
        }

        private void initSnake(Point head) {
            Field field = createGame(level);

            this.head = head;
            try {
                Hero hero = (Hero) parseSnake.invoke(level, head, field);
                direction = (Direction) getDirection.invoke(hero);
                if (hero.isFury() || hero.isFlying()) {
                    checkAndDecPills();
                }

                body.clear();
                for (Tail tail : hero.body()) {
                    body.addFirst(tail);
                }

            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
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

        void checkAndDecPills() {
            if (isFury()) furyCounter--;
            if (isFly()) flyCounter--;
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


    private class EnemyImpl extends SnakeImpl implements Enemy {
        public void initStep(Point head) {
            if (board.isAt(head, ENEMY_HEAD_EVIL)) {
                incFury();
            } else if (board.isAt(head, ENEMY_HEAD_FLY)) {
                incFly();
            }
            super.initStep(head);
        }
    }


    private class MeImpl extends SnakeImpl implements Me {
        int reward;

        public void reset() {
            super.reset();
            reward = 0;
        }

        public void initStep(Point head) {
            super.initStep(head);
            handleCurrentReward();
        }

        public void stepTo(Direction next) {
            Point point = next.change(head());
            updatePills(point);
            handleFutureReward(point);
        }

        private void updatePills(Point point) {
            if (board.isAt(point, Elements.FURY_PILL)) {
                incFury();
            } else if (board.isAt(point, Elements.FLYING_PILL)) {
                incFly();
            }
        }

        private void handleCurrentReward() {
            int dx = getCurrentReward();
            if (dx > 0) {
                reward += dx;
                System.out.println("+" + dx);
            }
        }

        private int getCurrentReward() {
            for (Point point: board.get(Elements.ENEMY_HEAD_DEAD)) {
                EnemyImpl enemy = new EnemyImpl();
                enemy.reset();
                enemy.initStep(point);

                for (Point p: me.body()) {
                    if (p.equals(enemy.head())) {
                        return enemy.size();
                    }
                }
            }
            return 0;
        }

        private void handleFutureReward(Point point) {
            int dx = getFutureReward(point);
            if (dx > 0) {
                reward += dx;
                System.out.println("+" + dx);
            }
        }

        private int getFutureReward(Point point) {
            if (endOfRoundWin()) {
                return 50;
            } else if (board.isAt(point, APPLE)) {
                return 1;
            } else if (board.isAt(point, GOLD)) {
                return 10;
            } else if (board.isAt(point, STONE) && size() >= 5) {
                return 5;
            } else if (board.isAt(point, ENEMY_ELEMENTS)) {
                for (Enemy enemy: enemies()) {
                    int i = 0;
                    for (Point p: enemy.body()) {
                        if (p.equals(point)) {
                            if (!flyCase(enemy) && (fightVictory(enemy, i) || eatEnemy(enemy, i))) {
                                return (enemy.size()-i) * 10;
                            }
                            return 0;
                        }
                        i++;
                    }
                }
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

        private boolean flyCase(Enemy enemy) {
            return isFly() || enemy.isFly();
        }

        private boolean fightVictory(Enemy enemy, int i) {
            return ((isFury() && enemy.isFury()) || (!isFury() && !enemy.isFury())) && (size() - enemy.size() > 1) && (i == 0);
        }

        private boolean eatEnemy(Enemy enemy, int i) {
            return isFury() && !enemy.isFury();
        }

        @Override
        public int reward() {
            return reward;
        }

        @Override
        String pillsToString() {
            return (isFury() ? ", fury[" + fury() + "]" : "") +
                    (isFly() ? ", fly[" + fly()+ "]" : "");
        }

        @Override
        public String toString() {
            return "{" + reward + "} " + super.toString();
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
}
