package com.picsauditing.struts.controller.pdf;

import com.lowagie.text.DocumentException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class GeneratePdf extends ContractorActionSupport {

    private ContractorAccount contractor;
    private final Logger logger = LoggerFactory.getLogger(GeneratePdf.class);

    public String ssipCertificate() {
        String url = getRequestHost() + "/ContractorCertification!ssipCertificate.action?contractor=" + contractor.getId();
        create(url, "SSIPCertificate.pdf");
        return SUCCESS;
    }

    public void create(final String url, String filename) {
        try {
            ServletActionContext.getResponse().setContentType("application/pdf");
            ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename = " + filename);

            final ITextRenderer iTextRenderer = new ITextRenderer();
            logger.info("DE377: GeneratePdf.create(): Calling setDocument with url = " + url);
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
