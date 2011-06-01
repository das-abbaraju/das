package com.picsauditing.util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class TranslationETL extends PicsActionSupport {
	@Autowired
	private AuditTypeDAO dao;
	// Lookup
	private SelectSQL sql;
	private Database db;
	private int foundRows;
	private SimpleDateFormat sdf;
	private SimpleDateFormat sdf2;
	// Data
	private boolean importTranslations = false;
	private boolean download = false;
	private Date startDate;
	private String translations;
	private DoubleMap<String, String, List<AppTranslation>> importedTranslations;
	private List<String> allKeys;
	private List<String> allLocales;
	// Import
	private File file;
	protected String fileContentType = null;
	protected String fileFileName = null;
	protected String fileName = null;

	@RequiredPermission(value = OpPerms.Translator)
	public String execute() throws Exception {
		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.Translator)
	public String save() throws Exception {
		try {
			file = new File(getFtpDir() + "/" + permissions.getUserId() + "-Translations.xml");
			importXML(FileUtils.getBytesFromFile(file));
		} catch (Exception e) {
			// Doesn't exist
			importTranslationAjax();
		}

		List<AppTranslation> translations = new ArrayList<AppTranslation>();
		for (String key : allKeys) {
			for (String locale : allLocales) {
				if (importedTranslations.get(key, locale) != null) {
					AppTranslation t = importedTranslations.get(key, locale).get(0);

					if (importedTranslations.get(key, locale).size() > 1) {
						AppTranslation t2 = importedTranslations.get(key, locale).get(1);

						if (t2 != null && t2.getId() > 0)
							t.setId(t2.getId());
					}

					translations.add(t);
					if (translations.size() == 100) {
						saveTranslations(translations);
						translations.clear();
					}
				}
			}
		}

		if (translations.size() > 0)
			saveTranslations(translations);

		addActionMessage("Saved " + allKeys.size() + " new/updated translations");

		if (file != null)
			FileUtils.deleteFile(file);

		I18nCache.getInstance().clear();

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.Translator)
	public String importTranslationAjax() throws Exception {
		importTranslations = true;
		importXML(translations.getBytes("utf-8"));

		return "data";
	}

	@RequiredPermission(value = OpPerms.Translator)
	public String upload() throws Exception {
		if (file != null && file.length() > 0) {
			importXML(FileUtils.getBytesFromFile(file));
			importTranslations = true;
			File newFile = new File(getFtpDir() + "/" + permissions.getUserId() + "-Translations.xml");
			file.renameTo(newFile);
		} else if (file == null || file.length() == 0) {
			addActionError("No file was selected");
		}

		return "upload";
	}

	@RequiredPermission(value = OpPerms.Translator)
	public String exportTranslationAjax() throws Exception {
		if (startDate == null)
			addActionError("Missing date");
		else {
			db = new Database();
			sql = new SelectSQL("app_translation t");

			String sqlDate = DateBean.toDBFormat(startDate);
			String where = "(t.creationDate > '" + sqlDate + "' OR t.updateDate > '" + sqlDate
					+ "') AND t.msgValue != 'Translation missing'";
			setupSQL(where);

			List<BasicDynaBean> data = db.select(sql.toString(), true);
			foundRows = db.getAllRows();

			if (foundRows <= 2000) {
				StringBuilder str = new StringBuilder();
				str.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
				str.append("<translations>\n");
				for (BasicDynaBean d : data) {
					str.append("\t<translation>\n");
					str.append("\t\t<msgKey>" + d.get("msgKey").toString() + "</msgKey>\n");
					str.append("\t\t<locale>" + d.get("locale").toString() + "</locale>\n");
					str.append("\t\t<msgValue>" + StringEscapeUtils.escapeXml(d.get("msgValue").toString())
							+ "</msgValue>\n");

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
			} else
				download = true;
		}

		return "data";
	}

	@RequiredPermission(value = OpPerms.Translator)
	public String download() throws Exception {
		if (startDate == null)
			addActionError("Missing date");
		else {
			String sqlDate = DateBean.toDBFormat(startDate);
			String where = "(t.creationDate > '" + sqlDate + "' OR t.updateDate > '" + sqlDate
					+ "') AND t.msgValue != 'Translation missing'";

			db = new Database();
			sql = new SelectSQL("app_translation t");
			setupSQL(where);

			List<BasicDynaBean> data = db.select(sql.toString(), true);
			foundRows = db.getAllRows();

			ServletActionContext.getResponse().setContentType("application/xml");
			ServletActionContext.getResponse()
					.setHeader("Content-Disposition", "attachment; filename=Translations.xml");
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			OutputStreamWriter outstreamWriter = new OutputStreamWriter(outstream);
			BufferedWriter writer = new BufferedWriter(outstreamWriter);
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			writer.newLine();
			writer.write("<translations>");
			writer.newLine();

			for (BasicDynaBean d : data) {
				writer.write("\t<translation>");
				writer.newLine();
				writer.write("\t\t<msgKey>" + d.get("msgKey").toString() + "</msgKey>");
				writer.newLine();
				writer.write("\t\t<locale>" + d.get("locale").toString() + "</locale>");
				writer.newLine();
				writer.write("\t\t<msgValue>" + StringEscapeUtils.escapeXml(d.get("msgValue").toString())
						+ "</msgValue>");
				writer.newLine();

				if (d.get("createdBy") != null) {
					writer.write("\t\t<createdBy>" + d.get("createdBy").toString() + "</createdBy>");
					writer.newLine();
				}
				if (d.get("creationDate") != null) {
					writer.write("\t\t<creationDate>" + d.get("creationDate").toString() + "</creationDate>");
					writer.newLine();
				}
				if (d.get("updatedBy") != null) {
					writer.write("\t\t<updatedBy>" + d.get("updatedBy").toString() + "</updatedBy>");
					writer.newLine();
				}
				if (d.get("updateDate") != null) {
					writer.write("\t\t<updateDate>" + d.get("updateDate").toString() + "</updateDate>");
					writer.newLine();
				}
				if (d.get("lastUsed") != null) {
					writer.write("\t\t<lastUsed>" + d.get("lastUsed").toString() + "</lastUsed>");
					writer.newLine();
				}

				writer.write("\t</translation>");
				writer.newLine();
			}

			writer.write("\t</translations>");
			writer.close();
			ServletActionContext.getResponse().flushBuffer();

			return null;
		}

		return "data";
	}

	private void importXML(byte[] byteArray) throws Exception {
		importedTranslations = new DoubleMap<String, String, List<AppTranslation>>();
		Set<String> allKeysSet = new HashSet<String>();
		Set<String> allLocalesSet = new HashSet<String>();

		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf2 = new SimpleDateFormat("yyyy-MM-dd");

		InputSource source = new InputSource(new ByteArrayInputStream(byteArray));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(source);

		document.getDocumentElement().normalize();
		NodeList xmlElements = document.getElementsByTagName("translation");

		for (int i = 0; i < xmlElements.getLength(); i++) {
			Node translation = xmlElements.item(i);
			if (translation.getNodeType() == Node.ELEMENT_NODE) {
				NodeList children = translation.getChildNodes();
				AppTranslation t = new AppTranslation();
				for (int j = 0; j < children.getLength(); j++)
					addField(t, children.item(j).getNodeName(), children.item(j).getTextContent());

				allKeysSet.add(t.getKey());
				allLocalesSet.add(t.getLocale());

				if (t.getKey() != null) {
					if (importedTranslations.get(t.getKey(), t.getLocale()) == null)
						importedTranslations.put(t.getKey(), t.getLocale(), new ArrayList<AppTranslation>());

					importedTranslations.get(t.getKey(), t.getLocale()).add(t);
				}
			}
		}

		setupSQL("t.msgKey IN ('" + Strings.implode(allKeys, "', '") + "')");
		List<BasicDynaBean> data = db.select(sql.toString(), false);

		for (BasicDynaBean d : data) {
			String msgKey = d.get("msgKey").toString();
			String locale = d.get("locale").toString();
			String msgValue = d.get("msgValue").toString();

			if (importedTranslations.get(msgKey, locale) != null) {
				if (msgValue.equals(importedTranslations.get(msgKey, locale).get(0).getValue()))
					allKeysSet.remove(msgKey);
				else {
					AppTranslation t = new AppTranslation();
					addField(t, "id", d.get("id").toString());
					addField(t, "msgKey", msgKey);
					addField(t, "locale", locale);
					addField(t, "msgValue", msgValue);
					addField(t, "createdBy", d.get("createdBy") == null ? null : d.get("createdBy").toString());
					addField(t, "updatedBy", d.get("updatedBy") == null ? null : d.get("updatedBy").toString());
					addField(t, "creationDate", d.get("creationDate") == null ? null : d.get("creationDate").toString());
					addField(t, "updateDate", d.get("updateDate") == null ? null : d.get("updateDate").toString());
					addField(t, "lastUsed", d.get("lastUsed") == null ? null : d.get("lastUsed").toString());

					importedTranslations.get(t.getKey(), t.getLocale()).add(t);
				}
			}
		}

		allKeys = new ArrayList<String>(allKeysSet);
		allLocales = new ArrayList<String>(allLocalesSet);
	}

	private void setupSQL(String where) {
		sql.addField("t.id");
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

		if (name.equals("id"))
			t.setId(Integer.parseInt(value));
		if (name.equals("msgKey"))
			t.setKey(value);
		if (name.equals("msgValue"))
			t.setValue(value);
		if (name.equals("locale"))
			t.setLocale(value);

		if (name.equals("createdBy"))
			t.setCreatedBy(new User(Integer.parseInt(value)));
		if (name.equals("updatedBy"))
			t.setUpdatedBy(new User(Integer.parseInt(value)));
		if (name.equals("creationDate"))
			t.setCreationDate(sdf.parse(value));
		if (name.equals("updateDate"))
			t.setUpdateDate(sdf.parse(value));
		if (name.equals("lastUsed"))
			t.setLastUsed(sdf2.parse(value));
	}

	@Transactional
	private void saveTranslations(List<AppTranslation> translations) {
		for (AppTranslation t : translations)
			dao.save(t);
	}

	public boolean isImportTranslations() {
		return importTranslations;
	}

	public void setImportTranslations(boolean importTranslations) {
		this.importTranslations = importTranslations;
	}

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
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

	public DoubleMap<String, String, List<AppTranslation>> getImportedTranslations() {
		return importedTranslations;
	}

	public List<String> getAllLocales() {
		return allLocales;
	}

	public List<String> getAllKeys() {
		return allKeys;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}