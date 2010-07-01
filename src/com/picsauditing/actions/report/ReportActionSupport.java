package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.search.Database;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ColorAlternater;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelSheet;

@SuppressWarnings("serial")
public class ReportActionSupport extends PicsActionSupport {
	protected Report report = new Report();
	protected ExcelSheet excelSheet = new ExcelSheet();
	protected List<BasicDynaBean> data;

	protected ListType listType;
	protected String orderByDefault = null;
	private String orderBy = null;
	protected int showPage;
	protected ColorAlternater color = new ColorAlternater();
	protected String reportName = null;

	protected boolean download = false;
	protected boolean mailMerge = false;
	private Boolean filtered = null;
	protected boolean filteredDefault = false;

	public void run(SelectSQL sql) throws SQLException, IOException {
		if (download) {
			this.report.setLimit(100000);
			showPage = 1;
		}

		if (mailMerge) {
			this.report.setLimit(100000);
			showPage = 1;
		}

		isFiltered();
		if (filtered == null)
			filtered = filteredDefault;

		if (orderBy == null && orderByDefault != null)
			orderBy = orderByDefault;
		report.setOrderBy(this.orderBy, orderByDefault);
		report.setSql(sql);

		if (showPage > 0)
			report.setCurrentPage(showPage);

		data = report.getPage();

		logFilter();
	}

	public int getShowPage() {
		return showPage;
	}

	public void setShowPage(int showPage) {
		this.showPage = showPage;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public List<BasicDynaBean> getData() {
		return data;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public ColorAlternater getColor() {
		return color;
	}

	public void setColor(ColorAlternater color) {
		this.color = color;
	}

	public boolean isFiltered() {
		for (String filterName : report.getFilters().keySet()) {
			if (report.getFilters().get(filterName).isSet())
				filtered = true;
		}
		if (filtered == null)
			return false;

		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}

	public boolean filterOn(Object value, Object defaultValue) {
		if (value == null)
			return false;
		if (value.equals(defaultValue))
			return false;
		return value.toString().trim().length() > 0;
	}

	public boolean filterOn(Object value) {
		if (value == null)
			return false;
		return value.toString().trim().length() > 0;
	}

	public boolean filterOn(Object[] value) {
		if (value == null)
			return false;
		return value.length > 0;
	}

	public boolean filterOn(int[] value) {
		if (value == null)
			return false;
		if (value.length == 1) {
			if (value[0] == 0)
				return false;
		}
		return value.length > 0;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	private void logFilter() {
		try {
			AppPropertyDAO appPropertyDAO = (AppPropertyDAO) SpringUtils.getBean("AppPropertyDAO");
			AppProperty filterlog = appPropertyDAO.find("filterlog.enabled");
			if (filterlog != null && filterlog.getValue().equals("1")) {
				Set<String> params = new HashSet<String>();
				AppProperty ignoreProp = appPropertyDAO.find("filterlog.ignore");
				AppProperty ignoreValues = appPropertyDAO.find("filterlog.ignorevalues");
				Map<String, String> ignoreList = new HashMap<String, String>();
				if (ignoreValues != null && !Strings.isEmpty(ignoreValues.getValue())) {
					for (String tuple : ignoreValues.getValue().split(",")) {
						String[] kv = tuple.split(":");
						ignoreList.put(kv[0], kv[1]);
					}
				}

				Map<String, Object> map = ServletActionContext.getContext().getParameters();
				for (String key : map.keySet()) {
					if (key.startsWith("filter.")) {
						String filterName = key.replaceAll("^filter.", "");

						// Is this in the blacklist?
						if (ignoreProp == null || !ignoreProp.getValue().contains(filterName)) {
							Object value = map.get(key);
							String ignore = ignoreList.get(filterName);

							if (value != null) {
								Object[] val;
								if (value instanceof Object[]) {
									val = (Object[]) value;
								} else {
									val = new Object[] { value };
								}

								for (Object o : val) {
									if (!Strings.isEmpty(o.toString()) && (ignore == null || !ignore.equals(o))
											&& !((String) o).trim().matches("^-[^-]*-$")) {
										params.add(filterName);
										break;
									}
								}
							}
						}
					}
				}

				params.add("total");

				String name = ServletActionContext.getContext().getName().replaceFirst("Ajax$", "");
				// Update all the used parameters
				Database db = new Database();
				int count = db
						.executeUpdate("UPDATE app_filter_stats SET requestCount = requestCount + 1 WHERE searchPage = '"
								+ name + "' AND filterName in (" + Strings.implodeForDB(params, ",") + ")");

				if (count < params.size()) {
					List<BasicDynaBean> filterNames = db.select(
							"SELECT filterName FROM app_filter_stats WHERE searchPage = '" + name
									+ "' AND filterName in (" + Strings.implodeForDB(params, ",") + ")", false);

					for (BasicDynaBean bean : filterNames)
						params.remove(bean.get("filterName"));

					for (String param : params) {
						db.executeInsert("INSERT INTO app_filter_stats (searchPage, filterName, requestCount) "
								+ "VALUES ('" + name + "', '" + param + "', 1)");
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
