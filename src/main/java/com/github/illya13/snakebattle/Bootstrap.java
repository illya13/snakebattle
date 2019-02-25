package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.github.illya13.snakebattle.board.Board;
import com.github.illya13.snakebattle.solver.BFSSolver;
import com.github.illya13.snakebattle.state.ObserverImpl;

import java.io.PrintStream;

public class Bootstrap implements com.codenjoy.dojo.client.Solver<Board> {
    static final String BASE_URL = "http://127.0.0.1:8080/codenjoy-contest/board/player/";
    static final String PLAYER_CODE = "285147973966974500";
    static final String PLAYER_HASH = "keme1dgf50kkvavrwzln";

    int total;
    boolean initialized;
    Observer observer;
    Solver solver;
    Direction direction;
    State state;

    Bootstrap(Dice dice) {
        observer = new ObserverImpl();
        solver = new BFSSolver();
        total = 0;
    }

    @Override
    public String get(Board board) {
        if (board.isGameOver()) {
            total += (state != null) ? state.me().reward() : 0;
            System.out.println("TOTAL: " + total);
            return "";
        }

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

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));

        String hash = PLAYER_HASH;
        String code = PLAYER_CODE;

        if (args.length > 0) {
            hash = args[0];
        }
        if (args.length > 1) {
            code = args[1];
        }

        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                BASE_URL + hash + "?code=" + code,
                new Bootstrap(new RandomDice()),
                new Board());
    }
}
