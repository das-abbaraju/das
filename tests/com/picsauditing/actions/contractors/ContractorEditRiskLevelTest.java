package com.picsauditing.actions.contractors;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ContractorEditRiskLevel.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class ContractorEditRiskLevelTest {
	private ContractorEditRiskLevel contractorEditRiskLevel = new ContractorEditRiskLevel();
	@Mock
	EmailSenderSpring emailSender;
	@Mock
	EmailBuilder emailBuilder;
	@Mock
	ContractorAccount contractor;
	@Mock
	EmailQueue emailQueue;
	@Before
	public void setUp() throws Exception {
		Whitebox.setInternalState(contractorEditRiskLevel, "emailBuilder", emailBuilder);
		Whitebox.setInternalState(contractorEditRiskLevel, "contractor", contractor);
		Whitebox.setInternalState(contractorEditRiskLevel, "emailSender", emailSender);
		Whitebox.setInternalState(contractorEditRiskLevel, "emailQueue", emailQueue);		
	}
	
	@Test
	public void testBuildAndSendBillingRiskDowngradeEmail() throws Exception {
		
		LowMedHigh newRisk = LowMedHigh.Low;
		LowMedHigh currentRisk = LowMedHigh.High;
		
		when(emailBuilder.build()).thenReturn(emailQueue);

		Whitebox.invokeMethod(contractorEditRiskLevel, "buildAndSendBillingRiskDowngradeEmail", newRisk, currentRisk);		

		verify(emailSender).send(emailQueue); 			
	}

	@Test
	public void testCheckSafetyStatus_highToLow() throws Exception {		
		LowMedHigh newRisk = LowMedHigh.Low;
		LowMedHigh oldRisk = LowMedHigh.High;		
		when(emailBuilder.build()).thenReturn(emailQueue);	
		Whitebox.invokeMethod(contractorEditRiskLevel, "checkSafetyStatus", oldRisk, newRisk);	

		verify(emailSender).send(emailQueue);

	}
	@Test
	public void testCheckSafetyStatus_lowToHigh() throws Exception {		
		LowMedHigh newRisk = LowMedHigh.High;
		LowMedHigh oldRisk = LowMedHigh.Low;		
		when(emailBuilder.build()).thenReturn(emailQueue);	
		Whitebox.invokeMethod(contractorEditRiskLevel, "checkSafetyStatus", oldRisk, newRisk);	
		Mockito.verifyZeroInteractions(emailSender);

	}
}
