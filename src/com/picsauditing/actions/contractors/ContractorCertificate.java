package com.picsauditing.actions.contractors;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;

import org.apache.struts2.ServletActionContext;

import com.ibm.icu.text.DateFormat;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.picsauditing.util.PicsDateFormat;

@SuppressWarnings("serial")
public class ContractorCertificate extends ContractorActionSupport {
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findContractor();
		
		PdfReader reader = new PdfReader(ServletActionContext.getServletContext().getRealPath("/resources/MemberCertTemplate.pdf"));

		ServletActionContext.getResponse().setContentType("application/pdf");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename = " + "PICS Certificate.pdf");
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		PdfStamper writer = new PdfStamper(reader, outstream);

		DateFormat shortDate = DateFormat.getDateInstance(DateFormat.SHORT, contractor.getLocale());
		DateFormat longDate = DateFormat.getDateInstance(DateFormat.LONG, contractor.getLocale());
		String dateGenerated = longDate.format(new Date());
		String dateMemberSince = shortDate.format((contractor.getMembershipDate() == null) ? new Date()
				: contractor.getMembershipDate());
		String name = contractor.getName();
		String dbaName = null;
		float nameOffset = 400;
		if (contractor.getDbaName() != null && contractor.getDbaName().trim().length() > 0) {
			dbaName = getText("ContractorAccount.dbaName.short") + " " + contractor.getDbaName().trim();
		}

		PdfContentByte cb = writer.getOverContent(1);

		cb.beginText();

		float size = 0f;
		
		Font dateGeneratedFont = FontFactory.getFont(FontFactory.HELVETICA);
		Font dateMemberSinceFont = FontFactory.getFont(FontFactory.HELVETICA);
		Font contractorNameFont = FontFactory.getFont(FontFactory.HELVETICA);

		dateMemberSinceFont.setStyle("bold");
		contractorNameFont.setStyle("bold");
		
		size = shrinkFontSizeIfNeeded(dateGenerated, dateGeneratedFont.getBaseFont(), 24, 600);
		cb.setFontAndSize(dateGeneratedFont.getBaseFont(), size);
		cb.setCMYKColorFillF(0f, .53f, 1f, .282f);
		cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, dateGenerated, 675, 475, 0);

		size = shrinkFontSizeIfNeeded(name + dbaName, contractorNameFont.getBaseFont(), 36, 600);	
		cb.setFontAndSize(contractorNameFont.getBaseFont(), size);
		cb.setCMYKColorFillF(0f, 0f, 0f, 1f);
		if (dbaName != null) {
			nameOffset = 400 + (size/2);
		}
		cb.showTextAligned(PdfContentByte.ALIGN_CENTER, contractor.getName(), 400, nameOffset, 0);

		if (dbaName != null) {
			cb.setFontAndSize(contractorNameFont.getBaseFont(), size);
			cb.setCMYKColorFillF(0f, 0f, 0f, 1f);
			nameOffset = 400 - (size/2);
			cb.showTextAligned(PdfContentByte.ALIGN_CENTER, dbaName, 400, nameOffset, 0);
		}
		
 		size = shrinkFontSizeIfNeeded(dateMemberSince, dateMemberSinceFont.getBaseFont(), 24, 600);
		cb.setFontAndSize(dateMemberSinceFont.getBaseFont(), size);
		cb.setCMYKColorFillF(1f, .508f, 0f, .290f);
		cb.showTextAligned(PdfContentByte.ALIGN_CENTER, dateMemberSince, 400, 315, 0);

		cb.endText();
		
		writer.close();
		reader.close();

		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();

		return null;
	}
	
	private float shrinkFontSizeIfNeeded(String text, BaseFont font, float size, int maxWidth) {
		while (font.getWidthPoint(text, size) > maxWidth) {
			size--;
		}
		
		return size;
	}
}
