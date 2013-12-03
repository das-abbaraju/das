package com.picsauditing.auditBuilder;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class AuditTypeRuleCacheTest extends PicsTest {
    AuditTypeRuleCache ruleCache;

    List<AuditTypeRule> rules = new ArrayList<>();

    Set<ContractorType> types = new HashSet<>();
    Set<ContractorTrade> trades = new HashSet<>();
    List<ContractorOperator> operators = new ArrayList<>();

    @Mock
    ContractorAccount contractor;
    @Mock
    AuditDecisionTableDAO auditDecisionTableDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ruleCache = new AuditTypeRuleCache();
        autowireEMInjectedDAOs(ruleCache);

        when(auditDecisionTableDAO.findAllRules(AuditTypeRule.class)).thenReturn(rules);
        when(contractor.getAccountTypes()).thenReturn(types);
        when(contractor.getTrades()).thenReturn(trades);
        when(contractor.getNonCorporateOperators()).thenReturn(operators);

        PicsTestUtil.forceSetPrivateField(ruleCache, "auditDecisionTableDAO", auditDecisionTableDAO);
    }

    @Test
    public void testGetRules_tradeSafetyRisk() throws Exception {
        List<AuditTypeRule> results;

        AuditTypeRule rule = new AuditTypeRule();
        rule.setAuditType(EntityFactory.makeAuditType());
        rules.add(rule);

        when(contractor.isSafetySensitive()).thenReturn(false);
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.None);
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.None);
        when(contractor.getSoleProprietor()).thenReturn(false);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);

        addTradeByTradeSafetyRisk(LowMedHigh.High);

        // no match
        rule.setTradeSafetyRisk(LowMedHigh.Med);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(0, results.size());

        // match
        rule.setTradeSafetyRisk(LowMedHigh.High);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());

        // wild card
        rule.setTradeSafetyRisk(null);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());
    }

    private void addTradeByTradeSafetyRisk(LowMedHigh risk) {
        Trade trade = new Trade();
        trade.setSafetyRisk(risk);

        ContractorTrade ct = new ContractorTrade();
        ct.setContractor(contractor);
        ct.setTrade(trade);

        trades.add(ct);
    }

    @Test
    public void testGetRules_NoRules() throws Exception {
        List<AuditTypeRule> results;

        when(contractor.isSafetySensitive()).thenReturn(false);
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.None);
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.None);
//        when(contractor.getAccountTypes()).thenReturn(types);
        when(contractor.getSoleProprietor()).thenReturn(false);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);
