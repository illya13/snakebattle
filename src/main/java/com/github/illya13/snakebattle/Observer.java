package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.Direction;

public interface Observer {
    State init(Board board);
    State update(Board board, Direction from);
}
