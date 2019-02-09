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

    @Test
    public void should2() {
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
                "☼☼              ┌──ö         ☼\n" +
                "☼☼        ☼☼☼   │            ☼\n" +
                "☼☼       ☼ ○☼   ˅            ☼\n" +
                "☼☼      ☼☼☼☼# ●   ☼☼   ☼#    ☼\n" +
                "☼☼      ☼   ☼   ●●☼ ☼ ☼○☼    ☼\n" +
                "*ø      ☼   ☼     ☼  ☼╔►☼    ☼\n" +
                "☼☼                ☼   ║ ☼    ☼\n" +
                "☼☼                ☼   ║ ☼   ○☼\n" +
                "☼☼             ●      ╙    ● ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼       ●              ●    ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.DOWN);
    }

    @Test
    public void should3() {
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                  ○        ☼\n" +
                "☼#                           ☼\n" +
                "☼☼             ●     ●       ☼\n" +
                "☼☼   ©                       ☼\n" +
                "☼☼       ○                   ☼\n" +
                "☼☼     ☼☼☼☼☼                 ☼\n" +
                "☼☼     ☼                     ☼\n" +
                "☼#     ☼☼☼ ●      ☼☼☼☼#      ☼\n" +
                "☼☼ ○   ☼      ●   ☼○  ☼      ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼\n" +
                "☼☼       ®        ☼          ☼\n" +
                "☼☼                ☼          ☼\n" +
                "☼☼                      ●    ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼        ☼☼☼                ☼\n" +
                "☼☼   ©   ☼○ ☼                ☼\n" +
                "☼☼      ☼☼☼☼#     ☼☼   ☼#    ☼\n" +
                "☼☼      ☼   ☼     ☼ ☼$☼ ☼ ●  ☼\n" +
                "☼#      ☼   ☼   ● ☼  ☼  ☼    ☼\n" +
                "☼☼●       ●       ☼     ☼    ☼\n" +
                "☼☼           ●    ☼     ☼    ☼\n" +
                "☼☼                   <┐      ☼\n" +
                "☼☼                    │      ☼\n" +
                "☼☼          ●●        │      ☼\n" +
                "☼#        ●●┌┐        │● ♥   ☼\n" +
                "☼☼         ×┘│        │  ╚═╕ ☼\n" +
                "☼☼           └────────┘      ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.LEFT);

    }

    @Test
    public void should4() {
        ai.prev = Direction.LEFT;
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                           ☼\n" +
                "☼#      ●                    ☼\n" +
                "☼☼    ○                      ☼\n" +
                "☼☼ ˄     ○                   ☼\n" +
                "☼☼ │                         ☼\n" +
                "☼☼ ¤   ☼☼☼☼☼    ●            ☼\n" +
                "☼☼     ☼ ○ ˄    ◄╕        $  ☼\n" +
                "☼#     ☼☼☼×┘    ˄●☼☼☼☼#      ☼\n" +
                "☼☼     ☼     ●  └ö☼○  ☼ ●  ● ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼\n" +
                "☼☼                ☼          ☼\n" +
                "☼☼                ☼          ☼\n" +
                "☼☼                           ☼\n" +
                "☼#                  ●        ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼        ☼☼☼                ☼\n" +
                "☼☼       ☼  ☼                ☼\n" +
                "☼☼      ☼☼☼☼#    ●☼☼   ☼#    ☼\n" +
                "☼☼      ☼   ☼     ☼○☼ ☼○☼    ☼\n" +
                "☼#      ☼   ☼     ☼  ☼  ☼    ☼\n" +
                "☼☼                ☼     ☼    ☼\n" +
                "☼☼     ●          ☼     ☼    ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼#                   ○       ☼\n" +
                "☼☼                   ●       ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", Direction.LEFT);
    }

    @Test
    public void should5() {
        ai.prev = Direction.UP;
        ai.learning.getStrategy().features.add(Learning.FEATURE.INSIDE);
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                      ┌───┐☼\n" +
                "☼#                      │<──┘☼\n" +
                "☼☼      ○●              └─┐  ☼\n" +
                "☼☼                        │  ☼\n" +
                "☼☼           ●           ○└┐ ☼\n" +
                "☼☼     ☼☼☼☼☼               │ ☼\n" +
                "☼☼     ☼○         ©        │ ☼\n" +
                "☼#     ☼☼☼        ☼☼☼☼#    │æ☼\n" +
                "☼☼     ☼          ☼ ○ ☼  ● └┘☼\n" +
                "☼☼     ☼☼☼☼#○     ☼☼☼☼#      ☼\n" +
                "☼☼              ● ☼          ☼\n" +
                "☼☼                ☼          ☼\n" +
                "☼☼  © ●             ○        ☼\n" +
                "☼#  ○                        ☼\n" +
                "☼☼           ╓               ☼\n" +
                "☼☼        ☼☼☼║               ☼\n" +
                "☼☼       ☼ ○☼║  ●       ○    ☼\n" +
                "☼☼      ☼☼☼☼#║    ☼☼   ☼#    ☼\n" +
                "☼☼    ● ☼   ☼║  ● ☼ ☼ ☼$☼    ☼\n" +
                "☼#      ☼   ☼║    ☼○ ☼  ☼    ☼\n" +
                "☼☼   ╔═══════╝    ☼     ☼    ☼\n" +
                "☼☼ ●╔╝ ●          ☼    ○☼    ☼\n" +
                "☼☼  ║                        ☼\n" +
                "☼☼  ║                        ☼\n" +
                "☼☼ ╔╝          ●             ☼\n" +
                "☼#▲║                         ☼\n" +
                "☼☼╚╝     ┌────────ö ˄        ☼\n" +
                "☼☼       └──────────┘        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.UP);
    }

    @Test
    public void should6() {
        ai.prev = Direction.LEFT;
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                     $     ☼\n" +
                "☼#                      ○●   ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼           ●              ●☼\n" +
                "☼☼  ●  ☼☼☼☼☼               ○ ☼\n" +
                "☼☼     ☼   ○           ˄     ☼\n" +
                "☼#     ☼☼☼○       ☼☼☼☼#│     ☼\n" +
                "☼☼     ☼          ☼   ☼│     ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼#│     ☼\n" +
                "☼☼                ☼    │     ☼\n" +
                "☼☼ ╓              ☼    ¤     ☼\n" +
                "☼☼ ║                         ☼\n" +
                "☼#◄╝                         ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼        ☼☼☼                ☼\n" +
                "☼☼       ☼© ☼                ☼\n" +
                "☼☼      ☼☼☼☼#○    ☼☼   ☼#    ☼\n" +
                "☼☼      ☼   ☼○    ☼ ☼ ☼ ☼    ☼\n" +
                "☼#      ☼   ☼     ☼  ☼  ☼    ☼\n" +
                "☼☼                ☼     ☼    ☼\n" +
                "☼☼     ●        ● ☼     ☼    ☼\n" +
                "☼☼       ○                   ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼#  ○     ●                  ☼\n" +
                "☼☼  ○                        ☼\n" +
                "☼☼     $                     ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.DOWN);
    }

    @Test
    public void should7() {
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                           ☼\n" +
                "☼#                           ☼\n" +
                "☼☼       ●  ●                ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼           ●        ╔╗     ☼\n" +
                "☼☼     ☼☼☼☼☼          ║║     ☼\n" +
                "☼☼     ☼         ╔════╝║     ☼\n" +
                "☼#     ☼☼☼       ▼☼☼☼☼#╙     ☼\n" +
                "☼☼     ☼ $©    ○  ☼   ☼  ●   ☼\n" +
                "☼☼     ☼☼☼☼#    ˄ ☼☼☼☼#      ☼\n" +
                "☼☼    ©         │ ☼          ☼\n" +
                "☼☼              └┐☼          ☼\n" +
                "☼☼        ○      └─┐         ☼\n" +
                "☼#                 │         ☼\n" +
                "☼☼                 ¤         ☼\n" +
                "☼☼      ® ☼☼☼                ☼\n" +
                "☼☼       ☼○○☼                ☼\n" +
                "☼☼      ☼☼☼☼#     ☼☼   ☼#    ☼\n" +
                "☼☼      ☼   ☼     ☼ ☼ ☼ ☼    ☼\n" +
                "☼#      ☼   ☼     ☼  ☼  ☼    ☼\n" +
                "☼☼                ☼ ○   ☼    ☼\n" +
                "☼☼     ●    ●     ☼ ˄   ☼    ☼\n" +
                "☼☼                  │        ☼\n" +
                "☼☼                  ¤        ☼\n" +
                "☼☼  ●                        ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.LEFT);
    }

    @Test
    public void should8() {
        ai.prev = Direction.LEFT;
        //  [MEDIUM, PREDICT, ATTACK, SHORT, DESTRUCT, STONES]
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                      ○ ®  ☼\n" +
                "☼#○                          ☼\n" +
                "☼☼       ●                   ☼\n" +
                "☼☼                      ○    ☼\n" +
                "☼☼                  ●        ☼\n" +
                "☼☼     ☼☼☼☼☼                 ☼\n" +
                "☼☼     ☼                     ☼\n" +
                "☼#     ☼☼☼        ☼☼☼☼#      ☼\n" +
                "☼☼     ☼  ○       ☼   ☼      ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼\n" +
                "☼☼    $           ☼     ○  © ☼\n" +
                "☼☼○               ☼          ☼\n" +
                "☼☼    ●                      ☼\n" +
                "☼#  $                        ☼\n" +
                "☼☼           ○               ☼\n" +
                "☼☼        ☼☼☼               ○☼\n" +
                "☼☼       ☼  ☼                ☼\n" +
                "☼☼      ☼☼☼☼#     ☼☼   ☼#    ☼\n" +
                "☼☼      ☼   ☼   ● ☼ ☼ ☼ ☼    ☼\n" +
                "☼#      ☼   ☼     ☼ ○☼  ☼    ☼\n" +
                "☼☼                ☼     ☼    ☼\n" +
                "☼☼     ●      ●   ☼     ☼    ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼      ┌ö                   ☼\n" +
                "☼#    ┌─┘◄╕                  ☼\n" +
                "☼☼    └─>                    ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", Direction.UP);
    }

    @Test
    public void should9() {
        // [MEDIUM, PREDICT, ATTACK, SHORT, DESTRUCT, STONES]
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼         ○                 ☼\n" +
                "☼#   ©                       ☼\n" +
                "☼☼  ○    ●©        ○         ☼\n" +
                "☼☼                      ○   ○☼\n" +
                "☼☼ ○○        ●    ○          ☼\n" +
                "☼☼     ☼☼☼☼☼                 ☼\n" +
                "☼☼     ☼                     ☼\n" +
                "☼#     ☼☼☼     ○  ☼☼☼☼#      ☼\n" +
                "☼☼     ☼          ☼   ☼  ●●  ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼\n" +
                "☼☼                ☼          ☼\n" +
                "☼☼○               ☼         $☼\n" +
                "☼☼    ●                      ☼\n" +
                "☼#                           ☼\n" +
                "☼☼       ×────┐●╘╗           ☼\n" +
                "☼☼        ☼☼☼ ˅ ╔╝           ☼\n" +
                "☼☼       ☼  ☼   ▼            ☼\n" +
                "☼☼      ☼☼☼☼#     ☼☼   ☼#    ☼\n" +
                "☼☼      ☼   ☼     ☼®☼ ☼ ☼    ☼\n" +
                "☼#      ☼   ☼   ● ☼  ☼  ☼    ☼\n" +
                "☼☼                ☼     ☼    ☼\n" +
                "☼☼                ☼     ☼    ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼             ●       ○     ☼\n" +
                "☼#                           ☼\n" +
                "☼☼           ●               ☼\n" +
                "☼☼   ®                       ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.RIGHT);
    }
    @Test
    public void should10() {
        ai.initRound();
        ai.initialized = true;
        ai.stoneCounter = 2;
        ai.prev = Direction.LEFT;
        //  [MEDIUM, PREDICT, ATTACK, SHORT, DESTRUCT, STONES]
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼         ○                 ☼\n" +
                "☼#           ○               ☼\n" +
                "☼☼  ○    ●         ○         ☼\n" +
                "☼☼                      ○    ☼\n" +
                "☼☼           ●    ○          ☼\n" +
                "☼☼     ☼☼☼☼☼                 ☼\n" +
                "☼☼     ☼                     ☼\n" +
                "☼#     ☼☼☼     ○  ☼☼☼☼#      ☼\n" +
                "☼☼     ☼          ☼  ○☼  ●   ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼\n" +
                "☼☼             ˄  ☼          ☼\n" +
                "☼☼             │  ☼         $☼\n" +
                "☼☼    ●       ×┘             ☼\n" +
                "☼#                    ○      ☼\n" +
                "☼☼              ●╘═══►       ☼\n" +
                "☼☼        ☼☼☼                ☼\n" +
                "☼☼       ☼  ☼                ☼\n" +
                "☼☼      ☼☼☼☼#     ☼☼   ☼# ©  ☼\n" +
                "☼☼      ☼   ☼   ● ☼ ☼ ☼ ☼ ○  ☼\n" +
                "☼# ●    ☼   ☼     ☼  ☼  ☼    ☼\n" +
                "☼☼                ☼     ☼    ☼\n" +
                "☼☼     ●          ☼     ☼    ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                       ○   ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                      ○    ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.UP);
    }

    @Test
    public void should11() {
        // [MEDIUM, PREDICT, ATTACK, SHORT, DESTRUCT, STONES]
        ai.prev = Direction.DOWN;
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼         ○                 ☼\n" +
                "☼#                           ☼\n" +
                "☼☼       ●         ○         ☼\n" +
                "☼☼                      ○    ☼\n" +
                "☼☼           ●    ○          ☼\n" +
                "☼☼     ☼☼☼☼☼                 ☼\n" +
                "☼☼     ☼                     ☼\n" +
                "☼#     ☼☼☼     ○  ☼☼☼☼#      ☼\n" +
                "☼☼     ☼          ☼   ☼  ●   ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼\n" +
                "☼☼                ☼          ☼\n" +
                "☼☼      æ         ☼         $☼\n" +
                "☼☼    ●┌┘○                   ☼\n" +
                "☼#   ╓<┘       ○      ○      ☼\n" +
                "☼☼   ▼       ○               ☼\n" +
                "☼☼       ●☼☼☼                ☼\n" +
                "☼☼       ☼  ☼              ○ ☼\n" +
                "☼☼      ☼☼☼☼#     ☼☼   ☼#   $☼\n" +
                "☼☼      ☼   ☼   ● ☼ ☼ ☼ ☼ ○  ☼\n" +
                "☼#      ☼   ☼     ☼  ☼  ☼    ☼\n" +
                "☼☼                ☼     ☼    ☼\n" +
                "☼☼     ●          ☼     ☼    ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                  ○        ☼\n" +
                "☼☼             ●         ○   ☼\n" +
                "☼#                           ☼\n" +
                "☼☼               ○ ○         ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.DOWN);
    }

    @Test
    public void should12() {
        ai.prev = Direction.RIGHT;
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                           ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                        ○  ☼\n" +
                "☼☼     ☼☼☼☼☼                 ☼\n" +
                "☼☼     ☼                     ☼\n" +
                "☼#     ☼☼☼        ☼☼☼☼#      ☼\n" +
                "☼☼     ☼○         ☼   ☼   ●  ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼\n" +
                "☼☼                ☼       ○  ☼\n" +
                "☼☼           ○    ☼          ☼\n" +
                "☼☼    ●  ○      ○            ☼\n" +
                "☼#      ○             ○      ☼\n" +
                "☼☼          ○   ○            ☼\n" +
                "☼☼        ☼☼☼                ☼\n" +
                "☼☼   ○   ☼  ☼    ○           ☼\n" +
                "☼☼      ☼☼☼☼#     ☼☼   ☼#    ☼\n" +
                "☼☼      ☼   ☼     ☼ ☼ ☼ ☼    ☼\n" +
                "☼#      ☼   ☼     ☼  ☼  ☼    ☼\n" +
                "☼☼  ●             ☼     ☼    ☼\n" +
                "☼☼     ●          ☼   ╓ ☼    ☼\n" +
                "☼☼                    ╚╗     ☼\n" +
                "☼☼                     ╚►    ☼\n" +
                "☼☼ ○    ○      ●        ┌ö   ☼\n" +
                "☼#     ○                ˅   ●☼\n" +
                "☼☼               ○           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.UP);
    }

    @Test
    public void should13() {
        ai.prev = Direction.DOWN;
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼ ●       ○            ○    ☼\n" +
                "☼#                           ☼\n" +
                "☼☼       ●         ○         ☼\n" +
                "☼☼    ●                 ○    ☼\n" +
                "☼☼           ●    ○  ○○      ☼\n" +
                "☼☼     ☼☼☼☼☼                 ☼\n" +
                "☼☼®    ☼          ○          ☼\n" +
                "☼#     ☼☼☼        ☼☼☼☼#      ☼\n" +
                "☼☼     ☼          ☼   ☼      ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼\n" +
                "☼☼                ☼○         ☼\n" +
                "☼☼ $              ☼          ☼\n" +
                "☼☼   ○               ○       ☼\n" +
                "☼#            ○              ☼\n" +
                "☼☼            ○              ☼\n" +
                "☼☼        ☼☼☼   ©            ☼\n" +
                "☼☼○      ☼  ☼                ☼\n" +
                "☼☼      ☼☼☼☼#     ☼☼©  ☼#    ☼\n" +
                "☼☼      ☼   ☼   ● ☼ ☼ ☼ ☼    ☼\n" +
                "☼#      ☼   ☼     ☼  ☼  ☼    ☼\n" +
                "☼☼                ☼     ☼ æ  ☼\n" +
                "☼☼     ●          ☼     ☼ │  ☼\n" +
                "☼☼ ○                      │  ☼\n" +
                "☼☼             ○          │  ☼\n" +
                "☼☼  ®                     │╓ ☼\n" +
                "☼#                 ○      ˅║ ☼\n" +
                "☼☼                    ○    ▼ ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.RIGHT);
    }

    @Test
    public void should14() {
        ai.prev = Direction.LEFT;
        ai.stoneCounter = 2;
        ai.learning.getStrategy().features.add(Learning.FEATURE.PREDICT);
        ai.learning.getStrategy().features.add(Learning.FEATURE.STONES);

        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼ ○○          ○  ○   $      ☼\n" +
                "☼#  ○                        ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                        ○  ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼     ☼☼☼☼☼                 ☼\n" +
                "☼☼     ☼               ◄╗    ☼\n" +
                "☼#     ☼☼☼        ☼☼☼☼# ║    ☼\n" +
                "☼☼     ☼          ☼   ☼ ║    ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼# ║    ☼\n" +
                "☼☼         ○ <──┐ ☼    ╔╝    ☼\n" +
                "☼☼              │ ☼  ╘═╝     ☼\n" +
                "☼☼              │            ☼\n" +
                "☼#              └┐           ☼\n" +
                "☼☼               │           ☼\n" +
                "☼☼        ☼☼☼    ¤     ○     ☼\n" +
                "☼☼       ☼  ☼                ☼\n" +
                "☼☼    © ☼☼☼☼#     ☼☼   ☼#    ☼\n" +
                "☼☼      ☼   ☼     ☼ ☼ ☼ ☼    ☼\n" +
                "☼#      ☼●  ☼     ☼  ☼  ☼    ☼\n" +
                "☼☼                ☼     ☼    ☼\n" +
                "☼☼                ☼     ☼    ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                  ●●●●●●●● ☼\n" +
                "☼☼       ©                   ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼", Direction.DOWN);
    }

    @Test
    public void should15() {
        ai.prev = Direction.RIGHT;

        ai.learning.getStrategy().features = new HashSet<>();
        ai.learning.getStrategy().features.add(Learning.FEATURE.INSIDE);
        ai.learning.getStrategy().features.add(Learning.FEATURE.PREDICT);
        ai.learning.getStrategy().features.add(Learning.FEATURE.STONES);
        ai.learning.getStrategy().features.add(Learning.FEATURE.ATTACK);
        ai.learning.getStrategy().features.add(Learning.FEATURE.DESTRUCT);
        ai.learning.getStrategy().features.add(Learning.FEATURE.MEDIUM);

        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼ ▲       ○                 ☼\n" +
                "☼# ╚╗        ®               ☼\n" +
                "☼☼  ╙    ●                   ☼\n" +
                "☼☼ <─────┐                   ☼\n" +
                "☼☼       └─ö      ○          ☼\n" +
                "☼☼     ☼☼☼☼☼                 ☼\n" +
                "☼☼     ☼                     ☼\n" +
                "☼#     ☼☼☼        ☼☼☼☼#      ☼\n" +
                "☼☼     ☼          ☼   ☼      ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼\n" +
                "☼☼                ☼          ☼\n" +
                "☼☼○○              ☼          ☼\n" +
                "☼☼    ●        ○             ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼ ○    ○ ☼☼☼                ☼\n" +
                "☼☼   ○   ☼  ☼                ☼\n" +
                "☼☼      ☼☼☼☼#     ☼☼   ☼#    ☼\n" +
                "☼☼      ☼ ○ ☼   ● ☼ ☼ ☼ ☼    ☼\n" +
                "☼#      ☼   ☼     ☼  ☼ ○☼    ☼\n" +
                "☼☼          ○     ☼○    ☼    ☼\n" +
                "☼☼     ●     ●    ☼     ☼ ●  ☼\n" +
                "☼☼                  ©        ☼\n" +
                "☼☼            $              ☼\n" +
                "☼☼ ○    ○   ○  ●             ☼\n" +
                "☼#                           ☼\n" +
                "☼☼               ○      ○    ☼\n" +
                "☼☼        ○                  ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.RIGHT);
    }

    private void assertAI(String board, Direction expected) {
        String actual = ai.get(board(board));
        Assert.assertEquals(expected.toString(), actual);
    }
}
