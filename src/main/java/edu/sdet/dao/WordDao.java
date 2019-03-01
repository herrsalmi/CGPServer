package edu.sdet.dao;

import edu.sdet.entities.Word;

/**
 * project FunctionalAnalysisClient
 * Created by ayyoub on 4/11/18.
 */
public interface WordDao {
    void insertWord(Word word);

    Word getWord(String lemm);
}
