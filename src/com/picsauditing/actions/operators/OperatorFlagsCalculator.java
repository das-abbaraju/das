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

import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorTag;
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
public class OperatorFlagsCalculator extends PicsActionSupport {

	private Database db = new Database();
	private int fcoID;
	private int opID;
	private String newHurdle;
	private boolean override = false;

	private List<FlagAndOverride> affected = new ArrayList<FlagAndOverride>();

	private FlagCriteriaOperatorDAO flagCriteriaOperatorDAO;
	private OperatorAccountDAO opDAO;

	private FlagCriteriaOperator flagCriteriaOperator;
	private OperatorAccount operator;

	public OperatorFlagsCalculator(FlagCriteriaOperatorDAO flagCriteriaOperatorDAO, OperatorAccountDAO opDAO) {
		this.flagCriteriaOperatorDAO = flagCriteriaOperatorDAO;
		this.opDAO = opDAO;
	}

	@Override
	public String execute() throws Exception {
		if (fcoID == 0 || opID == 0)
			throw new Exception("Missing fcoID or opID");

		flagCriteriaOperator = flagCriteriaOperatorDAO.find(fcoID);
		operator = opDAO.find(opID);
		if (flagCriteriaOperator.getCriteria().isAllowCustomValue() && !Strings.isEmpty(newHurdle)) {
			flagCriteriaOperator.setHurdle(newHurdle);
		}

		List<BasicDynaBean> results = getResults(flagCriteriaOperator, operator, db);

		if (results.size() > 0) {
			Map<Integer, List<ContractorAudit>> auditMap = buildAuditMap(results, flagCriteriaOperator, operator,
					permissions, db);

			for (BasicDynaBean row : results) {
				ContractorAccount contractor = new ContractorAccount(Database.toInt(row, "conID"));
				contractor.setName(row.get("contractor_name").toString());
				contractor.setAcceptsBids(Database.toBoolean(row, "acceptsBids"));
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

				FlagCriteriaContractor fcc = new FlagCriteriaContractor(contractor, flagCriteriaOperator.getCriteria(),
						row.get("answer") == null ? null : row.get("answer").toString());
				fcc.setVerified(Database.toBoolean(row, "verified"));

				FlagDataCalculator calculator = new FlagDataCalculator(fcc, flagCriteriaOperator);
				calculator.setOperator(operator);
				List<FlagData> conResults = calculator.calculate();
				for (FlagData flagData : conResults) {
					if (flagCriteriaOperator.getFlag().equals(flagData.getFlag())) {
						FlagAndOverride flagAndOverride = new FlagAndOverride(flagData);

						if (row.get("forceFlag") != null) {
							flagAndOverride.setForcedFlag(row.get("forceFlag").toString());
							override = true;
						}

						affected.add(flagAndOverride);
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

			// If this is a number datatype, print out the number
			if (columns > 1) {
				cell = row.createCell(2);
				// Double has a hard time parsing commas. Just remove them all.
				cell.setCellValue(Double.parseDouble(data.getFlagData().getCriteriaContractor().getAnswer()
						.replace(",", "")));
				cell.setCellStyle(numberStyle);
			}

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

	static private List<BasicDynaBean> getResults(FlagCriteriaOperator fco, OperatorAccount op, Database db)
			throws Exception {
		SelectSQL sql = new SelectSQL("flag_criteria_contractor fcc");
		sql.addJoin("JOIN accounts a ON a.id = fcc.conID");
		sql.addJoin("JOIN contractor_info c ON c.id = fcc.conID");
		sql.addJoin("JOIN generalcontractors gc ON gc.subID = fcc.conID AND gc.genID = " + op.getId());
		sql.addJoin("LEFT JOIN flag_data_override fdo ON fdo.conID = fcc.conID AND fdo.opID = gc.genID AND fdo.criteriaID = fcc.criteriaID");
		sql.addJoin("LEFT JOIN naics n on n.code = a.naics");
		sql.addJoin("LEFT JOIN contractor_tag ct ON ct.conID = fcc.conID AND ct.tagID = "
				+ (fco.getTag() != null ? fco.getTag().getId() : 0));
		sql.addField("n.lwcr");
		sql.addField("n.trir");
		sql.addField("n.code");
		sql.addWhere("fcc.criteriaID = " + fco.getCriteria().getId());
		if (op.getStatus().isDemo())
			sql.addWhere("a.status IN ('Active','Demo')");
		else
			sql.addWhere("a.status = 'Active'");
		sql.addField("fcc.conID");
		sql.addField("a.name contractor_name");
		sql.addField("a.acceptsBids");
		sql.addField("c.riskLevel");
		sql.addField("fcc.answer");
		sql.addField("fcc.verified");
		sql.addField("fdo.forceFlag");
		sql.addField("ct.tagID");
		sql.addOrderBy("a.name");

		return db.select(sql.toString(), false);
	}

	static private Map<Integer, List<ContractorAudit>> buildAuditMap(List<BasicDynaBean> results,
			FlagCriteriaOperator fco, OperatorAccount op, Permissions permissions, Database db) throws Exception {
		Map<Integer, List<ContractorAudit>> auditMap = new HashMap<Integer, List<ContractorAudit>>();
		if (results.size() > 0) {
			if (fco.getCriteria().getAuditType() != null && fco.getCriteria().getAuditType().getClassType().isPolicy()) {
				Set<String> conIDs = new HashSet<String>();
				for (BasicDynaBean row : results) {
					conIDs.add(row.get("conID").toString());
				}
				SelectSQL sql2 = new SelectSQL("contractor_audit_operator cao");
				sql2.addJoin("JOIN contractor_audit ca ON ca.id = cao.auditID");
				sql2.addField("ca.auditTypeID");
				sql2.addField("ca.conID");
				sql2.addField("cao.status");
				sql2.addWhere("ca.conID IN (" + Strings.implode(conIDs) + ")");
				sql2.addWhere("cao.opID = " + fco.getOperator().getId());
				sql2.addWhere("ca.expiresDate > NOW()");

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
		List<BasicDynaBean> results = getResults(fco, op, db);
		List<FlagCriteriaContractor> fccs = new ArrayList<FlagCriteriaContractor>();

		if (results.size() > 0) {
			Map<Integer, List<ContractorAudit>> auditMap = buildAuditMap(results, fco, op, permissions, db);

			for (BasicDynaBean row : results) {
				ContractorAccount contractor = new ContractorAccount(Database.toInt(row, "conID"));
				contractor.setName(row.get("contractor_name").toString());
				contractor.setAcceptsBids(Database.toBoolean(row, "acceptsBids"));
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