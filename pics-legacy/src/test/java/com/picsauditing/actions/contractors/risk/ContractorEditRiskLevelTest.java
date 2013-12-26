package com.picsauditing.actions.contractors.risk;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.toggle.FeatureToggle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContractorEditRiskLevelTest extends PicsTranslationTest {
	private ContractorEditRiskLevel contractorEditRiskLevel;

    @Mock
    private ContractorAccountDAO contractorAccountDao;
    @Mock
    private NoteDAO noteDAO;
    @Mock
    protected AppPropertyDAO propertyDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private Permissions permissions;
    @Mock
    private BillingService billingService;
	@Mock
	private EmailSender emailSender;
	@Mock
	private EmailBuilder emailBuilder;
	@Mock
	private ContractorAccount contractor;
	@Mock
	private EmailQueue emailQueue;
    @Mock
    private FeatureToggle featureToggle;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);


		contractorEditRiskLevel = new ContractorEditRiskLevel();

		when(emailBuilder.build()).thenReturn(emailQueue);
		Whitebox.setInternalState(contractorEditRiskLevel, "emailBuilder", emailBuilder);
		Whitebox.setInternalState(contractorEditRiskLevel, "contractor", contractor);
		Whitebox.setInternalState(contractorEditRiskLevel, "emailSender", emailSender);
		Whitebox.setInternalState(contractorEditRiskLevel, "emailQueue", emailQueue);
		Whitebox.setInternalState(contractorEditRiskLevel, "userDAO", userDAO);
        Whitebox.setInternalState(contractorEditRiskLevel, "featureToggleChecker", featureToggle);
        Whitebox.setInternalState(contractorEditRiskLevel, "propertyDAO", propertyDAO);
        Whitebox.setInternalState(contractorEditRiskLevel, "billingService", billingService);
        Whitebox.setInternalState(contractorEditRiskLevel, "contractorAccountDao", contractorAccountDao);
        Whitebox.setInternalState(contractorEditRiskLevel, "noteDAO", noteDAO);
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

    @Test
    public void testSave_safetySensitivity() throws Exception {
        when(contractor.getStatus()).thenReturn(AccountStatus.Active);
        when(userDAO.find(0)).thenReturn(new User("John Doe"));
        when(contractor.isSafetySensitive()).thenReturn(false);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);
        when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_SAFETY_SENSITIVE_ENABLED)).thenReturn(true);
        contractorEditRiskLevel.setPermissions(permissions);
        contractorEditRiskLevel.setSafetySensitive(true);

        contractorEditRiskLevel.save();

        verify(contractorAccountDao).save(contractor);
    }

    @Test
    public void testBuildAndSendBillingSafetySensitiveDowngradeEmail() throws Exception {
        Whitebox.invokeMethod(contractorEditRiskLevel, "buildAndSendBillingSafetySensitiveDowngradeEmail");

        verify(emailSender).send(emailQueue);
    }

    @Test
    public void testSave_MultipleChanges() throws Exception {
        when(contractor.getStatus()).thenReturn(AccountStatus.Active);
        when(userDAO.find(0)).thenReturn(new User("John Doe"));
        when(contractor.isSafetySensitive()).thenReturn(false);
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.Low);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);
        when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_SAFETY_SENSITIVE_ENABLED)).thenReturn(true);
        contractorEditRiskLevel.setPermissions(permissions);
        contractorEditRiskLevel.setSafetySensitive(true);
        contractorEditRiskLevel.setSafetyRisk(LowMedHigh.High);

        contractorEditRiskLevel.save();

        verify(contractorAccountDao).save(contractor);
        verify(contractor).setSafetySensitive(true);
        verify(contractor).setSafetyRisk(LowMedHigh.High);
    }
}
