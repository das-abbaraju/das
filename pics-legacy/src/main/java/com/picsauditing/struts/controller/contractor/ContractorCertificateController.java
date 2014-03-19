package com.picsauditing.struts.controller.contractor;

import com.picsauditing.access.UnauthorizedException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.model.contractor.CdmScopeItem;
import com.picsauditing.model.contractor.CertificateType;
import com.picsauditing.model.contractor.CertificationMethod;
import com.picsauditing.model.contractor.ContractorCertificate;
import com.picsauditing.service.contractor.AuditFinderService;
import com.picsauditing.service.contractor.ContractorCertificateService;
import com.picsauditing.util.Strings;
import com.picsauditing.validator.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;

import static com.picsauditing.PICS.DateBean.buildDate;

public class ContractorCertificateController extends PicsActionSupport {

    public static final int ZERO_BASED_OFFSET = 1;
    public static final String URL_FOR_MANUAL_AUDIT = "/Audit.action?auditID=";
    public static final String URL_FOR_CONTRACTOR_SUMMARY = "/ContractorView.action?id=";
    public static final String CERTIFICATION_METHOD_MISSING_KEY = "JS.Validation.Contractor.Certificate.CertificationMethodMissing";
    public static final String EXPIRATION_DATE_MUST_BE_AFTER_ISSUE_DATE_KEY = "JS.Validation.Contractor.Certificate.ExpirationDateMustBeAfterIssueDate";
    public static final String INVALID_ISSUE_DATE_KEY = "JS.Validation.Contractor.Certificate.InvalidIssueDate";
    public static final String INVALID_EXPIRATION_DATE_KEY = "JS.Validation.Contractor.Certificate.ExpirationDate";
    private ContractorAccount contractor;

    private String certificationMethod;
    private String cdmScope;

    private Integer issueYear;
    private Integer issueMonth;
    private Integer issueDay;

    private Integer expirationYear;
    private Integer expirationMonth;
    private Integer expirationDay;

    ContractorCertificate contractorCertificate;

    @Autowired
    private ContractorCertificateService contractorCertificateService;
    @Autowired
    private AuditFinderService auditFinderService;
    @Autowired
    private InputValidator inputValidator;
    private String manualAuditUrl;

    private final Logger logger = LoggerFactory.getLogger(ContractorCertificateController.class);


    public String execute() throws UnauthorizedException {
        if (!permissions.isAuditor()) {
            throw new UnauthorizedException();
        }
        manualAuditUrl = generateManualAuditUrl();
        return SUCCESS;
    }

    public String issueSsipCertificate() throws UnauthorizedException, IOException {
        if (!permissions.isAuditor()) {
            throw new UnauthorizedException();
        }
        manualAuditUrl = generateManualAuditUrl();

        Date issueDate = buildIssueDate();
        Date expirationDate = buildExpirationDate();

        validateInput(issueDate, expirationDate);

        if (hasFieldErrors()) {
            return INPUT_ERROR;
        }

        ContractorCertificate contractorCertificate = buildContractorCertificate(issueDate, expirationDate);

        contractorCertificateService.issueCertificate(contractorCertificate);

        addActionMessage(buildActionMessage());

        return setUrlForRedirect(manualAuditUrl);
    }

    private ContractorCertificate buildContractorCertificate(Date issueDate, Date expirationDate) {
        return ContractorCertificate.builder()
                    .contractor(contractor)
                    .issueDate(issueDate)
                    .expirationDate(expirationDate)
                    .certificateType(CertificateType.SSIP)
                    .certificationMethod(CertificationMethod.valueOf(certificationMethod))
                    .cdmScope(cdmScope)
                    .build();
    }

    public String generateManualAuditUrl() {
        ContractorAudit manualAudit = auditFinderService.findManualAudit(contractor);

        if (manualAudit != null) {
            return URL_FOR_MANUAL_AUDIT + manualAudit.getId();
        } else {
            return URL_FOR_CONTRACTOR_SUMMARY + contractor.getId();
        }
    }

