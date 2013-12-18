package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.duplicate.UniqueIndexable;
import com.picsauditing.employeeguard.validators.duplicate.DuplicateInfoProvider;
import com.picsauditing.jpa.entities.Account;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class DuplicateEntityCheckerTest {
    private DuplicateEntityChecker duplicateEntityChecker;

    @Mock
    private EntityManager em;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        duplicateEntityChecker = new DuplicateEntityChecker();

        Whitebox.setInternalState(duplicateEntityChecker, "em", em);
    }

    @Test(expected = RuntimeException.class)
    public void testIsDuplicate_ThrowRuntimeException() {
        duplicateEntityChecker.isDuplicate(null);
    }

    @Test
    public void testIsDuplicate_QueryIsValid() {
        duplicateEntityChecker.isDuplicate(getDuplicateInfoProvider());

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(em).createQuery(queryCaptor.capture(), eq(Long.class));

        assertEquals("SELECT COUNT(*) FROM com.picsauditing.employeeguard.entities.Employee " +
                "WHERE id != :id AND accountId = :accountId AND slug = :slug", queryCaptor.getValue());
    }

    private DuplicateInfoProvider getDuplicateInfoProvider() {
        return new DuplicateInfoProvider() {
            @Override
            public UniqueIndexable getUniqueIndexable() {
                return new Employee.EmployeeAccountSlugUniqueKey(1, Account.PicsID, "Slug");
            }

            @Override
            public Class<?> getType() {
                return Employee.class;
            }
        };
    }
}
