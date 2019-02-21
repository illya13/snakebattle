package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.Direction;

public interface Solver {
    Direction next(State state);
}
