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
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class OperatorFlagsCalculator extends PicsActionSupport {

	private Database db = new Database();
	private int fcoID;
	private String newHurdle;
	private boolean override = false;

	private List<FlagAndOverride> affected = new ArrayList<FlagAndOverride>();

	private FlagCriteriaOperatorDAO flagCriteriaOperatorDAO;
	private ContractorAccountDAO contractorAccountDAO;
	

	private FlagCriteriaOperator flagCriteriaOperator;

	public OperatorFlagsCalculator(FlagCriteriaOperatorDAO flagCriteriaOperatorDAO, ContractorAccountDAO contractorAccountDAO) {
		this.flagCriteriaOperatorDAO = flagCriteriaOperatorDAO;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	@Override
	public String execute() throws Exception {
		if (fcoID == 0)
			throw new Exception("Missing fcoID");

		flagCriteriaOperator = flagCriteriaOperatorDAO.find(fcoID);
		if (flagCriteriaOperator.getCriteria().isAllowCustomValue() && !Strings.isEmpty(newHurdle)) {
			flagCriteriaOperator.setHurdle(newHurdle);
		}

		SelectSQL sql = new SelectSQL("flag_criteria_contractor fcc");
		sql.addJoin("JOIN accounts a ON a.id = fcc.conID");
		sql.addJoin("JOIN contractor_info c ON c.id = fcc.conID");
		sql.addJoin("JOIN generalcontractors gc ON gc.subID = fcc.conID AND gc.genID = " + flagCriteriaOperator.getOperator().getId());
		sql.addJoin("LEFT JOIN flag_data_override fdo ON fdo.conID = fcc.conID AND fdo.opID = gc.genID AND fdo.criteriaID = fcc.criteriaID");
		sql.addWhere("fcc.criteriaID = " + flagCriteriaOperator.getCriteria().getId());
		if(flagCriteriaOperator.getOperator().getStatus().isDemo())
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
		sql.addOrderBy("a.name");

		List<BasicDynaBean> results = db.select(sql.toString(), false);

		if (results.size() > 0) {
			Map<Integer, List<ContractorAudit>> auditMap = new HashMap<Integer, List<ContractorAudit>>();

			if (flagCriteriaOperator.getCriteria().getAuditType() != null && flagCriteriaOperator.getCriteria().getAuditType().getClassType().isPolicy()) {
				Set<String> conIDs = new HashSet<String>();
				for (BasicDynaBean row : results) {
					conIDs.add(row.get("conID").toString());
				}
				SelectSQL sql2 = new SelectSQL("contractor_audit_operator cao");
				sql2.addJoin("JOIN contractor_audit ca ON ca.id = cao.auditID");
				sql2.addField("ca.auditTypeID");
				sql2.addField("ca.conID");
				sql2.addField("ca.auditStatus");
				sql2.addField("cao.status");
				sql2.addWhere("ca.conID IN (" + Strings.implode(conIDs) + ")");
				sql2.addWhere("cao.opID = " + flagCriteriaOperator.getOperator().getId());
				sql2.addWhere("ca.auditStatus != 'Expired'");

				List<BasicDynaBean> auditResults = db.select(sql2.toString(), false);
				for (BasicDynaBean row : auditResults) {
					int conID = Database.toInt(row, "conID");
					ContractorAudit ca = new ContractorAudit();
					ca.setAuditType(new AuditType(Database.toInt(row, "auditTypeID")));
					ca.setAuditStatus(AuditStatus.valueOf(row.get("auditStatus").toString()));
					{
						ContractorAuditOperator cao = new ContractorAuditOperator();
						cao.setAudit(ca);
						cao.setOperator(flagCriteriaOperator.getOperator());
						cao.setStatus(CaoStatus.valueOf(row.get("status").toString()));
						ca.setOperators(new ArrayList<ContractorAuditOperator>());
						ca.getOperators().add(cao);
					}
					if (auditMap.get(conID) == null)
						auditMap.put(conID, new ArrayList<ContractorAudit>());
					auditMap.get(conID).add(ca);
				}
			}

			for (BasicDynaBean row : results) {
				ContractorAccount contractor = new ContractorAccount(Database.toInt(row, "conID"));
				contractor.setName(row.get("contractor_name").toString());
				contractor.setAcceptsBids(Database.toBoolean(row, "acceptsBids"));
				contractor.setRiskLevel(LowMedHigh.valueOf(LowMedHigh.getName(Database.toInt(row, "riskLevel"))));
				contractor.setAudits(auditMap.get(contractor.getId()));

				FlagCriteriaContractor fcc = new FlagCriteriaContractor(contractor, flagCriteriaOperator.getCriteria(), row
						.get("answer") == null ? null : row.get("answer").toString());
				fcc.setVerified(Database.toBoolean(row, "verified"));

				if(!contractorAccountDAO.isContained(fcc.getContractor())) {
					fcc.setContractor(contractorAccountDAO.find(fcc.getContractor().getId()));
				}
				FlagDataCalculator calculator = new FlagDataCalculator(fcc, flagCriteriaOperator);
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
			if (Strings.isEmpty(newHurdle)) {
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

	public String getNewHurdle() {
		return newHurdle;
	}

	public void setNewHurdle(String newHurdle) {
		this.newHurdle = newHurdle;
	}

	public FlagCriteriaOperator getFlagCriteriaOperator() {
		return flagCriteriaOperator;
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
				cell.setCellValue(Double.parseDouble(data.getFlagData().getCriteriaContractor().getAnswer().replace(",", "")));
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