//        when(contractor.getTrades()).thenReturn(trades);
//        when(contractor.getNonCorporateOperators()).thenReturn(operators);

        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(0, results.size());
    }

    @Test
    public void testGetRules_SafetyRisk() throws Exception {
        List<AuditTypeRule> results;

        AuditTypeRule rule = new AuditTypeRule();
        rule.setAuditType(EntityFactory.makeAuditType());
        rules.add(rule);

        when(contractor.isSafetySensitive()).thenReturn(true);
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.High);
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.None);
        when(contractor.getSoleProprietor()).thenReturn(false);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);

        // no match
        rule.setSafetyRisk(LowMedHigh.Med);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(0, results.size());

        // match
        rule.setSafetyRisk(LowMedHigh.High);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());

        // wild card
        rule.setSafetyRisk(null);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());
    }

    @Test
    public void testGetRules_SafetySensitive() throws Exception {
        List<AuditTypeRule> results;

        AuditTypeRule rule = new AuditTypeRule();
        rule.setAuditType(EntityFactory.makeAuditType());
        rules.add(rule);

        when(contractor.isSafetySensitive()).thenReturn(true);
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.None);
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.None);
        when(contractor.getSoleProprietor()).thenReturn(false);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);

        // no match
        rule.setSafetySensitive(false);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(0, results.size());

        // match
        rule.setSafetySensitive(true);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());

        // wild card
        rule.setSafetySensitive(null);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());
    }

    @Test
    public void testGetRules_ProductRisk() throws Exception {
        List<AuditTypeRule> results;

        AuditTypeRule rule = new AuditTypeRule();
        rule.setAuditType(EntityFactory.makeAuditType());
        rules.add(rule);

        when(contractor.isSafetySensitive()).thenReturn(true);
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.None);
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.High);
        when(contractor.getSoleProprietor()).thenReturn(false);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);

        // no match
        rule.setProductRisk(LowMedHigh.Med);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(0, results.size());

        // match
        rule.setProductRisk(LowMedHigh.High);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());

        // wild card
        rule.setProductRisk(null);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());
    }

    @Test
    public void testGetRules_MultiTree() throws Exception {
        List<AuditTypeRule> results;

        AuditTypeRule safetyRule = new AuditTypeRule();
        AuditTypeRule sensitiveRule = new AuditTypeRule();
        AuditTypeRule product1Rule = new AuditTypeRule();
        AuditTypeRule product2Rule = new AuditTypeRule();
        AuditTypeRule tradeSafetyRule = new AuditTypeRule();

        safetyRule.setAuditType(EntityFactory.makeAuditType());
        sensitiveRule.setAuditType(EntityFactory.makeAuditType());
        product1Rule.setAuditType(EntityFactory.makeAuditType());
        product2Rule.setAuditType(EntityFactory.makeAuditType());
        tradeSafetyRule.setAuditType(EntityFactory.makeAuditType());
        rules.add(safetyRule);
        rules.add(sensitiveRule);
        rules.add(product1Rule);
        rules.add(product2Rule);
        rules.add(tradeSafetyRule);

        safetyRule.setSafetyRisk(LowMedHigh.Med);

        sensitiveRule.setSafetyRisk(null);
        sensitiveRule.setSafetySensitive(true);

        product1Rule.setSafetyRisk(null);
        product1Rule.setSafetySensitive(null);
        product1Rule.setProductRisk(LowMedHigh.Med);

        product2Rule.setSafetyRisk(null);
        product2Rule.setSafetySensitive(null);
        product2Rule.setProductRisk(LowMedHigh.Med);

        tradeSafetyRule.setSafetyRisk(null);
        tradeSafetyRule.setSafetySensitive(null);
        tradeSafetyRule.setProductRisk(null);
        tradeSafetyRule.setTradeSafetyRisk(LowMedHigh.Med);

        addTradeByTradeSafetyRisk(LowMedHigh.High);
        when(contractor.isSafetySensitive()).thenReturn(false);
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.High);
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.High);
        when(contractor.getSoleProprietor()).thenReturn(false);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);

        // no match
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(0, results.size());

        // match safety risk
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.Med);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());

        // match safety sensitive
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.High);
        when(contractor.isSafetySensitive()).thenReturn(true);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());

        // match product
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.High);
        when(contractor.isSafetySensitive()).thenReturn(false);
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.Med);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(2, results.size());

        // match trade safety
        addTradeByTradeSafetyRisk(LowMedHigh.Med);
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.High);
        when(contractor.isSafetySensitive()).thenReturn(false);
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.High);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());
    }

    @Test
    public void testGetRules_MultiAttribute() throws Exception {
        List<AuditTypeRule> results;

        AuditTypeRule rule = new AuditTypeRule();

        rule.setAuditType(EntityFactory.makeAuditType());
        rules.add(rule);

        rule.setSafetyRisk(LowMedHigh.Med);
        rule.setSafetySensitive(true);
        rule.setProductRisk(LowMedHigh.Med);
        rule.setTradeSafetyRisk(LowMedHigh.Med);

        addTradeByTradeSafetyRisk(LowMedHigh.High);
        when(contractor.isSafetySensitive()).thenReturn(false);
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.High);
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.High);
        when(contractor.getSoleProprietor()).thenReturn(false);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);

        // nothing matches
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(0, results.size());

        // safety matches
        when(contractor.getSafetyRisk()).thenReturn(LowMedHigh.Med);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(0, results.size());

        // sensitive safety matches
        when(contractor.isSafetySensitive()).thenReturn(true);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(0, results.size());

        // product matches
        when(contractor.getProductRisk()).thenReturn(LowMedHigh.Med);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(0, results.size());

        // everything matches
        addTradeByTradeSafetyRisk(LowMedHigh.Med);
        ruleCache.clear();
        ruleCache.initialize();
        results = ruleCache.getRules(contractor);
        assertEquals(1, results.size());
    }
}
