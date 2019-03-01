package edu.sdet.logic;

import edu.sdet.dao.GeneRepository;
import edu.sdet.dao.WordDao;
import edu.sdet.dao.WordDaoSqlite;
import edu.sdet.entities.Gene;
import edu.sdet.entities.Keyword;
import edu.sdet.entities.Sign;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * project FunctionalAnalysisClient
 * Created by ayyoub on 5/4/18.
 */
@Service
public class KeywordExtractor {

    @Autowired
    private GeneRepository geneRepository;

    private List<Gene> genes;

    public KeywordExtractor() {
    }

    public List<String> getKeywords() {
        Set<Keyword> venn = new HashSet<>();

        genes.stream().map(g -> geneRepository.findGeneBySymbol(g.getSymbol()).get(0)).map(this::getGeneKeywords).forEach(venn::addAll);

        //TODO remove this for production
        System.out.println("--------------------------");
        List<Keyword> l = venn.stream().filter(e -> e.getFrequency() == venn.stream().sorted().findFirst().map(Keyword::getFrequency).get())
                .sorted().collect(Collectors.toList());

        l.forEach(e -> System.out.println(String.format("|%-20.20s%-4d|", e.getLemm(), e.getFrequency())));
        System.out.println("--------------------------");

        return l.stream().map(Keyword::getLemm).collect(Collectors.toList());
    }

    /**
     * return a set of keywords for a given gene
     *
     * @param g the gene
     * @return set of keywords
     */
    public Set<Keyword> getGeneKeywords(Gene g) {
        StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
        StringBuilder sb = new StringBuilder();
        g.getEvidences().forEach(sb::append);
        g.getOmim().forEach(sb::append);
        g.getMousemine().forEach(sb::append);
        g.getArticles().forEach(e -> sb.append(e.getTitle()));

        Set<Keyword> list = lemmatizer.getKeywords(sb.toString());

        long limit = (long) (list.size() * 0.05);

        WordDao dao = new WordDaoSqlite();

        list = list.stream()
                .sorted()
                .limit(limit)
                .filter(e -> Objects.requireNonNull(getPOS(e.getLemm())).equals("NN"))
                .collect(Collectors.toSet());
        int totalCount = list.stream().mapToInt(Keyword::getFrequency).sum();
        int refTotalCount = list.stream().mapToInt(e -> dao.getWord(e.getLemm()).getCount()).sum();

        // TODO threshold should be an adjusted p-value
        ChiSquaredDistribution distribution = new ChiSquaredDistribution(1);
        double adjA = 1 - Math.pow((1 - 0.001), 1. / list.size());
        double th = distribution.inverseCumulativeProbability(1 - adjA);

        return list.stream()
                .filter(e -> significance(e.getFrequency(), dao.getWord(e.getLemm()).getCount(), totalCount, refTotalCount) > th)
                .filter(e -> sign(e.getFrequency(), dao.getWord(e.getLemm()).getCount(), totalCount, refTotalCount).equals(Sign.INCREASE))
                .map(e -> new Keyword(e.getLemm()))
                .collect(Collectors.toSet());

    }

    /**
     * find Part of speech annotation for a given word
     *
     * @param text word to annotate
     * @return Part of speech symbol
     */
    private String getPOS(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        // we have only one word: so one sentence with one token
        return sentences.get(0) // get sentence
                .get(CoreAnnotations.TokensAnnotation.class)
                .get(0) // get token
                .get(CoreAnnotations.PartOfSpeechAnnotation.class);

    }

    /**
     * Calculates G square statistic for the given inputs
     *
     * @param count    word count
     * @param refCount word count in reference DB
     * @param total    total count of words
     * @param refTotal total count of words from the DB
     * @return G square statistic
     */
    private double significance(int count, int refCount, int total, int refTotal) {
        double a = (double) count;
        double b = (double) refCount;
        double c = (double) total;
        double d = (double) refTotal;

        double E1 = c * (a + b) / (c + d);
        double E2 = d * (a + b) / (c + d);

        return 2 * ((a * Math.log(a / E1)) + (b * Math.log(b / E2)));
    }

    /**
     * Determines if the frequency increases or decreases
     *
     * @param count    word count
     * @param refCount word count in reference DB
     * @param total    total count of words
     * @param refTotal total count of words from the DB
     * @return sign
     */
    private Sign sign(int count, int refCount, int total, int refTotal) {
        double a = (double) count;
        double b = (double) refCount;
        double c = (double) total;
        double d = (double) refTotal;

        return (a / c) > (b / d) ? Sign.INCREASE : Sign.DECREASE;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }
}
