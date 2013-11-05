package com.picsauditing.actions;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.search.Database;
import com.picsauditing.util.EbixLoader;
import com.picsauditing.util.IndexerEngine;
import com.picsauditing.util.SapAppPropertyUtil;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.sql.DataSource;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
@UseReporter(DiffReporter.class)
public class CronTest extends PicsActionTest {
    private Cron cron;
    @Mock
    private BasicDAO dao;
    @Mock
    private ContractorAccountDAO contractorAccountDAO;
    @Mock
    private ContractorAuditDAO contractorAuditDAO;
    @Mock
    private ContractorAuditOperatorDAO contractorAuditOperatorDAO;
    @Mock
    private ContractorOperatorDAO contractorOperatorDAO;
    @Mock
    private ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
    @Mock
    private EmailQueueDAO emailQueueDAO;
    @Mock
    private EbixLoader ebixLoader;
    @Mock
    private InvoiceDAO invoiceDAO;
    @Mock
    private InvoiceFeeDAO invoiceFeeDAO;
    @Mock
    private InvoiceItemDAO invoiceItemDAO;
    @Mock
    private IndexerEngine indexer;
    @Mock
    private FlagDataOverrideDAO flagDataOverrideDAO;
    @Mock
    private OperatorAccountDAO operatorDAO;
    @Mock
    private NoteDAO noteDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private AccountStatusChanges accountStatusChanges;
    @Mock
    private EmailSender emailSender;
    @Mock
    private SapAppPropertyUtil mockSapAppPropertyUtil;
    @Mock
    private Database database;
    @Mock
    private ReportDAO reportDAO;

    @BeforeClass
    public static void classSetUp() {
        Whitebox.setInternalState(DBBean.class, "staticDataSource", Mockito.mock(DataSource.class));
    }

    @AfterClass
    public static void classTearDown() {
        Whitebox.setInternalState(DBBean.class, "staticDataSource", (DataSource) null);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);


        cron = new Cron();
        super.setUp(cron);

        PicsTestUtil.autowireDAOsFromDeclaredMocks(cron, this);
        Whitebox.setInternalState(cron, "dao", dao);
        Whitebox.setInternalState(cron, "emailSender", emailSender);
        Whitebox.setInternalState(cron, "accountStatusChanges", accountStatusChanges);
        Whitebox.setInternalState(cron, "ebixLoader", ebixLoader);
        Whitebox.setInternalState(cron, "indexer", indexer);
        Whitebox.setInternalState(cron, "reportDAO", reportDAO);
        cron.database = this.database;

        when(propertyDAO.find("admin_email_address")).thenReturn(
                new AppProperty("admin_email_address", "foo@example.com"));
    }

    @Test
    public void testExecute_All() throws Exception {
        // EBIX won't run because it requires an FTP connection TODO mock the FTP connection
        // when(propertyDAO.find(anyString())).thenReturn(new AppProperty("mock_property", "mock_value"));
        when(propertyDAO.getProperty("Cron.Task.EBIX_huntsmansync")).thenReturn("false");
        cron.execute();
        String report = cron.getReport();
        report = report.replaceAll(" Cron Job at: ([A-Za-z 0-9:]*)", " Cron Job at: TIME_WAS_HERE");
        report = report.replaceAll("\\([0-9]+ millis \\)", "\\(0 millis \\)");
        Approvals.verify(report);
    }

    @Test
    public void testExecute_RunNone() throws Exception {
        when(propertyDAO.getProperty(anyString())).thenReturn("false");
        cron.execute();
        String report = cron.getReport();
        report = report.replaceAll(" Cron Job at: ([A-Za-z 0-9:]*)", " Cron Job at: TIME_WAS_HERE");
        report = report.replaceAll("\\([0-9]+ millis \\)", "\\(0 millis \\)");
        Approvals.verify(report);
    }
}
