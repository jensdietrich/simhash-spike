package com.github.jensdietrich.simhashspike;

public record Edge(String source, String relationship, String destination) {
    String toCSV() {
        return source + "," + relationship + "," + destination;
    }
}
