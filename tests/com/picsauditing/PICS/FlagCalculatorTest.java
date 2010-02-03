package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagOshaCriterion;
import com.picsauditing.jpa.entities.HurdleType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;

public class FlagCalculatorTest extends TestCase {
	/* Create the main variables */
	private FlagCalculatorSingle calculator;
	private ContractorAccount contractor;
	private OperatorAccount operator;
	private List<ContractorAudit> conAudits;
	private List<AuditCriteriaAnswer> acaList;
	
	@Override
	/**
	 * Create a contractor, operator, and an empty conAudits and acaList
	 */
	protected void setUp() throws Exception {
		super.setUp();

		/* Create the main variables */
		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		
		EntityFactory.addContractorOperator(contractor, operator);
		
		conAudits = new ArrayList<ContractorAudit>();
		acaList = new ArrayList<AuditCriteriaAnswer>();
		
		/* Initialize the calculator */
		calculator = new FlagCalculatorSingle();
		calculator.setContractor(contractor);
		calculator.setOperator(operator);
		calculator.setConAudits(conAudits);
		calculator.setAcaList(acaList);
	}
	
	/***** Unit Tests *************/
	
	public void testEmptyContractor() {
		// Contractors with no requirements at all should default to Green
		operator.getAudits().add(EntityFactory.makeAuditOperator(1, operator));
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());
		
		operator.setApprovesRelationships(YesNo.Yes);
		assertEquals(WaitingOn.Operator, calculator.calculateWaitingOn());

