package com.github.illya13.snakebattle;


import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import static io.jenetics.engine.EvolutionResult.toBestGenotype;


public class GATest {
    Genotype<IntegerGene> best;
    private Factory<Genotype<IntegerGene>> genotypeFactory;
    private Engine<IntegerGene, Integer> engine;

    private Integer fitness(Genotype<IntegerGene> gt) {
        int sum = 0;
        Chromosome<IntegerGene> chromosome = gt.getChromosome();
        for (int i=0; i<chromosome.length(); i++) {
            sum += chromosome.getGene(i).intValue();
        }
        return sum;
    }


    @Before
    public void setup() {
        IntegerGene gene = IntegerGene.of(0, 10);
        best = Genotype.of(IntegerChromosome.of(
                gene.newInstance(10), gene.newInstance(10), gene.newInstance(10)
        ));


        final Selector<IntegerGene, Integer> survivorsSelector = new TournamentSelector(3);
        final Selector<IntegerGene, Integer> offspringSelector = new TournamentSelector(3);
        Alterer<IntegerGene, Integer> alterer = Alterer.of(new Alterer[]{
                new UniformCrossover(0.2d, 0.2d),
                new Mutator(0.15d)
        });


        genotypeFactory = Genotype.of(IntegerChromosome.of (0, 10 , 3));
        engine = Engine.builder(this::fitness, genotypeFactory)
                .populationSize(10)
                .survivorsSelector((population, count, optimize) -> {
                    ISeq<Phenotype<IntegerGene, Integer>> selected = survivorsSelector.select(population, count, optimize);
                    return selected.map(s -> s.newInstance(s.getGenotype()));
                })
                .offspringSelector(offspringSelector)
                .alterers(alterer)
                .build();

    }

    @After
    public void shutdown() {
    }

    @Test
    public void gaTest1() {
        Genotype<IntegerGene> result = engine.stream()
                .limit(100)
                .collect(toBestGenotype());
        Assert.assertEquals(best, result);
    }
}
