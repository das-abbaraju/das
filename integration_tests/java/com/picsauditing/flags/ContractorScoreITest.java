package com.picsauditing.flags;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/com/picsauditing/flags/ContractorScoreITest-context.xml"})
public class ContractorScoreITest {
    private final int BERNARDS_BROS_ID = 32709;

    @Autowired
    private ContractorAccountDAO contractorAccountDAO;
    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        Whitebox.setInternalState(DBBean.class, "staticDataSource", dataSource);
    }

    @Transactional
    @Test
    public void testCalculate() throws Exception {
        ContractorAccount contractorAccount = contractorAccountDAO.find(BERNARDS_BROS_ID);

        ContractorScore.calculate(contractorAccount);

        int score = contractorAccount.getScore();

        assertTrue(score > 0);
    }
}
