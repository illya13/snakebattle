package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.Direction;

public interface Solver {
    void init();
    Direction next(State state);
    void done(int reward);

    String status();
    void shutdown();
}
