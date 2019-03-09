package com.github.illya13.snakebattle.solver;

import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.util.Factory;
import io.jenetics.util.IO;
import io.jenetics.util.ISeq;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.*;

import static io.jenetics.engine.EvolutionResult.toBestEvolutionResult;

public class GAEngine {
    public static final int POPULATION = 100;
    public static final String OBJ_FILENAME = "population.obj";
    public static final String TXT_FILENAME = "population.txt";

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

        genotypeFactory = Genotype.of(IntegerChromosome.of (0, 10 , 16));
        engine = Engine.builder(this::fitness, genotypeFactory)
                .populationSize(POPULATION)
                .executor(engineExecutor)
                .survivorsSelector((population, count, optimize) -> {
                    ISeq<Phenotype<IntegerGene, Integer>> selected = survivorsSelector.select(population, count, optimize);
                    return selected.map(s -> s.newInstance(s.getGenotype()));
                })
//                .survivorsSelector(survivorsSelector)
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
        final File file = new File(OBJ_FILENAME);
        try {
            return (ISeq<Phenotype<IntegerGene, Integer>>) IO.object.read(file);
        } catch (IOException e) {
            return generate();
        }
    }

    private void save(ISeq<Phenotype<IntegerGene, Integer>> population) {
        File objFile = new File(OBJ_FILENAME);
        Path path = Paths.get(TXT_FILENAME);
        try {
            IO.object.write(population, objFile);

            BufferedWriter writer = (!Files.exists(path))
                    ? Files.newBufferedWriter(path, StandardOpenOption.CREATE_NEW)
                    : Files.newBufferedWriter(path, StandardOpenOption.APPEND);

            savePopulationTxt(population, writer);
            writer.close();
        } catch (IOException ignored) {}
    }

    private void savePopulationTxt(ISeq<Phenotype<IntegerGene, Integer>> population, BufferedWriter writer) throws IOException {
        writer.write("generation: ");
        writer.write(String.valueOf(generation));
        writer.write('\n');

        int min = Integer.MAX_VALUE;
        int max = 0;
        int total = 0;
        Chromosome<IntegerGene> best = population.get(0).getGenotype().getChromosome();
        for (int i=0; i < population.size(); i++) {
            writer.write(population.get(i).toString());
            writer.write('\n');
            if (population.get(i).getFitness()> max) {
                max = population.get(i).getFitness();
                best = population.get(i).getGenotype().getChromosome();
            }
            if (population.get(i).getFitness() < min) {
                min = population.get(i).getFitness();
            }
            total += population.get(i).getFitness();
        }

        writer.write("min: ");
        writer.write(String.valueOf(min));
        writer.write('\t');

        writer.write("max: ");
        writer.write(String.valueOf(max));
        writer.write('\t');

        writer.write("avg: ");
        writer.write(String.valueOf(total / population.size()));
        writer.write('\n');

        writer.write(best.toString());
        writer.write("\n\n");
    }

    private ISeq<Phenotype<IntegerGene, Integer>> generate() {
        ISeq<Phenotype<IntegerGene, Integer>> population = genotypeFactory.instances()
                .map(gt -> Phenotype.of(gt, 1, this::fitness))
                .limit(POPULATION).collect(ISeq.toISeq());
        return population;
/*

        // LIVENESS BARRIER ENEMY STONE BODY
        // APPLE GOLD FURY FLY AVERAGE
        // STONE_N_FURY STONE_N_SIZE ENEMY_N_FURY ENEMY_N_SIZE

        // 3 10 10 10 3
        // 7 7 7 0 1
        // 3 1 3 1
        IntegerGene gene = IntegerGene.of(0, 10);

        Genotype<IntegerGene> guess = Genotype.of(IntegerChromosome.of(
                gene.newInstance(3), gene.newInstance(10), gene.newInstance(10), gene.newInstance(10), gene.newInstance(3),
                gene.newInstance(7), gene.newInstance(7), gene.newInstance(7), gene.newInstance(0), gene.newInstance(1),
                gene.newInstance(3), gene.newInstance(1), gene.newInstance(6), gene.newInstance(1)
        ));

        List<Genotype<IntegerGene>> list = new LinkedList<>();
        for (int i=0; i<POPULATION; i++)
            list.add(guess);

        ISeq<Phenotype<IntegerGene, Integer>> population = list.stream()
                .map(gt -> Phenotype.of(gt, 1, this::fitness))
                .limit(POPULATION).collect(ISeq.toISeq());
        return population;
*/
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
