package com.codenjoy.dojo.snakebattle.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Learning {
    enum FEATURE {
        STONES, ATTACK, DESTRUCT, SHORT, MEDIUM
    }

    public static abstract class Strategy {
        protected Set<FEATURE> features;
        protected Map<FEATURE, Double> weights;

        protected Dice dice;

        public Strategy(Dice dice) {
            this.dice = dice;
        }

        public boolean hasFeature(FEATURE feature) {
            return features.contains(feature);
        }

        public abstract void init();
    }

    private Strategy strategy;

    public Learning() {
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void init(){
        strategy.init();
    }

    public static class DefaultStrategy extends Strategy {
        public DefaultStrategy(Dice dice) {
            super(dice);

            features = new HashSet<>();
            weights = new HashMap<>();

            weights.put(FEATURE.STONES, 90d);
            weights.put(FEATURE.ATTACK, 30d);
            weights.put(FEATURE.DESTRUCT, 70d);
            weights.put(FEATURE.SHORT, 70d);
            weights.put(FEATURE.MEDIUM, 80d);
        }

        public void init() {
            features.clear();
            for(FEATURE feature: weights.keySet()) {
                int rnd = dice.next(100);
                System.out.printf("%s %d", feature, rnd);
                if (rnd < weights.get(feature)) {
                    System.out.printf(" ... adding\n");
                } else {
                    System.out.printf(" ... skipping\n");
                }
            }
        }

        @Override
        public String toString() {
            return features.toString();
        }
    }

    public static class Builder {
        private Learning learning;

        private Builder() {
            learning = new Learning();
        }

        public static Builder newLearning(){
            return new Builder();
        }

        public Builder withStrategy(Strategy strategy) {
            learning.strategy = strategy;
            return this;
        }

        public Learning build() {
            return learning;
        }
    }
}
