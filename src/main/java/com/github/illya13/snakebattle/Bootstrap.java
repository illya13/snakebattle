package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.github.illya13.snakebattle.solver.BFSSolver;
import com.github.illya13.snakebattle.state.ObserverImpl;

public class Bootstrap implements com.codenjoy.dojo.client.Solver<Board> {
    static final String BASE_URL = "http://127.0.0.1:8080/codenjoy-contest/board/player/";
    static final String PLAYER_CODE = "?code=3607711244046729292";
    static final String PLAYER_HASH = "qk03ekf3sirb5dnhuoha";

    boolean initialized;
    Observer observer;
    Solver solver;
    Direction direction;
    State state;

    Bootstrap(Dice dice) {
        observer = new ObserverImpl();
        solver = new BFSSolver();
        initialized = false;
    }

    @Override
    public String get(Board board) {
        if (board.isGameOver()) return "";

        if (board.isGameStart()) {
            initialized = false;
            return "";
        }

        state = (!initialized) ? observer.init(board) : observer.update(board, direction);
        System.out.println(state.toString());

        direction = solver.next(state);

        if (!initialized) initialized = true;
        return direction.toString();
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                BASE_URL + PLAYER_HASH + PLAYER_CODE,
                new Bootstrap(new RandomDice()),
                new Board());
    }
}
