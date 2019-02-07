package com.github.illya13.snakebattle;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * @author K.ilya
 *         Это пример для реализации unit-тестов твоего бота
 *         Необходимо раскомментировать существующие тесты, добиться их выполнения ботом.
 *         Затем создавай свои тесты, улучшай бота и проверяй что не испортил предыдущие достижения.
 */

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
        assertAI("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼☼                           ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼     ☼☼☼☼☼                 ☼\n" +
                "☼☼     ☼                     ☼\n" +
                "☼#     ☼☼☼     ○  ☼☼☼☼#╔►    ☼\n" +
                "☼☼     ☼          ☼   ☼╙     ☼\n" +
                "☼☼     ☼☼☼☼#      ☼☼☼☼#      ☼\n" +
                "☼☼                ☼          ☼\n" +
                "☼☼     ○          ☼     ●   $☼\n" +
                "☼☼ ○  ●                      ☼\n" +
                "☼#                    ○      ☼\n" +
                "☼☼                        ○  ☼\n" +
                "☼☼        ☼☼☼                ☼\n" +
                "☼☼       ☼  ☼                ☼\n" +
                "☼☼      ☼☼☼☼#     ☼☼   ☼#    ☼\n" +
                "☼☼      ☼   ☼     ☼ ☼ ☼ ☼ ○  ☼\n" +
                "☼#      ☼   ☼     ☼  ☼  ☼    ☼\n" +
                "☼☼ ○        ©     ☼    ˄☼    ☼\n" +
                "☼☼     ●          ☼    │☼   ●☼\n" +
                "☼☼                     └─┐   ☼\n" +
                "☼☼                       ¤   ☼\n" +
                "☼☼ ○    ○      ●             ☼\n" +
                "☼#                           ☼\n" +
                "☼☼                         $ ☼\n" +
                "☼☼                           ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.RIGHT);
    }

    @Test
    public void should6() {
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
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.DOWN);
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
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼\n", Direction.LEFT);
    }

    private void assertAI(String board, Direction expected) {
        String actual = ai.get(board(board));
        Assert.assertEquals(expected.toString(), actual);
    }
}
