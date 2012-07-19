package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.LowMedHigh;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationMakePayment.class, I18nCache.class, TranslationActionSupport.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class RegistrationMakePaymentTest {
	RegistrationMakePayment registrationMakePayment;	
	private ContractorAccount contractor;
	@Mock
	private ContractorOperator contractorOperator;
	@Mock
	private I18nCache i18nCache;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);			
		PowerMockito.mockStatic(I18nCache.class);

		registrationMakePayment = new RegistrationMakePayment();
	}
		
	@Test
	public void testContractorRiskUrl() throws Exception{
		RegistrationMakePayment registrationMakePaymentSpy = spy(registrationMakePayment);
		contractor = new ContractorAccount(1);
		contractor.setMaterialSupplier(false);
		contractor.setTransportationServices(true);
		contractor.setSafetyRisk(LowMedHigh.None);
		contractor.setProductRisk(LowMedHigh.None);
		
		when(i18nCache.getText(anyString(), any(Locale.class), any())).thenReturn("Text");
		verify(registrationMakePaymentSpy, never()).addActionMessage(any(String.class));		
		Whitebox.setInternalState(registrationMakePayment, "contractor", contractor);
		
		//String url = Whitebox.invokeMethod(registrationMakePayment, "contractorRiskUrl", new String(""));
				
		//assertEquals("", url);
	}
}
