package com.picsauditing.actions.contractors;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.PICS.OpenTasks;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.User;

/**
 * Widgets for a single contractor
 * 
 * @author Trevor
 */
@SuppressWarnings("serial")
public class ContractorWidget extends ContractorActionSupport {
	@Autowired
	private AppPropertyDAO appPropDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private OpenTasks tasks;

	protected boolean reminderTask = false;

	protected boolean showAgreement = false;

	protected boolean openReq = false;

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		findContractor();
		return SUCCESS;
	}

	public void setShowAgreement(boolean showAgreement) {
		this.showAgreement = showAgreement;
	}

	public boolean getShowAgreement() {
		return showAgreement;
	}

	private List<String> openTasks = null;

	private CreditCard creditCard;

	public List<String> getOpenTasks() {
		if (openTasks == null) {
			User currentUser = userDAO.find(permissions.getUserId());
			openTasks = tasks.getOpenTasks(contractor, currentUser);
		}
		return openTasks;
	}

	public boolean isReminderTask() {

		if (Calendar.getInstance().get(Calendar.MONTH) == 0)
			if (contractor.getViewedFacilities() == null) {
				return true;
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				if (!sdf.format(contractor.getViewedFacilities()).equals(sdf.format(new Date()))) {
					return true;
				}
			}

		if (contractor.getViewedFacilities() == null) {
			return true;
		} else {
			Calendar ninetyDaysAgo = Calendar.getInstance();
			ninetyDaysAgo.setTime(new Date());
			ninetyDaysAgo.add(Calendar.DATE, -90);

			if (contractor.getViewedFacilities().compareTo(ninetyDaysAgo.getTime()) == -1) {
				return true;
			}

		}

		return false;
	}

	public boolean getHasUnpaidInvoices() {
		for (Invoice invoice : contractor.getInvoices()) {
			if (invoice.getStatus().isUnpaid())
				return true;
		}
		return false;
	}

	// Will return the earliest unpaid invoice with the assumption that
	// is the one we want to display on con_stats.jsp
	public Date getChargedOn() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
			Date d = format.parse("12/31/4000");
			for (Invoice invoice : contractor.getInvoices()) {
				if (invoice.getStatus().isUnpaid()) {
					Date d1 = invoice.getDueDate();
					if (d1.before(d))
						d = d1;
				}
			}
			return d;
		} catch (Exception ignoreFormattingErrors) {
		}
		return new Date();
	}

	public BrainTreeService.CreditCard getCreditCard() {
		if (creditCard == null) {
			try {
				BrainTreeService bt = new BrainTreeService();
				bt.setCanadaProcessorID(appPropDAO.find("brainTree.processor_id.canada").getValue());
				bt.setUsProcessorID(appPropDAO.find("brainTree.processor_id.us").getValue());
				bt.setUserName(appPropDAO.find("brainTree.username").getValue());
				bt.setPassword(appPropDAO.find("brainTree.password").getValue());

				creditCard = bt.getCreditCard(id);
			} catch (Exception itllJustStayNull) {
			}
		}
		return creditCard;
	}

	public List<BasicDynaBean> getSuggestedOperators() {
		try {
			return SmartFacilitySuggest.getSimilarOperators(contractor, 5);
		} catch (SQLException e) {
			return null;
		}
	}
}
