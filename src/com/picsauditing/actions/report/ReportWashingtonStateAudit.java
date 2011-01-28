package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.AuditBuilderController;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.ReportFilterCAO;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportWashingtonStateAudit extends ReportContractorAuditOperator {
	private AuditCategoryDAO auditCategoryDAO;
	private AuditTypeDAO auditTypeDAO;
	private ContractorAccountDAO conDAO;
	private ContractorAuditDAO conAuditDAO;
	// private ContractorAuditOperatorDAO caoDAO;
	private AuditBuilderController auditBuilder;

	private int conID;
	private ReportFilterWashingtonAudit filter = new ReportFilterWashingtonAudit();

	private List<AuditCategory> waCategories = new ArrayList<AuditCategory>();
	private Map<ContractorAccount, List<AuditCategory>> map = new HashMap<ContractorAccount, List<AuditCategory>>();
	private Map<ContractorAccount, ContractorAudit> previouslyRequested = new HashMap<ContractorAccount, ContractorAudit>();

	public ReportWashingtonStateAudit(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO, AuditCategoryDAO auditCategoryDAO,
			AuditTypeDAO auditTypeDAO, ContractorAccountDAO conDAO, ContractorAuditDAO conAuditDAO,
			AuditBuilderController auditBuilder) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);

		this.auditCategoryDAO = auditCategoryDAO;
		this.auditTypeDAO = auditTypeDAO;
		this.conDAO = conDAO;
		this.conAuditDAO = conAuditDAO;
		// this.caoDAO = caoDAO;
		this.auditBuilder = auditBuilder;
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();
		// Turn on/off filters
		getFilter().setShowConAuditor(false);
		getFilter().setShowCaoStatusChangedDate(false);
		getFilter().setShowOperator(false);
		getFilter().setShowCaoOperator(false);
		getFilter().setShowAuditType(false);
		
		if (!permissions.hasPermission(OpPerms.DevelopmentEnvironment))
			throw new NoRightsException("Administrators");
		
		if ("Request".equals(button) && conID > 0) {
			ContractorAccount con = conDAO.find(conID);
			OperatorAccount op = operatorAccountDAO.find(permissions.getAccountId());

			ContractorAudit ca = new ContractorAudit();
			ca.setContractorAccount(con);
			ca.setRequestingOpAccount(op);
			ca.setAuditType(new AuditType());
			ca.getAuditType().setId(5); // Field Audit
			ca.setManuallyAdded(true);
			ca.setAuditColumns(permissions);
			ca = conAuditDAO.save(ca);

			// TODO do we need to create caos?
			// ContractorAuditOperator cao = new ContractorAuditOperator();
			// cao.setAudit(ca);
			// cao.setOperator(op);
			// cao.setVisible(true);
			// cao.setAuditColumns(permissions);
			// caoDAO.save(cao);

			auditBuilder.buildAudits(con, null);

			// TODO clean up email language?
			EmailQueue email = new EmailQueue();
			email.setPriority(50);
			email.setBody(permissions.getName() + " from " + op.getName() + " has requested a field audit for "
					+ con.getName());
			email.setSubject(op.getName() + " requests a field audit");
			// email.setToAddresses("Auditors <auditors@picsauditing.com>");
			email.setToAddresses("Mina Mina <mmina@picsauditing.com>");

			try {
				EmailSender.send(email);
			} catch (Exception e) {
				addActionError("Could not send field audit request email to auditors");
			}
		}

		return super.execute();
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("LEFT JOIN audit_cat_data acdWa ON acdWa.auditID = ca.id AND acdWa.applies = 1 AND acdWa.numAnswered = acdWa.numVerified");
		sql.addJoin("LEFT JOIN audit_category acWa ON acWa.id = acdWa.categoryID AND acWa.number != 1");

		sql.addField("acWa.id waCatID");
		sql.addField("acWa.name waCatName");

		sql.addWhere("cao.opID IN (4, 5, 1813)");

		sql.addOrderBy("acWa.number ASC");
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		if (filterOn(getFilter().getWaAuditTypes()))
			sql.addWhere("ca.auditTypeID IN (" + Strings.implode(getFilter().getWaAuditTypes()) + ")");
		if (filterOn(getFilter().getWaCategories()))
			sql.addWhere("acWa.id IN (" + Strings.implode(getFilter().getWaCategories()) + ")");
	}

	@Override
	protected String returnResult() throws IOException {
		if (!filterOn(getFilter().getWaCategories()))
			waCategories = getFilter().getWaCategoryList();
		else
			waCategories = auditCategoryDAO.findWhere("id IN (" + Strings.implode(getFilter().getWaCategories()) + ")");

		for (BasicDynaBean d : data) {
			ContractorAccount c = new ContractorAccount();
			c.setId(Integer.parseInt(d.get("id").toString()));
			c.setName(d.get("name").toString());
			c.setStatus(AccountStatus.valueOf(d.get("status").toString()));
			c.setType(d.get("type").toString());

			if (map.get(c) == null)
				map.put(c, new ArrayList<AuditCategory>());

			if (d.get("waCatID") != null) {
				AuditCategory ac = new AuditCategory();
				ac.setId(Integer.parseInt(d.get("waCatID").toString()));
				ac.setName(d.get("waCatName").toString());

				if (!map.get(c).contains(ac))
					map.get(c).add(ac);
			}
		}

		Collections.sort(waCategories);

		List<ContractorAudit> requestedAudits = conAuditDAO.findWhere(1000,
				"auditType.id = 5 AND requestingOpAccount.id = " + 1813, "");
		for (ContractorAudit requested : requestedAudits) {
			previouslyRequested.put(requested.getContractorAccount(), requested);
		}

		return super.returnResult();
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	@Override
	public ReportFilterWashingtonAudit getFilter() {
		return filter;
	}

	public List<AuditCategory> getWaCategories() {
		return waCategories;
	}

	public Map<ContractorAccount, List<AuditCategory>> getMap() {
		return map;
	}

	public Map<ContractorAccount, ContractorAudit> getPreviouslyRequested() {
		return previouslyRequested;
	}

	public class ReportFilterWashingtonAudit extends ReportFilterCAO {
		private boolean showWaAuditTypes = true;
		private boolean showWaCategories = true;

		private int[] waAuditTypes;
		private int[] waCategories;

		public boolean isShowWaAuditTypes() {
			return showWaAuditTypes;
		}

		public void setShowWaAuditTypes(boolean showWaAuditTypes) {
			this.showWaAuditTypes = showWaAuditTypes;
		}

		public boolean isShowWaCategories() {
			return showWaCategories;
		}

		public void setShowWaCategories(boolean showWaCategories) {
			this.showWaCategories = showWaCategories;
		}

		public int[] getWaAuditTypes() {
			return waAuditTypes;
		}

		public void setWaAuditTypes(int[] waAuditTypes) {
			this.waAuditTypes = waAuditTypes;
		}

		public int[] getWaCategories() {
			return waCategories;
		}

		public void setWaCategories(int[] waCategories) {
			this.waCategories = waCategories;
		}

		@Override
		public AccountStatus[] getStatusList() {
			return new AccountStatus[] { AccountStatus.Pending, AccountStatus.Active };
		}

		public List<AuditType> getWaAuditTypesList() {
			return auditTypeDAO.findWhere("t.id IN (5, 176)");
		}

		public List<AuditCategory> getWaCategoryList() {
			List<AuditCategory> cats = auditCategoryDAO.findByAuditTypeID(AuditType.WA_STATE_VERIFICATION);
			cats.remove(0);
			Collections.sort(cats);
			return cats;
		}
	}
}