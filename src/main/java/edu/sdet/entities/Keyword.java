package edu.sdet.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * project keywordsExtraction
 * Created by ayyoub on 3/17/18.
 */
public class Keyword implements Comparable<Keyword>, Serializable {

    private final String lemm;
    private int frequency = 0;

    public Keyword(String lemm) {
        this.lemm = lemm;
        frequency++;
    }

    @Override
    public int compareTo(Keyword o) {
        // descending order
        return Integer.compare(o.frequency, frequency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Keyword keyword = (Keyword) o;
        if (Objects.equals(lemm, keyword.lemm)) {
            ((Keyword) o).incrementFrequency();
            return true;
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lemm);
    }

    public String getLemm() {
        return lemm;
    }

    public int getFrequency() {
        return frequency;
    }

    public synchronized void incrementFrequency() {
        this.frequency++;
    }
}
