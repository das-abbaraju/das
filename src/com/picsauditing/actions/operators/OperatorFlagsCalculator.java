package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
/**
 * This entire class needs a massive overhaul (throw it out and start over).
 * I suggest we do this once we start the Flag Calculator rewrite.
 * Trevor 7/7/2011
 */
public class OperatorFlagsCalculator extends PicsActionSupport {

	private Database db = new Database();
	private int fcoID;
	private int opID;
	private String newHurdle;
	private boolean override = false;

	private List<FlagAndOverride> affected = new ArrayList<FlagAndOverride>();
	@Autowired
	private FlagCriteriaOperatorDAO flagCriteriaOperatorDAO;
	@Autowired
	private OperatorAccountDAO opDAO;

	private FlagCriteriaOperator flagCriteriaOperator;
	private OperatorAccount operator;

	@Override
	public String execute() throws Exception {
		if (fcoID == 0 || opID == 0)
			throw new IllegalArgumentException("Missing fcoID or opID");

		flagCriteriaOperator = flagCriteriaOperatorDAO.find(fcoID);
		operator = opDAO.find(opID);
		if (flagCriteriaOperator.getCriteria().isAllowCustomValue() && !Strings.isEmpty(newHurdle)) {
			flagCriteriaOperator.setHurdle(newHurdle);
		}

		List<BasicDynaBean> results = getContractorResultsByCriteriaAndOperator(flagCriteriaOperator, operator, db);

		if (results.size() > 0) {
			List<Integer> opIDs = new ArrayList<Integer>();
			if (operator.isCorporate()) {
				for (Facility f : operator.getOperatorFacilities()) {
					opIDs.add(f.getOperator().getId());
				}
			}

			opIDs.add(operator.getId());

			List<Integer> conIDs = new ArrayList<Integer>();
			for (BasicDynaBean d : results) {
				conIDs.add((Integer) d.get("conID"));
			}

			SelectSQL sql = new SelectSQL("flag_data f");
			sql.addJoin("JOIN flag_criteria_contractor fcc ON fcc.criteriaID = f.criteriaID AND fcc.conID = f.conID");
			sql.addWhere(String.format("f.opID IN (%s)", Strings.implode(opIDs)));
			sql.addWhere(String.format("f.conID IN (%s)", Strings.implode(conIDs)));
			sql.addWhere(String.format("f.criteriaID = %d", flagCriteriaOperator.getCriteria().getId()));

			sql.addField("f.conID");
			sql.addField("f.opID");
			sql.addField("f.flag");
			sql.addField("fcc.answer");
			sql.addField("fcc.answer2");

			List<BasicDynaBean> flags = db.select(sql.toString(), false);
			Map<Integer, List<FlagData>> conFlags = new HashMap<Integer, List<FlagData>>();

			for (BasicDynaBean f : flags) {
				int conID = (Integer) f.get("conID");

				if (conFlags.get(conID) == null) {
					conFlags.put(conID, new ArrayList<FlagData>());
				}

				FlagData d = new FlagData();
				d.setContractor(new ContractorAccount());
				d.getContractor().setId(conID);
				d.setOperator(new OperatorAccount());
				d.getOperator().setId((Integer) f.get("opID"));
				d.setFlag(FlagColor.valueOf(f.get("flag").toString()));
				d.setCriteria(flagCriteriaOperator.getCriteria());

				FlagCriteriaContractor fcc = new FlagCriteriaContractor();
				if (f.get("answer") != null) {
					fcc.setAnswer(f.get("answer").toString());
				}

				if (f.get("answer2") != null) {
					fcc.setAnswer2(f.get("answer2").toString());
				}

				d.setCriteriaContractor(fcc);

				conFlags.get(conID).add(d);
			}

			Map<Integer, List<ContractorAudit>> auditMap = buildAuditMap(results, flagCriteriaOperator, operator,
					permissions, db);

			for (BasicDynaBean row : results) {
				ContractorAccount contractor = new ContractorAccount(Database.toInt(row, "conID"));
				contractor.setName(row.get("contractor_name").toString());

				if (Database.toBoolean(row, "acceptsBids"))
					contractor.setAccountLevel(AccountLevel.BidOnly);
				contractor.setAudits(auditMap.get(contractor.getId()));
				contractor.setNaics(new Naics());
				contractor.getNaics().setLwcr(Database.toFloat(row, "lwcr"));
				contractor.getNaics().setTrir(Database.toFloat(row, "trir"));
				contractor.getNaics().setCode(row.get("code").toString());

				if (row.get("tagID") != null) {
					int tagID = Database.toInt(row, "tagID");
					OperatorTag tag = new OperatorTag();
					tag.setId(tagID);

					contractor.setOperatorTags(new ArrayList<ContractorTag>());
					ContractorTag ct = new ContractorTag();
					ct.setTag(tag);
					contractor.getOperatorTags().add(ct);
				}

				if (conFlags.get(contractor.getId()) != null) {
					for (FlagData flagData : conFlags.get(contractor.getId())) {
						if (flagCriteriaOperator.getFlag().equals(flagData.getFlag())) {
							flagData.setContractor(contractor);
							FlagAndOverride flagAndOverride = new FlagAndOverride(flagData);

							if (row.get("forceFlag") != null) {
								flagAndOverride.setForcedFlag(row.get("forceFlag").toString());
								override = true;
							}

							affected.add(flagAndOverride);
							break;
						}
					}
				}
			}
		}

		if ("count".equals(button)) {
			if (Strings.isEmpty(newHurdle) && flagCriteriaOperator.getOperator().getId() == opID) {
				flagCriteriaOperator.setAffected(affected.size());
				flagCriteriaOperator.setLastCalculated(new Date());
				flagCriteriaOperatorDAO.save(flagCriteriaOperator);
			}
			output = "" + affected.size();
			return BLANK;
		}

		if ("download".equals(button)) {
			String filename = "AffectedContractors.xls";

			HSSFWorkbook wb = createWorkbook();

			ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			wb.write(outstream);
			outstream.flush();
			ServletActionContext.getResponse().flushBuffer();
			return null;
		}

		return SUCCESS;
	}

