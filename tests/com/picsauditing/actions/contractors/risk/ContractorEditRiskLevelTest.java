package com.picsauditing.actions.contractors.risk;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.contractors.risk.ContractorEditRiskLevel;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.Database;

public class ContractorEditRiskLevelTest {
	private ContractorEditRiskLevel contractorEditRiskLevel;
	
	@Mock private EmailSender emailSender;
	@Mock private EmailBuilder emailBuilder;
	@Mock private ContractorAccount contractor;
	@Mock private EmailQueue emailQueue;
	@Mock private Database databaseForTesting;
	
	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
	}
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
		
		contractorEditRiskLevel = new ContractorEditRiskLevel();
		
		when(emailBuilder.build()).thenReturn(emailQueue);
		Whitebox.setInternalState(contractorEditRiskLevel, "emailBuilder", emailBuilder);
		Whitebox.setInternalState(contractorEditRiskLevel, "contractor", contractor);
		Whitebox.setInternalState(contractorEditRiskLevel, "emailSender", emailSender);
		Whitebox.setInternalState(contractorEditRiskLevel, "emailQueue", emailQueue);		
	}
	
	@Test
	public void testBuildAndSendBillingRiskDowngradeEmail() throws Exception {
		LowMedHigh newRisk = LowMedHigh.Low;
		LowMedHigh currentRisk = LowMedHigh.High;

		Whitebox.invokeMethod(contractorEditRiskLevel, "buildAndSendBillingRiskDowngradeEmail", newRisk, currentRisk);		

		verify(emailSender).send(emailQueue); 			
	}

	@Test
	public void testCheckSafetyStatus_highToLow() throws Exception {		
		LowMedHigh newRisk = LowMedHigh.Low;
		LowMedHigh oldRisk = LowMedHigh.High;		
		
		Whitebox.invokeMethod(contractorEditRiskLevel, "checkSafetyStatus", oldRisk, newRisk);	

		verify(emailSender).send(emailQueue);

	}
	@Test
	public void testCheckSafetyStatus_lowToHigh() throws Exception {		
		LowMedHigh newRisk = LowMedHigh.High;
		LowMedHigh oldRisk = LowMedHigh.Low;		
		
		Whitebox.invokeMethod(contractorEditRiskLevel, "checkSafetyStatus", oldRisk, newRisk);	

		Mockito.verifyZeroInteractions(emailSender);
	}
}
