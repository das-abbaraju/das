package com.picsauditing.actions.rest.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.interceptor.ParameterAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Api;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.report.ReportData;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.search.Database;

@SuppressWarnings("serial")
public class ContractorList extends PicsActionSupport implements ParameterAware {
	private static final boolean USE_DYNAMIC_REPORTS = true;
	private static final Logger logger = LoggerFactory.getLogger(ContractorList.class);
	// private Report report;
	//
	// private ReportDAO reportDao;
	//
	// public Report getReport() {
	// return report;
	// }
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(final String apiKey) {
		logger.debug("Setting apiKey = " + apiKey);
		this.apiKey = apiKey;
	}

	@Override
	@Api
	public String execute() {
		if (USE_DYNAMIC_REPORTS) {
			try {
				// report = reportDao.find(Report.class, 1);
				return setUrlForRedirect("ReportData.action?report=10");
			} catch (IOException e) {
				return "";
			}
		} else {
			try {
				json = bruteForce();
				return JSON;
			} catch (SQLException e) {
				return "";
			}
		}
	}

	@Override
	public void setParameters(Map<String, String[]> parameters) {
		logger.debug("Setting parameters");
		for (String key : parameters.keySet()) {
			logger.debug(key + " = " + parameters.get(key)[0].toString());
			if ("apiKey".equals(key)) {
				setApiKey(parameters.get(key)[0]);
			}
		}

	}

	// public ReportDAO getReportDao() {
	// return reportDao;
	// }
	//
	// public void setReportDao(ReportDAO reportDao) {
	// this.reportDao = reportDao;
	// }

	private final static String TAX_ID = "contractorTaxID";
	private final static String ID = "contractorID";
	private final static String CITY = "contractorCity";
	private final static String ZIP = "contractorZip";
	private final static String STATE = "contractorState";
	private final static String NAME = "contractorName";
	private final static String COUNTRY = "contractorCountry";
	private final static String ADDRESS = "contractorAddress";
	private final static String FLAG = "contractorOperatorFlag";
	private final static String TYPE = "contractorOperatorNumberType";
	private final static String VALUE = "contractorOperatorNumberValue";

	@SuppressWarnings("unchecked")
	JSONObject bruteForce() throws SQLException {
		String sql = "select a.id, a.name, a.address, a.address2, a.address3, a.city, a.countrySubdivision, a.zip, a.country, c.taxID, con.type, con.value, gc.flag"
				+ " from accounts a"
				+ " join contractor_info c on a.id = c.id"
				+ " join generalcontractors gc on gc.subID = c.id and gc.genID = ?"
				+ " left join contractor_operator_number con on c.id = con.conID and con.opID = gc.genID";
		String sqlCount = "select count(a.id)" 
				+ " from accounts a" 
				+ " join contractor_info c on a.id = c.id"
				+ " join generalcontractors gc on gc.subID = c.id and gc.genID = ?";

		JSONObject output = new JSONObject();

		Integer recordCount = jdbcTemplate.queryForInt(sqlCount, new Object[] { getUser().getAccount().getId() });
		output.put("total", recordCount);

		JSONArray results = new JSONArray();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[] { getUser().getAccount().getId() });
		for (Map<String, Object> row : rows) {

			JSONObject data = new JSONObject();

			data.put(TAX_ID, row.get("taxID"));
			data.put(NAME, row.get("name"));
			data.put(ID, row.get("id"));
			data.put(COUNTRY, row.get("country"));
			data.put(CITY, row.get("city"));
			if (row.get("countrySubdivision") != null)
				data.put(STATE, row.get("countrySubdivision"));
			if (row.get("zip") != null)
				data.put(ZIP, row.get("zip"));
			if (row.get("type") != null)
				data.put(TYPE, row.get("type"));
			if (row.get("value") != null)
				data.put(VALUE, row.get("value"));
			if (row.get("flag") != null)
				data.put(FLAG, row.get("flag"));

			StringBuilder address = new StringBuilder();
			if (row.get("address") != null)
				address.append(row.get("address"));
			if (row.get("address2") != null)
				address.append(' ').append(row.get("address2"));
			if (row.get("address3") != null)
				address.append(' ').append(row.get("address3"));
			data.put(ADDRESS, address.toString());

			results.add(data);
		}
		output.put("data", results);
		return output;
	}
}
