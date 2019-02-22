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
    public void rewardTest() {
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                         ○ ☼\n" +
                "☼#                           ☼\n" +
                "☼☼      ☼#              ○    ☼\n" +
                "☼☼                       ○   ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼             $  ☼#         ☼\n" +
                "☼☼●     ☼☼☼  ○ ®   ☼  ☼      ☼\n" +
                "☼#      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼               ●  ☼\n" +
                "☼#              ☼#   ● ○     ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼    ●      ○       ☼       ☼\n" +
                "☼#             ○         ○   ☼\n" +
                "☼☼○                       ●  ☼\n" +
                "☼☼       ®         ☼#       ╓☼\n" +
                "☼#   ○   ☼☼ ☼               ║☼\n" +
                "☼☼      ○   ☼     ●         ║☼\n" +
                "☼☼    ©  ☼☼ ☼               ║☼\n" +
                "☼#          ☼               ║☼\n" +
                "☼☼         ☼#            ╔══╝☼\n" +
                "☼☼                 ┌>○◄╗ ║   ☼\n" +
                "☼#                 │☼☼☼║ ║   ☼\n" +
                "☼☼                 └─┐ ║ ║   ☼\n" +
                "☼☼               ☼☼☼#│ ╚═╝   ☼\n" +
                "☼#               ×───┘       ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.LEFT);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                         ○ ☼\n" +
                "☼#                           ☼\n" +
                "☼☼      ☼#              ○    ☼\n" +
                "☼☼                       ○   ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼             $  ☼#         ☼\n" +
                "☼☼●     ☼☼☼  ○ ®   ☼  ☼      ☼\n" +
                "☼#      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼               ●  ☼\n" +
                "☼#              ☼#   ● ○     ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼    ●      ○       ☼       ☼\n" +
                "☼#             ○         ○ $ ☼\n" +
                "☼☼○                       ●  ☼\n" +
                "☼☼       ®         ☼#       ╓☼\n" +
                "☼#   ○   ☼☼ ☼               ║☼\n" +
                "☼☼      ○   ☼     ●         ║☼\n" +
                "☼☼    ©  ☼☼ ☼               ║☼\n" +
                "☼#          ☼               ║☼\n" +
                "☼☼         ☼#            ╔══╝☼\n" +
                "☼☼                 ┌─☺═╗ ║   ☼\n" +
                "☼#                 │☼☼☼║ ║   ☼\n" +
                "☼☼                 └─┐ ║ ║   ☼\n" +
                "☼☼               ☼☼☼#│ ╚═╝   ☼\n" +
                "☼#                ×──┘       ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.UP);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                         ○ ☼\n" +
                "☼#                           ☼\n" +
                "☼☼      ☼#              ○    ☼\n" +
                "☼☼                       ○   ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼             $  ☼#         ☼\n" +
                "☼☼●     ☼☼☼  ○ ®   ☼  ☼      ☼\n" +
                "☼#      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼               ●  ☼\n" +
                "☼#              ☼#   ● ○     ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼    ●      ○       ☼       ☼\n" +
                "☼#             ○         ○ $ ☼\n" +
                "☼☼○                       ●  ☼\n" +
                "☼☼       ®         ☼#        ☼\n" +
                "☼#   ○   ☼☼ ☼                ☼\n" +
                "☼☼      ○   ☼     ●          ☼\n" +
                "☼☼    ©  ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼         ☼#        ▲       ☼\n" +
                "☼☼                   ╚═╗     ☼\n" +
                "☼#                  ☼☼☼║     ☼\n" +
                "☼☼                     ║     ☼\n" +
                "☼☼               ☼☼☼#  ╚╕    ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", Direction.RIGHT);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                         ○ ☼\n" +
                "☼#                           ☼\n" +
                "☼☼      ☼#              ○    ☼\n" +
                "☼☼                       ○   ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼             $  ☼#         ☼\n" +
                "☼☼●     ☼☼☼  ○ ®   ☼  ☼      ☼\n" +
                "☼#      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼               ●  ☼\n" +
                "☼#              ☼#   ● ○     ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼    ●      ○       ☼       ☼\n" +
                "☼#             ○         ○ $ ☼\n" +
                "☼☼○                       ●  ☼\n" +
                "☼☼       ®         ☼#        ☼\n" +
                "☼#   ○   ☼☼ ☼                ☼\n" +
                "☼☼      ○   ☼     ●          ☼\n" +
                "☼☼    ©  ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼         ☼#        ☻       ☼\n" +
                "☼☼                   ╚═╗     ☼\n" +
                "☼#                  ☼☼☼║     ☼\n" +
                "☼☼                     ║     ☼\n" +
                "☼☼               ☼☼☼#  ╚╕    ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", "");
    }

    private void assertAI(String board, Direction expected) {
        String actual = ai.get(board(board));
        Assert.assertEquals(expected.toString(), actual);
    }

    private void assertAI(String board, String string) {
        String actual = ai.get(board(board));
        Assert.assertEquals(string, actual);
    }
}
