package com.picsauditing.actions.contractors;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;

import org.apache.struts2.ServletActionContext;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

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

		SimpleDateFormat dateGeneratedFormat = new SimpleDateFormat("MMMM d, yyyy");
		String dateGenerated = dateGeneratedFormat.format(new Date());
		SimpleDateFormat dateMemberSinceFormat = new SimpleDateFormat("MM/dd/yyyy");
		String dateMemberSince = dateMemberSinceFormat.format((contractor.getMembershipDate() == null) ? new Date()
				: contractor.getMembershipDate());

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

		size = shrinkFontSizeIfNeeded(contractor.getName(), contractorNameFont.getBaseFont(), 36, 600);	
		cb.setFontAndSize(contractorNameFont.getBaseFont(), size);
		cb.setCMYKColorFillF(0f, 0f, 0f, 1f);
		cb.showTextAligned(PdfContentByte.ALIGN_CENTER, contractor.getName(), 400, 400, 0);

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
