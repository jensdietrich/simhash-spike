package com.github.jensdietrich.simhashspike;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class DataGenerator {

    public static final int LENGTH = 10;

    static List<String> generateEdges(int vertexTypesSize, int edgeTypesSize, int size) {
        List<String> edges = new ArrayList<>(); // may contain duplicates
        List<String> vertexTypes = generateRandomValues(vertexTypesSize);
        List<String> edgeTypes = generateRandomValues(edgeTypesSize);

        Random random = new Random();
        for (int i=0; i<size; i++) {
            Edge nextEdge = new Edge(
                vertexTypes.get(random.nextInt(vertexTypesSize)),
                edgeTypes.get(random.nextInt(edgeTypesSize)),
                vertexTypes.get(random.nextInt(vertexTypesSize))
            );
            edges.add(nextEdge.toCSV());
        }
        return edges;
    }

    static Set<Pair<Set<String>,Set<String>>> generateDataset(int vertexTypesSize, int edgeTypesSize, int datasetSize, int numberOfEdgesToBeUsed, int  minSetSize, int maxSetSize, int mutationCount) {
        List<String> edges = generateEdges(vertexTypesSize, edgeTypesSize, numberOfEdgesToBeUsed);
        Random random = new Random();
        Set<Pair<Set<String>,Set<String>>> dataset = new HashSet<>(datasetSize);
        for (int i=0; i<datasetSize; i++) {
            List<String> list1 = new ArrayList<>();
            int size = maxSetSize;
            for (int j=0; j<size; j++) {
                list1.add(edges.get(random.nextInt(edges.size())));
            }

            // set2  mutations
            List<String> list2 = new ArrayList<>(list1);

            for (int j=0; j<mutationCount; j++) {
                int mutation = random.nextInt(4);
                if (mutation == 0) {
                    // no mutation
                } else if (mutation == 1) { // addition
                    if (!list2.isEmpty()) {
                        int idx = random.nextInt(list2.size());
                        list2.add(idx, edges.get(random.nextInt(edges.size())));
                    }
                    else {
                        list2.add(edges.get(random.nextInt(edges.size())));
                    }
                } else if (mutation == 2) { // removal
                    if (!list2.isEmpty()  && list2.size()>minSetSize) {
                        int idx = random.nextInt(list2.size());
                        list2.remove(idx);
                    }
                } else if (mutation == 4) { // swap
                    if (!list2.isEmpty()) {
                        int idx1 = random.nextInt(list2.size());
                        int idx2 = random.nextInt(list2.size());
                        if (idx1 != idx2) {
                            Collections.swap(list2, idx1, idx2);
                        }
                    }
                }
            }

            Set<String> set1 = new HashSet<>(list1);
            Set<String> set2 = new HashSet<>(list2);

            dataset.add(Pair.of(set1, set2));

        }

        return dataset;



    }

    static List<String> generateRandomValues(int size) {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            values.add(RandomStringUtils.random(LENGTH,true,false));
        }
        return values;
    }
}
