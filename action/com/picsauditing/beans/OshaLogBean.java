package com.picsauditing.beans;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.ContractorInfoDAO;
import com.picsauditing.dao.DAOFactory;
import com.picsauditing.jpa.entities.ContractorInfo;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jsf.utils.JSFListDataModel;


public class OshaLogBean extends JSFListDataModel<VerifyOshaLog>{
	
	
	private Integer cid = 0;
	ContractorInfoDAO dao;
			
	@Override
	protected List<VerifyOshaLog> getList() {		
		
		DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());
		dao = daof.getContractorInfoDAO();		
		ContractorInfo ci = dao.findById(cid, false);
		
		List<OshaLog> list = ci.getOshaLogs();
		
		List<VerifyOshaLog> out = new ArrayList<VerifyOshaLog>();
		for(OshaLog log : list){
			 VerifyOshaLog vol = new VerifyOshaLog();
			 vol.setEntity(log);
			 out.add(vol);
		 }
		
		if(getSelectedItem() == null && out.size() > 0) 
			setSelectedItem(out.get(0));
		
		return out;
		
	}

	@Override
	public String select() {
		setSelectedItem((VerifyOshaLog)getDataModel().getRowData());
		return "success";
	}

	@Override
	protected List<VerifyOshaLog> sortList(List<VerifyOshaLog> list) {
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

	public String getNotes1() {
		if(getSelectedItem() != null){
			return getSelectedItem().getEntity().getComment1();
		}
		return null;
	}

	public void setNotes1(String notes) {
		getSelectedItem().getEntity().setComment1(notes);
	}
	
	public String getNotes2() {
		if(getSelectedItem() != null){
			return getSelectedItem().getEntity().getComment2();
		}
		
		return null;
	}

	public void setNotes2(String notes) {
		getSelectedItem().getEntity().setComment2(notes);
	}
	
	public String getNotes3() {
		if(getSelectedItem() != null){
			return getSelectedItem().getEntity().getComment3();
		}
		
		return null;
	}

	public void setNotes3(String notes) {		
		getSelectedItem().getEntity().setComment3(notes);
	}
	
	
	
}
