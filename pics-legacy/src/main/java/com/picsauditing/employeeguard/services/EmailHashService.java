package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.authentication.dao.EmailHashDAO;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.util.Strings;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;

public class EmailHashService {

	@Autowired
	private EmailHashDAO emailHashDAO;

	public boolean hashIsValid(final String hash) {
		if (isInvalidHash(hash)) {
			return false;
		}

		return true;
	}

	private boolean isInvalidHash(String hash) {
		if (Strings.isEmpty(hash)) {
			return true;
		}

		EmailHash emailHash = findByHash(hash);

		return (emailHash == null || !(emailHash.getExpirationDate().after(DateBean.today())));
	}

	public EmailHash findByHash(final String hash) {
		return emailHashDAO.findByHash(hash);
	}

	public void expire(final EmailHash emailHash) {
		emailHash.setExpirationDate(new Date());
		emailHashDAO.save(emailHash);
	}

	public EmailHash createNewHash(final SoftDeletedEmployee employeeRecord) throws Exception {
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

		return emailHashDAO.save(emailHash);
	}

	public EmailHash createNewHash(final Employee employee) throws Exception {
		EmailHash emailHash = new EmailHash();
		emailHash.setCreationDate(new Date());
		emailHash.setExpirationDate(new LocalDateTime().plusMonths(1).toDate());
		emailHash.setEmailAddress(employee.getEmail());

		emailHash.setEmployee(new SoftDeletedEmployee(employee.getId()));

		String hash = emailHash.toString();
		MessageDigest msgDigest = MessageDigest.getInstance("MD5");
		msgDigest.update(hash.getBytes());
		byte[] hashed = msgDigest.digest();

		BigInteger number = new BigInteger(1, hashed);
		emailHash.setHash(number.toString(16).replace("+", "_"));

		return emailHashDAO.save(emailHash);
	}
}
