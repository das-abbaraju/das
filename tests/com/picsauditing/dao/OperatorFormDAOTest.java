package com.picsauditing.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.OperatorForm;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class OperatorFormDAOTest {

	@Autowired
	OperatorFormDAO operatorFormDAO;

	@Test
	public void testDeleteOperatorForms() {
		List<OperatorForm> opList = operatorFormDAO.findByopID(784);
		System.out.println(opList.size());
	}

}
