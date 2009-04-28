package com.picsauditing.actions.contractors;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.struts2.ServletActionContext;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Images;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorView extends ContractorActionSupport {
	private AuditBuilder auditBuilder;
	private OperatorTagDAO operatorTagDAO;
	private ContractorTagDAO contractorTagDAO;
	private AuditDataDAO auditDataDAO;
	public List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	public int tagId;
	Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD, new Color(0xa8, 0x4d, 0x10));
	Font auditFont = FontFactory.getFont(FontFactory.HELVETICA, 20, Color.BLUE);
	Font categoryFont = FontFactory.getFont(FontFactory.HELVETICA, 20, new Color(0xa8, 0x4d, 0x10));
	Font subCategoryFont = FontFactory.getFont(FontFactory.HELVETICA, 16, new Color(0xa8, 0x4d, 0x10));
	Font questionFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
	Font answerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLUE);

	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditBuilder auditBuilder,
			OperatorTagDAO operatorTagDAO, ContractorTagDAO contractorTagDAO, AuditDataDAO auditDataDAO) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
		this.operatorTagDAO = operatorTagDAO;
		this.contractorTagDAO = contractorTagDAO;
		this.auditDataDAO = auditDataDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		if ("AddTag".equals(button)) {
			ContractorTag cTag = new ContractorTag();
			cTag.setContractor(contractor);
			cTag.setTag(new OperatorTag());
			cTag.getTag().setId(tagId);
			cTag.setAuditColumns(permissions);
			contractor.getOperatorTags().add(cTag);
			accountDao.save(contractor);
		}

		if ("RemoveTag".equals(button)) {
			contractorTagDAO.remove(tagId);
		}

		if ("PrintPDF".equals(button)) {
			String filename = contractor.getName();
			filename += ".pdf";
			ServletActionContext.getResponse().setContentType("application/pdf");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename = " + filename);
			Document document = new Document();
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			PdfWriter.getInstance(document, outstream);

			document.open();
			createDocument(document);
			document.close();
			outstream.flush();
			ServletActionContext.getResponse().flushBuffer();
			return null;
		}

		if (permissions.isOperator()) {
			operatorTags = getOperatorTagNamesList();

			for (ContractorTag contractorTag : contractor.getOperatorTags()) {
				if (operatorTags.contains(contractorTag.getTag()))
					operatorTags.remove(contractorTag.getTag());
			}
		}
		auditBuilder.setUser(getUser());
		auditBuilder.buildAudits(this.contractor);

		this.subHeading = "Contractor Details";

		return SUCCESS;
	}

	public int getLogoWidth() {
		// System.out.println("getLogoWidth for " + contractor.getId());
		if (contractor.getLogoFile() == null)
			return 0;
		if (contractor.getLogoFile().equals("No"))
			return 0;

		String filename = getFtpDir() + "/logos/" + contractor.getLogoFile();
		// System.out.println("filename = " + filename);
		int width = 0;
		try {
			width = Images.getWidth(filename);
		} catch (IOException e) {
			System.out.println("failed to get logo width of " + filename + ": " + e.getMessage());
		}
		// System.out.println("width = " + width);
		if (width > 300)
			return 300;
		return width;
	}

	public List<OperatorTag> getOperatorTagNamesList() throws Exception {
		if (operatorTags != null && operatorTags.size() > 0)
			return operatorTags;

		return operatorTagDAO.findByOperator(permissions.getAccountId());
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public List<OperatorTag> getOperatorTags() {
		return operatorTags;
	}

	public void setOperatorTags(List<OperatorTag> operatorTags) {
		this.operatorTags = operatorTags;
	}

	public void createDocument(Document document) {
		try {
			Paragraph conName = new Paragraph(contractor.getName(), headerFont);
			conName.setAlignment(Element.ALIGN_CENTER);
			document.add(conName);
			for (ContractorAudit conAudit : contractor.getAudits()) {
				if (!conAudit.getAuditStatus().isExpired()
						&& (conAudit.getAuditType().isPqf() || conAudit.getAuditType().isAnnualAddendum())) {
					String auditName = conAudit.getAuditType().getAuditName() + " - ";
					if (conAudit.getAuditType().isPqf())
						auditName += DateBean.format(conAudit.getEffectiveDate(), "MMM yyyy");
					else if (!Strings.isEmpty(conAudit.getAuditFor()))
						auditName += conAudit.getAuditFor();
					Paragraph name = new Paragraph(auditName, auditFont);
					name.setAlignment(Element.ALIGN_CENTER);
					document.add(name);
					AnswerMap answerMap = auditDataDAO.findAnswers(conAudit.getId());
					for (AuditCatData auditCatData : conAudit.getCategories()) {
						if (auditCatData.isAppliesB() && auditCatData.getPercentCompleted() > 0) {
							if (conAudit.getAuditType().isPqf())
								auditCatData.getCategory().setValidDate(new Date());
							else
								auditCatData.getCategory().setValidDate(conAudit.getCreationDate());

							Paragraph categoryParagraph = new Paragraph("Category "
									+ auditCatData.getCategory().getNumber() + " - "
									+ auditCatData.getCategory().getCategory(), categoryFont);
							categoryParagraph.setIndentationLeft(10);
							document.add(categoryParagraph);

							if (auditCatData.getCategory().getId() == 151 || auditCatData.getCategory().getId() == 157
									|| auditCatData.getCategory().getId() == 158)
								addOshaLog(document, conAudit);
							else {
								addAuditData(document, auditCatData, answerMap);
							}
						}
					}
				}
				document.newPage();
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public void addOshaLog(Document document, ContractorAudit conAudit) throws DocumentException {
		for (OshaAudit oshaAudit : conAudit.getOshas()) {
			String logInfo = oshaAudit.getType().toString() + " - " + oshaAudit.getLocation();
			if (!Strings.isEmpty(oshaAudit.getDescription()))
				logInfo += " " + oshaAudit.getDescription();

			Paragraph oshaSubCat = new Paragraph(logInfo, subCategoryFont);
			oshaSubCat.setIndentationLeft(20);
			document.add(oshaSubCat);
			if (!oshaAudit.isApplicable()) {
				document.add(new Paragraph("Exempt from submitting " + oshaAudit.getType().toString() + " Logs",
						answerFont));
			} else {
				PdfPTable oshaTable = new PdfPTable(3);

				oshaTable.setWidths(new int[] { 80, 5, 15 });
				oshaTable.setSpacingBefore(20);

				List<PdfPCell> cells = new ArrayList<PdfPCell>();

				PdfPCell cell = new PdfPCell(new Phrase("Total Hours Worked"));
				cell.setColspan(2);
				cells.add(cell);
				cells.add(new PdfPCell(new Phrase(format(oshaAudit.getManHours(), "#,##0"), questionFont)));

				cells.add(new PdfPCell(new Phrase("")));
				cells.add(new PdfPCell(new Phrase("#")));
				cells.add(new PdfPCell(new Phrase("Rate")));

				cells.add(new PdfPCell(new Phrase("Number of Fatalities", questionFont)));
				cells.add(new PdfPCell(new Phrase("" + oshaAudit.getFatalities(), questionFont)));
				cells.add(new PdfPCell(new Phrase(format(oshaAudit.getFatalitiesRate()), questionFont)));

				String lostWorkDaysCases = "Number of Lost Workday Cases - Has lost days AND is "
						+ oshaAudit.getDescriptionReportable();
				if (oshaAudit.getType().equals(OshaType.COHS))
					lostWorkDaysCases = "Number of Lost Time Injuries";
				cells.add(new PdfPCell(new Phrase(lostWorkDaysCases, questionFont)));
				cells.add(new PdfPCell(new Phrase("" + oshaAudit.getLostWorkCases(), questionFont)));
				cells.add(new PdfPCell(new Phrase(format(oshaAudit.getLostWorkCasesRate(), "#,##0"), questionFont)));

				String lostWorkDays = "All lost workdays (regardless of restricted days) AND is "
						+ oshaAudit.getDescriptionReportable();
				if (oshaAudit.getType().equals(OshaType.COHS))
					lostWorkDays = "Number of Days Away From Work";
				cells.add(new PdfPCell(new Phrase(lostWorkDays, questionFont)));
				cells.add(new PdfPCell(new Phrase("" + oshaAudit.getLostWorkDays(), questionFont)));
				cells.add(new PdfPCell(new Phrase(format(oshaAudit.getLostWorkDaysRate()), questionFont)));

				String injuryAndIllness = "Injury & Illnesses Medical Cases - No lost OR restricted days AND is "
						+ oshaAudit.getDescriptionReportable() + "(non-fatal)";
				if (oshaAudit.getType().equals(OshaType.COHS))
					injuryAndIllness = "Number of Medical Aid/Treatment Cases";
				cells.add(new PdfPCell(new Phrase(injuryAndIllness, questionFont)));
				cells.add(new PdfPCell(new Phrase("" + oshaAudit.getInjuryIllnessCases(), questionFont)));
				cells.add(new PdfPCell(new Phrase(format(oshaAudit.getInjuryIllnessCasesRate()), questionFont)));

				String restrictedCases = "Has restricted days AND no lost days AND is "
						+ oshaAudit.getDescriptionReportable();
				if (oshaAudit.getType().equals(OshaType.COHS))
					restrictedCases = "Number of Restricted/Modified Cases";
				cells.add(new PdfPCell(new Phrase(restrictedCases, questionFont)));
				cells.add(new PdfPCell(new Phrase("" + oshaAudit.getRestrictedWorkCases(), questionFont)));
				cells.add(new PdfPCell(new Phrase(format(oshaAudit.getRestrictedWorkCasesRate()), questionFont)));

				String totalInjuriesAndIllnesses = "Total " + oshaAudit.getDescriptionReportable()
						+ " Injuries and Illnesses";
				if (oshaAudit.getType().equals(OshaType.COHS))
					totalInjuriesAndIllnesses = "Total Recordable Injuries and Illnesses";
				cells.add(new PdfPCell(new Phrase(totalInjuriesAndIllnesses, questionFont)));
				cells.add(new PdfPCell(new Phrase("" + oshaAudit.getRecordableTotal(), questionFont)));
				cells.add(new PdfPCell(new Phrase(format(oshaAudit.getRecordableTotalRate()), questionFont)));

				if (oshaAudit.getType().equals(OshaType.COHS)) {
					cell = new PdfPCell(new Phrase("What is your CAD-7", questionFont));
					cell.setColspan(2);
					cells.add(cell);
					cells.add(new PdfPCell(new Phrase("" + oshaAudit.getCad7(), questionFont)));

					cell = new PdfPCell(new Phrase("What is your NEER", questionFont));
					cell.setColspan(2);
					cells.add(cell);
					cells.add(new PdfPCell(new Phrase("" + oshaAudit.getNeer(), questionFont)));
				}
				if (!oshaAudit.getType().equals(OshaType.COHS)) {
					cell = new PdfPCell(new Phrase("Uploaded Log Files", questionFont));
					cell.setColspan(2);
					cells.add(cell);
					if(oshaAudit.isFileUploaded()) {
						Anchor anchor = new Anchor("View File", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.UNDERLINE, new Color(0, 0, 255)));
						anchor.setReference("http://www.picsauditing.com/DownloadOsha.action?id="+oshaAudit.getId());
						anchor.setName("View File");
						Phrase phrase = new Phrase();
						phrase.add(anchor);
						cells.add(new PdfPCell(phrase));
					}
					else
						cells.add(new PdfPCell(new Phrase("No File Uploaded", questionFont)));
				}

				for (PdfPCell c : cells) {
					c.setBorderColor(new Color(0xa8, 0x4d, 0x10));
					c.setPadding(5);
					oshaTable.addCell(c);
				}

				document.add(oshaTable);

			}
		}
	}

	public void addAuditData(Document document, AuditCatData auditCatData, AnswerMap answerMap)
			throws DocumentException {
		for (AuditSubCategory auditSubCategory : auditCatData.getCategory().getValidSubCategories()) {
			Paragraph subCategoryParagraph = new Paragraph(20, "Sub Category "
					+ auditSubCategory.getCategory().getNumber() + " - " + auditSubCategory.getSubCategory(),
					subCategoryFont);
			subCategoryParagraph.setIndentationLeft(20);
			document.add(subCategoryParagraph);
			for (AuditQuestion auditQuestion : auditSubCategory.getQuestions()) {
				if (auditQuestion.isValid()) {
					Paragraph questionAnswer = new Paragraph();
					questionAnswer.setIndentationLeft(30);
					Chunk question = new Chunk(auditCatData.getCategory().getNumber() + "."
							+ auditQuestion.getSubCategory().getNumber() + "." + auditQuestion.getNumber() + " "
							+ auditQuestion.getQuestion(), questionFont);
					questionAnswer.add(question);
					if (answerMap.get(auditQuestion.getId()) != null) {
						Chunk answer = new Chunk(answerMap.get(auditQuestion.getId()).getAnswer(), answerFont);
						questionAnswer.add("   ");
						questionAnswer.add(answer);
					}
					document.add(questionAnswer);
				}
			}
		}
	}
}
