package com.picsauditing.actions.chart;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.chart.DataSet;
import com.picsauditing.util.chart.Set;

/**
 * Get a count of each flag color for this operator
 * 
 * @author Trevor
 */
public class ChartEmrRates extends ChartAction {

	public String execute() {
		try {
			loadPermissions();
			if (!permissions.isLoggedIn())
				throw new Exception();
		} catch (Exception e) {
			Set set = new Set();
			set.setLabel("Authentication ERROR");
			set.setValue(1);
			chart.getSets().add(set);
			return SUCCESS;
		}

		SelectSQL sql = new SelectSQL("accounts a");
		sql.addJoin("JOIN pqfdata d ON a.id = d.conID");
		sql.addField("d.questionID");
		sql.addField("floor(d.verifiedAnswer*10)/10 as label");
		sql.addField("count(*) as value");
		sql.addWhere("d.verifiedAnswer > 0");
		sql.addWhere("d.questionID IN ("+AuditQuestion.EMR06+", "+AuditQuestion.EMR07+")");
		sql.addGroupBy("questionID, label");
		sql.addOrderBy("questionID, label");
		
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " +permQuery.toString());

		try {
			Database db = new Database();
			List<BasicDynaBean> data = db.select(sql.toString(), false);
			chart.setMultiSeries(true);
			chart.setMaxCategory(1.2F);
			chart.setMinCategory(0.4F);
			chart.setCategoryDifference(0.1F);

			int id = 0;
			DataSet dataSet = new DataSet();
			for (BasicDynaBean row : data) {
				int questionID = (Integer)row.get("questionID");
				if (questionID != id) {
					dataSet = new DataSet();
					chart.getDataSets().add(dataSet);
					String seriesName = "Unknown";
					if (questionID == AuditQuestion.EMR07)
						seriesName = "2007 EMR";
					if (questionID == AuditQuestion.EMR06)
						seriesName = "2006 EMR";
					if (questionID == AuditQuestion.EMR05)
						seriesName = "2005 EMR";
					if (questionID == AuditQuestion.EMR04)
						seriesName = "2004 EMR";
					dataSet.setSeriesName(seriesName);
					id = questionID;
				}

				Float category = Float.parseFloat(row.get("label").toString());
				Float value = Float.parseFloat(row.get("value").toString());
				
				if (category < chart.getMinCategory())
					dataSet.addToMin(value);
				else if (category > chart.getMaxCategory())
					dataSet.getSets().get(category);
				else {
					Set set = new Set();
					set.setValue(value);
					int index = Math.round(category / chart.getCategoryDifference());
					dataSet.addSet(index, set);
				}
			}
			
		} catch (Exception e) {
			chart.setMultiSeries(false);
			Set set = new Set();
			set.setLabel("ERROR");
			set.setValue(1);
			chart.getSets().add(set);
		}

		return super.execute();
	}
}
