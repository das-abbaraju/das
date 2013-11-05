package com.picsauditing.actions.cron;

import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.search.Database;
import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class FlagChangesEmailTaskTest {
    @Mock
    private Database database;
    @Mock
    private EmailQueueDAO emailQueueDAO;
    private FlagChangesEmailTask cron;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        cron = new FlagChangesEmailTask(database, emailQueueDAO);
    }

    @Test
    public void testSumFlagChanges_EmptyBasicDynaBeanList() throws Exception {
        assertEquals(Integer.valueOf(0), Whitebox.invokeMethod(cron, "sumFlagChanges", (List<BasicDynaBean>) null));
        assertEquals(Integer.valueOf(0), Whitebox.invokeMethod(cron, "sumFlagChanges", new ArrayList<BasicDynaBean>()));
    }

    @Test
    public void testSumFlagChanges_ValidBasicDynaBeanList() throws Exception {
        BasicDynaBean mockDynaBean1 = Mockito.mock(BasicDynaBean.class);
        when(mockDynaBean1.get("changes")).thenReturn(1);
        BasicDynaBean mockDynaBean2 = Mockito.mock(BasicDynaBean.class);
        when(mockDynaBean2.get("changes")).thenReturn(2);

        List<BasicDynaBean> fakes = new ArrayList<BasicDynaBean>();
        fakes.add(mockDynaBean1);
        fakes.add(mockDynaBean2);
        assertEquals(Integer.valueOf(3), Whitebox.invokeMethod(cron, "sumFlagChanges", fakes));
    }

    @Test
    public void testSumFlagChanges_BasicDynaBeanListWithNonIntegerValue() throws Exception {
        BasicDynaBean mockDynaBean1 = Mockito.mock(BasicDynaBean.class);
        when(mockDynaBean1.get("changes")).thenReturn("Fail here!");
        BasicDynaBean mockDynaBean2 = Mockito.mock(BasicDynaBean.class);
        when(mockDynaBean2.get("changes")).thenReturn(2);
        BasicDynaBean mockDynaBean3 = Mockito.mock(BasicDynaBean.class);
        when(mockDynaBean3.get("changes")).thenReturn(null);

        List<BasicDynaBean> fakes = new ArrayList<BasicDynaBean>();
        fakes.add(mockDynaBean1);
        fakes.add(mockDynaBean2);
        fakes.add(mockDynaBean3);
        assertEquals(Integer.valueOf(2), Whitebox.invokeMethod(cron, "sumFlagChanges", fakes));
    }

    @Test
    public void testSumFlagChanges_BasicDynaBeanThrowsException() throws Exception {
        BasicDynaBean mockDynaBean1 = Mockito.mock(BasicDynaBean.class);
        when(mockDynaBean1.get("changes")).thenReturn(new IllegalArgumentException("Forcing an error"));
        BasicDynaBean mockDynaBean2 = Mockito.mock(BasicDynaBean.class);
        when(mockDynaBean2.get("changes")).thenReturn(5);
        List<BasicDynaBean> fakes = new ArrayList<BasicDynaBean>();
        fakes.add(mockDynaBean1);
        fakes.add(mockDynaBean2);
        assertEquals(Integer.valueOf(5), Whitebox.invokeMethod(cron, "sumFlagChanges", fakes));
    }


}
