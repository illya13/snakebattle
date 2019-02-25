package com.github.illya13.snakebattle.state;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import com.codenjoy.dojo.snakebattle.model.Elements;

import com.github.illya13.snakebattle.board.Board;
import com.github.illya13.snakebattle.State;
import com.github.illya13.snakebattle.board.Parser;

import java.util.*;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.board.Board.*;


public class StateImpl implements State {
    private Board board;

    private int step;

    private MeImpl me;
    private Map<Point, Enemy> enemies;

    private StateImpl(Board board) {
        this.board = board;

        me = new MeImpl(board.getMe());

        enemies = new HashMap<>();
        for(Parser.ParsedSnake snake: board.getEnemies()) {
            enemies.put(snake.head(), new EnemyImpl(snake));
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
        state.me.overwriteDirection(from);
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


    private abstract class SnakeImpl extends Parser.ParsedSnake implements Snake {
        private static final int MAX_DURATION = 10;

        int furyCounter;
        int flyCounter;
        int reward;

        private SnakeImpl(Parser.ParsedSnake other) {
            super(other);

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
        public int reward() {
            return reward;
        }

        @Override
        public String toString() {
            return "{" + reward + "} " + direction() + "[" + size() + "]" + pillsToString();
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
                result += 10 * prev.eatSize(head(), direction().inverted().change(head()));
            }
            if (!board.get(Elements.ENEMY_HEAD_DEAD).isEmpty()) {
                result += 10 * board.deadSize(this);
            }
            return result;
        }

        private boolean endOfRoundWin() {
            return board.isLastStep() ||
                    (step() == 300) && (size() > board.maxOtherSnakeSize(this));
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
        EnemyImpl(Parser.ParsedSnake other) {
            super(other);
        }
    }


    private class MeImpl extends SnakeImpl implements Me {
        MeImpl(Parser.ParsedSnake other) {
            super(other);
        }

        public void overwriteDirection(Direction direction) {
            this.direction = direction;
        }
    }
}
