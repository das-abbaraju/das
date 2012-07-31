package com.picsauditing.model.events;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.WaitingOn;


public class ContractorOperatorWaitingOnChangedListenerTest {
	private ContractorOperatorWaitingOnChangedListener contractorOperatorWaitingOnChangedListener;
	
	private ContractorOperatorWaitingOnChangedEvent event;
	
	@Mock private ContractorOperatorDAO contractorOperatorDAO;
	@Mock private ContractorOperator contractorOperator;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		contractorOperatorWaitingOnChangedListener = new ContractorOperatorWaitingOnChangedListener();
		
		Whitebox.setInternalState(contractorOperatorWaitingOnChangedListener, "contractorOperatorDAO", contractorOperatorDAO);
	}

	@Test
	public void testOnApplicationEvent_NotWaitingOnContractorSetsLastStepDateToNull() throws Exception {
		when(contractorOperator.getWaitingOn()).thenReturn(WaitingOn.None);
		event = new ContractorOperatorWaitingOnChangedEvent(contractorOperator);
	
		contractorOperatorWaitingOnChangedListener.onApplicationEvent(event);
		
		verify(contractorOperator).setLastStepToGreenDate(null);
		verify(contractorOperatorDAO).save(contractorOperator);
	}

	@Test
	public void testOnApplicationEvent_WaitingOnContractorSetsLastStepDate() throws Exception {
		when(contractorOperator.getWaitingOn()).thenReturn(WaitingOn.None);
		event = new ContractorOperatorWaitingOnChangedEvent(contractorOperator);
	
		contractorOperatorWaitingOnChangedListener.onApplicationEvent(event);
		
		verify(contractorOperator).setLastStepToGreenDate((Date)any());
		verify(contractorOperatorDAO).save(contractorOperator);
	}
	
	@Test
	public void testOnApplicationEvent_SourceNotContractorOperatorDoesNothing() throws Exception {
		event = new ContractorOperatorWaitingOnChangedEvent(new Date());
		
		contractorOperatorWaitingOnChangedListener.onApplicationEvent(event);
		
		verify(contractorOperator, never()).setLastStepToGreenDate((Date)any());
		verify(contractorOperatorDAO, never()).save(contractorOperator);
	}
	
	@Test
	public void testOnApplicationEvent_NullEventDoesNothing() throws Exception {
	
		contractorOperatorWaitingOnChangedListener.onApplicationEvent(null);
		
		verify(contractorOperator, never()).setLastStepToGreenDate((Date)any());
		verify(contractorOperatorDAO, never()).save(contractorOperator);
	}
	
	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testOnApplicationEvent_NullSourceThrowsException() throws Exception {
		event = new ContractorOperatorWaitingOnChangedEvent(null);
	
		contractorOperatorWaitingOnChangedListener.onApplicationEvent(event);
	}
	
}
