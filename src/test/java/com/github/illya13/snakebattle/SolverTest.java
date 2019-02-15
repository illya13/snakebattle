package com.github.illya13.snakebattle;


import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.HashSet;


public class SolverTest {

    private Dice dice;
    private Solver ai;

    @Before
    public void setup() {
        dice = Mockito.mock(Dice.class);
        Mockito.when(dice.next(Matchers.anyInt())).thenReturn(50);
        ai = new Solver(dice);
    }

    private Board board(String board) {
        return (Board) new Board().forString(board);
    }

    @Test
    public void should1() {
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                           ☼\n" +
                "☼#                      $    ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼     ☼☼☼☼☼            ○    ☼\n" +
                "☼☼     ☼                     ☼\n" +
                "☼#     ☼☼☼     ○  ☼☼☼☼#      ☼\n" +
                "☼☼     ☼          ☼   ☼      ☼\n" +
                "☼☼     ☼☼☼*ø      ☼☼☼*ø      ☼\n" +
                "☼☼                ☼          ☼\n" +
                "☼☼                ☼          ☼\n" +
                "☼☼                           ☼\n" +
                "☼#                           ☼\n" +
                "☼☼              ┌───ö        ☼\n" +
                "☼☼        ☼☼☼   ˅            ☼\n" +
                "☼☼       ☼ ○☼                ☼\n" +
                "☼☼      ☼☼☼☼# ●   ☼☼   ☼#    ☼\n" +
                "☼☼      ☼   ☼   ●●☼ ☼ ☼○☼    ☼\n" +
                "*ø      ☼   ☼     ☼  ☼▲ ☼    ☼\n" +
                "☼☼                ☼   ║ ☼    ☼\n" +
                "☼☼                ☼   ║ ☼   ○☼\n" +
                "☼☼             ●      ║    ● ☼\n" +
                "☼☼                    ╙      ☼\n" +
                "☼☼       ●              ●    ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.RIGHT);
    }

    private void assertAI(String board, Direction expected) {
        String actual = ai.get(board(board));
        Assert.assertEquals(expected.toString(), actual);
    }
}
