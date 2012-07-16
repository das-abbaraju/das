package com.picsauditing.models.contractors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyInt;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.OperatorAccount;
/*
 * TODO: add more tests for this class.
 * 
 */
public class ContractorFlagAnswerDisplayTest extends PicsTest {
	@Spy
	private ContractorFlagAnswerDisplay contractorFlagAnswerDisplay = new ContractorFlagAnswerDisplay();;
	@Mock
	private FlagCriteriaOperatorDAO flagCriteriaOperatorDao;
	@Mock
	private NaicsDAO naicsDao;
	@Mock
	private ContractorOperator contractorOperator;

	private FlagCriteriaContractor flagCriteriaContractor;
	private FlagData flagData;

	@Test
	public void testGetContractorAnswer() throws Exception {
		setupGetContractorAnswer();

		assertEquals("Yes", contractorFlagAnswerDisplay.getContractorAnswer(
				flagCriteriaContractor, flagData, false));
	}

	@Test
	public void testGetContractorAnswer_AmbClass() throws Exception {
		setupGetContractorAnswer();
		flagData.getCriteria().setCategory("Insurance AMB Class");
		flagCriteriaContractor.setAnswer("10");

		assertEquals("X", contractorFlagAnswerDisplay.getContractorAnswer(
				flagCriteriaContractor, flagData, false));
	}

	@Test
	public void testGetContractorAnswer_AmbRating() throws Exception {
		setupGetContractorAnswer();
		flagData.getCriteria().setCategory("Insurance AMB Rating");
		flagCriteriaContractor.setAnswer("10");

		assertEquals("A++", contractorFlagAnswerDisplay.getContractorAnswer(
				flagCriteriaContractor, flagData, false));
	}

	@Test
	public void testGetContractorAnswer_Insurance() throws Exception {
		setupGetContractorAnswer();

		OperatorAccount operatorForFlagCriteria = EntityFactory.makeOperator();
		operatorForFlagCriteria
				.setInheritInsuranceCriteria(operatorForFlagCriteria);
		when(contractorOperator.getOperatorAccount()).thenReturn(
				operatorForFlagCriteria);

		doReturn("Required Limit: ").when(contractorFlagAnswerDisplay).getText(
				"Insurance.RequiredLimit");
		doReturn("Your Limit: ").when(contractorFlagAnswerDisplay).getText(
				"Insurance.YourLimit");

		FlagCriteria insuranceCriteria = EntityFactory.makeFlagCriteria();
		insuranceCriteria.setInsurance(true);
		flagCriteriaContractor.setCriteria(insuranceCriteria);
		flagCriteriaContractor.setAnswer("1000000");
		flagData.setCriteria(insuranceCriteria);

		FlagCriteriaOperator insuranceCriteriaOperator = new FlagCriteriaOperator();
		insuranceCriteriaOperator.setHurdle("1000000");
		when(
				flagCriteriaOperatorDao.findByOperatorAndCriteriaId(anyInt(),
						anyInt())).thenReturn(insuranceCriteriaOperator);

		assertEquals("Required Limit: 1,000,000 Your Limit: 1,000,000",
				contractorFlagAnswerDisplay.getContractorAnswer(
						flagCriteriaContractor, flagData, false));
	}

	private void setupGetContractorAnswer() throws Exception {
		this.setUp();
		MockitoAnnotations.initMocks(this);
		autowireEMInjectedDAOs(contractorFlagAnswerDisplay);

		PicsTestUtil.forceSetPrivateField(contractorFlagAnswerDisplay,
				"flagCriteriaOperatorDao", flagCriteriaOperatorDao);
		PicsTestUtil.forceSetPrivateField(contractorFlagAnswerDisplay,
				"naicsDao", naicsDao);

		flagCriteriaContractor = EntityFactory
				.makeFlagCriteriaContractor("Yes");
		flagData = EntityFactory.makeFlagData();

		contractorFlagAnswerDisplay.setContractorOperator(contractorOperator);
	}
}
