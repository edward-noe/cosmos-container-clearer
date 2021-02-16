package com.github.edwardnoe.cosmoscontainerclearer;

public class PartitionKeyIdPair {
    private String id;
    private String partitionKey;

    public PartitionKeyIdPair() { }

    public PartitionKeyIdPair(String id, String partitionKey) {
        this.id = id;
        this.partitionKey = partitionKey;
    }

    public String getId() {
        return id;
    }

    public String getPartitionKey() {
        return partitionKey;
    }
}