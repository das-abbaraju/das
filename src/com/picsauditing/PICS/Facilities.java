package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;

public class Facilities extends DataBean{
	
	HashSet<String> contractorsPaySet = new HashSet<String>();
	HashSet<String> contractorsDontPaySet = new HashSet<String>();
	HashSet<String> contractorsPayMultipleSet = new HashSet<String>();
	HashSet<String> corporateSet = new HashSet<String>();
	public java.util.HashMap<String,String> nameMap = new HashMap<String,String>();

	public void resetFacilities() throws Exception{
		isSet = false;
	}

	public boolean isPaying(String opID) throws Exception{
		setFacilitiesFromDB();
		return contractorsPaySet.contains(opID);
	}
	
	public boolean isNotPaying(String opID) throws Exception{
		setFacilitiesFromDB();
		return contractorsDontPaySet.contains(opID);
	}
	
	public boolean isMultiple(String opID) throws Exception{
		setFacilitiesFromDB();
		return contractorsPayMultipleSet.contains(opID);
	}
	
	public boolean isCorporate(String opID) throws Exception{
		setFacilitiesFromDB();
		return corporateSet.contains(opID);
	}
	
	public void setFacilitiesFromDB() throws Exception{
		if (isSet)
			return;
		contractorsPaySet = new HashSet<String>();
		contractorsDontPaySet = new HashSet<String>();
		contractorsPayMultipleSet = new HashSet<String>();
		corporateSet = new HashSet<String>();
		nameMap = new HashMap<String,String>();
		
		String selectQuery = "SELECT id, name, type, doContractorsPay FROM accounts INNER JOIN operators USING(id) WHERE type IN('Operator','Corporate') ORDER BY name ASC";
		try {
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
		}finally{
			DBClose();
		}//finally		
		isSet = true;
		return;
	}

	public List<BasicDynaBean> listAll() throws SQLException {
		return listAll("");
	}
	public List<BasicDynaBean> listAll(String filter) throws SQLException {
		SelectAccount sql = new SelectAccount();
		sql.setType(SelectAccount.Type.Operator);
		sql.addField("a.type");
		sql.addWhere("a.type IN ('Operator','Corporate')");
		sql.addWhere("a.active = 'Y'");
		sql.addWhere(filter);
		sql.addOrderBy("a.type, a.name");

		Database db = new Database();
		return db.select(sql.toString(), false);
	}

	public String getNameFromID(String facilityID) throws Exception{
		setFacilitiesFromDB();
		if (!nameMap.containsKey(facilityID))
			return "";
		return (nameMap.get(facilityID));
	}//getNameFromID
}
