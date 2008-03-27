package com.picsauditing.dao;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

import com.picsauditing.jpa.entities.ContractorInfoReport;

public class ContractorInfoReportDAO extends PicsDAO {
						 
	public ContractorInfoReport save(ContractorInfoReport o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		ContractorInfoReport row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	public ContractorInfoReport find(int id) {
        return em.find(ContractorInfoReport.class, id);
    }
	
    public List<ContractorInfoReport> findActiveContractors() {
        Query query = em.createQuery("select cr from ContractorInfoReport cr where cr.account.active='Y' AND cr.pqfSubmittedDate <> '0000-00-00' order by cr.pqfSubmittedDate asc");
        applyQueryMetaData(query);
        
        return query.getResultList();
    }

    public List<ContractorInfoReport> findContractorsByAuditor( int auditorId ) {
    	Query query = em.createQuery("select cr from ContractorInfoReport cr where cr.pqfAuditorId=? order by cr.pqfSubmittedDate asc");
    	query.setParameter(1, auditorId);
    	applyQueryMetaData(query);
    	return query.getResultList();
    }
    public List<ContractorInfoReport> findContractorsByName( String name ) {
    	Query query = em.createQuery("select cr from ContractorInfoReport cr where cr.account.name like ? order by cr.pqfSubmittedDate asc");
    	query.setParameter(1, name);
    	applyQueryMetaData(query);
    	return query.getResultList();
    }

    public List<ContractorInfoReport> findContractorsByOperator( int id ) {
    	Query query = em.createQuery("select gc.contracotInfo from GeneralContractor gc where gc.id.genId=?");
    	query.setParameter(1, id);
    	applyQueryMetaData(query);
    	return query.getResultList();
    }

    public List<ContractorInfoReport> findContractInfoReports( String acctName, int auditorId, int operatorId ) {
    	
    	StringBuffer queryBuf = new StringBuffer("select cr from ContractorInfoReport cr where cr.account.active='Y' AND cr.pqfSubmittedDate <> '0000-00-00' AND ");
    	
		if(!acctName.equals("")){
			queryBuf.append("cr.account.name like :name AND ");
		}

		if(auditorId != 0){
			queryBuf.append("cr.pqfAuditorId=:pqfAuditorId AND ");
		}
		
		if(operatorId != 0){
			queryBuf.append(":genId in (select gc.id.genId from cr.generalContractors gc) AND ");
		}
    	
		queryBuf.setLength(queryBuf.length()-5);

		Query query = em.createQuery(queryBuf.toString());
		
		if(!acctName.equals("")){
			query.setParameter("name", acctName + "%");
		}

		if(auditorId != 0){
			query.setParameter("pqfAuditorId", auditorId);
		}
		
		if(operatorId != 0){
			query.setParameter("genId", operatorId );
		}
		
    	applyQueryMetaData(query);
    	return query.getResultList();
    }
    
    public List<ContractorInfoReport> needsEmrVerification()
    {
    	String theQuery = "select c.id, c.pqfAuditor_id, c.pqfSubmittedDate from contractor_info c join accounts a on c.id=a.id and a.active='Y' " +
    		" join pqfdata e on c.id=e.conID and e.questionID in (889,1519,1617) and e.dateVerified='0000-00-00' where pqfSubmittedDate <> '0000-00-00' "+
    		"order by pqfSubmittedDate asc";
    	Query query = em.createNativeQuery(theQuery, ContractorInfoReport.class);
    	applyQueryMetaData(query);
    	return query.getResultList();
    }
    public List<ContractorInfoReport> needsOshaAndEmrVerification()
    {
    	String theQuery = "select c.id, c.pqfAuditor_id, c.pqfSubmittedDate from contractor_info c join accounts a on c.id=a.id and a.active='Y' "+
		"join pqfdata e on c.id=e.conID and e.questionID in (889,1519,1617) and e.dateVerified='0000-00-00' where pqfSubmittedDate <> '0000-00-00' "+
		"union select c.id, c.pqfAuditor_id, c.pqfSubmittedDate from contractor_info c join accounts a on c.id=a.id and a.active='Y' "+
		"join osha o on c.id=o.conID and (o.verifiedDate1 is null or o.verifiedDate2 is null or o.verifiedDate3 is null) where pqfSubmittedDate <> '0000-00-00' "+
		"order by pqfSubmittedDate asc";
    	Query query = em.createNativeQuery(theQuery, ContractorInfoReport.class);
    	applyQueryMetaData(query);
    	return query.getResultList();
    }
    
}