	public int getFcoID() {
		return fcoID;
	}

	public void setFcoID(int fcoID) {
		this.fcoID = fcoID;
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public String getNewHurdle() {
		return newHurdle;
	}

	public void setNewHurdle(String newHurdle) {
		this.newHurdle = newHurdle;
	}

	public FlagCriteriaOperator getFlagCriteriaOperator() {
		return flagCriteriaOperator;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public List<FlagAndOverride> getAffected() {
		return affected;
	}

	private HSSFWorkbook createWorkbook() {
		// Create spreadsheet here
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Affected Contractors");

		// Set header font and style
		HSSFFont headerFont = wb.createFont();
		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setFontHeightInPoints((short) 12);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// Set normal font and style
		HSSFFont normalFont = wb.createFont();
		HSSFCellStyle normalStyle = wb.createCellStyle();
		normalStyle.setFont(normalFont);
		normalStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);

		// Set number style
		HSSFCellStyle numberStyle = wb.createCellStyle();
		numberStyle.setFont(normalFont);
		numberStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

		// Title
		HSSFCell cell = sheet.createRow(0).createCell(1);
		cell.setCellValue(new HSSFRichTextString(flagCriteriaOperator.getReplaceHurdle()));
		cell.setCellStyle(headerStyle);

		if (override) {
			// Forced column
			cell = sheet.getRow(0).createCell(flagCriteriaOperator.getCriteria().isAllowCustomValue() ? 3 : 2);
			cell.setCellValue(new HSSFRichTextString("Forced"));
			cell.setCellStyle(headerStyle);
		}

		int columns = flagCriteriaOperator.getCriteria().isAllowCustomValue() ? 2 : 1;
		int rows = 1;

		// Print out the data
		for (FlagAndOverride data : affected) {
			HSSFRow row = sheet.createRow(rows);
			cell = row.createCell(0);
			cell.setCellValue(rows);
			cell.setCellStyle(numberStyle);

			// Contractor name
			cell = row.createCell(1);
			cell.setCellValue(new HSSFRichTextString(data.getFlagData().getContractor().getName()));
			cell.setCellStyle(normalStyle);

			if (override) {
				// Forced Flag?
				cell = row.createCell(columns > 1 ? 3 : 2);
				cell.setCellValue(new HSSFRichTextString(data.getForcedFlag()));
				cell.setCellStyle(normalStyle);
			}

			rows++;
		}

		// Space out properly
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);

		if (columns > 1)
			sheet.autoSizeColumn((short) 2);

		if (override) {
			sheet.autoSizeColumn((short) 3);
		}

		return wb;
	}

