package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.Token;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class TokenDAOTest extends TestCase {

	@Autowired
	private TokenDAO tokendao;

	@Test
	public void testFind() {
		Token token = tokendao.find(1);
		assertEquals("CompanyName", token.getTokenName());
	}
	
	public void testFindByType() {
		List<Token> auditTokens = tokendao.findByType("Audits");
		List<Token> conTokens = tokendao.findByType("Audits");
		assertTrue(auditTokens.size() > conTokens.size());
	}
}
