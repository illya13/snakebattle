package com.github.illya13.snakebattle;


import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;


public class BasicTest {

    private Dice dice;
    private Bootstrap ai;

    @Before
    public void setup() {
        dice = Mockito.mock(Dice.class);
        Mockito.when(dice.next(Matchers.anyInt())).thenReturn(50);
        ai = new Bootstrap(dice);
    }

    private Board board(String board) {
        return (Board) new Board().forString(board);
    }

    @Test
    public void should1() {
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼       ×─☺►     ©      ○   ☼\n" +
                "☼#      ╘══╝                 ☼\n" +
                "☼☼      ☼#         ○         ☼\n" +
                "☼☼                      ○    ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼                ☼#         ☼\n" +
                "☼☼      ☼☼☼        ☼  ☼      ☼\n" +
                "☼#      ☼      ○   ☼  ☼      ☼\n" +
                "☼☼      ☼○         ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼               ●  ☼\n" +
                "☼#              ☼#         ○ ☼\n" +
                "☼☼                          $☼\n" +
                "☼☼    ●  ○           ☼       ☼\n" +
                "☼#                           ☼\n" +
                "☼☼        ○                  ☼\n" +
                "☼☼                 ☼#        ☼\n" +
                "☼#       ☼☼ ☼                ☼\n" +
                "☼☼          ☼     ●     ○    ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#○         ☼               ○☼\n" +
                "☼☼         ☼#                ☼\n" +
                "☼☼           ○               ☼\n" +
                "☼#                  ☼☼☼      ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼      ○        ☼☼☼#        ☼\n" +
                "☼#○                          ☼\n" +
                "☼☼               ○           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", Direction.RIGHT);
    }

    private void assertAI(String board, Direction expected) {
        String actual = ai.get(board(board));
        Assert.assertEquals(expected.toString(), actual);
    }
}
