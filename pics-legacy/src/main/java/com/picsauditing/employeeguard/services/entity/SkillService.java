package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SkillService implements EntityService<AccountSkill, Integer>, Searchable<AccountSkill> {

	@Autowired
	private AccountSkillDAO accountSkillDAO;

	/* All Find Methods */

	@Override
	public AccountSkill find(Integer id) {
		return accountSkillDAO.find(id);
	}

	/* All search related methods */

	@Override
	public List<AccountSkill> search(String searchTerm, int accountId) {
		if (Strings.isNotEmpty(searchTerm)) {
			return accountSkillDAO.search(searchTerm, accountId);
		}

		return Collections.emptyList();
	}

	/* All Save Operations */

	@Override
	public AccountSkill save(AccountSkill accountSkill, int createdBy, Date createdDate) {
		accountSkill.setCreatedBy(createdBy);
		accountSkill.setCreatedDate(createdDate);
		return accountSkillDAO.save(accountSkill);
	}

	/* All Update Operations */

	@Override
	public AccountSkill update(final AccountSkill accountSkill, int updatedBy, Date updatedDate) {
		AccountSkill accountSkillToUpdate = find(accountSkill.getId());

		accountSkillToUpdate.setName(accountSkill.getName());
		accountSkillToUpdate.setDescription(accountSkill.getDescription());
		accountSkillToUpdate.setSkillType(accountSkill.getSkillType());
		accountSkillToUpdate.setIntervalType(accountSkill.getIntervalType());
		accountSkillToUpdate.setIntervalPeriod(accountSkill.getIntervalPeriod());
		accountSkillToUpdate.setRuleType(accountSkill.getRuleType());

		return accountSkillToUpdate;
	}

	/* All Delete Operations */

	@Override
	public void delete(final AccountSkill accountSkill) {
		accountSkillDAO.delete(accountSkill);
	}

	@Override
	public void deleteById(Integer id) {
		AccountSkill accountSkill = find(id);
		delete(accountSkill);
	}
}
