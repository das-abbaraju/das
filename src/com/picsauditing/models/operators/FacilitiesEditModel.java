package com.picsauditing.models.operators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
public class FacilitiesEditModel {

	@Autowired
	private FacilitiesDAO facilitiesDAO;
	public static final int PICS_US = 5;
	public static final int PICS_CANADA = 6;
	public static final int PICS_UAE = 7;
	public static final int PICS_UK = 9;
	public static final int PICS_FRANCE = 10;
	public static final int PICS_GERMANY = 11;

	public void addPicsGlobal(OperatorAccount operator, Permissions permissions) {
		for (Facility f : operator.getCorporateFacilities()) {
			if (f.getCorporate().getId() == OperatorAccount.PicsConsortium) {
				return;
			}
		}

		Facility f = new Facility();
		f.setCorporate(new OperatorAccount());
		f.getCorporate().setId(OperatorAccount.PicsConsortium);
		f.setAuditColumns(permissions);
		f.setOperator(operator);
		facilitiesDAO.save(f);

		operator.getCorporateFacilities().add(f);
	}

	public void addPicsCountry(OperatorAccount operator, Permissions permissions) {
		boolean picsCountryNeedsToBeAdded = removeUnecessaryPicsCountries(operator);
	
		if (picsCountryNeedsToBeAdded) {
			Facility f = new Facility();
			f.setCorporate(new OperatorAccount());
			f.setAuditColumns(permissions);
			f.setOperator(operator);
			
			if (operator.getCountry().getIsoCode().equals("US"))
				f.getCorporate().setId(PICS_US);
			else if (operator.getCountry().getIsoCode().equals("CA"))
				f.getCorporate().setId(PICS_CANADA);
			else if (operator.getCountry().getIsoCode().equals("AE"))
				f.getCorporate().setId(PICS_UAE);
			else if (operator.getCountry().getIsoCode().equals("GB"))
				f.getCorporate().setId(PICS_UK);
			else if (operator.getCountry().getIsoCode().equals("FR"))
				f.getCorporate().setId(PICS_FRANCE);
			else if (operator.getCountry().getIsoCode().equals("DE"))
				f.getCorporate().setId(PICS_GERMANY);
			
			if (f.getCorporate().getId() > 0) {
				operator.getCorporateFacilities().add(f);
				facilitiesDAO.save(f);
			}
		}
	}

	private boolean removeUnecessaryPicsCountries(OperatorAccount operator) {
		boolean picsCountryNeedsToBeAdded = true;
		
		List<Facility> facilitiesToBeRemoved = new ArrayList<Facility>();
		for (Facility currrentFacility: operator.getCorporateFacilities()) {
			OperatorAccount corporate = currrentFacility.getCorporate();
			
			if (corporate.isPicsCorporate()
					&& corporate.getId() != OperatorAccount.PicsConsortium) {
				if (!corporate.getCountry().equals(operator.getCountry())) {
					facilitiesToBeRemoved.add(currrentFacility);
				}
				else {
					picsCountryNeedsToBeAdded = false;
				}
			}
		}
		
		operator.getCorporateFacilities().removeAll(facilitiesToBeRemoved);
		return picsCountryNeedsToBeAdded;
	}
}
