package com.picsauditing.dao.contractor;

import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.model.contractor.CertificateType;
import com.picsauditing.model.contractor.ContractorCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

public class ContractorCertificateDao extends PicsDAO {
    private final Logger logger = LoggerFactory.getLogger(ContractorCertificateDao.class);

    @Transactional(propagation = Propagation.NESTED)
    public ContractorCertificate save(ContractorCertificate o) {
        if (o.getId() == 0) {
            em.persist(o);
        } else {
            o = em.merge(o);
        }
        return o;
    }

    public List<ContractorCertificate> findByContractor(ContractorAccount contractorAccount) {
        Query query = em.createQuery("SELECT c FROM ContractorCertificate c " + "WHERE c.contractor = ? ORDER BY c.issueDate");
        query.setParameter(1, contractorAccount);
        return query.getResultList();
    }

    public ContractorCertificate findMostRecentByContractor(ContractorAccount contractorAccount, CertificateType certificateType) {
        Query query = em.createQuery("SELECT c FROM ContractorCertificate c " + "WHERE c.contractor = ? " +
                "AND c.certificateType = ? ORDER BY c.issueDate DESC");
        query.setParameter(1, contractorAccount);
        query.setParameter(2, certificateType);
        List<ContractorCertificate> list = query.getResultList();
        if (list == null || list.size() < 1)
            return null;
        return list.get(0);
    }
}
