package com.picsauditing.actions.chart;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.ChartType;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.FusionChart;
import com.picsauditing.util.chart.MultiSeriesConverterHistogram;

@SuppressWarnings("serial")
public class GraphEmrRates extends ChartMSAction {
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	
	private ChartType chartType = ChartType.MSLine;
	private String flashChart;
	private String[] years = { "2008", "2009" };
	private boolean showAvg = false;
	private int[] operatorIDs;

	@RequiredPermission(value=OpPerms.EMRReport)
	public String execute() throws Exception {
		super.execute();
		flashChart = FusionChart.createChart("charts/" + chartType.toString() + ".swf", "", output,
				chartType.toString(), 600, 500, false, false);
		return SUCCESS;
	}

	@Override
	public ChartMultiSeries buildChart() throws Exception {
		chart.setCaption(getText("GraphEmrRates.title"));
		chart.setXAxisName(getText("GraphEmrRates.label.EmrRate"));
		chart.setYAxisName(getText("global.Contractors"));

		SelectSQL sql = new SelectSQL("accounts a");

		if (showAvg) {
			sql.addJoin("JOIN flag_criteria_contractor fcc ON fcc.criteriaID = " + FlagCriteria.EMR_AVERAGE_ID
					+ " AND fcc.conID = a.id");
			sql.addWhere("fcc.answer IS NOT NULL");
			sql.addWhere("fcc.answer > 0");
			sql.addGroupBy("label");
			sql.addOrderBy("fcc.conID DESC, label");
			sql.addField("'AVERAGE' as series");
			sql.addField("floor(fcc.answer*10)/10 as label");
			sql.addField("count(*) as value");
		} else {
			sql.addJoin("JOIN contractor_audit pqf ON pqf.conID = a.id");
			sql.addJoin("JOIN pqfdata d ON d.auditID = pqf.id");
			sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = pqf.id");
			sql.addWhere("cao.visible = 1");
			sql.addField("pqf.auditFor as series");
			sql.addField("floor(d.answer*10)/10 as label");
			sql.addField("count(*) as value");
			sql.addWhere("d.answer > 0");
			sql.addWhere("pqf.auditTypeID = 11");
			sql.addWhere("d.questionID = " + AuditQuestion.EMR);
			String auditFor = Strings.implodeForDB(getYears(), ",");
			if (!Strings.isEmpty(auditFor))
				sql.addWhere("pqf.auditFor IN (" + auditFor + ")");

			sql.addGroupBy("questionID, label");
			sql.addOrderBy("questionID DESC, label");
		}

		if (permissions.isAdmin() || permissions.isCorporate()) {
			if (operatorIDs != null && operatorIDs.length > 0) {
				sql.addWhere("a.id IN (SELECT gc.subID FROM generalcontractors gc WHERE gc.genID IN ("
						+ Strings.implode(operatorIDs, ",") + "))");
			}
		}
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " + permQuery.toString());

		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		for (DataRow row : data) {
			if (!showAvg) {
				int year = Integer.parseInt(row.getSeries());
				row.setSeries(year + " EMR");
				float labelEnd = Float.parseFloat(row.getLabel()) + 0.1F;
				row.setLink("ReportEmrRates.action?year=" + year + "%26amp;filter.minEMR=" + row.getLabel()
						+ "%26amp;filter.maxEMR=" + labelEnd);
			}
		}
		MultiSeriesConverterHistogram converter = new MultiSeriesConverterHistogram();

		converter.setMaxCategory(2);
		converter.setMinCategory(0);
		converter.setCategoryDifference(0.1F);
		converter.setChart(chart);

		converter.addData(data);
		return chart;
	}

	static public ChartType[] getChartTypeList() {
		ChartType[] charts = { ChartType.MSArea, ChartType.MSBar2D, ChartType.MSBar3D, ChartType.MSColumn2D,
				ChartType.MSColumn3D, ChartType.MSLine };
		return charts;
	}

	public String getFlashChart() {
		return flashChart;
	}

	public void setFlashChart(String flashChart) {
		this.flashChart = flashChart;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	public String[] getYears() {
		return years;
	}

	public void setYears(String[] years) {
		this.years = years;
	}

	public boolean isShowAvg() {
		return showAvg;
	}

	public void setShowAvg(boolean showAvg) {
		this.showAvg = showAvg;
	}

	public List<OperatorAccount> getOperatorsList() {
		if (permissions == null)
			return null;
		
		return operatorAccountDAO.findWhere(false, "", permissions);
	}

	public int[] getOperatorIDs() {
		return operatorIDs;
	}

	public void setOperatorIDs(int[] operatorIDs) {
		this.operatorIDs = operatorIDs;
	}

	public List<String> getYearsList() {
		List<String> yearsList = new ArrayList<String>();
		int lastYear = DateBean.getCurrentYear() - 1;
		for (int i = lastYear; i > 2000; i--) {
			yearsList.add(Integer.toString(i));
		}
		return yearsList;
	}
}
