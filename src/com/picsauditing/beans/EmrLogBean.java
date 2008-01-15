package com.picsauditing.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorInfoDAO;
import com.picsauditing.dao.DAOFactory;
import com.picsauditing.dao.PqfLogDAO;
import com.picsauditing.dao.PqfquestionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorInfo;
import com.picsauditing.jpa.entities.PqfLog;
import com.picsauditing.jpa.entities.PqfLogId;
import com.picsauditing.jpa.entities.Pqfquestion;
import com.picsauditing.jsf.utils.JSFListDataModel;


public class EmrLogBean extends JSFListDataModel<PqfLog>{
	
	
	private Integer cid = 0;
	ContractorInfoDAO dao;
	private List<Short> questionIDs;
	
	@Override
	protected List<PqfLog> getList() {		
					
		DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());
		dao = daof.getContractorInfoDAO();
		PqfquestionDAO qdao = daof.getPqfquestionDAO();
		PqfLogDAO ldao = daof.getPqfLogDAO();
		AccountDAO adao = daof.getAccountDAO();		
		ContractorInfo ci = dao.findById(cid, false);
		Account auditor = adao.findById(ci.getPqfAuditorId(),false);
		List<Short> existingIds = new ArrayList<Short>();
		List<Short> missingIds = new ArrayList<Short>();
		missingIds.addAll(0, questionIDs);
		if(ci != null && questionIDs != null){
			List<PqfLog> logs = ci.getPqfLogs();
			for(PqfLog log : logs)
				existingIds.add(log.getPqfquestion().getQuestionId());
			
			if(missingIds.removeAll(existingIds)){
				for(Short id : missingIds){
					PqfLogId logId = new PqfLogId(ci.getId(), id);
					Pqfquestion pqfq = qdao.findById(id, false);
					PqfLog newLog = new PqfLog(logId, ci, auditor, pqfq, Short.valueOf("0"), "", "", null, "", "", "");					
					ci.getPqfLogs().add(newLog);
					
				}
			}
						
			return ci.getPqfLogs();
		}else
			return new ArrayList<PqfLog>();
		
	}
	
	@Override
	public String select() {
		
		return "success";
	}

	@Override
	protected List<PqfLog> sortList(List<PqfLog> list) {
		 return null;
	}

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
		clearModel();
				
	}
	
	public ContractorInfoDAO getDao() {
		return dao;
	}

	public void setDao(ContractorInfoDAO dao) {
		this.dao = dao;
	}

	public List<Short> getQuestionIDs() {
		return questionIDs;
	}

	public void setQuestionIDs(List<Short> questionIDs) {
		this.questionIDs = questionIDs;
	}
	
	public PqfLog getLog(short qID){
		List<PqfLog> logs = ((List<PqfLog>)getDataModel().getWrappedData());
		for(PqfLog log : logs)
			if(log.getPqfquestion().getQuestionId() == qID)
				return log;
			
		return null;
	}
	
	public PqfLog getStateOfOrigin(){
		return getLog(Short.parseShort("123"));
	}
	
	public PqfLog getAnniversary(){
		return getLog(Short.parseShort("124"));
	}
	
	public PqfLog getCalculatedBy(){
		return getLog(Short.parseShort("125"));
	}
	
	public PqfLog getUpload2005(){
		return getLog(Short.parseShort("872"));
	}
	
	public PqfLog getEmr2005(){
		return getLog(Short.parseShort("889"));
	}
	
	public PqfLog getDoesMaintain(){
		return getLog(Short.parseShort("891"));
	}
	
	public PqfLog getEmr2006(){
		return getLog(Short.parseShort("1519"));
	}
	
	public PqfLog getUpload2006(){
		return getLog(Short.parseShort("1522"));
	}
	
	public PqfLog getEmr2007(){
		return getLog(Short.parseShort("1617"));
	}
	
	public PqfLog getUpload2007(){
		return getLog(Short.parseShort("1618"));
	}
	
	public int getClearCid(){
		return 0;
	}
	
}
