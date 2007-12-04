package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.servlet.ServletContext;

/**
 * This is the billing class
 * 
 * @author Jeff Jensen
 */
public class Billing extends DataBean {
	public int calcPayingFacilities(Facilities F, Collection<String> generalContractors) throws Exception{
		int count = 0;
		int multipleCount = 0;
		for (String facID : generalContractors) {
			if(F.isPaying(facID))
				count++;
			if(F.isMultiple(facID))
				multipleCount++;
		}//for
		if (count>0)
			return count+multipleCount;
		if (multipleCount>1)
			return multipleCount;
		return 0;
	}//calcPayingFacilities

	public static int calcBillingAmount(int facilities, String isExempt, 
				String mustPay, boolean isPqfOnly) {
		if ("No".equals(mustPay))
			return 0;
		if ("Yes".equals(isExempt))
			return 99;
		if (isPqfOnly)
			return 99;
		return calcBillingAmount(facilities);
	}//cacalcBillingAmount

	public static int calcBillingAmount(int facilities){
		if (0==facilities)
			return 0;
		if (facilities<=1)
			return 399;
		if (facilities<=4)
			return 599;
		if (facilities<=8)
			return 799;
		if (facilities<=12)
			return 1099;
		if (facilities<=19)
			return 1399;
		return 1699;
	}//calcBillingAmount

	/**
	 * What is this for?
	 * 
	 * @throws Exception
	 */
	public void updateAllPayingFacilities(Facilities F, ServletContext appServlet) throws Exception {
		Collection<String> generalContractors = new ArrayList<String>();
		F.setFacilitiesFromDB();
		
		try{
			Date lastUpdate = (Date)appServlet.getAttribute("updateAllPayingFacilities");
			if (lastUpdate != null) {
				//System.out.println("lastUpdate: "+lastUpdate);
				// If it has been more than an hour, then don't run this method
				long timeSinceLastRun = new Date().getTime() - lastUpdate.getTime();
				//System.out.println("timeSinceLastRun: "+timeSinceLastRun);
				if (timeSinceLastRun < 30*1000)
					return;
			}
			// Set this to now
			lastUpdate = new Date();
			//System.out.println("setting lastUpdate: "+lastUpdate);
			appServlet.setAttribute("updateAllPayingFacilities", lastUpdate);
			
			
			String selectQuery = "SELECT isExempt, subID, genID, payingFacilities, newBillingAmount FROM generalContractors INNER JOIN "+
				"contractor_info ON(id=subID) WHERE mustPay='Yes' ORDER BY subID;";
			System.out.println(selectQuery);
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			String subID = "";
			String tempIsExempt = "";
			int tempPayingFacilities = 0;
			int tempNewBillingAmount = 0;
			boolean isPqfOnly = false;
			Collection<String> updateQueries = new ArrayList<String>();
			while (SQLResult.next()){
				String tempSubID = SQLResult.getString("subID");
				if (!subID.equals(tempSubID)){
					if (!"".equals(subID)){
						// Are all of the generalContractors PQF only?
						isPqfOnly = F.isPqfOnly(generalContractors);
						// How many paying (non-PQF only) facilities are there?
						int temp = calcPayingFacilities(F, generalContractors);
						// Figure out how much this contractor should pay
						int newBill = Billing.calcBillingAmount(temp,tempIsExempt,"Yes",isPqfOnly);
						if (tempPayingFacilities != temp || tempNewBillingAmount != newBill) {
							updateQueries.add("UPDATE contractor_info SET payingFacilities="+temp+
									",newBillingAmount="+newBill+" WHERE id="+subID+";");
						}
					}//if
					subID = tempSubID;
					tempPayingFacilities = SQLResult.getInt("payingFacilities");
					tempNewBillingAmount = SQLResult.getInt("newBillingAmount");
					tempIsExempt = SQLResult.getString("isExempt");
					generalContractors.clear();
				}//if
				generalContractors.add(SQLResult.getString("genID"));
			}//while
			SQLResult.close();
			// Process the last row here
			if (!"".equals(subID)){
				isPqfOnly = F.isPqfOnly(generalContractors);
				int temp = calcPayingFacilities(F, generalContractors);
				int newBill = Billing.calcBillingAmount(temp,tempIsExempt,"Yes",isPqfOnly);
				if (tempPayingFacilities != temp || tempNewBillingAmount != newBill) {
					updateQueries.add("UPDATE contractor_info SET payingFacilities="+temp+
							",newBillingAmount="+newBill+" WHERE id="+subID+";");
				}
			}//if
			// THIS IS BAD!!! We need to run one update instead of 6,000
			for (String updateQuery: updateQueries)
				SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//updateAllPayingFacilities
}//Billing
