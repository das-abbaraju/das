package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorRenewalPredictionView extends AbstractTable {

	public ContractorRenewalPredictionView() {
		super("(SELECT\n" +
                "\taccounts.id conID\n" +
                ",\tROUND((0.16065856 \n" +
                "\t+ ( 0.08340467 * \n" +
                "\t\tAVG(\n" +
                "\t\tCASE \tgeneralcontractors.flag\n" +
                "\t\tWHEN\n" +
                "\t\t\t\"Green\"\n" +
                "\t\tTHEN\n" +
                "\t\t\t3\n" +
                "\t\tWHEN\n" +
                "\t\t\t\"Amber\"\n" +
                "\t\tTHEN\n" +
                "\t\t\t2\n" +
                "\t\tELSE\n" +
                "\t\t\t1\n" +
                "\t\tEND \t\n" +
                "\t))\n" +
                "\t+ ( 0.20098547 * \n" +
                "\t\tCASE \tcontractor_info.paymentMethod\n" +
                "\t\tWHEN\n" +
                "\t\t\t\"Check\"\n" +
                "\t\tTHEN\n" +
                "\t\t\t1\n" +
                "\t\tELSE\n" +
                "\t\t\t0\n" +
                "\t\tEND\n" +
                "\t)\n" +
                "\t+ ( 0.461150415 * \n" +
                "\t\tIFNULL(contractor_info.ccOnFile,0)\n" +
                "\t)\n" +
                "\t+ ( 0.065215233 * \n" +
                "\t\tIFNULL(contractor_info.ccExpiration > \tcontractor_info.paymentExpires,0)\n" +
                "\t)\n" +
                "\t+ ( 0.050759683 * \n" +
                "\t\tCASE\n" +
                "\t\tWHEN\n" +
                "\t\t\tpayingFacilities \t<= 1\n" +
                "\t\tTHEN\n" +
                "\t\t\t1\n" +
                "\t\tWHEN\n" +
                "\t\t\tpayingFacilities \t= 2\n" +
                "\t\tTHEN\n" +
                "\t\t\t2\n" +
                "\t\tWHEN\n" +
                "\t\t\tpayingFacilities \t<= 12\n" +
                "\t\tTHEN\n" +
                "\t\t\t3\n" +
                "\t\tELSE\n" +
                "\t\t\t3.5\n" +
                "\t\tEND\n" +
                "\t))*100)\n" +
                "predictor\n" +
                "FROM\n" +
                "\taccounts\n" +
                "JOIN\n" +
                "\tcontractor_info\n" +
                "ON\tcontractor_info.id\t= accounts.id\n" +
                "JOIN\n" +
                "\tgeneralcontractors\n" +
                "ON\tgeneralcontractors.subID\t= contractor_info.id\n" +
                "JOIN\n" +
                "\taccounts \tclients\n" +
                "ON\tclients.id\t= generalcontractors.genID\n" +
                "AND\tclients.type\t= \"Operator\"\n" +
                "WHERE\t1=1\n" +
                "GROUP BY\n" +
                "\taccounts.id\n" +
                "\n)");

		addField(new Field("ContractorID", "conID", FieldType.Integer));
        addField(new Field("RenewalPrediction", "predictor", FieldType.Integer)).setImportance(FieldImportance.Required);
	}

    @Override
    protected void addJoins() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}