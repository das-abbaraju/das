package com.picsauditing.actions.report;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.DoubleMap;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.util.PermissionQueryBuilder;

@SuppressWarnings("serial")
public class OperatorFlagMatrix extends ReportAccount {

	private Set<FlagCriteria> flagCriteria = new TreeSet<FlagCriteria>();

	private TableDisplay tableDisplay;

	@Override
	protected void checkPermissions() throws Exception {
		super.checkPermissions();
		
		if (!permissions.isOperatorCorporate())
			throw new NoRightsException("You must be an operator to view this page");
	}
	
	public OperatorFlagMatrix(OperatorAccountDAO operatorDAO) {
		setReportName("Contractor Operator Flag Matrix");
		this.listType = ListType.Operator;
		this.orderByDefault = "fc.displayOrder, fc.label";
	}

	@Override
	protected void buildQuery() {

		sql = new SelectAccount();
		sql.setType(SelectAccount.Type.Contractor);
		sql.addJoin("JOIN flag_data fd ON fd.conID = a.id");
		sql.addJoin("JOIN flag_criteria fc ON fd.criteriaID = fc.id AND fc.insurance = 0");

		if (permissions.isCorporate())
			sql.addJoin("JOIN facilities f on fd.opID = f.opID AND f.corporateID = " + permissions.getAccountId());
		else if (permissions.isOperator())
			sql.addWhere("fd.opID = " + permissions.getAccountId());

		sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id "
				+ "AND gc.genID = fd.opID AND gc.flag IN ('Red', 'Amber')");

		sql.addWhere("fd.flag in('Red','Amber')");

		sql.addGroupBy("a.id, fc.id");

		sql.addField("fc.id criteriaID");
		sql.addField("fc.label");
		sql.addField("fc.description");
		sql.addField("MAX(fd.flag) flag");

		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		sql.addWhere("1 " + permQuery.toString());

		report.setLimit(100000);
	}

	public Set<FlagCriteria> getFlagCriteria() {
		return flagCriteria;
	}

	public TableDisplay getTableDisplay() {
		if (tableDisplay == null)
			tableDisplay = new TableDisplay(data);

		return tableDisplay;
	}

	public class TableDisplay {

		private Set<String> columns = new TreeSet<String>();
		private Map<String, String> columnIds = new HashMap<String, String>();
		private Set<String> headers = new LinkedHashSet<String>();
		private Map<String, String> headerHover = new HashMap<String, String>();

		private DoubleMap<String, String, String> content = new DoubleMap<String, String, String>();

		public TableDisplay(List<BasicDynaBean> data) {
			for (final BasicDynaBean d : data) {
				columns.add(d.get("name").toString());

				columnIds.put(d.get("name").toString(), d.get("id").toString());

				headers.add(d.get("label").toString());
				headerHover.put(d.get("label").toString(), d.get("label").toString());

				content.put(d.get("name").toString(), d.get("label").toString(), FlagColor.valueOf(
						d.get("flag").toString()).getSmallIcon());
			}
		}

		public String getContent(String k1, String k2) {
			return content.get(k1, k2);
		}

		public Set<String> getColumns() {
			return columns;
		}

		public Map<String, String> getColumnIds() {
			return columnIds;
		}

		public Set<String> getHeaders() {
			return headers;
		}

		public Map<String, String> getHeaderHover() {
			return headerHover;
		}
	}
}