	public boolean isOverride() {
		return override;
	}

	static private List<BasicDynaBean> getContractorResultsByCriteriaAndOperator(FlagCriteriaOperator fco,
			OperatorAccount op, Database db) throws Exception {
		List<Integer> children = new ArrayList<Integer>();
		children.add(op.getId());
		for (Facility f : op.getOperatorFacilities())
			children.add(f.getOperator().getId());

		SelectSQL sql = new SelectSQL("flag_criteria_contractor fcc");
		sql.addJoin("JOIN accounts a ON a.id = fcc.conID AND a.status IN ('Active'"
				+ (op.getStatus().isDemo() ? ",'Demo'" : "") + ")");
		sql.addJoin("JOIN contractor_info c ON c.id = fcc.conID");
		sql.addJoin("JOIN generalcontractors gc ON gc.subID = fcc.conID AND gc.genID IN (" + Strings.implode(children)
				+ ") AND gc.workStatus ='Y'");
		// Get flag overrides set by operator/corporate or operator parents
		// -- don't roll up operator flag overrides to corporate
		sql.addJoin("LEFT JOIN flag_data_override fdo ON fdo.conID = fcc.conID AND fdo.opID IN ("
				+ Strings.implode(op.getOperatorHeirarchy()) + ") AND fdo.criteriaID = fcc.criteriaID");
		sql.addJoin("LEFT JOIN naics n on n.code = a.naics");
		sql.addJoin("LEFT JOIN contractor_tag ct ON ct.conID = fcc.conID AND ct.tagID = "
				+ (fco.getTag() != null ? fco.getTag().getId() : 0));

		sql.addWhere("fcc.criteriaID = " + fco.getCriteria().getId());

		sql.addField("n.lwcr");
		sql.addField("n.trir");
		sql.addField("n.code");
		sql.addField("fcc.conID");
		sql.addField("a.name contractor_name");
		sql.addField("c.accountLevel AS `acceptsBids`");
		sql.addField("c.riskLevel");
		sql.addField("c.safetyRisk");
		sql.addField("c.productRisk");
		sql.addField("fcc.answer");
		sql.addField("fcc.verified");
		sql.addField("fdo.forceFlag");
		sql.addField("ct.tagID");

		sql.addGroupBy("a.id");
		sql.addOrderBy("a.name");

		return db.select(sql.toString(), false);
	}

