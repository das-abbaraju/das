package com.picsauditing.search;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PicsDBTest;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.IndexObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class IndexValueTypeTest extends PicsDBTest {

	@Autowired
	private AccountDAO accountdao;
	@Autowired
	private UserDAO userDao;
	@Autowired
	private EmployeeDAO empDao;
	@Autowired
	private TradeDAO tradeDao;

	@Test
	public void account() {
		Account acc = accountdao.find(3);

		for (IndexObject io : acc.getIndexValues()) {
			System.out.printf("Value %s has weight %d%n", io.getValue(), io.getWeight());
		}
	}

	@Test
	public void user() {
		User u = userDao.find(941);

		for (IndexObject io : u.getIndexValues()) {
			System.out.printf("Value %s has weight %d%n", io.getValue(), io.getWeight());
		}
	}

	@Test
	public void employee() {
		Employee emp = empDao.find(2);

		for (IndexObject io : emp.getIndexValues()) {
			System.out.printf("Value %s has weight %d%n", io.getValue(), io.getWeight());
		}
	}

	@Test
	public void trade() {
		Trade trade = tradeDao.find(16);

		for (IndexObject io : trade.getIndexValues()) {
			System.out.printf("Value %s has weight %d%n", io.getValue(), io.getWeight());
		}
	}
}
