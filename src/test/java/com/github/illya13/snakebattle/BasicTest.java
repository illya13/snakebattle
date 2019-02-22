package com.github.illya13.snakebattle;


import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.github.illya13.snakebattle.state.StateImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.lang.reflect.Field;


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
    public void rewardTest1() {
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
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.UP, 121);
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
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", Direction.RIGHT, 171);
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

    @Test
    public void rewardTest2() {
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                           ☼\n" +
                "☼#                           ☼\n" +
                "☼☼  ○   ☼#                   ☼\n" +
                "☼☼○            ╓             ☼\n" +
                "☼# ○         ● ║           ● ☼\n" +
                "☼☼             ║  ☼#         ☼\n" +
                "☼☼      ☼☼☼    ║   ☼  ☼      ☼\n" +
                "☼#      ☼      ╚►  ☼  ☼ $    ☼\n" +
                "☼☼      ☼○     ˄○  ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼    │         ●●  ☼\n" +
                "☼#             │☼#           ☼\n" +
                "☼☼             │         ○  $☼\n" +
                "☼☼    ●        │     ☼       ☼\n" +
                "☼#            ×┘     ©       ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                 ☼#        ☼\n" +
                "☼#       ☼☼ ☼                ☼\n" +
                "☼☼          ☼     ●     ○    ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼              © ☼\n" +
                "☼☼         ☼#                ☼\n" +
                "☼☼                           ☼\n" +
                "☼#                  ☼☼☼      ☼\n" +
                "☼☼                    ○      ☼\n" +
                "☼☼      ○        ☼☼☼#    ○   ☼\n" +
                "☼#                           ☼\n" +
                "☼☼               ○           ☼\n" +
                "☼☼        $                  ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.DOWN);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                           ☼\n" +
                "☼#                           ☼\n" +
                "☼☼  ○   ☼#                   ☼\n" +
                "☼☼○                          ☼\n" +
                "☼# ○         ● ╓           ● ☼\n" +
                "☼☼             ║  ☼#         ☼\n" +
                "☼☼      ☼☼☼    ║   ☼  ☼      ☼\n" +
                "☼#      ☼      ╚╗  ☼  ☼ $    ☼\n" +
                "☼☼      ☼○     ┌☻  ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼    │         ●●  ☼\n" +
                "☼#             │☼#           ☼\n" +
                "☼☼             │         ○  $☼\n" +
                "☼☼    ●        │     ☼       ☼\n" +
                "☼#             ¤     ©       ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                 ☼#        ☼\n" +
                "☼#       ☼☼ ☼                ☼\n" +
                "☼☼          ☼     ●     ○    ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼              © ☼\n" +
                "☼☼         ☼#                ☼\n" +
                "☼☼                           ☼\n" +
                "☼#                  ☼☼☼      ☼\n" +
                "☼☼                    ○      ☼\n" +
                "☼☼      ○        ☼☼☼#    ○   ☼\n" +
                "☼#                           ☼\n" +
                "☼☼               ○           ☼\n" +
                "☼☼        $                  ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", "");
    }

    @Test
    public void rewardTest3() {
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                    ○      ☼\n" +
                "☼#                           ☼\n" +
                "☼☼      ☼#   ○     ○         ☼\n" +
                "☼☼        ●                  ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼                ☼#         ☼\n" +
                "☼☼      ☼☼☼   ○    ☼  ☼      ☼\n" +
                "☼#      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼           ○   ●  ☼\n" +
                "☼#æ             ☼#           ☼\n" +
                "☼☼│                  ○       ☼\n" +
                "☼☼│   ● ○            ☼       ☼\n" +
                "☼#│                          ☼\n" +
                "☼☼│                          ☼\n" +
                "☼☼└──┐             ☼#        ☼\n" +
                "☼#   │   ☼☼ ☼          ○  ○○ ☼\n" +
                "☼☼ ● │      ☼     ●          ☼\n" +
                "☼☼┌──┘   ☼☼ ☼           $    ☼\n" +
                "☼#│         ☼                ☼\n" +
                "☼☼└♦       ☼#             $  ☼\n" +
                "☼☼╔═►                    ○   ☼\n" +
                "☼#║     ○           ☼☼☼      ☼\n" +
                "☼☼║               ○        ○ ☼\n" +
                "☼☼║    ╓         ☼☼☼#     ○  ☼\n" +
                "☼#║    ║               ●     ☼\n" +
                "☼☼║    ║            ©       ®☼\n" +
                "☼☼╚════╝                ○    ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.RIGHT);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                    ○      ☼\n" +
                "☼#                           ☼\n" +
                "☼☼      ☼#   ○     ○         ☼\n" +
                "☼☼        ●                  ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼                ☼#         ☼\n" +
                "☼☼      ☼☼☼   ○    ☼  ☼      ☼\n" +
                "☼#      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼           ○   ●  ☼\n" +
                "☼#              ☼#           ☼\n" +
                "☼☼æ                  ○       ☼\n" +
                "☼☼│   ● ○            ☼       ☼\n" +
                "☼#│                          ☼\n" +
                "☼☼│                          ☼\n" +
                "☼☼└──┐             ☼#        ☼\n" +
                "☼#   │   ☼☼ ☼          ○  ○○ ☼\n" +
                "☼☼ ● │      ☼     ●          ☼\n" +
                "☼☼┌──┘   ☼☼ ☼           $    ☼\n" +
                "☼#│         ☼                ☼\n" +
                "☼☼└┐       ☼#             $  ☼\n" +
                "☼☼╔♦═►                   ○   ☼\n" +
                "☼#║     ○           ☼☼☼      ☼\n" +
                "☼☼║               ○        ○ ☼\n" +
                "☼☼║              ☼☼☼#     ○  ☼\n" +
                "☼#║    ╓               ●     ☼\n" +
                "☼☼║    ║            ©       ®☼\n" +
                "☼☼╚════╝                ○    ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.RIGHT);
    }

    @Test
    public void rewardTest4() {
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼         ○          ○      ☼\n" +
                "☼#                           ☼\n" +
                "☼☼  ○  ®☼#                   ☼\n" +
                "☼☼                           ☼\n" +
                "☼# ○         ●               ☼\n" +
                "☼☼       ●        ☼#         ☼\n" +
                "☼☼      ☼☼☼        ☼  ☼   ○  ☼\n" +
                "☼#      ☼      ○   ☼  ☼      ☼\n" +
                "☼☼      ☼○         ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼               ●  ☼\n" +
                "☼#    ○         ☼#           ☼\n" +
                "☼☼           ○               ☼\n" +
                "☼☼    ●              ☼       ☼\n" +
                "☼#             ○     ●       ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                 ☼#        ☼\n" +
                "☼#       ☼☼ ☼                ☼\n" +
                "☼☼          ☼     ●      ●   ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼○        ☼#                ☼\n" +
                "☼☼                           ☼\n" +
                "☼#                  ☼☼☼      ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼               ☼☼☼#╔╕      ☼\n" +
                "☼#               ◄═══╝       ☼\n" +
                "☼☼               ○<───ö      ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.DOWN);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼         ○          ○      ☼\n" +
                "☼#                           ☼\n" +
                "☼☼  ○  ®☼#                   ☼\n" +
                "☼☼                           ☼\n" +
                "☼# ○         ●               ☼\n" +
                "☼☼       ●        ☼#         ☼\n" +
                "☼☼      ☼☼☼        ☼  ☼   ○  ☼\n" +
                "☼#      ☼      ○   ☼  ☼      ☼\n" +
                "☼☼      ☼○         ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼               ●  ☼\n" +
                "☼#    ○         ☼#           ☼\n" +
                "☼☼           ○               ☼\n" +
                "☼☼    ●              ☼       ☼\n" +
                "☼#             ○     ●       ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                 ☼#        ☼\n" +
                "☼#       ☼☼ ☼                ☼\n" +
                "☼☼          ☼     ●      ●   ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼○        ☼#                ☼\n" +
                "☼☼                           ☼\n" +
                "☼#                  ☼☼☼      ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼               ☼☼☼#╔╕      ☼\n" +
                "☼#               ╔═══╝       ☼\n" +
                "☼☼               ☺───ö       ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.LEFT, 51);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼         ○          ○      ☼\n" +
                "☼#                           ☼\n" +
                "☼☼  ○  ®☼#                   ☼\n" +
                "☼☼                           ☼\n" +
                "☼# ○         ●               ☼\n" +
                "☼☼       ●        ☼#         ☼\n" +
                "☼☼      ☼☼☼        ☼  ☼   ○  ☼\n" +
                "☼#      ☼      ○   ☼  ☼      ☼\n" +
                "☼☼      ☼○         ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼               ●  ☼\n" +
                "☼#    ○         ☼#           ☼\n" +
                "☼☼           ○               ☼\n" +
                "☼☼    ●              ☼       ☼\n" +
                "☼#             ○     ●       ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                 ☼#        ☼\n" +
                "☼#       ☼☼ ☼                ☼\n" +
                "☼☼          ☼     ●      ●   ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼○        ☼#                ☼\n" +
                "☼☼                           ☼\n" +
                "☼#                  ☼☼☼      ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼               ☼☼☼#        ☼\n" +
                "☼#               ╓           ☼\n" +
                "☼☼               ║           ☼\n" +
                "☼☼               ▼           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", Direction.LEFT, 101);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼         ○          ○      ☼\n" +
                "☼#                           ☼\n" +
                "☼☼  ○  ®☼#                   ☼\n" +
                "☼☼                           ☼\n" +
                "☼# ○         ●               ☼\n" +
                "☼☼       ●        ☼#         ☼\n" +
                "☼☼      ☼☼☼        ☼  ☼   ○  ☼\n" +
                "☼#      ☼      ○   ☼  ☼      ☼\n" +
                "☼☼      ☼○         ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼               ●  ☼\n" +
                "☼#    ○         ☼#           ☼\n" +
                "☼☼           ○               ☼\n" +
                "☼☼    ●              ☼       ☼\n" +
                "☼#             ○     ●       ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                 ☼#        ☼\n" +
                "☼#       ☼☼ ☼                ☼\n" +
                "☼☼          ☼     ●      ●   ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼○        ☼#                ☼\n" +
                "☼☼                           ☼\n" +
                "☼#                  ☼☼☼      ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼               ☼☼☼#        ☼\n" +
                "☼#               ╓           ☼\n" +
                "☼☼               ║           ☼\n" +
                "☼☼               ☻           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", "");
    }

    @Test
    public void rewardTest5() {
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼         ○           ○     ☼\n" +
                "☼#                           ☼\n" +
                "☼☼      ☼#         ○         ☼\n" +
                "☼☼                      ○    ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼                ☼#         ☼\n" +
                "☼☼      ☼☼☼        ☼  ☼      ☼\n" +
                "☼#      ☼      ○   ☼  ☼      ☼\n" +
                "☼☼ æ    ☼○         ☼  ☼      ☼\n" +
                "☼☼ │    ☼☼☼               ●  ☼\n" +
                "☼# └>           ☼#           ☼\n" +
                "☼☼ ╘═╗                      $☼\n" +
                "☼☼   ▼●              ☼       ☼\n" +
                "☼#             ○             ☼\n" +
                "☼☼                  ○        ☼\n" +
                "☼☼   ○             ☼#        ☼\n" +
                "☼#       ☼☼ ☼                ☼\n" +
                "☼☼          ☼     ●     ○    ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼         ☼#                ☼\n" +
                "☼☼           ○               ☼\n" +
                "☼#                  ☼☼☼      ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼      ○        ☼☼☼#    ○   ☼\n" +
                "☼#         ○                 ☼\n" +
                "☼☼               ○           ☼\n" +
                "☼☼      ○                    ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", Direction.DOWN);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼         ○           ○     ☼\n" +
                "☼#                           ☼\n" +
                "☼☼      ☼#         ○         ☼\n" +
                "☼☼                      ○    ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼                ☼#         ☼\n" +
                "☼☼      ☼☼☼        ☼  ☼      ☼\n" +
                "☼#      ☼      ○   ☼  ☼      ☼\n" +
                "☼☼      ☼○         ☼  ☼      ☼\n" +
                "☼☼ æ    ☼☼☼               ●  ☼\n" +
                "☼# └┐           ☼#           ☼\n" +
                "☼☼  ☺╗                      $☼\n" +
                "☼☼   ║●              ☼       ☼\n" +
                "☼#   ▼         ○             ☼\n" +
                "☼☼                  ○        ☼\n" +
                "☼☼   ○             ☼#        ☼\n" +
                "☼#       ☼☼ ☼                ☼\n" +
                "☼☼          ☼     ●     ○    ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼         ☼#                ☼\n" +
                "☼☼           ○               ☼\n" +
                "☼#                  ☼☼☼      ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼      ○        ☼☼☼#    ○   ☼\n" +
                "☼#         ○                 ☼\n" +
                "☼☼               ○           ☼\n" +
                "☼☼      ○                  ○ ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", Direction.DOWN, 40);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼         ○           ○     ☼\n" +
                "☼#                           ☼\n" +
                "☼☼      ☼#         ○         ☼\n" +
                "☼☼                      ○    ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼                ☼#         ☼\n" +
                "☼☼      ☼☼☼        ☼  ☼      ☼\n" +
                "☼#      ☼      ○   ☼  ☼      ☼\n" +
                "☼☼      ☼○         ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼               ●  ☼\n" +
                "☼#              ☼#           ☼\n" +
                "☼☼   ╓                      $☼\n" +
                "☼☼   ║●              ☼       ☼\n" +
                "☼#   ║         ○             ☼\n" +
                "☼☼   ▼              ○        ☼\n" +
                "☼☼   ○             ☼#        ☼\n" +
                "☼#       ☼☼ ☼                ☼\n" +
                "☼☼          ☼     ●     ○    ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼         ☼#                ☼\n" +
                "☼☼           ○               ☼\n" +
                "☼#                  ☼☼☼      ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼      ○        ☼☼☼#    ○   ☼\n" +
                "☼#         ○                 ☼\n" +
                "☼☼               ○           ☼\n" +
                "☼☼      ○                  ○ ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.DOWN, 90);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼         ○           ○     ☼\n" +
                "☼#                           ☼\n" +
                "☼☼      ☼#         ○         ☼\n" +
                "☼☼                      ○    ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼                ☼#         ☼\n" +
                "☼☼      ☼☼☼        ☼  ☼      ☼\n" +
                "☼#      ☼      ○   ☼  ☼      ☼\n" +
                "☼☼      ☼○         ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼               ●  ☼\n" +
                "☼#              ☼#           ☼\n" +
                "☼☼   ╓                      $☼\n" +
                "☼☼   ║●              ☼       ☼\n" +
                "☼#   ║         ○             ☼\n" +
                "☼☼   ☻              ○        ☼\n" +
                "☼☼   ○             ☼#        ☼\n" +
                "☼#       ☼☼ ☼                ☼\n" +
                "☼☼          ☼     ●     ○    ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼         ☼#                ☼\n" +
                "☼☼           ○               ☼\n" +
                "☼#                  ☼☼☼      ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼      ○        ☼☼☼#    ○   ☼\n" +
                "☼#         ○                 ☼\n" +
                "☼☼               ○           ☼\n" +
                "☼☼      ○                  ○ ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", "");
    }

    @Test
    public void rewardTest6() {
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼       $                   ☼\n" +
                "☼# ○                         ☼\n" +
                "☼☼    ○ ☼#               ○   ☼\n" +
                "☼☼                           ☼\n" +
                "☼#           ●               ☼\n" +
                "☼☼     ˄          ☼#         ☼\n" +
                "☼☼     │☼☼☼     ○  ☼  ☼®     ☼\n" +
                "☼# ┌┐  │☼          ☼  ☼      ☼\n" +
                "☼☼ │└──┘☼ ○        ☼  ☼ ©   ●☼\n" +
                "☼☼ │    ☼☼☼                  ☼\n" +
                "☼# │      ╓     ☼#®          ☼\n" +
                "☼☼ │      ║                  ☼\n" +
                "☼☼ │  ●   ╚╗         ☼      ●☼\n" +
                "☼# │       ║                 ☼\n" +
                "☼☼ │       ║                 ☼\n" +
                "☼☼ │       ║       ☼#        ☼\n" +
                "☼# │     ☼☼║☼  ○             ☼\n" +
                "☼☼ │       ║☼ ●   ●   ●      ☼\n" +
                "☼☼ │    ●☼☼║☼                ☼\n" +
                "☼# └───┐ ╔═╝☼                ☼\n" +
                "☼☼ ┌┐  │ ╚╗☼#                ☼\n" +
                "☼☼┌┘└──┘● ╚════╗       ●   ○ ☼\n" +
                "☼#└───ö        ║    ☼☼☼      ☼\n" +
                "☼☼             ║             ☼\n" +
                "☼☼            ╔╝ ☼☼☼#        ☼\n" +
                "☼#          ╔═╝              ☼\n" +
                "☼☼          ╚═══► ○          ☼\n" +
                "☼☼                   ○    ○  ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.RIGHT);

        StateImpl state = (StateImpl)ai.state;
        setStep(state, 299);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼       $                   ☼\n" +
                "☼# ○                         ☼\n" +
                "☼☼    ○ ☼#               ○   ☼\n" +
                "☼☼                           ☼\n" +
                "☼#     ˄     ●               ☼\n" +
                "☼☼     │          ☼#         ☼\n" +
                "☼☼     │☼☼☼     ○  ☼  ☼®     ☼\n" +
                "☼# ┌┐  │☼          ☼  ☼      ☼\n" +
                "☼☼ │└──┘☼ ○        ☼  ☼ ©   ●☼\n" +
                "☼☼ │    ☼☼☼                  ☼\n" +
                "☼# │            ☼#®          ☼\n" +
                "☼☼ │      ╓                  ☼\n" +
                "☼☼ │  ●   ╚╗         ☼      ●☼\n" +
                "☼# │       ║                 ☼\n" +
                "☼☼ │       ║                 ☼\n" +
                "☼☼ │       ║       ☼#        ☼\n" +
                "☼# │     ☼☼║☼  ○             ☼\n" +
                "☼☼ │       ║☼ ●   ●   ●      ☼\n" +
                "☼☼ │    ●☼☼║☼                ☼\n" +
                "☼# └───┐ ╔═╝☼                ☼\n" +
                "☼☼ ┌┐  │ ╚╗☼#                ☼\n" +
                "☼☼┌┘└──┘● ╚════╗       ●   ○ ☼\n" +
                "☼#└──ö         ║    ☼☼☼      ☼\n" +
                "☼☼             ║             ☼\n" +
                "☼☼            ╔╝ ☼☼☼#        ☼\n" +
                "☼#          ╔═╝              ☼\n" +
                "☼☼          ╚════►○          ☼\n" +
                "☼☼                   ○    ○  ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.RIGHT);
        Assert.assertEquals(ai.state.enemies().iterator().next().reward(), 50);

        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼       $                   ☼\n" +
                "☼# ○                         ☼\n" +
                "☼☼    ○ ☼#               ○   ☼\n" +
                "☼☼                           ☼\n" +
                "☼#     ☺     ●               ☼\n" +
                "☼☼     │          ☼#         ☼\n" +
                "☼☼     │☼☼☼     ○  ☼  ☼®     ☼\n" +
                "☼# ┌┐  │☼          ☼  ☼      ☼\n" +
                "☼☼ │└──┘☼ ○        ☼  ☼ ©   ●☼\n" +
                "☼☼ │    ☼☼☼                  ☼\n" +
                "☼# │            ☼#®          ☼\n" +
                "☼☼ │      ╓                  ☼\n" +
                "☼☼ │  ●   ╚╗         ☼      ●☼\n" +
                "☼# │       ║                 ☼\n" +
                "☼☼ │       ║                 ☼\n" +
                "☼☼ │       ║       ☼#        ☼\n" +
                "☼# │     ☼☼║☼  ○             ☼\n" +
                "☼☼ │       ║☼ ●   ●   ●      ☼\n" +
                "☼☼ │    ●☼☼║☼                ☼\n" +
                "☼# └───┐ ╔═╝☼                ☼\n" +
                "☼☼ ┌┐  │ ╚╗☼#                ☼\n" +
                "☼☼┌┘└──┘● ╚════╗       ●   ○ ☼\n" +
                "☼#└──ö         ║    ☼☼☼      ☼\n" +
                "☼☼             ║             ☼\n" +
                "☼☼            ╔╝ ☼☼☼#        ☼\n" +
                "☼#          ╔═╝              ☼\n" +
                "☼☼          ╚════☻○          ☼\n" +
                "☼☼                   ○    ○  ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", "");
    }

    @Test
    public void rewardTest7() {
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                     ┌───┐ ☼\n" +
                "☼#                     │  ┌┘ ☼\n" +
                "☼☼      ☼#      ┌──┐  ┌┘  │╔╗☼\n" +
                "☼☼              ¤  └──┘ ╔╗│║║☼\n" +
                "☼#           ●          ║║˅║║☼\n" +
                "☼☼                ☼#    ║╚═╝║☼\n" +
                "☼☼      ☼☼☼        ☼  ☼ ╙   ▼☼\n" +
                "☼#      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼    ○          ●  ☼\n" +
                "☼#              ☼#           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼    ●              ☼       ☼\n" +
                "☼#                           ☼\n" +
                "☼☼         ○                 ☼\n" +
                "☼☼                 ☼#        ☼\n" +
                "☼#       ☼☼ ☼       ○        ☼\n" +
                "☼☼          ☼     ●          ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼         ☼#    ○           ☼\n" +
                "☼☼                           ☼\n" +
                "☼#   ○         ○    ☼☼☼      ☼\n" +
                "☼☼  ●                        ☼\n" +
                "☼☼             ○ ☼☼☼#        ☼\n" +
                "☼#     ●                     ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼               ○           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.LEFT);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                     ┌───┐ ☼\n" +
                "☼#                     │  ┌┘ ☼\n" +
                "☼☼      ☼#      ×──┐  ┌┘  │╔╗☼\n" +
                "☼☼                 └──┘ ╔╗│║║☼\n" +
                "☼#           ●          ║║│║║☼\n" +
                "☼☼                ☼#    ╙╚☺╝║☼\n" +
                "☼☼      ☼☼☼        ☼  ☼ ○  ◄╝☼\n" +
                "☼#      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼    ○          ●  ☼\n" +
                "☼#              ☼#           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼    ●              ☼       ☼\n" +
                "☼#                           ☼\n" +
                "☼☼         ○                 ☼\n" +
                "☼☼                 ☼#        ☼\n" +
                "☼#       ☼☼ ☼       ○        ☼\n" +
                "☼☼          ☼     ●          ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼         ☼#    ○           ☼\n" +
                "☼☼                           ☼\n" +
                "☼#   ○         ○    ☼☼☼      ☼\n" +
                "☼☼  ●                        ☼\n" +
                "☼☼             ○ ☼☼☼#        ☼\n" +
                "☼#     ●                     ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼               ○           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", Direction.LEFT, 220);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                           ☼\n" +
                "☼#                           ☼\n" +
                "☼☼      ☼#                 ╔╗☼\n" +
                "☼☼                      ╔╗ ║║☼\n" +
                "☼#           ●          ╙║ ║║☼\n" +
                "☼☼                ☼#     ╚═╝║☼\n" +
                "☼☼      ☼☼☼        ☼  ☼ ○ ◄═╝☼\n" +
                "☼#      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼          ☼  ☼      ☼\n" +
                "☼☼      ☼☼☼    ○          ●  ☼\n" +
                "☼#              ☼#           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼    ●              ☼       ☼\n" +
                "☼#                           ☼\n" +
                "☼☼         ○                 ☼\n" +
                "☼☼                 ☼#        ☼\n" +
                "☼#       ☼☼ ☼       ○        ☼\n" +
                "☼☼          ☼     ●          ☼\n" +
                "☼☼       ☼☼ ☼                ☼\n" +
                "☼#          ☼                ☼\n" +
                "☼☼         ☼#    ○           ☼\n" +
                "☼☼                           ☼\n" +
                "☼#   ○         ○    ☼☼☼      ☼\n" +
                "☼☼  ●                        ☼\n" +
                "☼☼             ○ ☼☼☼#○       ☼\n" +
                "☼#     ●                     ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼               ○           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", Direction.DOWN, 270);
    }

    private void assertAI(String board, Direction expected) {
        String actual = ai.get(board(board));
        Assert.assertEquals(expected.toString(), actual);
    }

    private void assertAI(String board, Direction expected, int reward) {
        String actual = ai.get(board(board));
        Assert.assertEquals(expected.toString(), actual);
        Assert.assertEquals(reward, ai.state.me().reward());
    }

    private void assertAI(String board, String string) {
        String actual = ai.get(board(board));
        Assert.assertEquals(string, actual);
    }

    static Field stepField;

    static void setStep(StateImpl state, int step) {
        try {
            stepField.set(state, step);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            stepField = StateImpl.class.getDeclaredField("step");
            stepField.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
            // no op
        }
    }

}
