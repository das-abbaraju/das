package com.picsauditing.actions.i18n;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class TranslationETL extends PicsActionSupport {
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

	private Map<String, Integer> pageCount = new TreeMap<String, Integer>();
	private List<String> pagesToInclude = new ArrayList<String>();

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
		flagClearCache();

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
			List<BasicDynaBean> data = getData();
			foundRows = data.size();

			if (foundRows <= 5000) {
				translations = buildXML(data);
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
			ServletActionContext.getResponse().setContentType("application/xml");
			ServletActionContext.getResponse()
					.setHeader("Content-Disposition", "attachment; filename=Translations.xml");
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();

			OutputStreamWriter outstreamWriter = new OutputStreamWriter(outstream);
			BufferedWriter writer = new BufferedWriter(outstreamWriter);
			writer.write(buildXML(getData()));
			writer.close();
			ServletActionContext.getResponse().flushBuffer();

			return null;
		}

		return "data";
	}

	private List<BasicDynaBean> getData() throws Exception {
		db = new Database();
		sql = new SelectSQL("app_translation t");

		String sqlDate = DateBean.toDBFormat(startDate);
		String where = "(t.creationDate > '" + sqlDate + "' OR t.updateDate > '" + sqlDate
				+ "') AND t.msgValue != 'Translation missing'";

		setupSQL(where);
		return db.select(sql.toString(), true);
	}

	private String buildXML(List<BasicDynaBean> data) throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document document = builder.newDocument();

		Element translationsElement = document.createElement("translations");
		document.appendChild(translationsElement);

		for (BasicDynaBean d : data) {
			String msgKey = d.get("msgKey").toString();
			String msgKeyRoot = msgKey.split("\\.")[0];
			if (pageCount.containsKey(msgKeyRoot))
				pageCount.put(msgKeyRoot, pageCount.get(msgKeyRoot) + 1);
			else
				pageCount.put(msgKeyRoot, 1);

			if (pagesToInclude.isEmpty() || pagesToInclude.contains(msgKeyRoot)) {
				Element translation = document.createElement("translation");
				translationsElement.appendChild(translation);

				Element element = document.createElement("msgKey");
				translation.appendChild(element);
				Text elementText = document.createTextNode(d.get("msgKey").toString());
				element.appendChild(elementText);

				element = document.createElement("locale");
				translation.appendChild(element);
				elementText = document.createTextNode(d.get("locale").toString());
				element.appendChild(elementText);

				element = document.createElement("msgValue");
				translation.appendChild(element);
				CDATASection text = document.createCDATASection(d.get("msgValue").toString());
				element.appendChild(text);

				element = document.createElement("qualityRating");
				translation.appendChild(element);
				elementText = document.createTextNode(d.get("qualityRating").toString());
				element.appendChild(elementText);

				if (d.get("createdBy") != null) {
					element = document.createElement("createdBy");
					translation.appendChild(element);
					elementText = document.createTextNode(d.get("createdBy").toString());
					element.appendChild(elementText);
				}

				if (d.get("updatedBy") != null) {
					element = document.createElement("updatedBy");
					translation.appendChild(element);
					elementText = document.createTextNode(d.get("updatedBy").toString());
					element.appendChild(elementText);
				}

				if (d.get("creationDate") != null) {
					element = document.createElement("creationDate");
					translation.appendChild(element);
					elementText = document.createTextNode(d.get("creationDate").toString());
					element.appendChild(elementText);
				}

				if (d.get("updateDate") != null) {
					element = document.createElement("updateDate");
					translation.appendChild(element);
					elementText = document.createTextNode(d.get("updateDate").toString());
					element.appendChild(elementText);
				}

				if (d.get("lastUsed") != null) {
					element = document.createElement("lastUsed");
					translation.appendChild(element);
					elementText = document.createTextNode(d.get("lastUsed").toString());
					element.appendChild(elementText);
				}

				if (d.get("applicable") != null) {
					element = document.createElement("applicable");
					translation.appendChild(element);
					elementText = document.createTextNode(d.get("applicable").toString());
					element.appendChild(elementText);
				}

				if (d.get("sourceLanguage") != null) {
					element = document.createElement("sourceLanguage");
					translation.appendChild(element);
					elementText = document.createTextNode(d.get("sourceLanguage").toString());
					element.appendChild(elementText);
				}
			}
		}

		OutputFormat format = new OutputFormat(document);
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);
		Writer out = new StringWriter();
		XMLSerializer serializer = new XMLSerializer(out, format);
		serializer.serialize(document);

		return out.toString();
	}

	private void importXML(byte[] byteArray) throws Exception {
		db = new Database();
		sql = new SelectSQL("app_translation t");

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

				if (t.getKey() != null && t.getLocale() != null) {
					if (importedTranslations.get(t.getKey(), t.getLocale()) == null)
						importedTranslations.put(t.getKey(), t.getLocale(), new ArrayList<AppTranslation>());

					importedTranslations.get(t.getKey(), t.getLocale()).add(t);
				}
			}
		}

		setupSQL("t.msgKey IN ('" + Strings.implode(allKeysSet, "', '") + "')");
		List<BasicDynaBean> data = db.select(sql.toString(), false);

		for (BasicDynaBean d : data) {
			String msgKey = d.get("msgKey").toString();
			String locale = d.get("locale").toString();
			String msgValue = d.get("msgValue").toString();
			String qualityRating = d.get("qualityRating").toString();

			if (importedTranslations.get(msgKey, locale) != null) {
				// Check if the locale and msgKey are the same
				AppTranslation first = importedTranslations.get(msgKey, locale).get(0);
				if (msgValue.equals(first.getValue()) && locale.equals(first.getLocale()))
					allKeysSet.remove(msgKey);
				else {
					AppTranslation t = new AppTranslation();
					addField(t, "id", d.get("id").toString());
					addField(t, "msgKey", msgKey);
					addField(t, "locale", locale);
					addField(t, "msgValue", msgValue);
					addField(t, "qualityRating", qualityRating);
					addField(t, "createdBy", d.get("createdBy") == null ? null : d.get("createdBy").toString());
					addField(t, "updatedBy", d.get("updatedBy") == null ? null : d.get("updatedBy").toString());
					addField(t, "creationDate", d.get("creationDate") == null ? null : d.get("creationDate").toString());
					addField(t, "updateDate", d.get("updateDate") == null ? null : d.get("updateDate").toString());
					addField(t, "lastUsed", d.get("lastUsed") == null ? null : d.get("lastUsed").toString());
					addField(t, "applicable", d.get("applicable") == null ? null : d.get("applicable").toString());
					addField(t, "sourceLanguage", d.get("sourceLanguage") == null ? null : d.get("sourceLanguage")
							.toString());

					importedTranslations.get(t.getKey(), locale).add(t);
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
		sql.addField("t.qualityRating");
		sql.addField("t.createdBy");
		sql.addField("t.updatedBy");
		sql.addField("DATE_FORMAT(t.creationDate, '%Y-%m-%d %H:%i:%s') creationDate");
		sql.addField("DATE_FORMAT(t.updateDate, '%Y-%m-%d %H:%i:%s') updateDate");
		sql.addField("t.lastUsed");
		sql.addField("t.applicable");
		sql.addField("t.sourceLanguage");
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
		if (name.equals("qualityRating"))
			t.setQualityRating(TranslationQualityRating.getRatingFromOrdinal(Integer.parseInt(value)));

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
		if (name.equals("applicable"))
			t.setApplicable(Integer.parseInt(value) == 1);
		if (name.equals("sourceLanguage"))
			t.setSourceLanguage(value);
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

	public List<String> getAllKeys() {
		return allKeys;
	}

	public List<String> getAllLocales() {
		return allLocales;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public List<String> getPagesToInclude() {
		return pagesToInclude;
	}

	public void setPagesToInclude(List<String> pagesToInclude) {
		this.pagesToInclude = pagesToInclude;
	}

	public Map<String, Integer> getPageCount() {
		return pageCount;
	}
}