package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.dao.AuditCategoryDAO;
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
import com.picsauditing.search.Database;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportWashingtonStateAudit extends ReportAccount {
	@Autowired
	private AuditCategoryDAO auditCategoryDAO;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private ContractorAccountDAO conDAO;
	@Autowired
	private ContractorAuditDAO conAuditDAO;
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private EmailSender emailSender;

	private int conID;
	private ReportFilterWashingtonAudit filter = new ReportFilterWashingtonAudit();

	private Database db = new Database();
	private List<AuditCategory> waCategories = new ArrayList<AuditCategory>();
	private Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
	private Map<Integer, ContractorAudit> previouslyRequested = new HashMap<Integer, ContractorAudit>();

	private final Logger logger = LoggerFactory.getLogger(ReportWashingtonStateAudit.class);
	
	public ReportWashingtonStateAudit() {
		this.orderByDefault = "a.name";
	}

	@Override
	public String execute() throws Exception {
		getFilter().setShowConAuditor(false);
		getFilter().setShowOperator(false);

		if (permissions.getAccountId() != 1813 && !permissions.isAuditor()
				&& !permissions.hasPermission(OpPerms.DevelopmentEnvironment))
			throw new NoRightsException("BP Cherry Point user");

		if ("Request".equals(button) && conID > 0) {
			ContractorAccount con = conDAO.find(conID);
			OperatorAccount op = operatorAccountDAO.find(1813);

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

			auditBuilder.buildAudits(con);

			// TODO clean up email language?
			EmailQueue email = new EmailQueue();
			email.setMediumPriority();
			email.setBody(permissions.getName() + " from " + permissions.getAccountName()
					+ " has requested a field audit for " + con.getName());
			email.setSubject(op.getName() + " requests a field audit");
			// email.setToAddresses("Auditors <auditors@picsauditing.com>");
			email.setToAddresses("Mina Mina <mmina@picsauditing.com>");

			try {
				emailSender.send(email);
			} catch (Exception e) {
				addActionError("Could not send field audit request email to auditors");
			}
		}

		return super.execute();
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id AND ca.auditTypeID = "
				+ AuditType.WA_STATE_VERIFICATION);

		sql.addField("ca.id auditID");

		sql.addWhere("a.status = 'Active'");

		sql.setLimit(25);
		report.setLimit(25);
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		if (filterOn(getFilter().getWaAuditTypes()))
			sql.addWhere("ca.auditTypeID IN (" + Strings.implode(getFilter().getWaAuditTypes()) + ")");
		if (filterOn(getFilter().getWaCategories()))
			sql.addWhere("acWa.id IN (" + Strings.implode(getFilter().getWaCategories()) + ")");
		if (filterOn(getFilter().getWaCategories()))
			waCategories = auditCategoryDAO.findWhere("id IN (" + Strings.implode(getFilter().getWaCategories()) + ")");
		else
			waCategories = getFilter().getWaCategoryList();

		Collections.sort(waCategories);
	}

	@Override
	protected String returnResult() throws IOException {
		sql
				.addJoin("JOIN audit_cat_data acd ON acd.auditID = ca.id AND acd.applies = 1 AND acd.numAnswered = acd.numVerified "
						+ "AND acd.numAnswered > 0");
		sql.addField("acd.categoryID");

		List<BasicDynaBean> data2 = null;
		try {
			data2 = db.select(sql.toString(), false);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e.getCause());
		}

		for (BasicDynaBean d : data2) {
			int conID = Integer.parseInt(d.get("id").toString());
			int catID = Integer.parseInt(d.get("categoryID").toString());

			if (map.get(conID) == null)
				map.put(conID, new ArrayList<Integer>());

			map.get(conID).add(catID);
		}

		List<ContractorAudit> requestedAudits = conAuditDAO.findWhere(1000,
				"auditType.id = 5 AND requestingOpAccount.id = " + 1813, "");
		for (ContractorAudit requested : requestedAudits) {
			previouslyRequested.put(requested.getContractorAccount().getId(), requested);
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

	public Map<Integer, List<Integer>> getMap() {
		return map;
	}

	public Map<Integer, ContractorAudit> getPreviouslyRequested() {
		return previouslyRequested;
	}

	public class ReportFilterWashingtonAudit extends ReportFilterContractor {
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
