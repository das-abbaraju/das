package com.picsauditing.auditBuilder;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ScoreType;

public class AuditPercentCalculatorTest extends PicsTest {
	private AuditPercentCalculator calculator = new AuditPercentCalculator();
	private ContractorAccount contractor;
	private ContractorAudit audit;
	private AuditType auditType;

	AuditCategoryRuleCache catRuleCache = new AuditCategoryRuleCache();
	List<AuditCategoryRule> catRules = new ArrayList<AuditCategoryRule>();
	AuditCategoriesBuilder catBuilder;
	
	private AuditCatData acd1;
	private AuditCatData acd2;
	private AuditCatData acd3;
	private AuditCatData acd4;
	private List<AuditCatData> auditCatDataList = new ArrayList<AuditCatData>();
	private List<AuditCategory> auditCategoryList = new ArrayList<AuditCategory>();

	@Before
	public void setUp() throws Exception {
		super.setUp();
		autowireEMInjectedDAOs(calculator);

		PicsTestUtil.forceSetPrivateField(calculator, "auditCategoryRuleCache",
				catRuleCache);

		catRules.clear();
		catRuleCache.clear();

		catRules.add(new AuditCategoryRule());
		catRuleCache.initialize(catRules);

		// make standard entities
		contractor = EntityFactory.makeContractor();
		auditType = EntityFactory.makeAuditType(AuditType.PQF);
		audit = EntityFactory.makeContractorAudit(auditType, contractor);
	}

	@Test
	public void test() throws Exception {
		// The AuditPercentCalculator has the highest Complexity score (13+) but
		// absolutely no unit tests
		// http://cobertura.picsauditing.com/com.picsauditing.auditBuilder.AuditPercentCalculator.html
		// TODO Get this under test ASAP
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_All_Answer_100() throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(10f, 10f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(10f, 10f, 1f);
		acd4 = createWeightedAuditCatData(10f, 10f, 1f);
		createRelationships();

		calculator.percentCalculateComplete(audit);
		assertEquals(100, audit.getScore());
		}

	@Test
	public void testPercentCalculateComplete_WeightedScore_All_NA_100() throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(0f, 0f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(0f, 0f, 1f);
		acd4 = createWeightedAuditCatData(0f, 0f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit);
		assertEquals(100, audit.getScore());
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_First_100_Second_NA_100() throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(10f, 10f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(0f, 0f, 1f);
		acd4 = createWeightedAuditCatData(0f, 0f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit);
		assertEquals(100, audit.getScore());
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_First_NA_Second_100_100() throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(0f, 0f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(10f, 10f, 1f);
		acd4 = createWeightedAuditCatData(10f, 10f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit);
		assertEquals(100, audit.getScore());
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_First_100_Second_Mix_100() throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(10f, 10f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(10f, 10f, 1f);
		acd4 = createWeightedAuditCatData(0f, 0f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit);
		assertEquals(100, audit.getScore());
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_First_75() throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(5f, 10f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(10f, 10f, 1f);
		acd4 = createWeightedAuditCatData(10f, 10f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit);
		assertEquals(75, audit.getScore());
	}

	@Test
	public void testPercentCalculateComplete_WeightedScore_Second_75() throws Exception {
		auditType.setScoreType(ScoreType.Weighted);

		acd1 = createWeightedAuditCatData(10f, 10f, 50f);
		acd2 = createWeightedAuditCatData(0f, 0f, 50f);
		acd3 = createWeightedAuditCatData(5f, 10f, 1f);
		acd4 = createWeightedAuditCatData(5f, 10f, 1f);

		createRelationships();
		calculator.percentCalculateComplete(audit);
		assertEquals(75, audit.getScore());
	}

	private void createRelationships() {
		acd3.getCategory().setParent(acd2.getCategory());
		acd4.getCategory().setParent(acd2.getCategory());
		
		if (acd2.getCategory().getSubCategories() == null)
			acd2.getCategory().setSubCategories(new ArrayList<AuditCategory>());
		acd2.getCategory().getSubCategories().clear();
		acd2.getCategory().getSubCategories().add(acd3.getCategory());
		acd2.getCategory().getSubCategories().add(acd4.getCategory());

		auditCatDataList.clear();
		auditCatDataList.add(acd1);
		auditCatDataList.add(acd2);
		auditCatDataList.add(acd3);
		auditCatDataList.add(acd4);
		audit.setCategories(auditCatDataList);

		auditCategoryList.clear();
		auditCategoryList.add(acd1.getCategory());
		auditCategoryList.add(acd2.getCategory());
		auditCategoryList.add(acd3.getCategory());
		auditCategoryList.add(acd4.getCategory());
		auditType.setCategories(auditCategoryList);
	}

	private AuditCatData createWeightedAuditCatData(float score,
			float scorePossible, float scoreWeight) {
		AuditCatData acd = EntityFactory.makeAuditCatData();
		acd.setScore(score);
		acd.setScorePossible(scorePossible);
		acd.getCategory().setScoreWeight(scoreWeight);

		return acd;
	}
}
