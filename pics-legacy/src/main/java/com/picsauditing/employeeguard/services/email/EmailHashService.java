package com.picsauditing.employeeguard.services.email;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.authentication.dao.EmailHashDAO;
import com.picsauditing.employeeguard.daos.softdeleted.SoftDeletedEmployeeDAO;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.util.Strings;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.NoSuchAlgorithmException;

public class EmailHashService {

	@Autowired
	private EmailHashDAO emailHashDAO;
	@Autowired
	private SoftDeletedEmployeeDAO softDeletedEmployeeDAO;

	public EmailHash findByHash(final String hash) {
		return emailHashDAO.findByHash(hash);
	}

	public EmailHash findByEmployee(final Employee employee) {
		return emailHashDAO.findByEmployee(employee.getId(), employee.getEmail());
	}

	public void expire(final EmailHash emailHash) {
		emailHash.setExpirationDate(DateBean.today());
		emailHashDAO.save(emailHash);
	}

	public boolean isUserRegistered(final EmailHash emailHash) {
		return validHash(emailHash) && userAlreadyRegistered(emailHash);
	}

	private boolean userAlreadyRegistered(EmailHash emailHash) {
		return emailHash.getEmployee().getProfile() != null;
	}

	public boolean invalidHash(final EmailHash emailHash) {
		return !isValid(emailHash);
	}

	public boolean isValid(final EmailHash emailHash) {
		return validHash(emailHash) && !isExpired(emailHash) && !userAlreadyRegistered(emailHash);
	}

	public boolean isExpired(final EmailHash emailHash) {
		return emailHash.getExpirationDate().before(DateBean.today());
	}

	private boolean validHash(final EmailHash emailHash) {
		return emailHash == null ? false : true;
	}

	/**
	 * Should throw an exception if the email address already exists in the system
	 *
	 * @param employee
	 * @return
	 * @throws Exception
	 */
	public EmailHash createNewHash(final Employee employee) throws Exception {
		if (invalidEmployee(employee)) {
			throw new CannotCreateEmailHashException();
		}

		EmailHash emailHash = findByEmployee(employee);
		if (emailHash == null) {
			return createNewEmailHash(employee);
		}

		return updateEmailHash(emailHash, employee);
	}

	private boolean invalidEmployee(final Employee employee) {
		return employee.getId() <= 0 || Strings.isEmpty(employee.getEmail());
	}

	private EmailHash createNewEmailHash(final Employee employee) throws NoSuchAlgorithmException {
		EmailHash emailHash = new EmailHash();
		emailHash.setCreatedDate(DateBean.today());
		emailHash.setEmployee(softDeletedEmployeeDAO.find(employee.getId()));

		return emailHashDAO.save(setExpirationDateEmailAndHashCode(emailHash, employee));
	}

	private EmailHash updateEmailHash(final EmailHash emailHash, final Employee employee) throws NoSuchAlgorithmException {
		return emailHashDAO.save(setExpirationDateEmailAndHashCode(emailHash, employee));
	}

	private EmailHash setExpirationDateEmailAndHashCode(final EmailHash emailHash, final Employee employee)
			throws NoSuchAlgorithmException {

		emailHash.setExpirationDate(new LocalDateTime().plusMonths(1).toDate());
		emailHash.setEmailAddress(employee.getEmail());
		emailHash.setHashCode(EmailHashCodeGenerator.generateHashCode(emailHash));

		return emailHash;
	}
}
