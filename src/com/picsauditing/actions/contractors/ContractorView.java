package com.picsauditing.actions.contractors;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.struts2.ServletActionContext;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.picsauditing.PICS.AuditBuilder;
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
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Images;

@SuppressWarnings("serial")
public class ContractorView extends ContractorActionSupport {
	private AuditBuilder auditBuilder;
	private OperatorTagDAO operatorTagDAO;
	private ContractorTagDAO contractorTagDAO;
	private AuditDataDAO auditDataDAO;
	public List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	public int tagId;

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
			createDocument(document);
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
		Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD, new Color(0xa8, 0x4d, 0x10));
		Font categoryFont = FontFactory.getFont(FontFactory.HELVETICA, 20, new Color(0xa8, 0x4d, 0x10));
		Font subCategoryFont = FontFactory.getFont(FontFactory.HELVETICA, 16, new Color(0xa8, 0x4d, 0x10));
		Font questionFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
		Font answerFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLUE);
		document.open();
		try {
			document.add(new Paragraph(contractor.getName(), headerFont));
			for (ContractorAudit conAudit : contractor.getAudits()) {
				if (!conAudit.getAuditStatus().isExpired()
						&& (conAudit.getAuditType().isPqf() || conAudit.getAuditType().isAnnualAddendum())) {
					document.add(new Paragraph(conAudit.getAuditType().getAuditName(), answerFont));
					AnswerMap answerMap = auditDataDAO.findAnswers(conAudit.getId());
					for (AuditCatData auditCatData : conAudit.getCategories()) {
						if (auditCatData.isAppliesB() && auditCatData.getPercentCompleted() > 0) {
							Paragraph categoryParagraph = new Paragraph("Category "
									+ auditCatData.getCategory().getNumber() + " - "
									+ auditCatData.getCategory().getCategory(), categoryFont);
							categoryParagraph.setIndentationLeft(10);
							document.add(categoryParagraph);
							for (AuditSubCategory auditSubCategory : auditCatData.getCategory().getValidSubCategories()) {
								Paragraph subCategoryParagraph = new Paragraph(20, "Sub Category "
										+ auditSubCategory.getCategory().getNumber() + " - "
										+ auditSubCategory.getSubCategory(), subCategoryFont);
								subCategoryParagraph.setIndentationLeft(20);
								document.add(subCategoryParagraph);
								for (AuditQuestion auditQuestion : auditSubCategory.getQuestions()) {
									Paragraph questionAnswer = new Paragraph();
									questionAnswer.setIndentationLeft(30);
									Chunk question = new Chunk(auditCatData.getCategory().getNumber() + "."
											+ auditQuestion.getSubCategory().getNumber() + "."
											+ auditQuestion.getNumber() + " " + auditQuestion.getQuestion(),
											questionFont);
									questionAnswer.add(question);
									if(answerMap.get(auditQuestion.getId()) != null) {
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
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.close();
	}
}
