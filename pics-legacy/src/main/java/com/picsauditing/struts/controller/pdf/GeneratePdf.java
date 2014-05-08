package com.picsauditing.struts.controller.pdf;

import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.model.contractor.ContractorCertificate;
import com.picsauditing.service.contractor.ContractorCertificateService;
import com.picsauditing.util.VelocityAdaptor;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.ServletOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GeneratePdf extends ContractorActionSupport {

    private ContractorAccount contractor;
    private final Logger logger = LoggerFactory.getLogger(GeneratePdf.class);

    @Autowired
    private ContractorCertificateService contractorCertificateService;

    public String ssipCertificate() throws Exception {

        String templateFolderPath = getWebAppRootPath() + "struts/contractor";
        String baseUrl = getRequestHost();

        Map<String, Object> data = buildSsipCertificateData();

        VelocityAdaptor velocityAdaptor = getVelocityAdaptor();
        String templateHtml = velocityAdaptor.mergeTemplateAndData("ssip_certificate_template.html", data, templateFolderPath);

        create(templateHtml, "SSIPCertificate.pdf", baseUrl);
        return SUCCESS;
    }

    private String getWebAppRootPath() {
        return ServletActionContext.getServletContext().getRealPath("/");
    }

    private Map<String, Object> buildSsipCertificateData() throws PageNotFoundException {
        Map<String, Object> data = new HashMap<>();
        ContractorCertificate contractorCertificate = contractorCertificateService.getSsipCertificate(contractor);
        if (contractorCertificate == null) {
            logger.error("No certificate found for contractor: " + contractor.getId() + ". Throwing PageNotFoundException...");
            throw new PageNotFoundException();
        }

        data.put("contractorCertificate", contractorCertificate);
        data.put("contractorName", StringEscapeUtils.escapeHtml(contractorCertificate.getContractor().getName()));
        return data;
    }

    private VelocityAdaptor getVelocityAdaptor() {
        Properties properties = new Properties();
        properties.put("resource.loader", "file");
        properties.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        return new VelocityAdaptor(properties);
    }

    public void create(final String templateHtml, String filename, String baseUrl) {
        try {
            ServletActionContext.getResponse().setContentType("application/pdf");
            ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename = " + filename);

            final ITextRenderer iTextRenderer = new ITextRenderer();

            iTextRenderer.setDocumentFromString(templateHtml, baseUrl);
            iTextRenderer.layout();

            ServletOutputStream outputStream = ServletActionContext.getResponse().getOutputStream();
            iTextRenderer.createPDF(outputStream);
            outputStream.close();

        } catch (Exception e) {
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
