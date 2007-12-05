package com.picsauditing.beans;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.ContractorInfoDAO;
import com.picsauditing.dao.DAOFactory;
import com.picsauditing.jpa.entities.ContractorInfo;
import com.picsauditing.jpa.entities.PqfLog;
import com.picsauditing.jsf.utils.JSFListDataModel;


public class EmrLogBean extends JSFListDataModel<PqfLog>{
	
	
	private Integer cid = 0;
	ContractorInfoDAO dao;
	
	@Override
	protected List<PqfLog> getList() {		
					
		DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());
		dao = daof.getContractorInfoDAO();		
		ContractorInfo ci = dao.findById(cid, false);
		if(ci != null)
			return ci.getPqfLogs();
		else
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
	
}
