package com.picsauditing.actions.contractors;

import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ActionContext.class})
@PowerMockIgnore({"javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*"})
public class ContractorPaymentOptionsTest extends PicsTest {
	ContractorPaymentOptions contractorPaymentOptions;
	ContractorAccount contractor;
	Permissions permissions;
	
	@Mock
	ActionContext context;
	
	@Before
	public void setUp() throws Exception {
//		MockitoAnnotations.initMocks(this);
//
//		PowerMockito.mockStatic(ActionContext.class);
//		PowerMockito.when(ActionContext.getContext()).thenReturn(context);
//		PowerMockito.when(context.getName()).thenReturn("Test");
//		PowerMockito.when(context.get(ActionContext.LOCALE)).thenReturn(Locale.ENGLISH);
//		
//		Mockito.when(i18nCache.hasKey("Test.title", null)).thenReturn(Boolean.TRUE);
//		Mockito.when(i18nCache.hasKey("Test.title", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
//		Mockito.when(i18nCache.getText("Test.title", Locale.ENGLISH, (Object[])null)).thenReturn("Test");
//
//		contractorPaymentOptions = new ContractorPaymentOptions();
//		
//		permissions = EntityFactory.makePermission();
//		PicsTestUtil.forceSetPrivateField(contractorPaymentOptions, "permissions", permissions);
//		
//		contractor = EntityFactory.makeContractor();
//		PicsTestUtil.forceSetPrivateField(contractorPaymentOptions, "contractor", contractor);
		
	}

//	@Ignore
//	public void testDifferingExpirationDates() throws Exception{
//		/*
//		 * 
//		 * 		key = appPropDao.find("brainTree.key").getValue();
//		key_id = appPropDao.find("brainTree.key_id").getValue();
//
//*/
//		Whitebox.invokeMethod(contractorPaymentOptions, "loadCC");
//		fail("Not yet implemented");
//	}

}
