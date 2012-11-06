package com.picsauditing.PICS;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.hierarchy.HierarchyBuilder;

public class FacilityChangerTest {
	private FacilityChanger facilityChanger;
	@Mock
	private BillingCalculatorSingle billingService;
	@Mock
	private ContractorAccountDAO contractorAccountDAO;
	@Mock
	private ContractorOperatorDAO contractorOperatorDAO;
	@Mock
	private NoteDAO noteDAO;
	@Mock
	private AuditDataDAO auditDataDAO;
	@Mock
	private AccountLevelAdjuster accountLevelAdjuster;
	@Mock
	private HierarchyBuilder hierarchyBuilder;

	@Before
	public void setup() {
		facilityChanger = new FacilityChanger();

		Permissions permissions = new Permissions();
		permissions.setHierarchyBuilder(hierarchyBuilder);
		Whitebox.setInternalState(permissions, "accountType", "Contractor");

		facilityChanger.setPermissions(permissions);

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(facilityChanger, "billingService", billingService);
		Whitebox.setInternalState(facilityChanger, "contractorAccountDAO", contractorAccountDAO);
		Whitebox.setInternalState(facilityChanger, "contractorOperatorDAO", contractorOperatorDAO);
		Whitebox.setInternalState(facilityChanger, "noteDAO", noteDAO);
		Whitebox.setInternalState(facilityChanger, "auditDataDAO", auditDataDAO);
		Whitebox.setInternalState(facilityChanger, "accountLevelAdjuster", accountLevelAdjuster);
	}
	
	@Test
	public void testRemove_LoggedInContractor() {
		try {
			facilityChanger.setContractor(EntityFactory.makeContractor());
			facilityChanger.setOperator(EntityFactory.makeOperator());
			
			Permissions permissions = new Permissions();
			Whitebox.setInternalState(permissions, "accountType", "Contractor");
			Whitebox.setInternalState(permissions, "accountStatus", AccountStatus.Active);
			facilityChanger.setPermissions(permissions);

			facilityChanger.remove();
			fail("No exception thrown when there should have been");
		} catch (Exception e) {
			// what we want to happen
		}
	}

	@Test
	public void testRemove_AnyStatusContractorCanRemoveAnyStatusOperator() throws Exception {
		for (AccountStatus contractorStatus : AccountStatus.values()) {
			ContractorAccount contractor = createContractorWithStatus(contractorStatus);

			for (AccountStatus operatorStatus : AccountStatus.values()) {
				OperatorAccount operator = createOperatorWithStatus(operatorStatus);

				List<ContractorOperator> contractorOperators = buildContractorOperators(contractor, operator);
				contractor.setOperators(contractorOperators);

				assertTrue(!contractor.getOperators().isEmpty());

				facilityChanger.setContractor(contractor);
				facilityChanger.setOperator(operator);
				facilityChanger.remove();

				assertTrue(contractor.getOperators().isEmpty());
			}
		}
	}

	private ContractorAccount createContractorWithStatus(AccountStatus status) {
		ContractorAccount contractor = new ContractorAccount(3);
		contractor.setStatus(status);
		return contractor;
	}

	private OperatorAccount createOperatorWithStatus(AccountStatus status) {
		OperatorAccount operator = new OperatorAccount();
		operator.setId(16);
		operator.setStatus(status);
		return operator;
	}

	public List<ContractorOperator> buildContractorOperators(ContractorAccount contractor, OperatorAccount operator) {
		List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
		operators.add(operator);

		return buildContractorOperators(contractor, operators);
	}

	public List<ContractorOperator> buildContractorOperators(ContractorAccount contractor,
			List<OperatorAccount> operators) {
		List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();

		for (OperatorAccount operator : operators) {
			ContractorOperator co = new ContractorOperator();
			co.setContractorAccount(contractor);
			co.setOperatorAccount(operator);

			contractorOperators.add(co);
		}

		return contractorOperators;
	}
}
