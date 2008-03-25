package com.picsauditing.beans;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorInfoDAO;
import com.picsauditing.dao.PqfLogDAO;
import com.picsauditing.dao.PqfquestionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorInfo;
import com.picsauditing.jpa.entities.PqfLog;
import com.picsauditing.jpa.entities.PqfLogId;
import com.picsauditing.jpa.entities.Pqfquestion;
import com.picsauditing.jsf.utils.JSFListDataModel;


public class EmrLogBean extends JSFListDataModel<VerifyEMRLog>{
	
	
	private Integer cid = 0;
	ContractorInfoDAO dao;
	private List<Short> questionIDs;
	
	
	@Override
	protected List<VerifyEMRLog> getList() {		
					
		//DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());
		dao = (ContractorInfoDAO) SpringJSFUtil.getSpringContext().getBean("ContractorInfoDAO");
		PqfquestionDAO qdao = (PqfquestionDAO) SpringJSFUtil.getSpringContext().getBean("PqfquestionDAO");
		PqfLogDAO ldao = (PqfLogDAO) SpringJSFUtil.getSpringContext().getBean("PqfLogDAO");
		AccountDAO adao = (AccountDAO) SpringJSFUtil.getSpringContext().getBean("AccountDAO");
		ContractorInfo ci = dao.find(cid);
		Account auditor = adao.find(ci.getPqfAuditorId());
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
					Pqfquestion pqfq = qdao.find(id);
					PqfLog newLog = new PqfLog(logId, ci, auditor, pqfq, Short.valueOf("0"), "", "", null, "", "", "");					
					ci.getPqfLogs().add(newLog);
					
				}
			}
			
			List<VerifyEMRLog> out = new ArrayList<VerifyEMRLog>();
			for(PqfLog log : ci.getPqfLogs()){
				 VerifyEMRLog vemrl = new VerifyEMRLog();
				 vemrl.setEntity(log);
				 out.add(vemrl);
			 }
						
			return out;
		}else
			return new ArrayList<VerifyEMRLog>();
		
	}
	
	@Override
	public String select() {
		
		return "success";
	}

	@Override
	protected List<VerifyEMRLog> sortList(List<VerifyEMRLog> list) {
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
	
	public VerifyEMRLog getLog(short qID){
		List<VerifyEMRLog> logs = ((List<VerifyEMRLog>)getDataModel().getWrappedData());
		for(VerifyEMRLog log : logs)
			if(log.getEntity().getPqfquestion().getQuestionId() == qID)
				return log;
			
		return null;
	}
	
	public VerifyEMRLog getStateOfOrigin(){
		return getLog(Short.parseShort("123"));
	}
	
	public VerifyEMRLog getAnniversary(){
		return getLog(Short.parseShort("124"));
	}
	
	public VerifyEMRLog getCalculatedBy(){
		return getLog(Short.parseShort("125"));
	}
	
	public VerifyEMRLog getUpload2005(){
		return getLog(Short.parseShort("872"));
	}
	
	public VerifyEMRLog getEmr2005(){
		return getLog(Short.parseShort("889"));
	}
	
	public VerifyEMRLog getDoesMaintain(){
		return getLog(Short.parseShort("891"));
	}
	
	public VerifyEMRLog getEmr2006(){
		return getLog(Short.parseShort("1519"));
	}
	
	public VerifyEMRLog getUpload2006(){
		return getLog(Short.parseShort("1522"));
	}
	
	public VerifyEMRLog getEmr2007(){
		return getLog(Short.parseShort("1617"));
	}
	
	public VerifyEMRLog getUpload2007(){
		return getLog(Short.parseShort("1618"));
	}
	
	public int getClearCid(){
		return 0;
	}	
}
