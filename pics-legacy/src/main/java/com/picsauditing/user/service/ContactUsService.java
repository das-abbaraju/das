package com.picsauditing.user.service;

import com.picsauditing.PICS.MainPage;
import com.picsauditing.access.UnauthorizedException;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.contractor.ContractorEmailService;
import com.picsauditing.user.model.ContactUsInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class ContactUsService {

	@Autowired
	private ContractorEmailService contractorEmailService;

	public ContactUsInfo getContactUsInfo(User user) throws UnauthorizedException {
		if (user.getAccount().isContractor()) {
			ContractorAccount contractorAccount = (ContractorAccount) user.getAccount();
			ContactUsInfo contactUsInfo = buildContactUsInfo(contractorAccount);
			return contactUsInfo;
		} else {
			throw new UnauthorizedException();
		}
	}

	private ContactUsInfo buildContactUsInfo(ContractorAccount contractorAccount) {
		User currentCsr = contractorAccount.getCurrentCsr();

		ContactUsInfo contactUsInfo = new ContactUsInfo();
		contactUsInfo.setCsrName(currentCsr.getName());
		String csrPhoneNumberForCountry = findCsrPhoneNumberForCountry(contractorAccount.getCountry());
		contactUsInfo.setCsrPhoneNumber(csrPhoneNumberForCountry);
		contactUsInfo.setCsrPhoneNumberExtension(currentCsr.getPhone());

		return contactUsInfo;
	}

	public String findCsrPhoneNumberForCountry(Country country) {
		if (country != null) {
			return country.getCsrPhone();
		} else {
			return MainPage.PICS_PHONE_NUMBER;
		}
	}

	public void sendMessageToCsr(String subject, String message, User fromContractorUser) throws Exception {
		contractorEmailService.sendEmailToCsr(subject, message, fromContractorUser);
	}

}
