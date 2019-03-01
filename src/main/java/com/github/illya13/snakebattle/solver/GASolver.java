package com.github.illya13.snakebattle.solver;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.github.illya13.snakebattle.Solver;
import com.github.illya13.snakebattle.State;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.util.Factory;
import io.jenetics.util.IO;
import io.jenetics.util.ISeq;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.github.illya13.snakebattle.board.Board.*;
import static io.jenetics.engine.EvolutionResult.toBestEvolutionResult;

public class GASolver implements Solver {
    public static final int POPULATION = 5;

    private Factory<Genotype<IntegerGene>> genotypeFactory;
    private Engine<IntegerGene, Integer> engine;
    private EvolutionResult<IntegerGene, Integer> result;
    private EvolutionStart<IntegerGene, Integer> start;


    private Map<Genotype<IntegerGene>, Integer> rewards;
    private ISeq<Phenotype<IntegerGene, Integer>> population;
    private int populationIndex;

    private Integer fitness(Genotype<IntegerGene> gt) {
        return rewards.get(gt);
    }

    public GASolver() {
        genotypeFactory = Genotype.of(IntegerChromosome.of (0, 10 , 11));
        engine = Engine.builder(this::fitness, genotypeFactory)
                .populationSize(POPULATION)
                .build();
    }

    @Override
    public void init() {
        initPopulation();
        System.out.println(population.get(populationIndex).getGenotype());
    }

    @Override
    public Direction next(State state) {
        Direction direction = findBest(state);
        return (direction != null) ? direction : Direction.RIGHT;
    }

    @Override
    public void done(int reward) {
        System.out.println(population.get(populationIndex).getGenotype() + " -> " + reward);
        rewards.put(population.get(populationIndex++).getGenotype(), reward);
    }


    private void initPopulation() {
        if ((population != null) && (populationIndex != population.size()))
            return;

        population = (population == null) ? loadResultOrCreateStart() : evolve();
        populationIndex = 0;
        rewards = new HashMap<>();
    }

    private ISeq<Phenotype<IntegerGene, Integer>> evolve() {
        if (result != null)
            save(result);

        EvolutionStream<IntegerGene, Integer> stream = (result != null) ? engine.stream(result) : engine.stream(start);
        result = stream.limit(1).collect(toBestEvolutionResult());

        System.out.printf("evolve: %d\n", result.getGeneration());
        return result.getPopulation();
    }

    private ISeq<Phenotype<IntegerGene, Integer>> loadResultOrCreateStart() {
        result = load();
        start = start();

        return (result == null) ? start.getPopulation() : result.getPopulation();
    }

    private EvolutionResult<IntegerGene, Integer> load() {
        final File file = new File("evolution.obj");
        try {
            return (EvolutionResult<IntegerGene, Integer>) IO.object.read(file);
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return null;
    }

    private void save(EvolutionResult<IntegerGene, Integer> result) {
        final File file = new File("evolution.obj");
        try {
            IO.object.write(result, file);
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    private EvolutionStart<IntegerGene, Integer> start() {
        ISeq<Phenotype<IntegerGene, Integer>> population = genotypeFactory.instances()
                .map(gt -> Phenotype.of(gt, 1, this::fitness))
                .limit(POPULATION).collect(ISeq.toISeq());

        return EvolutionStart.of(population, 1);
    }


    private Direction findBest(State state) {
        double max = Double.NEGATIVE_INFINITY;
        Direction direction = null;

        for (Action action: getActions(state)) {
            System.out.println(action);
            if (action.rewards() > max ) {
                max  = action.rewards();
                direction = action.direction();
            }
        }
        return direction;
    }

    private List<Action> getActions(State state) {
        Direction inverted = state.me().direction().inverted();
        List<Action> actions = new LinkedList<>();
        for(Direction direction: all) {
            if (direction.equals(inverted))
                continue;

            actions.add(new Action(state, direction));
        }
        return actions;
    }

    private static class Action {
        private Direction direction;
        private Point point;
        private Map<Features.FEATURE, Features.Reward> features;

        public Action(State state, Direction direction) {
            this.direction = direction;
            this.point = direction.change(state.me().head());

            features = new Features(state, point).all();
        }

        public Direction direction() {
            return direction;
        }

        public String rewardsAsString() {
            StringBuilder sb = new StringBuilder();
            for (Features.FEATURE feature: features.keySet()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(String.format("%s: %.3f", feature, features.get(feature).reward()));
            }
            return sb.toString();
        }

        public double rewards(){
            double total = 0;
            for (Features.FEATURE feature: features.keySet()) {
                total += features.get(feature).reward();
            }
            return total;
        }

        public String toString() {
            return direction + "[" + String.format("%.3f", rewards())+ "] {" + rewardsAsString() + "}";
        }
    }
}
