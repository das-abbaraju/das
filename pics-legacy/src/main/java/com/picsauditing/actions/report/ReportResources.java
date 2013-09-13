package com.picsauditing.actions.report;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorFormDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Downloader;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.Strings;


@SuppressWarnings("serial")
public class ReportResources extends ReportActionSupport {
	@Autowired
	protected OperatorAccountDAO operatorDao;
	@Autowired
	private OperatorFormDAO operatorFormDAO;
	
	protected OperatorAccount operator;
	List<Resource> resources;
	protected SelectSQL sql = new SelectSQL("operatorforms o");
	private ReportFilterAccount filter = new ReportFilterAccount();
	
	int id;
	String loc;
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		getFilter().setShowTitleName(true);
		getFilter().setShowIncludePicsReources(true);
		
		findOperator();
		
		Set<Integer> ids = new HashSet<Integer>();

		if (operator != null) {
			if (getFilter().isIncludePicsResources())
				ids.add(Account.PicsID); // PICS
			ids.add(operator.getId());
			for (OperatorAccount op : operator.getChildOperators()) {
				ids.add(op.getId());
			}
			for (OperatorAccount op : operator.getParentOperators()) {
				ids.add(op.getId());
			}
		}

		if (ids.size() > 0) {
			sql.addWhere("o.opID IN (" + Strings.implode(ids, ",") + ")");
		}
		if (Strings.isEmpty(filter.getTitleName())) {
			sql.addWhere("o.parentID IS NULL");
		} else {
			sql.addWhere("(o.parentID IS NULL and o.formName like '%" + Strings.escapeQuotes(filter.getTitleName().trim())
					+ "%') OR o.id IN (select of.parentID from operatorforms of where of.formName like '%"
					+ Strings.escapeQuotes(filter.getTitleName().trim()) + "%' and of.parentID IS NOT NULL)");
		}
		
		if (!ReportFilterAccount.getDefaultName().equals(filter.getAccountName())){
			sql.addWhere("a.name LIKE '%" + Strings.escapeQuotes(filter.getAccountName()) + "%'");
		}
		
		if (!Strings.isEmpty(filter.getStartsWith())) {
			sql.addWhere("o.formName LIKE '" + Strings.escapeQuotes(filter.getStartsWith()) + "%'");
		}
		sql.addField("a.name operator");
		sql.addField("o.id id");
		sql.addField("o.opID opID");
		sql.addField("o.formName formName");
		sql.addField("o.parentID parentID");
		sql.addField("o.locale locale");
		sql.addField("o.clientSiteOnly clientSiteOnly");
		sql.addJoin("JOIN accounts a ON a.id = o.opID");
		sql.addOrderBy("a.name");
		sql.addOrderBy("o.formName");
		
		run(sql);
		
		HashMap<Integer, Resource> map = new HashMap<Integer, Resource>();
		resources = new ArrayList<Resource>();
		
		for (BasicDynaBean row : data) {
			Resource resource = new Resource(row);
			map.put(resource.getId(), resource);
			resources.add(resource);
		}
		
		if (map.size() > 0) {
			List<OperatorForm> children = operatorFormDAO.findChildrenByOperators(map.keySet());
			for (OperatorForm child : children) {
				Resource resource = map.get(child.getParent().getId());
				if (resource != null) {
					resource.addLocale(child.getLocale());
				}
			}
		}
		
		return SUCCESS;
	}

	protected void findOperator() {
		loadPermissions();

		int id = 0;

		if (operator == null) {
			if (permissions.isOperatorCorporate())
				id = permissions.getAccountId();

			if (id != 0)
				operator = operatorDao.find(id);
		}
	}
	
	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public ReportFilterAccount getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterAccount filter) {
		this.filter = filter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isEditableByUser(Resource resource) {
		if (permissions.isAdmin())
			return true;
		if (Account.PICS_CORPORATE.contains(resource.getOperatorId()))
			return false;
		if (permissions.getAccountId() == resource.getOperatorId())
			return true;
		if (permissions.getCorporateParent().contains(resource.getOperatorId()))
			return true;
		return false;
	}
	
	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}
	
	private Locale createLocale(String loc) {
		String[] codes = loc.split("_");
		Locale locale;
		
		if (codes.length == 1)
			locale = new Locale(codes[0]);
		else if (codes.length == 2)
			locale = new Locale(codes[0], codes[1]);
		else if (codes.length == 3)
			locale = new Locale(codes[0], codes[1], codes[2]);
		else
			locale = permissions.getLocale();
		
		return locale;
	}

	public String download() {
		OperatorForm parent = operatorFormDAO.find(id);
		if (parent == null) {
			addActionError(getText("ReportResources.error.ResourceNotFound"));
			return SUCCESS;
		}

		Locale locale;
		if (!Strings.isEmpty(loc)) {
			locale = createLocale(loc);
		} else {
			locale = permissions.getLocale();
		}

		OperatorForm resource = parent.getMostApplicableForm(locale);

		Downloader downloader = new Downloader(ServletActionContext.getResponse(),
				ServletActionContext.getServletContext());
		try {
			if (resource.getFile().startsWith("form")) {
				downloader.download(new File(getFtpDir() + "/forms/" + resource.getFile()), null);
				return null;
			} else {
				downloader.download(new File(getFtpDir() + "/files/" + FileUtils.thousandize(resource.getId())  + resource.getFile()), null);
				return null;
			}
		} catch (Exception e) {
			addActionError(getText("ReportResources.error.FailedDownload") + e.getMessage());
		}

		return SUCCESS;
	}

	public class Resource implements Comparable<Resource> {
		private int id;
		private int operatorId;
		private String operatorName;
		private String formName;
		private boolean clientSiteOnly;
		private ArrayList<Locale> locales = new ArrayList<Locale>();
		
		public Resource(BasicDynaBean row) {
			Object parentId = row.get("parentID");
			if (parentId != null)
				id = Integer.parseInt(parentId.toString());
			else 
				id = Integer.parseInt(row.get("id").toString());
			operatorId = Integer.parseInt(row.get("opID").toString());
			operatorName = row.get("operator").toString();
			formName = row.get("formName").toString();
			clientSiteOnly = row.get("clientSiteOnly") == 1;
			addLocale(createLocale(row.get("locale").toString()));
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getOperatorId() {
			return operatorId;
		}

		public void setOperatorId(int operatorId) {
			this.operatorId = operatorId;
		}

		public String getOperatorName() {
			return operatorName;
		}

		public void setOperatorName(String operatorName) {
			this.operatorName = operatorName;
		}

		public String getFormName() {
			return formName;
		}

		public void setFormName(String formName) {
			this.formName = formName;
		}

		public ArrayList<Locale> getLocales() {
			return locales;
		}

		public void setLocales(ArrayList<Locale> locales) {
			this.locales = locales;
		}
		
		public void addLocale(Locale locale) {
			locales.add(locale);
		}

		public boolean isClientSiteOnly() {
			return clientSiteOnly;
		}

		public void setClientSiteOnly(boolean clientSiteOnly) {
			this.clientSiteOnly = clientSiteOnly;
		}

		@Override
		public int compareTo(Resource o) {
			
			if (!operatorName.equals(o.getOperatorName())) {
				return operatorName.compareTo(o.getOperatorName());
			}
			
			if (!formName.equals(o.getFormName())) {
				return formName.compareTo(o.getFormName());
			}
			
			return id - o.getId();
		}
	}
}
