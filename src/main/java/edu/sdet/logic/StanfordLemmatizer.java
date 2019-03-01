package edu.sdet.logic; /**
 * project keywordsExtraction
 * Created by ayyoub on 3/17/18.
 */

import edu.sdet.entities.Keyword;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class StanfordLemmatizer {

    private StanfordCoreNLP pipeline;
    private Set<String> m_Words = new HashSet<>();
    private static final Map<Character, String> GREEK_TO_ROMAN = new HashMap<>();

    static {
        GREEK_TO_ROMAN.put('\u03b1', "alpha");
        GREEK_TO_ROMAN.put('\u03b2', "beta");
        GREEK_TO_ROMAN.put('\u03b3', "gamma");
        GREEK_TO_ROMAN.put('\u03b4', "delta");
        GREEK_TO_ROMAN.put('\u0394', "delta");
        GREEK_TO_ROMAN.put('\u03b5', "epsilon");
        GREEK_TO_ROMAN.put('\u03b6', "zeta");
        GREEK_TO_ROMAN.put('\u03b7', "eta");
        GREEK_TO_ROMAN.put('\u03b8', "theta");
        GREEK_TO_ROMAN.put('\u03b9', "iota");
        GREEK_TO_ROMAN.put('\u03ba', "kappa");
        GREEK_TO_ROMAN.put('\u03bb', "lambda");
        GREEK_TO_ROMAN.put('\u03bc', "mu");
        GREEK_TO_ROMAN.put('\u03bd', "nu");
        GREEK_TO_ROMAN.put('\u03be', "xi");
        GREEK_TO_ROMAN.put('\u03bf', "omicron");
        GREEK_TO_ROMAN.put('\u03c0', "pi");
        GREEK_TO_ROMAN.put('\u03c1', "rho");
        GREEK_TO_ROMAN.put('\u03c2', "sigma");
        GREEK_TO_ROMAN.put('\u03c3', "sigma");
        GREEK_TO_ROMAN.put('\u03a3', "sigma");
        GREEK_TO_ROMAN.put('\u03c4', "tau");
        GREEK_TO_ROMAN.put('\u03c5', "upsilon");
        GREEK_TO_ROMAN.put('\u03c6', "phi");
        GREEK_TO_ROMAN.put('\u03c7', "chi");
        GREEK_TO_ROMAN.put('\u03c8', "psi");
        GREEK_TO_ROMAN.put('\u03c9', "omega");
        GREEK_TO_ROMAN.put('\u03a9', "omega");
    }

    public StanfordLemmatizer() {
        Properties cfg;
        cfg = new Properties();
        cfg.put("annotators", "tokenize, ssplit, pos, lemma");
        loadStopWords();
        //cfg.setProperty("customAnnotatorClass.stopword", "StopwordAnnotator");

        this.pipeline = new StanfordCoreNLP(cfg);
    }

    public Set<Keyword> getKeywords(String text) {
        Set<Keyword> set = new HashSet<>();
        List<String> list = lemmatize(text);
        list = filter(list);
        list.forEach(e -> set.add(new Keyword(e)));
        return set;
    }

    private List<String> filter(List<String> list) {
        // convert to lower case, remove stop words + numbers + words shorter than 3 char
        list = list.stream().map(String::toLowerCase).filter(this::notStopWord)
                .filter(e -> !e.matches("[\\d/.]+"))
                .filter(e -> e.length() > 2)
                .filter(e -> e.matches("[\\p{InGREEK}\\p{IsLatin}\\w\\d-_]+"))
                .collect(Collectors.toList());

        // transform greek letters to english words
        list = list.stream().map(this::partialReplaceGreek).collect(Collectors.toList());
        return list;
    }

    private String partialReplaceGreek(String word) {
        for (Character c : GREEK_TO_ROMAN.keySet()) {
            if (word.contains(c.toString()))
                word = word.replaceAll(c.toString(), GREEK_TO_ROMAN.get(c));
        }
        return word;
    }

    private List<String> lemmatize(String text) {
        List<String> words = new ArrayList<>();
        Annotation document = new Annotation(text);
        this.pipeline.annotate(document);

        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                words.add(token.get(LemmaAnnotation.class));
            }
        }
        return words;
    }

    private void loadStopWords() {
        try {
            InputStream in = getClass().getResourceAsStream("/stopwords.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            br.lines().forEach(e -> m_Words.add(e));
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean notStopWord(String word) {
        return !m_Words.contains(word);
    }

}