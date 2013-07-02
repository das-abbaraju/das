package com.picsauditing.models.contractors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Locale;

import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.TextProvider;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;

public class ContractorFlagAnswerDisplayTest extends PicsTest {

	private ContractorFlagAnswerDisplay contractorFlagAnswerDisplay;

	@Mock
	private FlagCriteriaOperatorDAO flagCriteriaOperatorDao;
	@Mock
	private ContractorOperator contractorOperator;
	@Mock
	private TextProvider textProvider;

	private FlagCriteriaContractor flagCriteriaContractor;
	private FlagData flagData;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		contractorFlagAnswerDisplay = new ContractorFlagAnswerDisplay();

		autowireDAOsFromDeclaredMocks(contractorFlagAnswerDisplay, this);
		flagCriteriaContractor = EntityFactory.makeFlagCriteriaContractor("Yes");
		flagData = EntityFactory.makeFlagData();
		contractorFlagAnswerDisplay.setContractorOperator(contractorOperator);
		Whitebox.setInternalState(contractorFlagAnswerDisplay, "textProvider", textProvider);
		Whitebox.setInternalState(contractorFlagAnswerDisplay, "picsEnvironment", picsEnvironment);
	}

	@Test
	public void testGetContractorAnswer() throws Exception {
		assertEquals("Yes", contractorFlagAnswerDisplay.getContractorAnswer(flagCriteriaContractor, flagData, false));
	}

	@Test
	public void testGetContractorAnswer_AmbClass() throws Exception {
		flagData.getCriteria().setCategory(FlagCriteriaCategory.InsuranceAMBClass);
		flagCriteriaContractor.setAnswer("10");

		assertEquals("X", contractorFlagAnswerDisplay.getContractorAnswer(flagCriteriaContractor, flagData, false));
	}

	@Test
	public void testGetContractorAnswer_AmbRating() throws Exception {
		flagData.getCriteria().setCategory(FlagCriteriaCategory.InsuranceAMBRating);
		flagCriteriaContractor.setAnswer("10");

		assertEquals("A++", contractorFlagAnswerDisplay.getContractorAnswer(flagCriteriaContractor, flagData, false));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetContractorAnswer_Insurance() throws Exception {
		OperatorAccount operatorForFlagCriteria = EntityFactory.makeOperator();
		operatorForFlagCriteria.setInheritInsuranceCriteria(operatorForFlagCriteria);
		when(contractorOperator.getOperatorAccount()).thenReturn(operatorForFlagCriteria);

		when(translationService.hasKey(anyString(), eq(Locale.ENGLISH))).thenReturn(true);
		when(translationService.getText(eq("Insurance.RequiredLimit"), (Locale) any(), anyVararg())).thenReturn(
				"Required Limit: ");
		when(translationService.getText(eq("Insurance.YourLimit"), (Locale) any(), anyVararg())).thenReturn(
				"Your Limit: ");
		// when(textProvider.getText("Insurance.YourLimit")).thenReturn("Your Limit: ");

		FlagCriteria insuranceCriteria = EntityFactory.makeFlagCriteria();
		insuranceCriteria.setInsurance(true);
		flagCriteriaContractor.setCriteria(insuranceCriteria);
		flagCriteriaContractor.setAnswer("1000000");
		flagData.setCriteria(insuranceCriteria);

		FlagCriteriaOperator insuranceCriteriaOperator = new FlagCriteriaOperator();
		insuranceCriteriaOperator.setHurdle("1000000");
		when(flagCriteriaOperatorDao.findByOperatorAndCriteriaId(anyInt(), anyInt())).thenReturn(
				insuranceCriteriaOperator);

		String contractorAnswer = contractorFlagAnswerDisplay.getContractorAnswer(flagCriteriaContractor, flagData,
				false);

		assertEquals("Required Limit: 1,000,000 Your Limit: 1,000,000", contractorAnswer);

	}

}
