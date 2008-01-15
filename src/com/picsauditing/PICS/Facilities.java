package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;

public class Facilities extends DataBean{
	
	boolean isSet = false;
	HashSet<String> contractorsPaySet = new HashSet<String>();
	HashSet<String> contractorsDontPaySet = new HashSet<String>();
	HashSet<String> contractorsPayMultipleSet = new HashSet<String>();
	HashSet<String> corporateSet = new HashSet<String>();
	public Collection<String> pqfOnlySet = new HashSet<String>();
	public java.util.HashMap<String,String> nameMap = new HashMap<String,String>();

	public void resetFacilities() throws Exception{
		isSet = false;
	}//resetFacilities

	public boolean isPaying(String opID) throws Exception{
		setFacilitiesFromDB();
		return contractorsPaySet.contains(opID);
	}//isPaying
	public boolean isNotPaying(String opID) throws Exception{
		setFacilitiesFromDB();
		return contractorsDontPaySet.contains(opID);
	}//isNotPaying
	public boolean isMultiple(String opID) throws Exception{
		setFacilitiesFromDB();
		return contractorsPayMultipleSet.contains(opID);
	}//isMultiple
	public boolean isCorporate(String opID) throws Exception{
		setFacilitiesFromDB();
		return corporateSet.contains(opID);
	}//isCorporate
	public boolean isPqfOnly(String opID) throws Exception{
		setFacilitiesFromDB();
		return pqfOnlySet.contains(opID);
	}//ispqfOnly
	
	public boolean isPqfOnly(Collection<String> generalContractors) throws Exception{
		setFacilitiesFromDB();
		for (String facID : generalContractors) {
			if (!isPqfOnly(facID))
				return false;
		}//if
		return true;
	}//ispqfOnly

	public void setFacilitiesFromDB() throws Exception{
		if (isSet)
			return;
		contractorsPaySet = new HashSet<String>();
		contractorsDontPaySet = new HashSet<String>();
		contractorsPayMultipleSet = new HashSet<String>();
		corporateSet = new HashSet<String>();
		pqfOnlySet = new HashSet<String>();
		nameMap = new HashMap<String,String>();
		
		String selectQuery = "SELECT * FROM accounts INNER JOIN operators USING(id) WHERE type IN('Operator','Corporate') ORDER BY name ASC;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()){
				String doesPay = SQLResult.getString("doContractorsPay");
				String type = SQLResult.getString("type");
				String opID = SQLResult.getString("id");
				String name = SQLResult.getString("name");
				if ("Yes".equals(doesPay))
					contractorsPaySet.add(opID);
				else if ("No".equals(doesPay))
					contractorsDontPaySet.add(opID);
				else if ("Multiple".equals(doesPay))
					contractorsPayMultipleSet.add(opID);
				if ("Corporate".equals(type))
					corporateSet.add(opID);
				nameMap.put(opID,name);
			}//while
			SQLResult.close();
			selectQuery = "SELECT id FROM operators WHERE canSeePQF='Yes' "+
				"AND canSeeDesktop='No' AND canSeeDA='No' AND "+
				"canSeeOffice='No' AND canSeeField='No';";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next())
				pqfOnlySet.add(SQLResult.getString("id"));
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
		isSet = true;
		return;
	}//setFacilitiesFromDB

	public String getNameFromID(String facilityID) throws Exception{
		setFacilitiesFromDB();
		if (!nameMap.containsKey(facilityID))
			return "";
		return (nameMap.get(facilityID));
	}//getNameFromID
}//Facilities