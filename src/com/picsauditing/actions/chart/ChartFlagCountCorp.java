package com.picsauditing.actions.chart;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.chart.ChartMultiSeries;
import com.picsauditing.util.chart.DataRow;
import com.picsauditing.util.chart.DataSet;
import com.picsauditing.util.chart.MultiSeriesConverter;
import com.picsauditing.util.chart.Set;

@SuppressWarnings("serial")
public class ChartFlagCountCorp extends ChartMSAction {
	private static final Logger logger = LoggerFactory.getLogger(ChartFlagCountCorp.class);

	@Autowired
	private OperatorAccountDAO operatorAccountDAO;

	public ChartMultiSeries buildChart() throws Exception {
		setupChart();
		List<DataRow> data = findData();
		convertChartToMultiSeries(data);
		return chart;
	}

	private void convertChartToMultiSeries(List<DataRow> data) {
		MultiSeriesConverter converter = new MultiSeriesConverter();
		converter.setChart(chart);
		converter.addData(data);
		Map<String, DataSet> dataSet = converter.getChart().getDataSets();
		for (DataSet row : dataSet.values()) {
			row.setShowValues(false);
			FlagColor flagColor = FlagColor.valueOf(row.getSeriesName());

			for (Set set : row.getSets().values()) {
				set.setColor(flagColor.getHex());
			}

			row.setSeriesName(getText(flagColor.getI18nKey()));
		}
	}

	private List<DataRow> findData() throws SQLException {
		SelectSQL sql = createSql();
		List<DataRow> data = runSql(sql);
		replaceLinksExpandCorporateToOperatorChildren(data);
		return data;
	}

	private List<DataRow> runSql(SelectSQL sql) throws SQLException {
		ChartDAO db = new ChartDAO();
		List<DataRow> data = db.select(sql.toString());
		return data;
	}

	private SelectSQL createSql() {
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addField("flag as series");
		sql.addField("op.name as label");
		sql.addField("count(*) as value");
		sql.addField("CONCAT('ReportContractorOperatorFlag.action?button=Search%26filter.flagStatus=',gc.flag,'%26filter.operator=',op.id) as link");
		sql.addGroupBy("series, label");
		sql.addOrderBy("series, label");
		sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
		sql.addJoin("JOIN operators o on o.id = gc.genID");
		sql.addJoin("JOIN accounts op ON op.id = gc.genID");
		sql.addJoin("JOIN facilities fac ON fac.opID = gc.genID");
		sql.addWhere("a.status IN ('Active'" + (permissions.getAccountStatus().isDemo() ? ",'Demo')" : ")"));
		sql.addWhere("op.status IN ('Active'" + (permissions.getAccountStatus().isDemo() ? ",'Demo')" : ")"));
		sql.addWhere("gc.flag != 'Clear'");
		sql.addWhere("o.approvesRelationships='No' OR gc.workStatus='Y'");
		sql.addWhere("fac.corporateID = " + permissions.getAccountId());
		return sql;
	}

	private void setupChart() {
		chart.setShowLegend(false);
		chart.setShowValues(false);
		chart.setAnimation(false);
	}

	private void replaceLinksExpandCorporateToOperatorChildren(List<DataRow> data) {
		for (DataRow row : data) {
			String link = row.getLink();
			if (Strings.isNotEmpty(link)) {
				Integer opId = extractOperatorIdFromLink(link);
				OperatorAccount operator = operatorAccountDAO.find(opId);
				List<Integer> opIds = new ArrayList<Integer>();
				childrenOperatorsFromCorporate(operator, opIds);
				StringBuffer newLink = new StringBuffer();
				String[] linkParts = link.split("%26");
				int count = 1;
				for (String part : linkParts) {
					if (part.startsWith("filter.operator")) {
						for (Integer id : opIds) {
							newLink.append("%26").append("filter.operator=").append(id);
						}
					} else {
						if (count > 1) {
							newLink.append("%26");
						}
						newLink.append(part);
					}
					count++;
				}
				row.setLink(newLink.toString());
			}
		}
	}

	private void childrenOperatorsFromCorporate(OperatorAccount operator, List<Integer> opIds) {
		if (operator.getChildOperators().isEmpty()) {
			opIds.add(operator.getId());
		} else {
			List<OperatorAccount> childOperators = operator.getChildOperators();
			for (OperatorAccount childOperator : childOperators) {
				childrenOperatorsFromCorporate(childOperator, opIds);
			}
		}
	}

	private Integer extractOperatorIdFromLink(String link) {
		if (Strings.isNotEmpty(link)) {
			String[] linkParts = link.split("%26");
			for (String part : linkParts) {
				if (part.startsWith("filter.operator")) {
					String[] argumentParts = part.split("=");
					try {
						return new Integer(argumentParts[1]);
					} catch (NumberFormatException e) {
						logger.error("the filter.operator argument is not a valid integer: {}", e.getMessage());
					}
				}
			}
		}
		return null;
	}
}
