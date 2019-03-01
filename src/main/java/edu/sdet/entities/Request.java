package edu.sdet.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * project server
 * Created by ayyoub on 2019-01-12.
 */
public class Request {
    @JsonProperty("query")
    private String query;

    @JsonProperty("seedGenes")
    private List<String> seedGenes;

    @JsonProperty("candidateGenes")
    private List<String> candidateGenes;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getSeedGenes() {
        return seedGenes;
    }

    public void setSeedGenes(List<String> seedGenes) {
        this.seedGenes = seedGenes;
    }

    public List<String> getCandidateGenes() {
        return candidateGenes;
    }

    public void setCandidateGenes(List<String> candidateGenes) {
        this.candidateGenes = candidateGenes;
    }
}
