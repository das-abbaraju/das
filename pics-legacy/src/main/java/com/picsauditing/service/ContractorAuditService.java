package com.picsauditing.service;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ContractorAuditService {

	private static final Logger logger = LoggerFactory.getLogger(ContractorAuditService.class);

	@Autowired
	protected ContractorAuditDAO contractorAuditDAO;
    @Autowired
    protected CertificateDAO certificateDAO;

    private Calendar today = null;

    public ContractorAudit findContractorAudit(int auditId) {
		return contractorAuditDAO.find(auditId);
	}

    public Calendar getToday() {
        Calendar date = Calendar.getInstance();
        if (today != null)
            date.setTime(today.getTime());
        return date;
    }

    public void setToday(Calendar today) {
        this.today = today;
    }

    public void extendExpirationOfAttachedCertificates(ContractorAudit audit) {
        List<Certificate> certificates = certificateDAO.findByConId(audit.getContractorAccount().getId());
        if (certificates.size() == 0)
            return;

        for (AuditData data : audit.getData()) {
            if (isValidCertificateQuestion(data)) {
                Certificate certificate = findCertificate(certificates, data);
                if (certificate != null)
                    updateCertificateExpiration(certificate, audit.getExpiresDate());
            }
        }
    }

    private boolean isValidCertificateQuestion(AuditData data) {
        if (!AuditQuestion.TYPE_FILE_CERTIFICATE.equals(data.getQuestion().getQuestionType()))
            return false;
        if (!data.isAnswered())
            return false;

        return true;
    }

    private Certificate findCertificate(List<Certificate> certificates, AuditData data) {
        try {
            int certId = Integer.parseInt(data.getAnswer());
            for (Certificate certificate:certificates) {
                if (certificate.getId() == certId)
                    return certificate;
            }
        } catch (Exception ignore) {
        }

        return null;
    }

    public void updateAuditEffectiveDateBasedOnAnswer(ContractorAudit audit, AuditData data) {
        if (StringUtils.isEmpty(data.getAnswer()))
            return;
        if (!isUniqueCodeForAuditEffectiveDate(data))
            return;

        Date effectiveDate = DateBean.parseDate(data.getAnswer());

        if (!DateBean.isNullDate(effectiveDate)) {
            audit.setEffectiveDate(effectiveDate);
            audit.setCreationDate(effectiveDate);
        }

        if (audit.getEffectiveDate() == null) {
            audit.setEffectiveDate(DateBean.setToEndOfDay(DateBean.addMonths(audit.getCreationDate(), 12)));
        }
        contractorAuditDAO.save(audit);
    }


    public void updateAuditExpirationDateBasedOnAnswer(ContractorAudit audit, AuditData data) {
        if (StringUtils.isEmpty(data.getAnswer()))
            return;
        if (!isUniqueCodeForAuditExpirationDate(data))
            return;

        Date expiresDate = DateBean.setToEndOfDay(DateBean.parseDate(data.getAnswer()));

        expiresDate = adjustExpirationDate(data, expiresDate);

        if (!DateBean.isNullDate(expiresDate)) {
            audit.setExpiresDate(expiresDate);
        }

        if (audit.getExpiresDate() == null) {
            audit.setExpiresDate(DateBean.setToEndOfDay(DateBean.addMonths(audit.getCreationDate(), 12)));
        }

        extendExpirationOfAttachedCertificates(audit);
        contractorAuditDAO.save(audit);
    }

    private Date adjustExpirationDate(AuditData data, Date expiresDate) {
        String uniqueCode = data.getQuestion().getUniqueCode();
        if ("policyExpirationDate".equals(uniqueCode))
            expiresDate = DateBean.getNextDayMidnight(expiresDate);

        if ("policyExpirationDatePlus120".equals(uniqueCode)) {
            expiresDate = DateBean.getNextDayMidnight(DateBean.parseDate(data.getAnswer()));
            if (!DateBean.isNullDate(expiresDate)) {
                Calendar date = getToday();
                date.setTime(expiresDate);
                date.add(Calendar.DATE, 120);
                expiresDate = date.getTime();
            }
        }

        if ("policyExpirationDatePlusMonthsToExpire".equals(uniqueCode)) {
            int monthsToExpire = 12;
            Integer specifiedMonthsToExpire = data.getAudit().getAuditType().getMonthsToExpire();
            if (specifiedMonthsToExpire != null) {
                monthsToExpire = specifiedMonthsToExpire.intValue();
            }

            expiresDate = DateBean.getNextDayMidnight(DateBean.parseDate(data.getAnswer()));
            if (!DateBean.isNullDate(expiresDate)) {
                Calendar date = getToday();
                date.setTime(expiresDate);
                date.add(Calendar.MONTH, monthsToExpire);
                expiresDate = date.getTime();
            }
        }

        if ("exipireMonths12".equals(uniqueCode)) {
            if (!DateBean.isNullDate(expiresDate)) {
                Calendar date = getToday();
                date.setTime(expiresDate);
                date.add(Calendar.MONTH, 12);
                expiresDate = date.getTime();
            }
        }

        return expiresDate;
    }

    private boolean isUniqueCodeForAuditExpirationDate(AuditData data) {
        if ("expirationDate".equals(data.getQuestion().getUniqueCode()))
            return true;
        if ("policyExpirationDate".equals(data.getQuestion().getUniqueCode()))
            return true;
        if ("policyExpirationDatePlus120".equals(data.getQuestion().getUniqueCode()))
            return true;
        if ("policyExpirationDatePlusMonthsToExpire".equals(data.getQuestion().getUniqueCode()))
            return true;
        if ("exipireMonths12".equals(data.getQuestion().getUniqueCode()))
            return true;
        return false;
    }
    private boolean isUniqueCodeForAuditEffectiveDate(AuditData data) {
        if ("policyEffectiveDate".equals(data.getQuestion().getUniqueCode()))
            return true;
        return false;
    }

    private void updateCertificateExpiration(Certificate certificate, Date auditExpiration) {
        boolean update = false;

        if (certificate.getExpirationDate() == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 6);
            certificate.setExpirationDate(cal.getTime());
            update = true;
        }

        if (auditExpiration != null && auditExpiration.after(certificate.getExpirationDate())) {
            certificate.setExpirationDate(auditExpiration);
            update = true;
        }

        if (update) {
            certificateDAO.save(certificate);
        }
    }

}
