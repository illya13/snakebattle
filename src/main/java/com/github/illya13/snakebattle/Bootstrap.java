package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.github.illya13.snakebattle.board.Board;
import com.github.illya13.snakebattle.solver.BFSSolver;
import com.github.illya13.snakebattle.solver.GASolver;
import com.github.illya13.snakebattle.state.ObserverImpl;

import java.io.PrintStream;

public class Bootstrap implements com.codenjoy.dojo.client.Solver<Board> {
    static final String BASE_URL = "http://127.0.0.1:8080/codenjoy-contest/board/player/";
    static final String PLAYER_CODE = "3485839216718225428";
    static final String PLAYER_HASH = "6ejguzn33aqhhawzdyao";

    boolean initialized;
    Observer observer;
    Solver solver;
    Direction direction;
    State state;
    Statistics statistics;

    Bootstrap(Solver solver, String filename) {
        observer = new ObserverImpl();
        this.solver = solver;

        if (filename != null)
            statistics = new Statistics(filename);
    }

    @Override
    public String get(Board board) {
        long ts = System.currentTimeMillis();

        if (board.isGameOver()) {
            if (state != null && statistics != null) {
                statistics.update(state.me().reward(), solver.status());
                solver.done(state.me().reward());
            }
            return "";
        }

        if (board.isGameStart()) {
            initialized = false;
            return "";
        }

        state = (!initialized) ? observer.init(board) : observer.update(board, direction);
        if (!initialized) {
            solver.init();
            initialized = true;
        }

        System.out.println(state.toString());
        direction = solver.next(state);

        System.out.printf("latency: %d ms\n", System.currentTimeMillis() - ts);
        return direction.toString();
    }

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));

        String solverName = "";
        String hash = PLAYER_HASH;
        String code = PLAYER_CODE;
        String filename = "./stats.json";

        if (args.length > 0) {
            solverName = args[0];
        }
        if (args.length > 1) {
            hash = args[1];
        }
        if (args.length > 2) {
            code = args[2];
        }
        if (args.length > 3) {
            filename = args[3];
        }

        Solver solver;
        switch (solverName) {
            case "BFS":
                solver = new BFSSolver();
                break;

            case "GA":
                solver = new GASolver();
                break;

            default:
                solver = new BFSSolver();
                break;
        }

        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                BASE_URL + hash + "?code=" + code,
                new Bootstrap(solver, filename),
                new Board());
    }
}
