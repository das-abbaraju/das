package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.authentication.dao.EmailHashDAO;
import com.picsauditing.employeeguard.daos.softdeleted.SoftDeletedEmployeeDAO;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.util.Strings;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class EmailHashService {

	@Autowired
	private EmailHashDAO emailHashDAO;
	@Autowired
	private SoftDeletedEmployeeDAO softDeletedEmployeeDAO;

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
		emailHash.setCreatedDate(DateBean.today());
		emailHash.setExpirationDate(new LocalDateTime().plusMonths(1).toDate());
		emailHash.setEmailAddress(employeeRecord.getEmail());
		emailHash.setEmployee(employeeRecord);

		String hash = emailHash.toString();
		MessageDigest msgDigest = MessageDigest.getInstance("MD5");
		msgDigest.update(hash.getBytes());
		byte[] hashed = msgDigest.digest();

		BigInteger number = new BigInteger(1, hashed);
		emailHash.setHashCode(number.toString(16).replace("+", "_"));

		return emailHashDAO.save(emailHash);
	}

	/**
	 * Should throw an exception if the email address already exists in the system
	 *
	 * @param employee
	 * @return
	 * @throws Exception
	 */
	public EmailHash createNewHash(final Employee employee) throws Exception {
		if (employee.getId() <= 0 || Strings.isEmpty(employee.getEmail())) {
			throw new CannotCreateEmailHashException();
		}

		EmailHash emailHash = findHash(employee);
		if (emailHash == null) {
			return createNewEmailHash(employee);
		}

		return updateEmailHash(emailHash, employee);
	}

	private EmailHash createNewEmailHash(final Employee employee) throws NoSuchAlgorithmException {
		EmailHash emailHash = new EmailHash();

		emailHash.setCreatedDate(DateBean.today());
		emailHash.setExpirationDate(new LocalDateTime().plusMonths(1).toDate());
		emailHash.setEmailAddress(employee.getEmail());
		emailHash.setEmployee(softDeletedEmployeeDAO.find(employee.getId()));

		emailHash.setHashCode(buildHashCode(emailHash));

		return emailHashDAO.save(emailHash);
	}

	private EmailHash findHash(final Employee employee) {
		return emailHashDAO.findByEmployee(employee.getId(), employee.getEmail());
	}

	private EmailHash updateEmailHash(final EmailHash emailHash, final Employee employee) throws NoSuchAlgorithmException {
		emailHash.setExpirationDate(new LocalDateTime().plusMonths(1).toDate());
		emailHash.setEmailAddress(employee.getEmail());
		emailHash.setHashCode(buildHashCode(emailHash));

		return emailHashDAO.save(emailHash);
	}

	private String buildHashCode(final EmailHash emailHash) throws NoSuchAlgorithmException {
		String hash = emailHash.toString();
		MessageDigest msgDigest = MessageDigest.getInstance("MD5");
		msgDigest.update(hash.getBytes());
		byte[] hashed = msgDigest.digest();

		BigInteger number = new BigInteger(1, hashed);
		return number.toString(16).replace("+", "_");
	}
}
