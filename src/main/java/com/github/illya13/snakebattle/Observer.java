package com.github.illya13.snakebattle;

import com.codenjoy.dojo.services.Direction;
import com.github.illya13.snakebattle.board.Board;

public interface Observer {
    State init(Board board);
    State update(Board board, Direction from);
}