    private void validateInput(Date issueDate, Date expirationDate) {

        if (certificationMethod == null) {
            addFieldErrorIfMessage("certificateType", CERTIFICATION_METHOD_MISSING_KEY);
        }


        String errorMessageKey = inputValidator.validateDate(issueDate, true);
        if (!errorMessageKey.equals(InputValidator.NO_ERROR)) {
            addFieldErrorIfMessage("issueDay", INVALID_ISSUE_DATE_KEY);
        }

        errorMessageKey = inputValidator.validateDate(expirationDate, true);
        if (!errorMessageKey.equals(InputValidator.NO_ERROR)) {
            addFieldErrorIfMessage("expirationDay", INVALID_EXPIRATION_DATE_KEY);
        }

        if (issueDate != null && expirationDate != null && issueDate.after(expirationDate)) {
            addFieldErrorIfMessage("issueDay", EXPIRATION_DATE_MUST_BE_AFTER_ISSUE_DATE_KEY);
        }
    }

    private String addLeadingZeroIfNeccessary(Integer number) {
        String result = String.valueOf(number);
        if (number < 10 && number > 0) {
            result = "0" + result;
        }
        return result;
    }

    private Date buildIssueDate() {
        return buildDate(issueDay == null ? 0 : issueDay,
                (issueMonth == null ? 0: issueMonth) - ZERO_BASED_OFFSET,
                issueYear == null ? 0 :issueYear, true);
    }

    private Date buildExpirationDate() {
        return buildDate(expirationDay == null ? 0 :expirationDay,
                (expirationMonth == null ? 0: expirationMonth) - ZERO_BASED_OFFSET,
                expirationYear == null ? 0 :expirationYear, true);
    }

    private String buildActionMessage() {
        return "<strong>" + getText("Audit.CertificateIssued.Title") + "</strong>" +
                "<p>" + getText("Audit.CertificateDownload.PleaseVisit") +
                " <a href=\"/ContractorView.action?id=" + contractor.getId() + "\">" + getText("Audit.CertificateDownload.ContractorDashboard") + "</a> " +
                getText("Audit.CertificateDownload.Download") + "</p>";
    }

    public ContractorAccount getContractor() {
        return contractor;
    }

    public void setContractor(ContractorAccount contractor) {
        this.contractor = contractor;
    }

    public String getCurrentUserName() {
        return permissions.getName();
    }

    public String getManualAuditUrl() {
        return manualAuditUrl;
    }

    public String getCertificationMethod() {
        return certificationMethod;
    }

    public void setCertificationMethod(String certificationMethod) {
        this.certificationMethod = certificationMethod;
    }

    public CdmScopeItem[] getCdmScopeItems() {
        return CdmScopeItem.values();
    }

    public String[] getCdmScopeDbValues() {
        return cdmScope.split(",");
    }

    public String getCdmScope() {
        return cdmScope;
    }

    public void setCdmScope(String cdmScope) {
        this.cdmScope = removeSpacesInBetweenValues(cdmScope);
    }

    public int getIssueYear() {
        return issueYear;
    }

    public void setIssueYear(int issueYear) {
        this.issueYear = issueYear;
    }

    public String getIssueMonth() {
        return addLeadingZeroIfNeccessary(issueMonth);
    }

    public void setIssueMonth(int issueMonth) {
        this.issueMonth = issueMonth;
    }

    public String getIssueDay() {
        return addLeadingZeroIfNeccessary(issueDay);
    }

    public void setIssueDay(int issueDay) {
        this.issueDay = issueDay;
    }

    public int getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(int expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getExpirationMonth() {
        return addLeadingZeroIfNeccessary(expirationMonth);
    }

    public void setExpirationMonth(int expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public String getExpirationDay() {
        return addLeadingZeroIfNeccessary(expirationDay);
    }

    public void setExpirationDay(int expirationDay) {
        this.expirationDay = expirationDay;
    }

    public ContractorCertificate getContractorCertificate() {
        return contractorCertificate;
    }

    public void setContractorCertificate(ContractorCertificate contractorCertificate) {
        this.contractorCertificate = contractorCertificate;
    }

    private String removeSpacesInBetweenValues(String cdmScope) {
        if(Strings.isNotEmpty(cdmScope)) {
            return cdmScope.replaceAll(", ", ",");
        }
        return cdmScope;
    }
}
