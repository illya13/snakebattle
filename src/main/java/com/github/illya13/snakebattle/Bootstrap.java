package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.github.illya13.snakebattle.board.Board;
import com.github.illya13.snakebattle.solver.BFSSolver;
import com.github.illya13.snakebattle.solver.GAEngine;
import com.github.illya13.snakebattle.solver.GASolver;
import com.github.illya13.snakebattle.state.ObserverImpl;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        final String solverName = (args.length > 0) ? args[0] : "";
        final List<String> hash = new LinkedList<>();
        final List<String> code = new LinkedList<>();
        final List<String> filename = new LinkedList<>();

        int i = 0;
        do {
            hash.add((args.length > i + 1) ? args[i+1] : PLAYER_HASH);
            code.add((args.length > i + 2) ? args[i+2] : PLAYER_CODE);
            filename.add((args.length > i + 3) ? args[i+3] : "./stats.json");
            i  += (i==0) ? 3 : 2;
        } while (i+3 < args.length);

        run(solverName, hash, code, filename);
    }

    private static CountDownLatch latch;
    private static void run(final String solverName, final List<String> hash, final List<String> code, final List<String> filename) {
        latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(4);

        GAEngine gaEngine = new GAEngine();

        try {
            for (int i=0; i<hash.size(); i++) {
                Solver solver;
                switch (solverName) {
                    case "BFS":
                        solver = new BFSSolver();
                        break;

                    case "GA":
                        solver = new GASolver(gaEngine);
                        break;

                    default:
                        solver = new BFSSolver();
                        break;
                }
                runOne(executor, solver, hash.get(i), code.get(i), filename.get(i));
            }

            latch.await();
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void runOne(final ExecutorService executor, final Solver solver, final String hash, final String code, final String filename) {
        executor.submit(() -> {
            WebSocketRunner.runClient(BASE_URL + hash + "?code=" + code,
                    new Bootstrap(solver, filename),
                    new Board());
        });
    }
}
