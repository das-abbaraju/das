package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.search.SelectCase;
import com.picsauditing.search.SelectSQL;

public class ContractorRenewalPredictionView extends AbstractTable {

    private static final double INTERSECT = 0.16065856;
    private static final double AVG_FLAG_COEFFICIENT = 0.08340467;
    private static final double PAYS_WITH_CC_COEFFICIENT = 0.20098547;
    private static final double CC_ONFILE_COEFFICIENT = 0.461150415;
    private static final double CC_EXP_COEFFICIENT = 0.065215233;
    private static final double PAYING_FAC_COEFFICIENT = 0.050759683;

    public ContractorRenewalPredictionView() {
        super(getSelectClause());

        addField(new Field("ContractorID", "conID", FieldType.Integer));
        addField(new Field("RenewalPrediction", "predictor", FieldType.Integer)).setImportance(FieldImportance.Required);
    }

    private static String getSelectClause() {
        SelectSQL sql = new SelectSQL("accounts");
        sql.addJoin("JOIN contractor_info ON contractor_info.id = accounts.id");
        sql.addJoin("JOIN generalcontractors ON generalcontractors.subID = contractor_info.id");
        sql.addJoin("JOIN accounts clients ON clients.id = generalcontractors.genID AND clients.type = 'Operator'");
        sql.addGroupBy("accounts.id");
        sql.addField("accounts.id AS conID");
        String predictor = INTERSECT +
                getAverageFlagColor() +
                getPaysWithCreditCard() +
                getCcOnFile() +
                getCcExpiration() +
                getPayingFacilitiesRange();
        sql.addField("ROUND(100 * (" + predictor +
                ")) AS predictor");
        return "(" + sql.toString() + " )";
    }

    private static String add(double coefficient, String sql) {
        return " + (" + coefficient +
                " * " + sql + ")\n";
    }

    private static String getAverageFlagColor() {
        SelectCase column = new SelectCase("generalcontractors.flag");
        column.addCondition("'Green'", 3);
        column.addCondition("'Amber'", 2);
        column.setElse(1);
        return add(AVG_FLAG_COEFFICIENT, "AVG(" +
                column.toString() + ")");
    }

    private static String getPaysWithCreditCard() {
        SelectCase column = new SelectCase("contractor_info.paymentMethod");
        column.addCondition("'Check'", 1);
        column.setElse(0);
        return add(PAYS_WITH_CC_COEFFICIENT, column.toString());
    }

    private static String getCcOnFile() {
        String sql = "IFNULL(contractor_info.ccOnFile,0)";
        return add(CC_ONFILE_COEFFICIENT, sql);
    }

    private static String getCcExpiration() {
        String sql = "IFNULL(contractor_info.ccExpiration > contractor_info.paymentExpires,0)";
        return add(CC_EXP_COEFFICIENT, sql);
    }

    private static String getPayingFacilitiesRange() {
        SelectCase column = new SelectCase();
        column.addCondition("payingFacilities <= 1", 1);
        column.addCondition("payingFacilities <= 2", 2);
        column.addCondition("payingFacilities <= 12", 3);
        column.setElse("3.5");
        return add(PAYING_FAC_COEFFICIENT, column.toString());
    }

    @Override
    protected void addJoins() {
    }
}