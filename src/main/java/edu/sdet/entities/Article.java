package edu.sdet.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * project FunctionalAnalysisClient
 * Created by ayyoub on 4/11/18.
 */
public class Article implements Serializable {
    @JsonProperty("articleId")
    private String articleID;

    @JsonProperty("title")
    private String title;

    @JsonProperty("abstract")
    private String abstractText;

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    @Override
    public String toString() {
        return articleID.startsWith("10") ? String.format("%s [doi:%s]", title, articleID) : String.format("%s [pmid:%s]", title, articleID);
    }
}
