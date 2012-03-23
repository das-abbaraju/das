package com.picsauditing.actions.i18n;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterTranslation;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class UnsyncedTranslations extends ReportActionSupport {
	private SelectSQL sql = new SelectSQL("app_translation local");
	private ReportFilterTranslation filter = new ReportFilterTranslation();

	private int[] translationsToTransfer;

	private String target = "pics_config";
	private String local;
	private Database database;

	private String[] auditColumns = new String[] { "createdBy", "creationDate", "updatedBy", "updateDate" };

	public UnsyncedTranslations() {
		orderByDefault = "local.msgKey, local.locale";
	}

	@Override
	public String execute() throws Exception {
		addErrorIfOnTargetDatabase();

		if (!hasActionErrors()) {
			addAlertMessage("Syncing translations to <strong>" + target + "</strong>");
			buildQuery();
			addFilterToSQL();
			run(sql);
		}

		return SUCCESS;
	}

	public String sendToTarget() throws Exception {
		if (translationsToTransfer != null && translationsToTransfer.length > 0) {
			database = new Database();
			String fields = Strings.implode(getColumnsFromAppTranslation(), ", ");

			executeInsertSelectUpdate(fields);
			executeUpdateOtherLocalesToQuestionable();

			addActionMessage("Successfully inserted/updated " + translationsToTransfer.length + " translations into "
					+ target);
		} else {
			addActionError("Please select translations to transfer");
		}

		return execute();
	}

	public ReportFilterTranslation getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterTranslation filter) {
		this.filter = filter;
	}

	public int[] getTranslationsToTransfer() {
		return translationsToTransfer;
	}

	public void setTranslationsToTransfer(int[] translationsToTransfer) {
		this.translationsToTransfer = translationsToTransfer;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getLocal() throws SQLException {
		if (local == null) {
			local = Database.getDatabaseName();
		}

		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	private void buildQuery() {
		sql.addJoin("LEFT JOIN " + target + ".app_translation target ON target.msgKey = local.msgKey "
				+ "AND target.locale = local.locale");
		sql.addWhere("target.id IS NULL OR local.msgValue != target.msgValue");

		sql.addField("local.id");
		sql.addField("local.msgKey");
		sql.addField("local.locale");
		sql.addField("local.msgValue translationLocal");
		sql.addField("target.msgValue translationTarget");
	}

	private void addFilterToSQL() {
		ReportFilterTranslation f = getFilter();

		if (filterOn(f.getQualityRating())) {
			String where = "local.qualityRating IN (";
			for (int index = 0; index < f.getQualityRating().length; index++) {
				if (index > 0)
					where += ", ";

				where += f.getQualityRating()[index].ordinal();
			}

			where += ")";

			sql.addWhere(where);
		}

		if (filterOn(f.getKey())) {
			sql.addWhere("local.msgKey LIKE '%" + f.getKey() + "%'");
		}

		if (f.isRestrictToCurrentUser()) {
			sql.addWhere(String.format("local.createdBy = %1$d OR local.updatedBy = %1$d", permissions.getUserId()));
			setFiltered(true);
		}
	}

	private void addErrorIfOnTargetDatabase() throws SQLException {
		if (target.equals(Database.getDatabaseName())) {
			addActionError("You're currently on the target database, <strong>" + target
					+ "</strong>. NO translations will be moved.");
		}
	}

	private List<String> getColumnsFromAppTranslation() throws SQLException {
		List<String> fieldList = new ArrayList<String>();
		List<String> auditColumnsList = Arrays.asList(auditColumns);

		List<BasicDynaBean> columns = database.select("SHOW COLUMNS FROM " + target + ".app_translation", false);
		for (BasicDynaBean row : columns) {
			String field = row.get("Field").toString();

			if (!"id".equals(field) && !auditColumnsList.contains(field))
				fieldList.add(field);
		}

		return fieldList;
	}

	private void executeInsertSelectUpdate(String fields) throws SQLException {
		String insert = String.format("INSERT INTO %s.app_translation(%s, %s) ", target, fields, getAuditColumnList());
		String select = String.format("SELECT %s, %d, NOW(), NULL, NULL FROM %s.app_translation a WHERE a.id IN (%s) ",
				fields, permissions.getUserId(), getLocal(), Strings.implode(translationsToTransfer));
		String update = String.format("ON DUPLICATE KEY UPDATE msgValue = a.msgValue, updatedBy = %d, "
				+ "updateDate = NOW()", permissions.getUserId());

		database.executeInsert(insert + select + update);
	}

	private void executeUpdateOtherLocalesToQuestionable() throws SQLException {
		String updateOtherLocalesToQuestionable = "UPDATE " + target
				+ ".app_translation c SET c.qualityRating = 1, c.updatedBy = " + permissions.getUserId()
				+ ", c.updateDate = NOW() WHERE c.msgKey IN (SELECT msgKey FROM " + getLocal()
				+ ".app_translation WHERE id IN (" + Strings.implode(translationsToTransfer)
				+ ")) AND c.locale != c.sourceLanguage";

		database.executeUpdate(updateOtherLocalesToQuestionable);
	}

	private String getAuditColumnList() {
		String auditColumnList = "";

		for (int index = 0; index < auditColumns.length; index++) {
			if (index > 0)
				auditColumnList += ", ";

			auditColumnList += auditColumns[index];
		}

		return auditColumnList;
	}
}