		contractor.getOperators().get(0).setWorkStatus("Y");
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());

		//contractor.setActive('N');
		contractor.setStatus(AccountStatus.Deactivated);
		assertEquals(WaitingOn.Contractor, calculator.calculateWaitingOn());
		
    }
	
	public void testAudits() {
		// We're missing the required PQF
		operator.getAudits().add(EntityFactory.makeAuditOperator(1, operator));
		contractor.setRiskLevel(LowMedHigh.High);
		assertEquals(FlagColor.Red, calculator.calculate());

		contractor.setRiskLevel(LowMedHigh.Low);
		assertEquals(FlagColor.Green, calculator.calculate());
		contractor.setRiskLevel(LowMedHigh.High);

		ContractorAudit conAudit = EntityFactory.makeContractorAudit(1, contractor);
		conAudits.add(conAudit);
		// Now we have a valid PQF
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());

		conAudit.setAuditStatus(AuditStatus.Exempt);
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());

		conAudit.setAuditStatus(AuditStatus.Resubmitted);
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.PICS, calculator.calculateWaitingOn());

		conAudit.setAuditStatus(AuditStatus.Pending);
		assertEquals(FlagColor.Red, calculator.calculate());
		assertEquals(WaitingOn.Contractor, calculator.calculateWaitingOn());

		conAudit.setAuditStatus(AuditStatus.Submitted);
		assertEquals(FlagColor.Red, calculator.calculate());
		assertEquals(WaitingOn.PICS, calculator.calculateWaitingOn());

		operator.getAudits().get(0).setRequiredForFlag(FlagColor.Amber);
		assertEquals(FlagColor.Amber, calculator.calculate());
		assertEquals(WaitingOn.PICS, calculator.calculateWaitingOn());
		
		operator.getAudits().get(0).setRequiredAuditStatus(AuditStatus.Submitted);
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());
		
		operator.getAudits().get(0).setCanSee(false);
		assertEquals(FlagColor.Green, calculator.calculate());
		assertEquals(WaitingOn.None, calculator.calculateWaitingOn());
	}
	
	public void testPolicies() {
		// Require the Policy 1
		AuditOperator ao = EntityFactory.makeAuditOperator(1, operator);
		ao.getAuditType().setClassType(AuditTypeClass.Policy);

		// Add the Policy 1 to the contractor
		// Make sure that the flag remains red no matter what the AuditStatus
		ContractorAudit conAudit = EntityFactory.makeContractorAudit(1, contractor);
		conAudit.getAuditType().setClassType(AuditTypeClass.Policy);
		conAudits.add(conAudit);
		assertEquals(FlagColor.Red, calculator.calculate());

		conAudit.setAuditStatus(AuditStatus.Submitted);
		assertEquals(FlagColor.Red, calculator.calculate());
		
		conAudit.setAuditStatus(AuditStatus.Pending);
		assertEquals(FlagColor.Red, calculator.calculate());
		
		// Now have the operator approve this audit
		EntityFactory.addCao(conAudit, operator); // Approved
		assertEquals(FlagColor.Green, calculator.calculate());
		
		ContractorAuditOperator cao = conAudit.getOperators().get(0);
		cao.setStatus(CaoStatus.Rejected);
		assertEquals(FlagColor.Red, calculator.calculate());
		
		cao.setFlag(FlagColor.Red);
		assertEquals(FlagColor.Red, calculator.calculate());

		cao.setStatus(CaoStatus.NotApplicable);
		assertEquals(FlagColor.Green, calculator.calculate());

		conAudit.setAuditStatus(AuditStatus.Pending);
		assertEquals("Pending policies where the operator said the cao was N/A are not required", 
				FlagColor.Green, calculator.calculate());

	}
	
	public void testAuditAnswers() {
		// Add the PQF
		operator.getAudits().add(EntityFactory.makeAuditOperator(1, operator));
		ContractorAudit conAudit = EntityFactory.makeContractorAudit(1, contractor);
		conAudits.add(conAudit);

	}
	
	public void testShaTypeAnswers() {
		FlagOshaCriteria flagOshaCriteria = new FlagOshaCriteria();
		flagOshaCriteria.setFlagColor(FlagColor.Red);
		
		// LWCR is 3 Year Average
		FlagOshaCriterion lwcr = new FlagOshaCriterion();
		lwcr.setHurdle(50.00f);
		lwcr.setHurdleFlag(HurdleType.Absolute);
		lwcr.setTime(3);
		flagOshaCriteria.setLwcr(lwcr);
		// Trir is LastYearOnly
		FlagOshaCriterion trir = new FlagOshaCriterion();
		trir.setHurdle(2000.00f);
		trir.setHurdleFlag(HurdleType.Absolute);
		trir.setTime(2);
		flagOshaCriteria.setTrir(trir);
		// Facilities is Individual Years
		FlagOshaCriterion fatalities = new FlagOshaCriterion();
		fatalities.setHurdle(1.00f);
		fatalities.setHurdleFlag(HurdleType.Absolute);
		fatalities.setTime(1);
		flagOshaCriteria.setFatalities(fatalities);
		
		flagOshaCriteria.setDart(new FlagOshaCriterion());
		flagOshaCriteria.setCad7(new FlagOshaCriterion());
		flagOshaCriteria.setNeer(new FlagOshaCriterion());
		
		operator.getFlagOshaCriteria().add(flagOshaCriteria);
		operator.getInheritFlagCriteria().setOshaType(OshaType.OSHA);
		
		// Creating a Annual Update 2008 
		ContractorAudit conAudit1 = EntityFactory.makeAnnualUpdate(11, contractor,"2008");
		conAudit1.setCategories(new ArrayList<AuditCatData>());
		conAudit1.getCategories().add(EntityFactory.addCategories(conAudit1, AuditCategory.OSHA_AUDIT));
		conAudit1.getOshas().add(EntityFactory.makeShaLogs(conAudit1, 12000));
		conAudits.add(conAudit1); 

		// Creating a Annual Update 2007 
		ContractorAudit conAudit2 = EntityFactory.makeAnnualUpdate(11, contractor,"2007");
		conAudit2.getCategories().add(EntityFactory.addCategories(conAudit2, AuditCategory.OSHA_AUDIT));
		conAudit2.getOshas().add(EntityFactory.makeShaLogs(conAudit2, 123434));
		conAudits.add(conAudit2);
		
		// Creating a Annual Update 2006 
		ContractorAudit conAudit3 = EntityFactory.makeAnnualUpdate(11, contractor,"2006");
		conAudit3.getCategories().add(EntityFactory.addCategories(conAudit3, AuditCategory.OSHA_AUDIT));
		conAudit3.getOshas().add(EntityFactory.makeShaLogs(conAudit3, 123434));
		conAudits.add(conAudit3);
		
		contractor.getAudits().add(conAudit1);
		contractor.getAudits().add(conAudit2);
		contractor.getAudits().add(conAudit3);
		contractor.setNaics(new Naics());
		contractor.getNaics().setCode("0");
		
		System.out.println(calculator.calculate());
		Map<String, OshaAudit> oshaMap = contractor.getOshas().get(operator.getInheritFlagCriteria().getOshaType());
		for(OshaAudit oshaAudit : oshaMap.values()) {
			System.out.println("Osha Audit " + oshaAudit.getConAudit().getAuditFor() + " flag " + oshaAudit.getFlagColor());
		}
		// OSHA Average we are flagging on LWCR
		System.out.println("OSHA AVG : " + oshaMap.get(OshaAudit.AVG).getFlagColor());
		assertEquals(oshaMap.get(OshaAudit.AVG).getFlagColor().toString(), "Red");
		System.out.println("Flagging for LWCR AVG rate 50 :" + oshaMap.get(OshaAudit.AVG).getLostWorkCasesRate());
		assertNotSame(oshaMap.get(OshaAudit.AVG).getLostWorkCasesRate(), 50);
		
		// For 2008 we are flagging on TRIR
		assertEquals(oshaMap.get("2008").getFlagColor().toString(), "Red");
		System.out.println(oshaMap.get("2008").getRecordableTotalRate());
		System.out.println("Flagging for TRIR LAST YEAR ONLY rate 2000 :" + oshaMap.get("2008").getRecordableTotalRate());
		assertNotSame(oshaMap.get(OshaAudit.AVG).getLostWorkCasesRate(), 50);
		// For 2007 and 2006 we are flagging on Fatalities 
		assertEquals(oshaMap.get("2007").getFlagColor().toString(), "Green");
		assertEquals(oshaMap.get("2006").getFlagColor().toString(), "Green");
		System.out.println("Flagging on Fatalities of 1 " + oshaMap.get("2007").getFatalities());
		assertEquals(oshaMap.get("2007").getFatalities(), 1);
	}
}
