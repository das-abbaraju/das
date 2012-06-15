package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class ContractorActionSupportTest extends PicsTest {
	ContractorActionSupport testClass;
	ContractorAccount contractor;
	OperatorAccount operator;
	OperatorAccount anotherOperator;
	List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
	
	List<Certificate> certList = new ArrayList<Certificate>();
	Map<Integer, List<Integer>> opIdsByCertIds = new HashMap<Integer, List<Integer>>();

	@Mock
	private Permissions permissions = new Permissions();
	@Mock
	CertificateDAO certDao = new CertificateDAO();
	@Mock
	private OperatorAccountDAO operatorDAO = new OperatorAccountDAO();
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		testClass = new ContractorActionSupport();
		autowireEMInjectedDAOs(testClass);
		
		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		anotherOperator = EntityFactory.makeOperator();
		operators.add(EntityFactory.addContractorOperator(contractor, operator));
		operators.add(EntityFactory.addContractorOperator(contractor, anotherOperator));
		
		PicsTestUtil.forceSetPrivateField(testClass, "contractor", contractor);
		PicsTestUtil.forceSetPrivateField(testClass, "permissions", permissions);
	}

	@Test
	public void testGetCertificates() {
		initCertificates();
		
		List<Certificate> certificates = testClass.getCertificates();
		assertEquals(1, certificates.size());
	}

	@Test
	public void testGetOperatorsUsingCertificate() {
		initCertificates();
		List<OperatorAccount> operators = testClass.getOperatorsUsingCertificate(1);
		assertEquals(1, operators.size());
	}

	private void initCertificates() {
		PicsTestUtil.forceSetPrivateField(testClass,
				"certificateDAO", certDao);
		when(certDao.findByConId(contractor.getId(), permissions, true)).thenReturn(certList);
		when(certDao.findOpsMapByCert(Matchers.anyListOf(Integer.class))).thenReturn(opIdsByCertIds);
		
		PicsTestUtil.forceSetPrivateField(testClass, "certificateDAO", certDao);
		PicsTestUtil.forceSetPrivateField(testClass, "operatorDAO", operatorDAO);
		PicsTestUtil.forceSetPrivateField(testClass, "operators", operators);
		
		operator.setType("Operator");
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getTopAccountID()).thenReturn(operator.getId());
		when(operatorDAO.find(Matchers.anyInt())).thenReturn(operator);
		
		Certificate cert1 = new Certificate();
		Certificate cert2 = new Certificate();
		cert1.setId(1);
		cert2.setId(2);
		certList.add(cert1);
		certList.add(cert2);
		
		List<Integer> opIds = new ArrayList<Integer>();
		opIds.add(operator.getId());
		opIdsByCertIds.put(cert1.getId(), opIds);
		opIdsByCertIds.put(cert2.getId(), new ArrayList<Integer>());
	}
}
