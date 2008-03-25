package com.picsauditing.beans;

import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.picsauditing.dao.ContractorInfoDAO;
import com.picsauditing.dao.ContractorInfoReportDAO;
import com.picsauditing.jpa.entities.ContractorInfoReport;

public class ContractorInfoReportActionListener implements ActionListener {

	@Override
	public void processAction(ActionEvent arg0) throws AbortProcessingException {
		Map<String,Object> attrs = arg0.getComponent().getAttributes();
		//ContractorInfoReportDAO dao = (ContractorInfoReportDAO)attrs.get("crDao");
		ContractorInfoReportDAO dao = (ContractorInfoReportDAO) SpringJSFUtil.getSpringContext().getBean("ContractorInfoReportDAO");
		ContractorInfoReport cr = (ContractorInfoReport)attrs.get("cr");
		
		try {
			dao.find(cr.getId());
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new AbortProcessingException(e);			
		}
		

	}

}
