package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;

public class FlagDataCalculatorTest extends TestCase {
	/* Create the main variables */
	private FlagDataCalculator calculator;
	private FlagCriteriaContractor fcCon;
	private FlagCriteriaOperator fcOp;
	private FlagCriteria fc;
	List<FlagCriteriaContractor> conCrits;
	List<FlagCriteriaOperator> opCrits;
	Map<Integer, List<ContractorAuditOperator>> caoMap;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		/* Create the main variables */
		fc = new FlagCriteria();
		fc.setId(1);
		fc.setCategory("Safety");
		fc.setQuestion(new AuditQuestion());
		fc.setAuditType(new AuditType());
		fc.setComparison("=");
		fc.setDataType(FlagCriteria.STRING);
		fc.setDefaultValue("Default");

		fcCon = new FlagCriteriaContractor();
		fcCon.setCriteria(fc);
		fcCon.setAnswer("Default");

		fcOp = new FlagCriteriaOperator();
		fcOp.setCriteria(fc);
		fcOp.setFlag(FlagColor.Green);

		conCrits = new ArrayList<FlagCriteriaContractor>();
		conCrits.add(fcCon);

		opCrits = new ArrayList<FlagCriteriaOperator>();
		opCrits.add(fcOp);

		/* Initialize the calculator */
		calculator = new FlagDataCalculator(conCrits, opCrits);
		caoMap = null;
	}

	/*****
	 * Unit Tests
	 ************/
	public void testGreen() {
		assertNull(getSingle()); // Green flags are ignored
	}

	public void testDifferentConAnswer() throws Exception {
		fcCon.setAnswer("Custom");
		assertEquals("Red", getSingle().getFlag().name());

		fcCon.setAnswer("12345");
		assertEquals("Red", getSingle().getFlag().name());
	}

	public void testCustomFlag() {
		// Amber flags should show up
		fcOp.setFlag(FlagColor.Amber);
		fcCon.setAnswer(fc.getDefaultValue());
		assertEquals("Amber", getSingle().getFlag().name());
	}

	public void testCustomValue() throws Exception {
		fc.setAllowCustomValue(true);
		fcOp.setHurdle("Hurdle");
		assertEquals("Red", getSingle().getFlag().name());

		fcCon.setAnswer("Hurdle");
		assertNull(getSingle()); // Green
	}

	public void testComparison() {
		fc.setDataType(FlagCriteria.NUMBER);
		fc.setComparison(">");
		fc.setDefaultValue("2");
		fcCon.setAnswer("3");
		assertNull(getSingle()); // Green

		fc.setComparison("<=");
		assertEquals("Red", getSingle().getFlag().name());

		fc.setAllowCustomValue(true);
		fcOp.setHurdle("3");
		assertNull(getSingle()); // Green

		fc.setDataType(FlagCriteria.STRING);
		fc.setComparison("=");
		fc.setAllowCustomValue(false);
		fc.setDefaultValue("Answer");
		fcCon.setAnswer("Answer");
		assertNull(getSingle()); // Green
	}

	public void testDataTypes() {
		fc.setDataType(FlagCriteria.NUMBER);
		fc.setDefaultValue("3.5");
		fcCon.setAnswer("3.5");
		assertNull(getSingle()); // Green

		fc.setDataType(FlagCriteria.BOOLEAN);
		fc.setDefaultValue("true");
		fcCon.setAnswer("true");
		assertNull(getSingle()); // Green

		fc.setDataType(FlagCriteria.STRING);
		fc.setDefaultValue("String");
		fcCon.setAnswer("String");
		assertNull(getSingle()); // Green

		fc.setDataType(FlagCriteria.DATE);
		fc.setDefaultValue("2010-01-01");
		fc.setComparison(">");
		fcCon.setAnswer("2010-01-02");
		assertNull(getSingle()); // Green
	}

	public void testMatch() {
		// See if criteria IDs match
		boolean caughtException = false;

		try {
			getSingle();
		} catch (Exception e) {
			caughtException = true;
		}

		assertFalse(caughtException);

		FlagCriteria fcTemp = new FlagCriteria();
		fcTemp.setId(5);
		fcCon.setCriteria(fcTemp);

		// Criterias don't match, return nothing
		assertNull(getSingle());
	}

	public void testBoolean() {
		fc.setDataType(FlagCriteria.BOOLEAN);
		fc.setDefaultValue("true");
		fcCon.setAnswer("true");
		assertNull(getSingle()); // Green

		fc.setAllowCustomValue(true);
		fcOp.setHurdle("false");
		fcCon.setAnswer("false");
		assertNull(getSingle()); // Green

		fcCon.setAnswer("true");
		assertEquals("Red", getSingle().getFlag().name());

		fc.setAllowCustomValue(false);
		fcCon.setAnswer("false");
		assertEquals("Red", getSingle().getFlag().name());

		fc.setDefaultValue("false");
		assertNull(getSingle()); // Green

		fcCon.setAnswer("true");
		assertEquals("Red", getSingle().getFlag().name());
	}

	public void testNumber() {
		fc.setDataType(FlagCriteria.NUMBER);
		fc.setDefaultValue("5.55");
		fc.setComparison(">");
		fcCon.setAnswer("6.78");
		assertNull(getSingle()); // Green

		fcCon.setAnswer("4.56");
		assertEquals("Red", getSingle().getFlag().name());

		fcCon.setAnswer("5.55");
		assertEquals("Red", getSingle().getFlag().name());

		fc.setComparison("<=");
		fcCon.setAnswer("6.78");
		assertEquals("Red", getSingle().getFlag().name());

		fcCon.setAnswer("4.56");
		assertNull(getSingle()); // Green

		fcCon.setAnswer("5.55");
		assertNull(getSingle()); // Green

		fc.setComparison("=");
		fcCon.setAnswer("6.78");
		assertEquals("Red", getSingle().getFlag().name());

		fcCon.setAnswer("4.56");
		assertEquals("Red", getSingle().getFlag().name());

		fcCon.setAnswer("5.55");
		assertNull(getSingle()); // Green

		fc.setComparison("!=");
		fcCon.setAnswer("6.78");
		assertNull(getSingle()); // Green

		fcCon.setAnswer("4.56");
		assertNull(getSingle()); // Green

		fcCon.setAnswer("5.55");
		assertEquals("Red", getSingle().getFlag().name());
	}

	public void testString() {
		fc.setDefaultValue("New String");
		fcCon.setAnswer("New String");
		assertNull(getSingle()); // Green

		fcCon.setAnswer("Old String");
		assertEquals("Red", getSingle().getFlag().name());

		fcCon.setAnswer("String ");
		assertEquals("Red", getSingle().getFlag().name());

		fcCon.setAnswer(" String");
		assertEquals("Red", getSingle().getFlag().name());

		fcCon.setAnswer("string");
		assertEquals("Red", getSingle().getFlag().name());

		fc.setDefaultValue("string");
		fcCon.setAnswer("string");
		assertNull(getSingle()); // Green
		
		fc.setComparison("!=");
		assertEquals("Red", getSingle().getFlag().name());
		
		fcCon.setAnswer("new string");
		assertNull(getSingle()); // Green
		
		fcCon.setAnswer("String");
		assertNull(getSingle()); // Green
	}

	public void testDates() {
		fc.setDataType(FlagCriteria.DATE);
		fc.setDefaultValue("2010-01-01");
		fcCon.setAnswer("2010-01-01");
		assertNull(getSingle()); // Green

		fc.setComparison(">");
		fcCon.setAnswer("2011-02-02");
		assertNull(getSingle()); // Green

		fcCon.setAnswer("2009-12-12");
		assertEquals("Red", getSingle().getFlag().name());

		fc.setComparison("<");
		fcCon.setAnswer("2009-12-12");
		assertNull(getSingle()); // Green

		fcCon.setAnswer("2011-02-02");
		assertEquals("Red", getSingle().getFlag().name());
	}
	
	public void testPolicy() {
		// Create a map of <Integer, List<CAO>>
		AuditType at = new AuditType();
		at.setId(5);
		at.setClassType(AuditTypeClass.Policy);
		ContractorAudit ca = new ContractorAudit();
		ca.setAuditType(at);
		fc.setAuditType(at);
		caoMap = new HashMap<Integer, List<ContractorAuditOperator>>();
		ContractorAuditOperator cao = new ContractorAuditOperator();
		cao.setStatus(CaoStatus.Pending);
		cao.setAudit(ca);
		List<ContractorAuditOperator> caoList = new ArrayList<ContractorAuditOperator>();
		caoList.add(cao);
		caoMap.put(5, caoList);
		
		assertEquals("Red", getSingle().getFlag().name());
		
		caoMap = new HashMap<Integer, List<ContractorAuditOperator>>();
		cao = new ContractorAuditOperator();
		cao.setStatus(CaoStatus.Approved);
		cao.setAudit(ca);
		caoList = new ArrayList<ContractorAuditOperator>();
		caoList.add(cao);
		caoMap.put(5, caoList);
		
		assertNull(getSingle()); // Green
	}

	private FlagData getSingle() {
		conCrits.set(0, fcCon);
		opCrits.set(0, fcOp);
		calculator = new FlagDataCalculator(conCrits, opCrits);
//		calculator.setCaoMap(caoMap);
		List<FlagData> data = calculator.calculate();

		if (data.size() > 0)
			return data.get(0);
		else
			return null;
	}
}
