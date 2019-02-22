package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.github.illya13.snakebattle.solver.BFSSolver;
import com.github.illya13.snakebattle.state.StateImpl;

public class Bootstrap implements com.codenjoy.dojo.client.Solver<Board> {
    static final String BASE_URL = "https://game3.epam-bot-challenge.com.ua/codenjoy-contest/board/player/";
    static final String PLAYER_CODE = "?code=4006943722048935230";
    static final String PLAYER_HASH = "mi95vlm7roqt46jc3vgc";

    StateImpl state;
    boolean initialized;
    Solver solver = new BFSSolver();

    Bootstrap(Dice dice) {
        state = new StateImpl();
        initialized = false;
    }

    @Override
    public String get(Board board) {
        if (board.isGameOver()) return "";

        if (board.isGameStart()) {
            initialized = false;
            return "";
        }

        if (!initialized) {
            initialized = true;
            state.reset();
        }
        state.initStep(board);
        System.out.println(state.toString());

        Direction next = solver.next(state);
        state.stepTo(next);

        return next.toString();
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                BASE_URL + PLAYER_HASH + PLAYER_CODE,
                new Bootstrap(new RandomDice()),
                new Board());
    }
}
