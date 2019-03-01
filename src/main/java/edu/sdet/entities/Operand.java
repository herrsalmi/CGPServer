package edu.sdet.entities;

/**
 * project FunctionalAnalysisClient
 * Created by ayyoub on 5/10/18.
 */
public enum Operand {
    AND("&&"), OR("||");

    private String operand;

    Operand(String operand) {
        this.operand = operand;
    }

    public String getOperand() {
        return operand;
    }
}
