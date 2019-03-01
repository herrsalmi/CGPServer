package edu.sdet.logic;

import edu.sdet.dao.GeneRepository;
import edu.sdet.entities.Gene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * project FunctionalAnalysisClient
 * Created by ayyoub on 4/18/18.
 */
@Service
public class ElasticSearchHandler {
    private static final Logger LOGGER = LogManager.getRootLogger();
    @Autowired
    private GeneRepository geneRepository;
    private SearchQuery query;

    public ElasticSearchHandler() {
    }

    public ElasticSearchHandler(SearchQuery query) {
        this.query = query;
    }

    /**
     * Lookup gene from Elasticsearch using gene symbol
     *
     * @param symbols set of gene symbol
     * @return set of genes
     */
    public Set<Gene> lookUpGenes(Set<String> symbols) {
        Set<Gene> genes = new HashSet<>();
        symbols.forEach(s -> {
            List<Gene> g = geneRepository.findGeneBySymbol(s);
            if (!g.isEmpty()) {
                Gene gene = new Gene();
                gene.setSymbol(g.get(0).getSymbol());
                // check if the gene satisfies the search query
                if (! query.evaluate(g.get(0)))
                    return;
                // add aliases
                g.get(0).getAlias().stream().filter(e -> TextUtils.containsIgnoreCase(e, query.getTerms())).forEach(gene::addAlias);

                // add evidences
                g.get(0).getEvidences().stream().filter(e -> TextUtils.containsIgnoreCase(e, query.getTerms())).forEach(gene::addEvidence);

                // add articles
                g.get(0).getArticles().stream().filter(e -> TextUtils.containsIgnoreCase(e.getTitle(), query.getTerms())
                        || TextUtils.containsIgnoreCase(e.getAbstractText(), query.getTerms())).forEach(gene::addArticle);

                // add OMIM data
                g.get(0).getOmim().stream().filter(e -> TextUtils.containsIgnoreCase(e, query.getTerms())).forEach(gene::addOmim);

                // add KO studies
                g.get(0).getMousemine().stream().filter(e -> TextUtils.containsIgnoreCase(e, query.getTerms())).forEach(gene::addKoStudy);

                // add interactors
                gene.setInteractors(g.get(0).getInteractors());

                // add the gene to list
                genes.add(gene);
            } else {
                LOGGER.warn("Gene " + s + " not found!");
            }
        });
        return genes;
    }

    public void setQuery(SearchQuery query) {
        this.query = query;
    }
}
