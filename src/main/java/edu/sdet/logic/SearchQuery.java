package edu.sdet.logic;

import edu.sdet.entities.Gene;
import edu.sdet.entities.Operand;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;

/**
 * project FunctionalAnalysisClient
 * Created by ayyoub on 5/10/18.
 */
public class SearchQuery {

    private ArrayList<String> terms;
    private ArrayList<Operand> operands;

    public SearchQuery() {
        terms = new ArrayList<>();
        operands = new ArrayList<>();
    }

    public void parse(String query) {
        String[] splited = query.trim().toLowerCase().split("\\s");
        for (int i = 0; i < splited.length; i++) {
            if (!splited[i].equals("and") && !splited[i].equals("or")) {
                terms.add(splited[i]);
                if (i+1 < splited.length && !splited[i+1].equals("and") && !splited[i+1].equals("or")) {
                    operands.add(Operand.OR);
                }
            } else if (splited[i].equals("and")) {
                operands.add(Operand.AND);
            } else if (splited[i].equals("or")) {
                operands.add(Operand.OR);
            }
        }
    }

    public boolean evaluate(Gene gene) {
        ArrayList<Boolean> ev = new ArrayList<>();

        for (String term : terms) {
            ev.add(gene.getAlias().stream().anyMatch(e -> TextUtils.containsIgnoreCase(e, term))
                    || gene.getEvidences().stream().anyMatch(e -> TextUtils.containsIgnoreCase(e, term))
                    || gene.getArticles().stream().anyMatch(e -> TextUtils.containsIgnoreCase(e.getTitle(), term)
                        || TextUtils.containsIgnoreCase(e.getAbstractText(), term))
                    || gene.getOmim().stream().anyMatch(e -> TextUtils.containsIgnoreCase(e, term))
                    || gene.getMousemine().stream().anyMatch(e -> TextUtils.containsIgnoreCase(e, term)));
        }

        try {
            ScriptEngineManager sem = new ScriptEngineManager();
            ScriptEngine se = sem.getEngineByName("JavaScript");
            StringBuilder expression = new StringBuilder();
            for (int i = 0; i < operands.size(); i++) {
                expression.append(ev.get(i)).append(operands.get(i).getOperand());
            }
            expression.append(ev.get(ev.size() - 1));
            return Boolean.valueOf(se.eval(expression.toString()).toString());
        } catch (ScriptException e) {
            System.out.println("Invalid Expression");
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<String> getTerms() {
        return terms;
    }
}
