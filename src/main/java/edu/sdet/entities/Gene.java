package edu.sdet.entities;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * project FunctionalAnalysisClient
 * Created by ayyoub on 4/11/18.
 */

@Document(indexName = "genome", type = "genes")
@JsonFilter("resultFilter")
public class Gene implements Serializable {

    @Id
    private String id;

    @JsonProperty("symbol")
    private String symbol;

    @JsonIgnore
    private String description;

    @JsonProperty("alias")
    private List<String> alias;

    @JsonProperty("evidence")
    private List<String> evidences;

    @Field(type = FieldType.Nested)
    @JsonProperty("article")
    private List<Article> articles;

    @JsonProperty("omim")
    private List<String> omim;

    @JsonProperty("mouseMine")
    private List<String> mousemine;

    @JsonProperty("interact")
    private List<String> interactors;

    @JsonProperty("score")
    private double score;

    public Gene(String symbol) {
        this.symbol = symbol;
    }

    public Gene() {
        alias = new ArrayList<>();
        evidences = new ArrayList<>();
        articles = new ArrayList<>();
        omim = new ArrayList<>();
        mousemine = new ArrayList<>();
        interactors = new ArrayList<>();
        description = "";
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }

    public List<String> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<String> evidences) {
        this.evidences = evidences;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public List<String> getOmim() {
        return omim;
    }

    public void setOmim(List<String> omim) {
        this.omim = omim;
    }

    public List<String> getMousemine() {
        return mousemine;
    }

    public void setMousemine(List<String> mousemine) {
        this.mousemine = mousemine;
    }

    public List<String> getInteractors() {
        return interactors;
    }

    public void setInteractors(List<String> interactors) {
        this.interactors = interactors;
    }

    public void addAlias(String a) {
        this.alias.add(a);
    }

    public void addEvidence(String evidence) {
        this.evidences.add(evidence);
    }

    public void addArticle(Article article) {
        this.articles.add(article);
    }

    public void addOmim(String text) {
        this.omim.add(text);
    }

    public void addKoStudy(String text) {
        this.mousemine.add(text);
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
