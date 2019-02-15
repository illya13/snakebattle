package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.client.WebSocketRunner;



import java.util.*;

import static com.codenjoy.dojo.snakebattle.model.Elements.*;
import static com.github.illya13.snakebattle.Board.*;

public class Solver implements com.codenjoy.dojo.client.Solver<com.codenjoy.dojo.snakebattle.client.Board> {
    static final String BASE_URL = "https://game3.epam-bot-challenge.com.ua/codenjoy-contest/board/player/";
    static final String PLAYER_CODE = "?code=3406317695233273382";
    static final String PLAYER_HASH = "iu0fzz81lugtq1z8ceet";

    Solver(Dice dice) {
    }

    @Override
    public String get(com.codenjoy.dojo.snakebattle.client.Board board) {
        return "UP";
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                BASE_URL + PLAYER_HASH + PLAYER_CODE,
                new Solver(new RandomDice()),
                new Board());
    }
}
