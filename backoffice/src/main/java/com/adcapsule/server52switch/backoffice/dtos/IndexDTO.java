package com.adcapsule.server52switch.backoffice.dtos;

public class IndexDTO {
    private final String collection;
    private final String indexKey;
    private final String indexValue;
    private final String indexShowKey;
    private final String indexShowValue;


    public IndexDTO(String collection, String indexKey, String indexValue, String indexShowKey,String indexShowValue) {
        this.collection = collection;
        this.indexKey = indexKey;
        this.indexValue = indexValue;
        this.indexShowKey = indexShowKey;
        this.indexShowValue = indexShowValue;
    }

    // Getters and Setters
    public String getCollection() {
        return collection;
    }


    public String getIndexKey() {
        return indexKey;
    }

    public String getIndexValue() {
        return indexValue;
    }


    public String getIndexShowKey() {
        return indexShowKey;
    }


    public String getIndexShowValue() {
        return indexShowValue;
    }


}
