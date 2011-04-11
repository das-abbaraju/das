package com.picsauditing.util;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.beanutils.BasicDynaBean;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class TranslationETL extends PicsActionSupport {
	private boolean importTranslations = false;
	private Date startDate;
	private String translations;
	private Map<String, List<AppTranslation>> importedTranslations;

	private SelectSQL sql = new SelectSQL("app_translation t");
	private Database db = new Database();
	private int foundRows;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

	@RequiredPermission(value = OpPerms.Translator)
	public String execute() throws Exception {
		return SUCCESS;
	}

	public String importTranslationAjax() throws Exception {
		importTranslations = true;
		List<String> msgKeyTerms = importXML();
		setupSQL("t.msgKey IN (" + Strings.implode(msgKeyTerms, ", ") + ")");
		List<BasicDynaBean> data = db.select(sql.toString(), false);

		for (BasicDynaBean d : data) {
			if (importedTranslations.get(d.get("msgKey").toString()) != null) {
				AppTranslation t = new AppTranslation();
				addField(t, "msgKey", d.get("msgKey").toString());
				addField(t, "msgValue", d.get("msgValue").toString());
				addField(t, "locale", d.get("locale").toString());
				addField(t, "createdBy", d.get("createdBy") == null ? null : d.get("createdBy").toString());
				addField(t, "updatedBy", d.get("updatedBy") == null ? null : d.get("updatedBy").toString());
				addField(t, "creationDate", d.get("creationDate") == null ? null : d.get("creationDate").toString());
				addField(t, "updateDate", d.get("updateDate") == null ? null : d.get("updateDate").toString());
				addField(t, "lastUsed", d.get("lastUsed") == null ? null : d.get("lastUsed").toString());

				importedTranslations.get(t.getKey()).add(t);
			}
		}

		return "data";
	}

	public String exportTranslationAjax() throws Exception {
		if (startDate == null)
			addActionError("Missing date");
		else {
			String sqlDate = DateBean.toDBFormat(startDate);
			String where = "t.creationDate > '" + sqlDate + "' OR t.updateDate > '" + sqlDate
					+ "' AND t.msgValue != 'Translation missing'";
			setupSQL(where);

			List<BasicDynaBean> data = db.select(sql.toString(), true);
			foundRows = db.getAllRows();

			StringBuilder str = new StringBuilder();
			str.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			str.append("<translations>\n");
			for (BasicDynaBean d : data) {
				str.append("\t<translation>\n");
				str.append("\t\t<msgKey>" + d.get("msgKey").toString() + "</msgKey>\n");
				str.append("\t\t<locale>" + d.get("locale").toString() + "</locale>\n");
				str.append("\t\t<msgValue>" + d.get("msgValue").toString() + "</msgValue>\n");

				if (d.get("createdBy") != null)
					str.append("\t\t<createdBy>" + d.get("createdBy").toString() + "</createdBy>\n");
				if (d.get("creationDate") != null)
					str.append("\t\t<creationDate>" + d.get("creationDate").toString() + "</creationDate>\n");
				if (d.get("updatedBy") != null)
					str.append("\t\t<updatedBy>" + d.get("updatedBy").toString() + "</updatedBy>\n");
				if (d.get("updateDate") != null)
					str.append("\t\t<updateDate>" + d.get("updateDate").toString() + "</updateDate>\n");
				if (d.get("lastUsed") != null)
					str.append("\t\t<lastUsed>" + d.get("lastUsed").toString() + "</lastUsed>\n");

				str.append("\t</translation>\n");
			}

			str.append("</translations>\n");
			translations = str.toString().trim();

			return "data";
		}

		return SUCCESS;
	}

	private List<String> importXML() throws Exception {
		List<String> query = new ArrayList<String>();
		importedTranslations = new HashMap<String, List<AppTranslation>>();

		InputSource source = new InputSource(new StringReader(translations));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(source);

		document.getDocumentElement().normalize();
		NodeList xmlElements = document.getElementsByTagName("translation");

		for (int i = 0; i < xmlElements.getLength(); i++) {
			Node translation = xmlElements.item(i);
			System.out.println(i + 1);
			if (translation.getNodeType() == Node.ELEMENT_NODE) {
				NodeList children = translation.getChildNodes();
				AppTranslation t = new AppTranslation();
				for (int j = 0; j < children.getLength(); j++) {
					if (isDebugging() && !children.item(j).getNodeName().equals("#text"))
						System.out.println(children.item(j).getNodeName() + " - " + children.item(j).getTextContent());

					addField(t, children.item(j).getNodeName(), children.item(j).getTextContent());

					if (children.item(j).getNodeName().equals("msgKey"))
						query.add("'" + t.getKey() + "'");
				}

				if (t.getKey() != null) {
					if (!importedTranslations.containsKey(t.getKey()))
						importedTranslations.put(t.getKey(), new ArrayList<AppTranslation>());

					importedTranslations.get(t.getKey()).add(t);
				}
			}
		}

		return query;
	}

	private void setupSQL(String where) {
		sql.addField("t.msgKey");
		sql.addField("t.locale");
		sql.addField("t.msgValue");
		sql.addField("t.createdBy");
		sql.addField("t.updatedBy");
		sql.addField("DATE_FORMAT(t.creationDate, '%Y-%m-%d %H:%i:%s') creationDate");
		sql.addField("DATE_FORMAT(t.updateDate, '%Y-%m-%d %H:%i:%s') updateDate");
		sql.addField("t.lastUsed");
		sql.addWhere(where);
		sql.addOrderBy("t.updateDate DESC, t.creationDate DESC");
	}

	private void addField(AppTranslation t, String name, String value) throws Exception {
		if (Strings.isEmpty(value) || (Strings.isEmpty(name) || name.equals("#text")))
			return;

		if (name.equals("msgKey"))
			t.setKey(value);
		if (name.equals("msgValue"))
			t.setValue(value);
		if (name.equals("locale"))
			t.setLocale(value);

		if (name.equals("createdBy")) {
			int id = Integer.parseInt(value);
			t.setCreatedBy(new User(id));
		}

		if (name.equals("updatedBy")) {
			int id = Integer.parseInt(value);
			t.setUpdatedBy(new User(id));
		}

		if (name.equals("creationDate")) {
			Date date = sdf.parse(value);
			t.setCreationDate(date);
		}

		if (name.equals("updateDate")) {
			Date date = sdf.parse(value);
			t.setUpdateDate(date);
		}

		if (name.equals("lastUsed")) {
			Date date = sdf2.parse(value);
			t.setLastUsed(date);
		}
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

	public Map<String, List<AppTranslation>> getImportedTranslations() {
		return importedTranslations;
	}
}
