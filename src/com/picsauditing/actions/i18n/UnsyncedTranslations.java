package com.picsauditing.actions.i18n;

import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterTranslation;

@SuppressWarnings("serial")
public class UnsyncedTranslations extends ReportActionSupport {
	private SelectSQL sql = new SelectSQL("app_translation local");
	private ReportFilterTranslation filter = new ReportFilterTranslation();

	public UnsyncedTranslations() {
		orderByDefault = "local.msgKey, local.locale";
	}

	@Override
	public String execute() throws Exception {
		buildQuery();
		addFilterToSQL();
		run(sql);

		return SUCCESS;
	}

	private void buildQuery() {
		sql.addJoin("LEFT JOIN pics_config.app_translation config ON config.msgKey = local.msgKey AND config.locale = local.locale");
		sql.addWhere("config.id IS NULL OR local.msgValue != config.msgValue");
		
		sql.addField("local.msgKey");
		sql.addField("local.locale");
		sql.addField("local.msgValue translationLocal");
		sql.addField("config.msgValue translationConfig");
	}

	private void addFilterToSQL() {
		ReportFilterTranslation f = getFilter();

		if (filterOn(f.getQualityRating())) {
			String where = "local.qualityRating IN (";
			for (int index = 0; index < f.getQualityRating().length; index++) {
				if (index > 0)
					where += ", ";

				where += f.getQualityRating()[index];
			}

			where += ")";

			sql.addWhere(where);
		}

		if (filterOn(f.getKey())) {
			sql.addWhere("local.msgKey LIKE '%" + f.getKey() + "%'");
		}

		if (f.isRestrictToCurrentUser()) {
			sql.addWhere(String.format("local.createdBy = %d OR local.updatedBy = %d", permissions.getUserId()));
			setFiltered(true);
		}
	}

	public ReportFilterTranslation getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterTranslation filter) {
		this.filter = filter;
	}
}
