package com.picsauditing.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class TranslationETL extends PicsActionSupport {
	private boolean importTranslations = false;
	private Date startDate;
	private String translations;

	private SelectSQL sql = new SelectSQL("app_translation t");
	private Database db = new Database();
	private int foundRows;

	public String execute() throws Exception {
		return SUCCESS;
	}

	public String importTranslation() throws Exception {
		return SUCCESS;
	}

	public String exportTranslation() throws Exception {
		if (startDate == null)
			addActionError("Missing date");
		else {
			String sqlDate = DateBean.toDBFormat(startDate);
			sql.addField("t.msgKey");
			sql.addField("t.locale");
			sql.addField("t.msgValue");
			sql.addField("t.createdBy");
			sql.addField("t.updatedBy");
			sql.addField("t.creationDate");
			sql.addField("t.updateDate");
			sql.addField("t.lastUsed");
			sql.addWhere("t.creationDate > '" + sqlDate + "' OR t.updateDate > '" + sqlDate + "'");
			sql.addOrderBy("t.updateDate DESC, t.creationDate DESC");

			List<BasicDynaBean> data = db.select(sql.toString(), true);
			foundRows = db.getAllRows();

			StringBuilder str = new StringBuilder();
			str.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			for (BasicDynaBean d : data) {
				str.append("<translation>\n");
				str.append("\t<msgKey>" + d.get("msgKey").toString() + "</msgKey>\n");
				str.append("\t<locale>" + d.get("locale").toString() + "</locale>\n");
				str.append("\t<msgValue>" + d.get("msgValue").toString() + "</msgValue>\n");

				if (d.get("createdBy") != null)
					str.append("\t<createdBy>" + d.get("createdBy").toString() + "</createdBy>\n");
				if (d.get("creationDate") != null)
					str.append("\t<creationDate>" + d.get("creationDate").toString() + "</creationDate>\n");
				if (d.get("updatedBy") != null)
					str.append("\t<updatedBy>" + d.get("updatedBy").toString() + "</updatedBy>\n");
				if (d.get("updateDate") != null)
					str.append("\t<updateDate>" + d.get("updateDate").toString() + "</updateDate>\n");
				if (d.get("lastUsed") != null)
					str.append("\t<lastUsed>" + d.get("lastUsed").toString() + "</lastUsed>\n");
				
				str.append("</translation>\n");
			}

			translations = str.toString();
		}

		return SUCCESS;
	}

	public boolean isImportTranslations() {
		return importTranslations;
	}

	public void setImportTranslations(boolean importTranslations) {
		this.importTranslations = importTranslations;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getTranslations() {
		return translations;
	}

	public void setTranslations(String translations) {
		this.translations = translations;
	}

	public int getFoundRows() {
		return foundRows;
	}
}
