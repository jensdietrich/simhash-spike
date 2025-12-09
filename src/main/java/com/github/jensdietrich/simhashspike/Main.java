package com.github.jensdietrich.simhashspike;

import com.dynatrace.hash4j.hashing.Hashing;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToLongFunction;
import com.dynatrace.hash4j.similarity.*;

public class Main {

    public static void main(String[] args) throws IOException {

        Set<Pair<Set<String>, Set<String>>> dataset = DataGenerator.generateDataset(1000, 100, 200, 1000, 10, 50, 50);

        List<String> results = new ArrayList<>();
        results.add("jaccard\tminhash");

        for (Pair<Set<String>, Set<String>> pair : dataset) {
            Set<String> set1 = new HashSet<>(pair.getLeft());
            Set<String> set2 = new HashSet<>(pair.getRight());
            double jaccard = jaccard(set1, set2);
            // int tlsh = tlsh(set1, set2);
            double minhash = minhash(set1, set2);
            results.add(""+jaccard + "\t" + minhash);
            System.out.println("jaccard: " + jaccard + ", minhash: " + minhash);
        }

        Path output = Path.of("output.tsv");
        Files.write(output,results);
        System.out.println("output: " + output);

    }



    static double jaccard(Set<String> set1, Set<String> set2) {
        Set<String> intersection = Sets.intersection(set1, set2);
        Set<String> union = Sets.union(set1, set2);
        if (union.isEmpty()) {
            return 0.0;
        }
        return (double) intersection.size() / union.size();
    }

    static double minhash(Set<String> set1, Set<String> set2) {

        int numberOfComponents = 1024;
        int bitsPerComponent = 1;
        SimilarityHashPolicy policy =
            SimilarityHashing.superMinHash(numberOfComponents, bitsPerComponent);
        SimilarityHasher simHasher = policy.createHasher();

        byte[] hash1 = minHashSign(set1,policy);
        byte[] hash2 = minHashSign(set2,policy);

        double fractionOfEqualComponents = policy.getFractionOfEqualComponents(hash1, hash2);

        // this formula estimates the Jaccard similarity from the fraction of equal components
        double estimatedJaccardSimilarity =
            (fractionOfEqualComponents - Math.pow(2., -bitsPerComponent))
                / (1. - Math.pow(2., -bitsPerComponent)); // gives a value close to 0.8

        return estimatedJaccardSimilarity;

    }

    private static byte[] minHashSign (Set<String> set,SimilarityHashPolicy policy ) {
        ToLongFunction<String> stringHashFunc = s -> Hashing.komihash5_0().hashCharsToLong(s);
        SimilarityHasher simHasher = policy.createHasher();
        return simHasher.compute(ElementHashProvider.ofCollection(set, stringHashFunc));
    }


}

