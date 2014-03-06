package com.picsauditing.struts.controller.pdf;

import com.lowagie.text.DocumentException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.service.contractor.ContractorCertificateService;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class GeneratePdf extends ContractorActionSupport {

    private ContractorAccount contractor;
    private final Logger logger = LoggerFactory.getLogger(GeneratePdf.class);

    @Autowired
    private ContractorCertificateService contractorCertificateService;

    public String ssipCertificate() throws Exception {
        String apiKey = contractorCertificateService.getApiKeyForPdfGeneration();
        String url = getRequestHost() + "/ContractorCertification!ssipCertificate.action?contractor=" + contractor.getId() + "&apiKey=" + apiKey;
        create(url, "SSIPCertificate.pdf");
        return SUCCESS;
    }

    public void create(final String url, String filename) {
        try {
            ServletActionContext.getResponse().setContentType("application/pdf");
            ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename = " + filename);

            final ITextRenderer iTextRenderer = new ITextRenderer();
            logger.info("GeneratePdf.create(): Calling setDocument with url = " + url);
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
