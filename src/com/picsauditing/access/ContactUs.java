package com.picsauditing.access;

import java.util.Date;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class ContactUs extends PicsActionSupport {
	private UserDAO userDAO = null;
	private AccountUserDAO accountUserDAO = null;
	private User user = null;
	private OperatorAccount operatorAccount;
	private ContractorAccount contractorAccount;
	private AccountUser accountRep;
	
	public ContactUs(UserDAO userDAO, AccountUserDAO accountUserDAO) {
		this.userDAO = userDAO;
		this.accountUserDAO = accountUserDAO;
	}

	@Override
	public String execute() throws Exception {
		loadPermissions(false);
		
		if(permissions.isLoggedIn()) {
			user = userDAO.find(permissions.getUserId());
			if(permissions.isContractor()) 
				contractorAccount = (ContractorAccount) user.getAccount();
			else if (permissions.isOperatorCorporate()) {
				operatorAccount = (OperatorAccount) user.getAccount();
				List<AccountUser> accountUsers = accountUserDAO.findByAccount(operatorAccount.getId());
				for(AccountUser accountUser :  accountUsers) {
					if(accountUser.getRole().getDescription().equals("Account Manager")) {
						if(accountUser.getEndDate().after(new Date())) {
							accountRep = accountUser;
							break;
						}
					}
				}
			}	
		}
		
//		if(button != null) {
//			if(button.equals("SendEmail")) {
//				if(!Strings.isValidEmail(email)) {
//					addActionError("Please Enter a Valid Email Address");
//					return SUCCESS;
//				}
//				if(Strings.isEmpty(message)) {
//					addActionError("Please Enter a Message");
//				}
//				List<String> toAddresses = new ArrayList<String>();
//				if (getSendTo().equals("Sales"))
//					toAddresses.add("jmoreland@picsauditing.com");
//				if (getSendTo().equals("Sales"))
//					toAddresses.add("jsmith@picsauditing.com");
//				if (getSendTo().equals("Billing"))
//					toAddresses.add("billing@picsauditing.com");
//				if (getSendTo().equals("Audits"))
//					toAddresses.add("jcota@picsauditing.com");
//				if (getSendTo().equals("General"))
//					toAddresses.add("jfazeli@picsauditing.com");
//				if (getSendTo().equals("Support"))
//					toAddresses.add("jfazeli@picsauditing.com");
//				if (getSendTo().equals("Careers"))
//					toAddresses.add("careers@picsauditing.com");
//				
//				String body = "";
//				body += "Name: " + getName();
//				body += "\nCompany: " + getCompany();
//				body += "\nEmail: " + getEmail();
//				body += "\nPhone: " + getPhone();
//				body += "\nMessage:\nContact about " + getSendTo() + ". Sent to:\n";
//
//				for (String toAddress : toAddresses)
//					body += toAddress + "\n";
//				body += "\n" + getMessage();
//				
//				EmailQueue emailQueue = new EmailQueue();
//				emailQueue.setSubject("Email from PICS website");
//				emailQueue.setBody(body);
//				emailQueue.setToAddresses(Strings.implode(toAddresses, ","));
//				
//				EmailSender sender = new EmailSender();
//				sender.sendNow(emailQueue);
//			}
//		}
		
		return SUCCESS;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public AccountUser getAccountRep() {
		return accountRep;
	}
}
