package com.picsauditing.actions.contractors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.actions.audits.AuditActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditPdfConverter extends AuditActionSupport {
	@Autowired
	protected OperatorAccountDAO operatorAccountDAO;
	@Autowired
	protected AuditDataDAO auditDataDAO;

	private Map<String, File> attachments = new TreeMap<String, File>();

	private Font headerFont = FontFactory
			.getFont(FontFactory.HELVETICA, 24, Font.BOLD, new BaseColor(0xa8, 0x4d, 0x10));
	private Font auditFont = FontFactory.getFont(FontFactory.HELVETICA, 20, new BaseColor(0x01, 0x21, 0x42));
	private Font categoryFont = FontFactory.getFont(FontFactory.HELVETICA, 20, new BaseColor(0xa8, 0x4d, 0x10));
	private Font subCategoryFont = FontFactory.getFont(FontFactory.HELVETICA, 16, new BaseColor(0xa8, 0x4d, 0x10));
	private Font questionTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 13, new BaseColor(0xa8, 0x4d, 0x10));
	private Font questionFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
	private Font answerFont = FontFactory.getFont(FontFactory.COURIER, 10, BaseColor.BLUE);

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		if (auditID > 0) {
			findConAudit();
		} else
			findContractor();

		String filename = contractor.getName();
		filename += ".pdf";

		ServletActionContext.getResponse().setContentType("application/pdf");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename = " + filename);
		Document document = new Document();
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		PdfWriter pdfWriter = PdfWriter.getInstance(document, outstream);
		try {
			document.open();
			createDocument(document, contractor);
			showOshaLogs(document, pdfWriter);

			document.close();
		} catch (IOException ioe) {
			// assuming user canceled download
		}
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
		return null;
	}

	private void createDocument(Document document, ContractorAccount contractor) throws Exception {
		try {
			Paragraph conName = new Paragraph(contractor.getName(), headerFont);
			conName.setAlignment(Element.ALIGN_CENTER);
			document.add(conName);
			if (conAudit != null) {
				loadAuditDocument(document, conAudit);
			} else {
				for (ContractorAudit audit : contractor.getAudits()) {
					if (!audit.isExpired() && (audit.getAuditType().isPqf() || audit.getAuditType().isAnnualAddendum())) {
						loadAuditDocument(document, audit);
					}
					document.newPage();
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private void loadAuditDocument(Document document, ContractorAudit audit) throws DocumentException {
		String auditName = audit.getAuditType().getName().toString() + " - ";
		if (audit.getAuditType().isPqf())
			auditName += DateBean.format(audit.getEffectiveDateLabel(), "MMM yyyy");
		else if (!Strings.isEmpty(audit.getAuditFor()))
			auditName += audit.getAuditFor();

		Paragraph name = new Paragraph(auditName, auditFont);
		name.setAlignment(Element.ALIGN_CENTER);
		document.add(name);

		AnswerMap answerMap = auditDataDAO.findAnswers(audit.getId());

		Map<AuditCategory, AuditCatData> allCategories = getCategories(audit, true);

		for (AuditCategory category : allCategories.keySet()) {
			if (category.getParent() == null)
				handleCategory(document, allCategories, answerMap, category, 0);
		}
	}
	// TODO: FIX ME
	private void addOshaLog(Document document, ContractorAudit audit, AuditCategory category) throws DocumentException {
		/*for (OshaAudit oshaAudit : audit.getOshas()) {
			if (matchesType(category.getId(), oshaAudit.getType())) {
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
										Font.UNDERLINE, new BaseColor(0, 0, 255)));
								anchor.setReference("http://www.picsorganizer.com/DownloadOsha.action?id="
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
					c.setBorderColor(new BaseColor(0xa8, 0x4d, 0x10));
					c.setPadding(5);
					oshaTable.addCell(c);
				}

				document.add(oshaTable);
			}
		}*/
	}

	private void handleCategory(Document document, Map<AuditCategory, AuditCatData> allCategories, AnswerMap answerMap,
			AuditCategory category, int indentLevel) throws DocumentException {
		AuditCatData auditCatData = allCategories.get(category);
		if (auditCatData != null && auditCatData.isApplies()) {
			Paragraph categoryParagraph = new Paragraph(category.getFullNumber() + ". " + category.getName(),
					categoryFont);
			categoryParagraph.setIndentationLeft(indentLevel);
			document.add(categoryParagraph);

			if (category.isSha())
				addOshaLog(document, auditCatData.getAudit(), category);
			else {
				for (AuditQuestion auditQuestion : category.getQuestions()) {
					if (auditQuestion.isCurrent())
						handleQuestion(document, auditQuestion, answerMap, indentLevel);
				}
			}

			for (AuditCategory subCategory : category.getSubCategories()) {
				handleCategory(document, allCategories, answerMap, subCategory, indentLevel + 10);
			}
		}
	}

	private void handleQuestion(Document document, AuditQuestion auditQuestion, AnswerMap answerMap, int indentLevel)
			throws DocumentException {

		if (auditQuestion.getTitle() != null && !Strings.isEmpty(auditQuestion.getTitle().toString())) {
			Paragraph questionTitleParagraph = new Paragraph(20, auditQuestion.getTitle().toString(), questionTitleFont);
			questionTitleParagraph.setIndentationLeft(indentLevel + 30);
			document.add(questionTitleParagraph);
		}
		Paragraph questionAnswer = new Paragraph();
		questionAnswer.setIndentationLeft(indentLevel + 30);
		String questionLine = "";
		if (auditQuestion.isRequired())
			questionLine += "* ";
		questionLine += auditQuestion.getNumber() + ". " + auditQuestion.getName();

		processQuestion(questionAnswer, questionLine);

		if (answerMap.get(auditQuestion.getId()) != null) {
			AuditData auditData = answerMap.get(auditQuestion.getId());
			if (!Strings.isEmpty(auditData.getAnswer())) {
				if (auditQuestion.getQuestionType().startsWith("File")) {
					if (auditData.getAnswer().length() > 0) {
						Anchor anchor = new Anchor("View File", FontFactory.getFont(FontFactory.COURIER, 10,
								Font.UNDERLINE, new BaseColor(0, 0, 255)));
						anchor.setReference("http://www.picsorganizer.com/DownloadAuditData.action?auditID="
								+ auditData.getAudit().getId() + "&auditData.question.id="
								+ auditData.getQuestion().getId());
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

	private void processQuestion(Paragraph paragraph, String question) {
		int index;
		String working = question;

		working = working.replaceAll("&nbsp;", " ");

		while (working.length() > 0) {
			index = (working.indexOf("<") >= 0) ? working.indexOf("<") : working.length();
			if (index != 0) {
				paragraph.add(new Chunk(working.substring(0, index), questionFont));
				working = working.substring(index, working.length());
			}
			if (working.length() > 0) {
				int tagIndex = working.indexOf(">");
				if (tagIndex >= 0) {
					String tag = working.substring(0, tagIndex + 1);
					working = working.substring(tagIndex + 1, working.length());

					// process special tags and ignore all others
					String lower = tag.toLowerCase();
					lower = lower + "";

					if (tag.toLowerCase().startsWith("<br") || tag.toLowerCase().startsWith("<li")) {
						paragraph.add(new Chunk(""));
					} else if (tag.toLowerCase().startsWith("<a")) {
						String ref = "";
						int refIndex = tag.indexOf("href=\"");
						if (refIndex >= 0) {
							int refEndIndex = tag.indexOf("\"", refIndex + 7);
							if (refEndIndex >= 0) {
								ref = tag.substring(refIndex + 6, refEndIndex);
							}
						}
						String text = "";
						int endIndex = working.indexOf("</a>");
						if (endIndex >= 0) {
							text = working.substring(0, endIndex);
							working = working.substring(endIndex + 4, working.length());
						}
						Anchor anchor = new Anchor(text);
						anchor.setReference(ref);
						paragraph.add(anchor);
					}
				}
			}
		}
	}

	private void showOshaLogs(Document document, PdfWriter pdfWriter) throws DocumentException, IOException {
		for (File oshaFile : attachments.values()) {
			InputStream pdfs = null;
			PdfReader pdfReader = null;
			try {
				pdfs = new FileInputStream(oshaFile);
				pdfReader = new PdfReader(pdfs);
				PdfContentByte cb = pdfWriter.getDirectContent();
				if (!pdfReader.isEncrypted()) {
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
				}
			} catch (Exception e) {
			} finally {
				try {
					if (pdfs != null) {
						pdfs.close();
						pdfs = null;
					}
					if (pdfReader != null) {
						pdfReader.close();
						pdfReader = null;
					}
				} catch (Exception ignore) {
				}
			}
		}
	}

	@Override
	public OperatorAccount getOperatorAccount() {
		if (permissions.isOperatorCorporate()) {
			return operatorAccountDAO.find(permissions.getAccountId());
		}
		return null;
	}
}
