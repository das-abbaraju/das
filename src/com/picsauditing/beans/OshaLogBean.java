package com.picsauditing.beans;

import java.text.DecimalFormat;
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
	DecimalFormat intFormatter = new DecimalFormat("###,##0");
			
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
		
		if(out.size() == 0){
			VerifyOshaLog vol = new VerifyOshaLog();
			OshaLog log = new OshaLog();
			log.setContractorInfo(ci);
			log.setShatype("OSHA");
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
	
	public void updateNumRequired(String cID, String location) throws Exception {
		if (!"Corporate".equals(location))
			return;
		com.picsauditing.PICS.pqf.CategoryBean pcBean = new com.picsauditing.PICS.pqf.CategoryBean();
		VerifyOshaLog osha = getSelectedItem();	
		OshaLog log = osha.getEntity();
	    int numRequired = 9;
		int requiredCompleted = 0;
		
		//Adjust for NA
		if(osha.isNa1())
			requiredCompleted+=3;
		if(osha.isNa2())
			requiredCompleted+=3;		
		if(osha.isNa3())
			requiredCompleted+=3;
		
		//Adjust for man hours
		if(!osha.isNa1() && 0 != log.getManHours1()){
			requiredCompleted+=1;
			numRequired = 6;
		}		
		if(!osha.isNa2() && 0 != log.getManHours2()){
			requiredCompleted+=1;
			numRequired = 6;
		}
		if(!osha.isNa3() && 0 != log.getManHours3()){
			requiredCompleted+=1;
			numRequired = 6;
		}
		
		//Adjust for file uploads
		if(!osha.isNa1() && "Yes".equals(log.getFile2yearAgo())){
			requiredCompleted+=1;
			numRequired = 6;
		}		
		if(!osha.isNa2() && "Yes".equals(log.getFile3yearAgo())){
			requiredCompleted+=1;
			numRequired = 6;
		}
		if(!osha.isNa3() && "Yes".equals(log.getFile3yearAgo())){
			requiredCompleted+=1;
			numRequired = 6;
		}
		
		
		
		String percentCompleted = "100";
		if(numRequired !=0)			
			percentCompleted = intFormatter.format(((float)requiredCompleted*100)/numRequired);
		
		pcBean.replaceCatData("29",cID,"Yes",""+requiredCompleted,""+numRequired,percentCompleted);	
	}//udpateNumRequired
	
	
}
