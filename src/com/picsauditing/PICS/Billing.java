package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.util.Date;
import javax.servlet.ServletContext;
import com.picsauditing.search.SelectAccount;

/**
 * This is the billing class
 * 
 * @author Jeff Jensen
 */
public class Billing extends DataBean {
	public void updateAllPayingFacilities(ServletContext appServlet) throws Exception {
		Date lastUpdate = (Date)appServlet.getAttribute("updateAllPayingFacilities");
		if (lastUpdate != null) {
			// If it has been more than an hour, then don't run this method
			long milliSecsSinceLastRun = new Date().getTime() - lastUpdate.getTime();
			float hoursSinceLastRun = (float)milliSecsSinceLastRun / (1000 * 60 * 60);
			//System.out.println("lastUpdate: "+lastUpdate);
			//System.out.println("timeSinceLastRun: "+timeSinceLastRun);
			if (hoursSinceLastRun < 24)
				return;
		}
		// Set this to now
		lastUpdate = new Date();
		//System.out.println("setting lastUpdate: "+lastUpdate);
		appServlet.setAttribute("updateAllPayingFacilities", lastUpdate);
		
		updateAllPayingFacilities();
	}

	/**
	 * Calculate annual billing amounts for every contractor
	 * 
	 * @throws Exception
	 */
	public void updateAllPayingFacilities() throws Exception {
		try{
			SelectAccount sql = new SelectAccount();
			sql.setType(SelectAccount.Type.Contractor);
			//sql.addWhere("a.id = 153");
			
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(sql.toString());
			while (SQLResult.next()) {
				// For each contractor
				BillContractor bill = new BillContractor();
				bill.setContractor(SQLResult.getString("id"));
				int previousPrice = Integer.parseInt(bill.getContractor().newBillingAmount);
				int newPrice = bill.calculatePrice();
				
				if (newPrice != previousPrice) {
					bill.writeToDB();
				}
			}
			SQLResult.close();
		}finally{
			DBClose();
		}
	}
}
