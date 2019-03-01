package edu.sdet.dao;

import edu.sdet.entities.Gene;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * project server
 * Created by ayyoub on 5/10/18.
 */
public interface GeneRepository extends ElasticsearchRepository<Gene, String> {
    List<Gene> findGeneBySymbol(String symbol);
}
