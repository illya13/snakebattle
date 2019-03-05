package com.github.illya13.snakebattle.solver;

import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.util.Factory;
import io.jenetics.util.IO;
import io.jenetics.util.ISeq;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

import static io.jenetics.engine.EvolutionResult.toBestEvolutionResult;

public class GAEngine {
    public static final int POPULATION = 50;
    public static final String FILENAME = "population.obj";

    private Factory<Genotype<IntegerGene>> genotypeFactory;
    private Engine<IntegerGene, Integer> engine;

    private BlockingQueue<Request> requests;

    private ExecutorService engineExecutor;
    private ExecutorService mainExecutor;

    int generation;
    ISeq<Phenotype<IntegerGene, Integer>> population;


    public GAEngine() {
        final Selector<IntegerGene, Integer> survivorsSelector = new TournamentSelector(3);
        final Selector<IntegerGene, Integer> offspringSelector = new TournamentSelector(3);
        Alterer<IntegerGene, Integer> alterer = Alterer.of(new Alterer[]{
                new UniformCrossover(0.2d, 0.2d),
                new Mutator(0.15d)
        });

        engineExecutor = Executors.newFixedThreadPool(4);
        mainExecutor = Executors.newSingleThreadExecutor();

        requests = new LinkedBlockingQueue<>();

        genotypeFactory = Genotype.of(IntegerChromosome.of (0, 10 , 11));
        engine = Engine.builder(this::fitness, genotypeFactory)
                .populationSize(POPULATION)
                .executor(engineExecutor)
                .survivorsSelector((population, count, optimize) -> {
                    ISeq<Phenotype<IntegerGene, Integer>> selected = survivorsSelector.select(population, count, optimize);
                    return selected.map(s -> s.newInstance(s.getGenotype()));
                })
                .offspringSelector(offspringSelector)
                .alterers(alterer)
                .build();

        mainExecutor.submit(this::run);
    }


    private void run() {
        generation = 1;

        population = load();
        while (!mainExecutor.isShutdown()) {
            population = evolve(population, generation);
            save(population);
            System.out.println("population: " + population);
            generation++;
        }
    }

    private ISeq<Phenotype<IntegerGene, Integer>> evolve(ISeq<Phenotype<IntegerGene, Integer>> population, int generation) {
        EvolutionStart<IntegerGene, Integer> start = EvolutionStart.of(population, generation);
        EvolutionResult<IntegerGene, Integer> result = engine.stream(start).limit(1).collect(toBestEvolutionResult());
        return result.getPopulation();
    }

    private Integer fitness(Genotype<IntegerGene> gt) {
        try {
            Request request = new Request(gt, new Exchanger<>());
            requests.put(request);
            return request.getExchanger().exchange(0);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public Request next() {
        try {
            return requests.take();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(generation);
    }

    // HELPERS

    private ISeq<Phenotype<IntegerGene, Integer>> load() {
        final File file = new File(FILENAME);
        try {
            return (ISeq<Phenotype<IntegerGene, Integer>>) IO.object.read(file);
        } catch (IOException e) {
            return generate();
        }
    }

    private void save(ISeq<Phenotype<IntegerGene, Integer>> population) {
        final File file = new File(FILENAME);
        try {
            IO.object.write(population, file);
        } catch (IOException ignored) {}
    }

    private ISeq<Phenotype<IntegerGene, Integer>> generate() {
        ISeq<Phenotype<IntegerGene, Integer>> population = genotypeFactory.instances()
                .map(gt -> Phenotype.of(gt, 1, this::fitness))
                .limit(POPULATION).collect(ISeq.toISeq());
        return population;
    }

    public void shutdown() {
        engineExecutor.shutdown();
        mainExecutor.shutdown();
    }

    public static class Request {
        private Genotype<IntegerGene> genotype;
        private Exchanger<Integer> exchanger;

        public Request(Genotype<IntegerGene> genotype, Exchanger<Integer> exchanger) {
            this.genotype = genotype;
            this.exchanger = exchanger;
        }

        public Genotype<IntegerGene> getGenotype() {
            return genotype;
        }

        public Exchanger<Integer> getExchanger() {
            return exchanger;
        }
    }
}
