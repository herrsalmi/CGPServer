package edu.sdet.server.web;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import edu.sdet.dao.GeneRepository;
import edu.sdet.entities.Article;
import edu.sdet.entities.Gene;
import edu.sdet.entities.Request;
import edu.sdet.logic.ElasticSearchHandler;
import edu.sdet.logic.KeywordExtractor;
import edu.sdet.logic.SearchQuery;
import edu.sdet.logic.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.stream.Collectors;


/**
 * project server
 * Created by ayyoub on 5/30/18.
 */
@RestController
@CrossOrigin("*")
public class GeneRestService {

    @Autowired
    private GeneRepository geneRepository;

    @Autowired
    private KeywordExtractor keywordExtractor;

    @Autowired
    private ElasticSearchHandler esh;

    private boolean useSeedKeywords = false;


    @RequestMapping(value = "/gene/{symbol}", method=RequestMethod.GET)
    public Gene getGene(@PathVariable String symbol) {
        List<Gene> g = geneRepository.findGeneBySymbol(symbol);
        return g.get(0);
    }

    @RequestMapping(value = "/genes", method=RequestMethod.POST)
    public MappingJacksonValue getRanking(@RequestBody Request request) {
        Set<String> symbolSet = new HashSet<>(request.getCandidateGenes());
        // parse query
        SearchQuery query = new SearchQuery();
        query.parse(request.getQuery());

        // read seed genes and get seed keywords
        if (request.getSeedGenes() != null && !request.getSeedGenes().isEmpty()) {
            useSeedKeywords = true;
            List<String> keywords = getSeedKeywords(new HashSet<>(request.getSeedGenes()));
            // if one of the original terms appear in the additional keyword set, we remove it so that we don't count it twice
            keywords.removeAll(query.getTerms());
            if (!keywords.isEmpty())
                query.parse(String.join(" OR ", keywords));
        }

        // lookup genes from Elasticsearch
        esh.setQuery(query);
        Set<Gene> genes = esh.lookUpGenes(symbolSet);

        setScores(genes, query.getTerms());

        if (!useSeedKeywords) {
            double avg = genes.stream().filter(e -> new BigDecimal(e.getScore()).round(new MathContext(5)).doubleValue() > 0).mapToDouble(Gene::getScore).average().getAsDouble();
            //TODO fix the selection threshold
            //double avg = genes.stream().filter(e -> e.getScore() > 0).sorted(Comparator.comparingDouble(Gene::getScore)).limit(10).mapToDouble(Gene::getScore).average().getAsDouble();

            List<Gene> list = genes.stream().filter(e -> e.getScore() >= avg).collect(Collectors.toList());
            keywordExtractor.setGenes(list);
            List<String> keywords = keywordExtractor.getKeywords();
            // if one of the original terms appear in the additional keyword set, we remove it so that we don't count it twice
            keywords.removeAll(query.getTerms());
            if (!keywords.isEmpty())
                query.parse(String.join(" OR ", keywords));

            esh.setQuery(query);
            genes = esh.lookUpGenes(new HashSet<>(request.getCandidateGenes()));
            setScores(genes, query.getTerms());
        }

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("symbol", "score");

        FilterProvider filters = new SimpleFilterProvider().addFilter("resultFilter", filter);

        List<Gene> output = genes.stream().filter(e -> e.getScore() > 0)
                .sorted(Comparator.comparingDouble(Gene::getScore)).collect(Collectors.toList());

        Collections.reverse(output);

        MappingJacksonValue mapping = new MappingJacksonValue(output);

        mapping.setFilters(filters);
        return mapping;
    }

    private List<String> getSeedKeywords(Set<String> seedSymbols) {
        SearchQuery emptyQuery = new SearchQuery();
        emptyQuery.parse("");

        esh.setQuery(emptyQuery);
        List<Gene> seedGenes = new ArrayList<>(esh.lookUpGenes(seedSymbols));

        keywordExtractor.setGenes(seedGenes);
        return keywordExtractor.getKeywords();
    }


    @Deprecated
    private void setScores(Set<Gene> geneSet) {
        long Ft = geneSet.stream().
                filter(e -> !e.getArticles().isEmpty() && !e.getEvidences().isEmpty()).count();

        geneSet.forEach(e -> {
            int Ftd = e.getAlias().size() * 10 + e.getArticles().size() * 2
                    + e.getEvidences().size() + e.getMousemine().size() * 5;
            e.setScore(Math.log(1 + Ftd) * Math.log(geneSet.size() / (double) (1 + Ft)));
        });
    }

    private void setScores(Set<Gene> geneSet, List<String> terms) {
        long Ft = geneSet.stream().
                filter(e -> !e.getArticles().isEmpty() || !e.getEvidences().isEmpty()
                        || !e.getAlias().isEmpty() || !e.getMousemine().isEmpty()).count();

        for (String term : terms) {
            geneSet.forEach(e -> {
                int Ftd = countOccurence(e.getAlias(), term, 10)
                        + countOccurence(e.getEvidences(), term, 1)
                        + countOccurence(e.getArticles().stream().map(Article::getAbstractText).collect(Collectors.toList()), term, 2)
                        + countOccurence(e.getMousemine(), term, 5);
                e.setScore(Math.log(1 + Ftd) * Math.log((geneSet.size() * 2) / (double) (1 + Ft)));
            });
        }

    }

    private int countOccurence(List<String> list, String term, int boostFactor) {
        return boostFactor * list.stream().mapToInt(e -> TextUtils.occurences(e, term)).sum();
    }

}
