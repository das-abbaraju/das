package com.picsauditing.menu.builder;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.menu.MenuComponent;
import com.picsauditing.menu.MenuWriter;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.json.simple.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class ContractorSubmenuBuilderTest extends PicsTranslationTest {

	public static final int ACCOUNT_ID = 5;
	public static final int CONTRACTOR_ID = 5;

	private ContractorSubmenuBuilder contractorSubmenuBuilder;

	@Mock
	private Permissions permissions;
	@Mock
	private ContractorAccount contractorAccount;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(translationService.getText(anyString(), any(Locale.class))).then(returnMockTranslation());

		contractorSubmenuBuilder = new ContractorSubmenuBuilder();

		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);
		when(contractorAccount.getId()).thenReturn(CONTRACTOR_ID);
	}

	@Test
	public void testAddSupportMenu_defaultCase() throws Exception {
		MenuComponent menubar = new MenuComponent();

		menubar = contractorSubmenuBuilder.addSupportMenu(menubar, permissions, contractorAccount, true);

		JSONArray result = MenuWriter.convertMenuToJSON(menubar);
		Approvals.verify(result.toString());
	}

	private List<MenuComponent> buildTestAuditMenu() {
		List<MenuComponent> auditMenu = new ArrayList<>();

		// PQF
		MenuComponent pqfComponent = new MenuComponent(ContractorSubmenuBuilder.PQF, "/ContractorDocuments.action?id=3");

		MenuComponent childItem = new MenuComponent(ContractorSubmenuBuilder.PQF, "/Audit.action?auditID=1");
		childItem.setAuditId(10);
		pqfComponent.getChildren().add(childItem);

		auditMenu.add(pqfComponent);

		// Auditguard
		MenuComponent auditguardComponent = new MenuComponent(ContractorSubmenuBuilder.AUDITGUARD,
				"/ContractorDocuments.action?id=3");

		childItem = new MenuComponent(ContractorSubmenuBuilder.MANUAL_AUDIT + " '12", "/Audit.action?auditID=1");
		childItem.setAuditId(20);
		auditguardComponent.getChildren().add(childItem);

		childItem = new MenuComponent(ContractorSubmenuBuilder.IMPLEMENTATION_AUDIT + " '12", "/Audit.action?auditID=1");
		childItem.setAuditId(30);
		auditguardComponent.getChildren().add(childItem);

		auditMenu.add(auditguardComponent);

		// Insureguard
		MenuComponent insureguardComponent = new MenuComponent(ContractorSubmenuBuilder.INSUREGUARD,
				"/ContractorDocuments.action?id=3");

		childItem = new MenuComponent(ContractorSubmenuBuilder.MANAGE_CERTIFICATES, "/Audit.action?auditID=1");
		childItem.setAuditId(40);
		insureguardComponent.getChildren().add(childItem);

		childItem = new MenuComponent(ContractorSubmenuBuilder.AUTOMOBILE_LIABILITY + " (New)",
				"/Audit.action?auditID=1");
		childItem.setAuditId(50);
		insureguardComponent.getChildren().add(childItem);

		childItem = new MenuComponent(ContractorSubmenuBuilder.EXCESS_UMBRELLA_LIABILITY + " (New)",
				"/Audit.action?auditID=1");
		childItem.setAuditId(60);
		insureguardComponent.getChildren().add(childItem);

		childItem = new MenuComponent(ContractorSubmenuBuilder.GENERAL_LIABILITY + " (New)", "/Audit.action?auditID=1");
		childItem.setAuditId(70);
		insureguardComponent.getChildren().add(childItem);

		childItem = new MenuComponent(ContractorSubmenuBuilder.WORKER_COMP + " (New)", "/Audit.action?auditID=1");
		childItem.setAuditId(80);
		insureguardComponent.getChildren().add(childItem);

		auditMenu.add(insureguardComponent);

		return auditMenu;
	}

	private static Answer<String> returnMockTranslation() {
		return new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return args[0].toString();
			}
		};
	}

}
