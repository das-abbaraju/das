package com.picsauditing.service.email;

import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailBuilderServiceTest {

	private EmailBuilderService emailBuilderService;
	@Mock
	private EmailBuilder emailBuilder;
	@Mock
	private EmailTemplate emailTemplate;
	@Mock
	private User fromUser;
	@Mock
	private Map<String, Object> tokenMap;

	@Before
	public void setUp() throws Exception {
		emailBuilderService = new EmailBuilderService();

		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(emailBuilderService, "emailBuilder", emailBuilder);

		when(fromUser.getEmail()).thenReturn("someuser@example.com");
	}

	@Test
	public void testBuildEmail() throws Exception {

		Map<String, Object> tokenMap = new HashMap<>();
		tokenMap.put("token1Key", "token1Value");
		tokenMap.put("token2Key", "token2Value");

		emailBuilderService.buildEmail(emailTemplate, fromUser, "csr@example.com", tokenMap);
		
		verify(emailBuilder).setTemplate(emailTemplate);
		verify(emailBuilder).setFromAddress(fromUser);
		verify(emailBuilder).setToAddresses("csr@example.com");
		verify(emailBuilder).addToken("token1Key", tokenMap.get("token1Key"));
		verify(emailBuilder).addToken("token2Key", tokenMap.get("token2Key"));
		verify(emailBuilder).build();

	}
}
