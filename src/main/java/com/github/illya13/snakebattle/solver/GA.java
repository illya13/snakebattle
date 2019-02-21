package com.github.illya13.snakebattle.solver;

import io.jenetics.*;

import java.util.Random;

public class GA {
    final static int TARGET = 13;

    static Random r = new Random();

    public static IntegerGene generateRandom() {
        return IntegerGene.of(r.nextInt(100), 0, 100);
    }

    private static Integer fitness(Genotype<IntegerGene> gt) {
        return 100-Math.abs(TARGET - gt.getChromosome().getGene().intValue());
    }

    public static void main(String[] args) {
/*

        Factory<Genotype<IntegerGene>> gtf = Genotype
                .of(IntegerChromosome.of());
        final Engine<IntegerGene, Integer> engine = Engine.builder(GA::fitness, gtf)
                .offspringSelector(new RouletteWheelSelector<>()).build();
        engine.

        // System.out.println(engine.stream().limit(1).toArray());
        //Genotype<IntegerGene> result = engine.stream().limit(1).collect(EvolutionResult.toBestGenotype());
        // System.out.println("Selected: " + result);
*/
    }
}