package com.picsauditing.beans;

import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.picsauditing.dao.ContractorInfoDAO;

public class EMRLogActionListener implements ActionListener {

	@Override
	public void processAction(ActionEvent arg0) throws AbortProcessingException {
		Map<String,Object> attrs = arg0.getComponent().getAttributes();
		ContractorInfoDAO dao = (ContractorInfoDAO)attrs.get("emrDao");
		
		try {
			dao.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new AbortProcessingException(e);			
		}
		

	}

}
