package com.picsauditing.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Certificate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class CertificateDAOTest {

	@Autowired
	CertificateDAO certificateDAO;

	@Test
	public void testFindExpiredCertificate() {
		List<Certificate> cerList = certificateDAO.findExpiredCertificate();
		System.out.println(cerList.get(0));
	}

}