	static private Map<Integer, List<ContractorAudit>> buildAuditMap(List<BasicDynaBean> results,
			FlagCriteriaOperator fco, OperatorAccount op, Permissions permissions, Database db) throws Exception {
		Map<Integer, List<ContractorAudit>> auditMap = new HashMap<Integer, List<ContractorAudit>>();
		if (results.size() > 0) {
			if (fco.getCriteria().getAuditType() != null) {
				// Use the operator we're viewing for pulling up
				// audits/calculating affected contractors -- does it matter who
				// the flag criteria belongs to?
				Set<String> conIDs = new HashSet<String>();
				for (BasicDynaBean row : results) {
					conIDs.add(row.get("conID").toString());
				}
				SelectSQL sql2 = new SelectSQL("contractor_audit_operator cao");
				sql2.addJoin("JOIN contractor_audit ca ON ca.id = cao.auditID AND ca.conID IN ("
						+ Strings.implode(conIDs) + ")");
				if (op.isOperator())
					sql2.addJoin("JOIN contractor_audit_operator_permission caop ON caop.caoID = cao.id AND caop.opID = "
							+ op.getId());

				sql2.addWhere("cao.visible = 1");

				sql2.addField("ca.auditTypeID");
				sql2.addField("ca.conID");
				sql2.addField("cao.status");
				// Always add the PICS corporate umbrella
				Set<Integer> viewableOps = new HashSet<Integer>(Account.PICS_CORPORATE);
				if (op.isOperator()) {
					// If I'm an operator, show audits created for myself, my
					// corporate
					viewableOps.addAll(op.getOperatorHeirarchy());
				} else {
					// If I'm corporate, show audits created for myself, my
					// facilities
					for (Facility f : op.getOperatorFacilities())
						viewableOps.add(f.getOperator().getId());
				}

				sql2.addWhere("cao.opID IN (" + Strings.implode(viewableOps) + ")");

				List<BasicDynaBean> auditResults = db.select(sql2.toString(), false);
				for (BasicDynaBean row : auditResults) {
					int conID = Database.toInt(row, "conID");
					ContractorAudit ca = new ContractorAudit();
					ca.setAuditType(new AuditType(Database.toInt(row, "auditTypeID")));
					{
						ContractorAuditOperator cao = new ContractorAuditOperator();
						cao.setAudit(ca);
						cao.setOperator(op);
						cao.changeStatus(AuditStatus.valueOf(row.get("status").toString()), permissions);
						ca.setOperators(new ArrayList<ContractorAuditOperator>());
						ca.getOperators().add(cao);

						ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
						caop.setCao(cao);
						caop.setOperator(op);
						cao.setCaoPermissions(new ArrayList<ContractorAuditOperatorPermission>());
						cao.getCaoPermissions().add(caop);
					}
					if (auditMap.get(conID) == null)
						auditMap.put(conID, new ArrayList<ContractorAudit>());
					auditMap.get(conID).add(ca);
				}
			}
		}

		return auditMap;
	}

	static public List<FlagCriteriaContractor> getFlagCriteriaContractorList(FlagCriteriaOperator fco,
			OperatorAccount op, Permissions permissions) throws Exception {
		Database db = new Database();
		List<BasicDynaBean> results = getContractorResultsByCriteriaAndOperator(fco, op, db);
		List<FlagCriteriaContractor> fccs = new ArrayList<FlagCriteriaContractor>();

		if (results.size() > 0) {
			Map<Integer, List<ContractorAudit>> auditMap = buildAuditMap(results, fco, op, permissions, db);

			for (BasicDynaBean row : results) {
				ContractorAccount contractor = new ContractorAccount(Database.toInt(row, "conID"));
				contractor.setName(row.get("contractor_name").toString());
				if (Database.toBoolean(row, "acceptsBids"))
					contractor.setAccountLevel(AccountLevel.BidOnly);
				contractor.setAudits(auditMap.get(contractor.getId()));
				contractor.setNaics(new Naics());
				contractor.getNaics().setLwcr(Database.toFloat(row, "lwcr"));
				contractor.getNaics().setTrir(Database.toFloat(row, "trir"));
				contractor.getNaics().setCode(row.get("code").toString());

				if (row.get("tagID") != null) {
					int tagID = Database.toInt(row, "tagID");
					OperatorTag tag = new OperatorTag();
					tag.setId(tagID);

					contractor.setOperatorTags(new ArrayList<ContractorTag>());
					ContractorTag ct = new ContractorTag();
					ct.setTag(tag);
					contractor.getOperatorTags().add(ct);
				}

				FlagCriteriaContractor fcc = new FlagCriteriaContractor(contractor, fco.getCriteria(),
						row.get("answer") == null ? null : row.get("answer").toString());
				fcc.setVerified(Database.toBoolean(row, "verified"));

				fccs.add(fcc);
			}
		}

		return fccs;
	}

	private class FlagAndOverride {
		private FlagData flagData;
		private String forcedFlag;

		public FlagAndOverride(FlagData flagData) {
			this.flagData = flagData;
		}

		public FlagData getFlagData() {
			return flagData;
		}

		public String getForcedFlag() {
			return forcedFlag;
		}

		public void setForcedFlag(String forcedFlag) {
			this.forcedFlag = forcedFlag;
		}
	}
}