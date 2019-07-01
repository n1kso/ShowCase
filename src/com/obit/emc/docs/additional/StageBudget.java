package com.obit.emc.docs.additional;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 23.06.14
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
public class StageBudget {

    public StageBudget(String _stageNumber, String _lineNumber, String _payacc_id) {
        lineNumber = _lineNumber;
        orgaccount_id = _payacc_id;
        stageNumber = _stageNumber;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public String getOrgaccount_id() {
        return orgaccount_id;
    }

    public String getStageNumber() {
        return stageNumber;
    }

    private String stageNumber = "";
    private String lineNumber = "";
    private String orgaccount_id = "";
}
