package com.picsauditing.actions.contractors;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;

import org.apache.struts2.ServletActionContext;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditPdfConverter extends ContractorActionSupport {
	private Map<String, File> attachments = new TreeMap<String, File>();
	private AuditDataDAO auditDataDAO;
	protected AuditCategoryDataDAO catDataDao;
	private Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD, new Color(0xa8, 0x4d, 0x10));
	private Font auditFont = FontFactory.getFont(FontFactory.HELVETICA, 20, Color.BLUE);
	private Font categoryFont = FontFactory.getFont(FontFactory.HELVETICA, 20, new Color(0xa8, 0x4d, 0x10));
	private Font subCategoryFont = FontFactory.getFont(FontFactory.HELVETICA, 16, new Color(0xa8, 0x4d, 0x10));
	private Font questionTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 13, new Color(0xa8, 0x4d, 0x10));
	private Font questionFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
	private Font answerFont = FontFactory.getFont(FontFactory.COURIER, 10, Color.BLUE);

	public AuditPdfConverter(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditDataDAO auditDataDAO,
			AuditCategoryDataDAO catDataDao) {
		super(accountDao, auditDao);
		this.auditDataDAO = auditDataDAO;
		this.catDataDao = catDataDao;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		String filename = contractor.getName();
		filename += ".pdf";

		ServletActionContext.getResponse().setContentType("application/pdf");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename = " + filename);
		Document document = new Document();
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		PdfWriter pdfWriter = PdfWriter.getInstance(document, outstream);

		document.open();
		createDocument(document, contractor);
		showOshaLogs(document, pdfWriter);
		document.close();
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
		return null;
	}

	private void createDocument(Document document, ContractorAccount contractor) throws Exception {
		try {
			Paragraph conName = new Paragraph(contractor.getName(), headerFont);
			conName.setAlignment(Element.ALIGN_CENTER);
			document.add(conName);
			for (ContractorAudit conAudit : contractor.getAudits()) {
				if (!conAudit.getAuditStatus().isExpired()
						&& (conAudit.getAuditType().isPqf() || conAudit.getAuditType().isAnnualAddendum())) {
					List<AuditCatData> aList = new ArrayList<AuditCatData>();
					String auditName = conAudit.getAuditType().getAuditName() + " - ";
					if (conAudit.getAuditType().isPqf())
						auditName += DateBean.format(conAudit.getEffectiveDate(), "MMM yyyy");
					else if (!Strings.isEmpty(conAudit.getAuditFor()))
						auditName += conAudit.getAuditFor();
					Paragraph name = new Paragraph(auditName, auditFont);
					name.setAlignment(Element.ALIGN_CENTER);
					document.add(name);
					AnswerMap answerMap = auditDataDAO.findAnswers(conAudit.getId());
					if (conAudit.getAuditType().isPqf())
						aList = catDataDao.findByAudit(conAudit, permissions);
					else
						aList = conAudit.getCategories();
					for (AuditCatData auditCatData : aList) {
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

	private void addOshaLog(Document document, ContractorAudit conAudit) throws DocumentException {
		for (OshaAudit oshaAudit : conAudit.getOshas()) {
			String logInfo = oshaAudit.getType().toString() + " - " + oshaAudit.getLocation();
			if (!Strings.isEmpty(oshaAudit.getDescription()))
				logInfo += " " + oshaAudit.getDescription();

			Paragraph oshaSubCat = new Paragraph(logInfo, subCategoryFont);
			oshaSubCat.setIndentationLeft(20);
			document.add(oshaSubCat);
			PdfPTable oshaTable = new PdfPTable(3);

			oshaTable.setWidths(new int[] { 75, 10, 15 });
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
				if (oshaAudit.isFileUploaded()) {
					File oshaDir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(oshaAudit.getId()));
					File[] files = FileUtils.getSimilarFiles(oshaDir, PICSFileType.osha.toString() + "_"
							+ oshaAudit.getId());
					if (files.length > 0) {
						File oshaFile = files[0];
						String filename = oshaFile.getName();
						String extension = filename.substring(filename.lastIndexOf('.') + 1, filename.length());
						if ("pdf".equalsIgnoreCase(extension)) {
							String fileMD5 = FileUtils.getFileMD5(oshaFile);
							if (fileMD5 == null || !attachments.containsKey(fileMD5)) {
								attachments.put(fileMD5, oshaFile);
							}
							cells.add(new PdfPCell(new Phrase("See Attached", questionFont)));
						} else {
							Anchor anchor = new Anchor("View File", FontFactory.getFont(FontFactory.COURIER, 10,
									Font.UNDERLINE, new Color(0, 0, 255)));
							anchor.setReference("http://www.picsauditing.com/DownloadOsha.action?id="
									+ oshaAudit.getId());
							anchor.setName("View File");
							Phrase phrase = new Phrase();
							phrase.add(anchor);
							cells.add(new PdfPCell(phrase));
						}
					}
				} else
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

	private void addAuditData(Document document, AuditCatData auditCatData, AnswerMap answerMap)
			throws DocumentException {
		for (AuditSubCategory auditSubCategory : auditCatData.getCategory().getValidSubCategories()) {
			Paragraph subCategoryParagraph = new Paragraph(20, "Sub Category "
					+ auditSubCategory.getCategory().getNumber() + " - " + auditSubCategory.getSubCategory(),
					subCategoryFont);
			subCategoryParagraph.setIndentationLeft(20);
			document.add(subCategoryParagraph);
			for (AuditQuestion auditQuestion : auditSubCategory.getQuestions()) {
				if (auditQuestion.isValid()) {
					if (!Strings.isEmpty(auditQuestion.getTitle())) {
						Paragraph questionTitleParagraph = new Paragraph(20, auditQuestion.getTitle(),
								questionTitleFont);
						questionTitleParagraph.setIndentationLeft(30);
						document.add(questionTitleParagraph);
					}
					Paragraph questionAnswer = new Paragraph();
					questionAnswer.setIndentationLeft(30);
					String questionLine = "";
					if ("Yes".equals(auditQuestion.getIsRequired()))
						questionLine += "*";
					questionLine += auditCatData.getCategory().getNumber() + "."
							+ auditQuestion.getSubCategory().getNumber() + "." + auditQuestion.getNumber() + " "
							+ auditQuestion.getQuestion();

					Chunk question = new Chunk(questionLine, questionFont);
					questionAnswer.add(question);
					if (answerMap.get(auditQuestion.getId()) != null) {
						AuditData auditData = answerMap.get(auditQuestion.getId());
						if (!Strings.isEmpty(auditData.getAnswer())) {
							if (auditQuestion.getQuestionType().startsWith("File")) {
								if (auditData.getAnswer().length() > 0) {
									Anchor anchor = new Anchor("View File", FontFactory.getFont(FontFactory.COURIER,
											10, Font.UNDERLINE, new Color(0, 0, 255)));
									anchor.setReference("http://www.picsauditing.com/DownloadAuditData.action?auditID="
											+ auditData.getAudit().getId() + "&answer.id=" + auditData.getId());
									anchor.setName("View File");
									questionAnswer.add(anchor);
								} else {
									questionAnswer.add(new Chunk("File Not Uploaded", answerFont));
								}
							} else {
								Chunk answer = new Chunk(auditData.getAnswer(), answerFont);
								questionAnswer.add("   ");
								questionAnswer.add(answer);
							}
						}
					}
					document.add(questionAnswer);
				}
			}
		}
	}

	private void showOshaLogs(Document document, PdfWriter pdfWriter) throws DocumentException, IOException {
		for (File oshaFile : attachments.values()) {
			try {
				InputStream pdfs = new FileInputStream(oshaFile);
				PdfReader pdfReader = new PdfReader(pdfs);
				PdfContentByte cb = pdfWriter.getDirectContent();
				for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
					Rectangle rec = pdfReader.getPageSizeWithRotation(i);
					if (rec.getWidth() > rec.getHeight())
						document.setPageSize(PageSize.A3.rotate());
					else
						document.setPageSize(PageSize.A4);
					document.newPage();
					PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, i);
					cb.addTemplate(page, 0, 0);
				}
			} catch (FileNotFoundException e) {
				continue;
			} catch (IOException e) {
				continue;
			}
		}
	}

}
