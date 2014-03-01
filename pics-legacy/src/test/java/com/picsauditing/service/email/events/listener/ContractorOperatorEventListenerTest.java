package com.picsauditing.service.email.events.listener;

import com.picsauditing.PICS.RegistrationRequestEmailHelper;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.account.events.ContractorOperatorEventType;
import com.picsauditing.service.account.events.SpringContractorOperatorEvent;
import com.picsauditing.service.notes.NoteService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

import java.util.Date;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ContractorOperatorEventListenerTest {
    private static int CLIENT_SITE_ID = 987;

    @Mock
    private RegistrationRequestEmailHelper registrationRequestEmailHelper;
    @Mock
    private ContractorAccountDAO contractorAccountDAO;
    @Mock
    private NoteService noteService;
    @Mock
    private SpringContractorOperatorEvent event;
    @Mock
    private ContractorOperator contractorOperator;
    @Mock
    private ContractorAccount contractor;
    @Mock
    private OperatorAccount clientSiteAccount;
    @Mock
    private User primaryContact;
    @Mock
    private User requestedByUser;
    @Mock
    private Logger logger;

    private ContractorOperatorEventListener contractorOperatorEventListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        contractorOperatorEventListener = new ContractorOperatorEventListener();

        when(event.getContractorOperator()).thenReturn(contractorOperator);
        when(event.getEvent()).thenReturn(ContractorOperatorEventType.RegistrationRequest);
        when(contractorOperator.getContractorAccount()).thenReturn(contractor);
        when(contractorOperator.getOperatorAccount()).thenReturn(clientSiteAccount);
        when(contractor.getPrimaryContact()).thenReturn(primaryContact);
        when(clientSiteAccount.getId()).thenReturn(CLIENT_SITE_ID);
        when(contractorOperator.getRequestedBy()).thenReturn(requestedByUser);

        Whitebox.setInternalState(contractorOperatorEventListener, "registrationRequestEmailHelper", registrationRequestEmailHelper);
        Whitebox.setInternalState(contractorOperatorEventListener, "contractorAccountDAO", contractorAccountDAO);
        Whitebox.setInternalState(contractorOperatorEventListener, "noteService", noteService);
        Whitebox.setInternalState(contractorOperatorEventListener, "logger", logger);
    }

    @Test
    public void testOnApplicationEvent_DoesNothingIfNotRegistrationRequest() throws Exception {
        when(event.getEvent()).thenReturn(ContractorOperatorEventType.addConnection);

        contractorOperatorEventListener.onApplicationEvent(event);

        verify(contractorOperator, never()).getContractorAccount();
        verify(registrationRequestEmailHelper, never()).sendInitialEmail(eq(contractor), eq(primaryContact), eq(contractorOperator), anyString());
        verify(contractorAccountDAO, never()).save(contractor);
    }

    @Test
    public void testOnApplicationEvent_EmailIsSent() throws Exception {
        contractorOperatorEventListener.onApplicationEvent(event);

        verify(registrationRequestEmailHelper).sendInitialEmail(eq(contractor), eq(primaryContact), eq(contractorOperator), anyString());
    }

    @Test
    public void testOnApplicationEvent_SuccessNoteAdded() throws Exception {
        contractorOperatorEventListener.onApplicationEvent(event);

        verify(noteService).addNote(contractor, ContractorOperatorEventListener.EMAIL_SUCCESS_NOTE_TEXT, "", NoteCategory.Registration, ContractorOperatorEventListener.DEFAULT_NOTE_PRIORITY, true, CLIENT_SITE_ID);
    }

    @Test
    public void testOnApplicationEvent_ContractorContactInfoUpdated() throws Exception {
        contractorOperatorEventListener.onApplicationEvent(event);

        verify(contractor).contactByEmail();
        verify(contractor).setLastContactedByInsideSalesDate(any(Date.class));
        verify(contractor).setLastContactedByAutomatedEmailDate(any(Date.class));
        verify(contractorAccountDAO).save(contractor);
    }

    @Test
    public void testOnApplicationEvent_FailureToSendNoteAddedIfSendException() throws Exception {
        doThrow(new Exception("failed to send")).when(registrationRequestEmailHelper).sendInitialEmail(eq(contractor), eq(primaryContact), eq(contractorOperator), anyString());

        contractorOperatorEventListener.onApplicationEvent(event);

        verify(contractorAccountDAO, never()).save(contractor);
        verify(noteService).addNote(contractor, ContractorOperatorEventListener.EMAIL_FAILURE_NOTE_TEXT, "", NoteCategory.Registration, ContractorOperatorEventListener.DEFAULT_NOTE_PRIORITY, true, CLIENT_SITE_ID);
    }

    @Test
    public void testOnApplicationEvent_FailureToSaveContractorLogsError() throws Exception {
        doThrow(new RuntimeException("failed to save")).when(contractorAccountDAO).save(contractor);

        contractorOperatorEventListener.onApplicationEvent(event);

        verify(logger).error(ContractorOperatorEventListener.CONTRACTOR_SAVE_FAILURE_MESSAGE, "failed to save");
    }

    @Test
    public void testOnApplicationEvent_lastContactedByWillUseRequestedByWhenSet() throws Exception {
        contractorOperatorEventListener.onApplicationEvent(event);

        verify(contractor).setLastContactedByInsideSales(requestedByUser);
    }

    @Test
    public void testOnApplicationEvent_lastContactedByWillUseGeneratingEventUserIDWhenRequestedByNotSet() throws Exception {
        when(contractorOperator.getRequestedBy()).thenReturn(null);

        contractorOperatorEventListener.onApplicationEvent(event);

        verify(event).getGeneratingEventUserID();
    }

}
