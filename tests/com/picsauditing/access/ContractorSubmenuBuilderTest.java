package com.picsauditing.access;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.search.Database;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.json.simple.JSONArray;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class ContractorSubmenuBuilderTest {

	public static final int ACCOUNT_ID = 5;

	private ContractorSubmenuBuilder contractorSubmenuBuilder;
	@Mock
	private Permissions permissions;
	@Mock
	protected I18nCache i18nCache;
	@Mock
	private ContractorAccount contractorAccount;
	@Mock
	private ContractorAccountDAO contractorAccountDao;

	@BeforeClass
	public static void setUpClass() throws Exception {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", mock(Database.class));
	}

	@Before
	public void setUp() throws Exception {
		contractorSubmenuBuilder = new ContractorSubmenuBuilder();
		MockitoAnnotations.initMocks(this);

		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);
		when(i18nCache.getText(anyString(), any(Locale.class))).then(returnMockTranslation());
		Whitebox.setInternalState(ReportUtil.class, "i18nCache", i18nCache);
	}

	@AfterClass
	public static void tearDownClass() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
		Whitebox.setInternalState(ReportUtil.class, "i18nCache", (I18nCache) null);
	}

	@Test
	public void testAddCompanyMenu_defaultCase() throws Exception {
		MenuComponent menubar = new MenuComponent();

		menubar = contractorSubmenuBuilder.addCompanyMenu(menubar, permissions);

		JSONArray result = MenuWriter.convertMenuToJSON(menubar);
		Approvals.verify(result.toString());
	}

	@Test
	public void testAddCompanyMenu_admin() throws Exception {
		when(permissions.isAdmin()).thenReturn(true);
		MenuComponent menubar = new MenuComponent();

		menubar = contractorSubmenuBuilder.addCompanyMenu(menubar, permissions);

		JSONArray result = MenuWriter.convertMenuToJSON(menubar);
		Approvals.verify(result.toString());
	}

	@Test
	public void testAddCompanyMenu_adminAndDemo() throws Exception {
		when(permissions.isAdmin()).thenReturn(true);
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Demo);
		MenuComponent menubar = new MenuComponent();

		menubar = contractorSubmenuBuilder.addCompanyMenu(menubar, permissions);

		JSONArray result = MenuWriter.convertMenuToJSON(menubar);
		Approvals.verify(result.toString());
	}

	@Test
	public void testAddCompanyMenu_contractor() throws Exception {
		when(permissions.isContractor()).thenReturn(true);
		MenuComponent menubar = new MenuComponent();

		menubar = contractorSubmenuBuilder.addCompanyMenu(menubar, permissions);

		JSONArray result = MenuWriter.convertMenuToJSON(menubar);
		Approvals.verify(result.toString());
	}

	@Test
	public void testAddCompanyMenu_generalContractor() throws Exception {
		when(permissions.isGeneralContractor()).thenReturn(true);
		MenuComponent menubar = new MenuComponent();

		menubar = contractorSubmenuBuilder.addCompanyMenu(menubar, permissions);

		JSONArray result = MenuWriter.convertMenuToJSON(menubar);
		Approvals.verify(result.toString());
	}

	@Test
	public void testAddDocuguardMenu_defaultCase() throws Exception {
		MenuComponent menubar = new MenuComponent();
		List<MenuComponent> auditMenu = buildTestAuditMenu();

		menubar = contractorSubmenuBuilder.addDocuguardMenu(menubar, permissions, auditMenu);

		JSONArray result = MenuWriter.convertMenuToJSON(menubar);
		Approvals.verify(result.toString());
	}

	@Test
	public void testAddAuditguardMenu_defaultCase() throws Exception {
		MenuComponent menubar = new MenuComponent();
		List<MenuComponent> auditMenu = buildTestAuditMenu();

		menubar = contractorSubmenuBuilder.addAuditguardMenu(menubar, permissions, auditMenu);

		JSONArray result = MenuWriter.convertMenuToJSON(menubar);
		Approvals.verify(result.toString());
	}

	@Test
	public void testAddInsureguardMenu_defaultCase() throws Exception {
		MenuComponent menubar = new MenuComponent();
		List<MenuComponent> auditMenu = buildTestAuditMenu();

		menubar = contractorSubmenuBuilder.addInsureguardMenu(menubar, permissions, auditMenu);

		JSONArray result = MenuWriter.convertMenuToJSON(menubar);
		Approvals.verify(result.toString());
	}

	@Test
	public void testAddSupportMenu_defaultCase() throws Exception {
		MenuComponent menubar = new MenuComponent();

		menubar = contractorSubmenuBuilder.addSupportMenu(menubar, permissions, true);

		JSONArray result = MenuWriter.convertMenuToJSON(menubar);
		Approvals.verify(result.toString());
	}

	@Test
	public void testAddEmployeeguardMenu_defaultCase() throws Exception {
		when(contractorAccount.isHasEmployeeGUARDTag()).thenReturn(true);
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isAdmin()).thenReturn(true);
		MenuComponent menubar = new MenuComponent();

		menubar = contractorSubmenuBuilder.addEmployeeguardMenu(menubar, permissions, contractorAccount);

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
		MenuComponent auditguardComponent = new MenuComponent(ContractorSubmenuBuilder.AUDITGUARD, "/ContractorDocuments.action?id=3");

		childItem = new MenuComponent(ContractorSubmenuBuilder.MANUAL_AUDIT + " '12", "/Audit.action?auditID=1");
		childItem.setAuditId(20);
		auditguardComponent.getChildren().add(childItem);

		childItem = new MenuComponent(ContractorSubmenuBuilder.IMPLEMENTATION_AUDIT + " '12", "/Audit.action?auditID=1");
		childItem.setAuditId(30);
		auditguardComponent.getChildren().add(childItem);

		auditMenu.add(auditguardComponent);

		// Insureguard
		MenuComponent insureguardComponent = new MenuComponent(ContractorSubmenuBuilder.INSUREGUARD, "/ContractorDocuments.action?id=3");

		childItem = new MenuComponent(ContractorSubmenuBuilder.MANAGE_CERTIFICATES, "/Audit.action?auditID=1");
		childItem.setAuditId(40);
		insureguardComponent.getChildren().add(childItem);

		childItem = new MenuComponent(ContractorSubmenuBuilder.AUTOMOBILE_LIABILITY + " (New)", "/Audit.action?auditID=1");
		childItem.setAuditId(50);
		insureguardComponent.getChildren().add(childItem);

		childItem = new MenuComponent(ContractorSubmenuBuilder.EXCESS_UMBRELLA_LIABILITY + " (New)", "/Audit.action?auditID=1");
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

	private Answer<String> returnMockTranslation() {
		return new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return Arrays.toString(args);
			}
		};
	}

}
