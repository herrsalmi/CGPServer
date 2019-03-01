package edu.sdet.entities;

/**
 * project FunctionalAnalysisClient
 * Created by ayyoub on 4/11/18.
 */
public class Word {

    private String lemm;
    private int count;

    public Word(String lemm, int count) {
        this.lemm = lemm;
        this.count = count;
    }

    public String getLemm() {
        return lemm;
    }

    public void setLemm(String lemm) {
        this.lemm = lemm;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Word{" +
                "lemm='" + lemm + '\'' +
                ", count=" + count +
                '}';
    }
}
