package com.github.illya13.snakebattle.state;

import com.codenjoy.dojo.services.Direction;
import com.github.illya13.snakebattle.board.Board;
import com.github.illya13.snakebattle.Observer;
import com.github.illya13.snakebattle.State;

public class ObserverImpl implements Observer {
    Board prev;
    StateImpl state;

    @Override
    public State init(Board board) {
        state = StateImpl.fromBoard(board);

        state.update();

        prev = copy(board);
        return state;
    }

    @Override
    public State update(Board board, Direction from) {
        state = StateImpl.fromState(state, from, board);

        state.stepFrom(prev);
        state.update();

        prev = copy(board);
        return state;
    }

    private Board copy(Board board) {
        return (Board) new Board().forString(board.boardAsString());
    }
}
