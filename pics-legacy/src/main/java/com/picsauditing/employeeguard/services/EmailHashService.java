package com.picsauditing.employeeguard.services;

import com.picsauditing.authentication.dao.EmailHashDAO;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.util.Strings;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;

public class EmailHashService {
	@Autowired
	private EmailHashDAO emailHashDAO;

	public boolean hashIsValid(String hash) {
		if (Strings.isEmpty(hash) ||
				!emailHashDAO.hashExists(hash) ||
				emailHashDAO.hashIsExpired(hash)) {
			return false;
		}

		return true;
	}

	public EmailHash findByHash(String hash) {
		return emailHashDAO.findByHash(hash);
	}

	public void save(EmailHash emailHash) {
		emailHashDAO.save(emailHash);
	}

	public EmailHash createNewHash(Employee employeeRecord) throws Exception {
		EmailHash emailHash = new EmailHash();
		emailHash.setCreationDate(new Date());
		emailHash.setExpirationDate(new LocalDateTime().plusMonths(1).toDate());
		emailHash.setEmailAddress(employeeRecord.getEmail());
		emailHash.setEmployee(employeeRecord);

		String hash = emailHash.toString();
		MessageDigest msgDigest = MessageDigest.getInstance("MD5");
		msgDigest.update(hash.getBytes());
		byte[] hashed = msgDigest.digest();

		BigInteger number = new BigInteger(1, hashed);
		emailHash.setHash(number.toString(16).replace("+", "_"));

		emailHashDAO.save(emailHash);

		return emailHash;
	}
}
