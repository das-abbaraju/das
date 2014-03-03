package com.picsauditing.struts.controller.pdf;

import com.lowagie.text.DocumentException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.apache.struts2.ServletActionContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class GeneratePdf extends ContractorActionSupport {

    private ContractorAccount contractor;


    public String ssipCertificate() {
        String url = getRequestHost() + "/ContractorCertification!ssipCertificate.action?contractor=" + contractor.getId();
        create(url, "SSIP Certificate.pdf");
        return SUCCESS;
    }

    public void create(final String url, String filename) {
        try {
            ServletActionContext.getResponse().setContentType("application/pdf");
            ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename = " + filename);

            final ITextRenderer iTextRenderer = new ITextRenderer();
            iTextRenderer.setDocument(url);
            iTextRenderer.layout();

            ServletOutputStream outputStream = ServletActionContext.getResponse().getOutputStream();
            iTextRenderer.createPDF(outputStream);
            outputStream.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public ContractorAccount getContractor() {
        return contractor;
    }

    public void setContractor(ContractorAccount contractor) {
        this.contractor = contractor;
    }
}
