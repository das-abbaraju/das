package com.picsauditing.beans;

import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.picsauditing.dao.ContractorInfoDAO;
import com.picsauditing.dao.ContractorInfoReportDAO;

public class OshaLogActionListener implements ActionListener {

	@Override
	public void processAction(ActionEvent arg0) throws AbortProcessingException {
		Map<String,Object> attrs = arg0.getComponent().getAttributes();
		ContractorInfoDAO dao = (ContractorInfoDAO) SpringJSFUtil.getSpringContext().getBean("ContractorInfoDAO");
		//ContractorInfoDAO dao = (ContractorInfoDAO)attrs.get("oshaDao");
		OshaLogBean oBean = (OshaLogBean)attrs.get("oBean");
		try {
			//dao.flush();
			oBean.updateNumRequired(oBean.getCid().toString(), oBean.getSelectedItem().getEntity().getLocation());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new AbortProcessingException(e);			
		}
		
		
		
	}
}
