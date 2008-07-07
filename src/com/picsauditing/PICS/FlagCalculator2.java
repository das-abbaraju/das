package com.picsauditing.PICS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.Email;
import com.picsauditing.mail.EmailSender;

/**
 * Business Engine used to calculate the flag 
 * color for a contractor at a given facility
 * 
 * @author Trevor
 *
 */
public class FlagCalculator2 {
	
	//protected EntityManager em = null;
	
	private boolean debug = false;
	
	private OperatorAccountDAO operatorDAO;
	private ContractorAccountDAO contractorDAO;
	private ContractorAuditDAO conAuditDAO;
	private AuditDataDAO auditDataDAO;
	private ContractorOperatorFlagDAO coFlagDAO;
	protected AppPropertyDAO appPropDao = null;
	
	private List<OperatorAccount> operators = new ArrayList<OperatorAccount>(); // List of operators to be processed
	private List<Integer> contractorIDs = new ArrayList<Integer>(); // List of contractors to be processed
	
	public FlagCalculator2(OperatorAccountDAO operatorDAO, ContractorAccountDAO contractorDAO, 
		ContractorAuditDAO conAuditDAO, AuditDataDAO auditDataDAO, ContractorOperatorFlagDAO coFlagDAO, AppPropertyDAO appProps) {
		this.operatorDAO = operatorDAO;
		this.contractorDAO = contractorDAO;
		this.conAuditDAO = conAuditDAO;
		this.auditDataDAO = auditDataDAO;
		this.coFlagDAO = coFlagDAO;
		this.appPropDao = appProps;
	}
	
	public void runAll() {
		execute();
	}
	
	public void runByOperatorLimited(int opID) {
		OperatorAccount operator = operatorDAO.find(opID);
		operators.add(operator);
		
		contractorIDs = contractorDAO.findIdsByOperator(operator);
		execute();
	}
	
	public void runByOperator(int opID) {
		OperatorAccount operator = operatorDAO.find(opID);
		operators.add(operator);
		execute();
	}
	
	public void runByContractor(int conID) {
		contractorIDs.add(conID);
		execute();
	}
	
	public void runOne(int conID, int opID) {
		OperatorAccount operator = operatorDAO.find(opID);
		operators.add(operator);
		contractorIDs.add(conID);
		execute();
	}
	
	
	private void execute() {
		debug("FlagCalculator.execute()");
		// Load ALL operators and contractors by default
		if (operators.size() == 0)
			operators = operatorDAO.findWhere(false, "type='Operator'");
		if (contractorIDs.size() == 0) {
			contractorIDs = contractorDAO.findAll();
		}
		debug("...getting question for operators");
		

		List<Integer> questionIDs = new ArrayList<Integer>();
		// Create a list of questions that the operators want to ask
		for(OperatorAccount operator : operators) {
			// Read the operator data from database
			operator.getFlagOshaCriteria();
			operator.getAudits();
			questionIDs.addAll(operator.getQuestionIDs());
		}
		

		int count = 1;
		int testValue = 5;
		float lastAvg = 1000000;
		long startTime = System.currentTimeMillis();
		
		int errorCount = 0;

		for(Integer conID : contractorIDs) {

			
			try {
				System.out.println("FlagCalculator2: calculating flags for : " + conID);
				runCalc(questionIDs, conID);
			}
			catch( Throwable t ) {
				
				Email email = new Email();
				email.setSubject("There was an error calculating flags for contractor :" + conID.toString() );
				
				StringBuffer body = new StringBuffer();
				
				body.append("There was an error calculating flags for contractor ");
				body.append(conID.toString());
				body.append( "\n\n" );
				
				body.append(t.getMessage());
				body.append( "\n" );
				
				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter(sw));
				body.append( sw.toString() );
				
				email.setBody( body.toString() );
				email.setToAddress("errors@picsauditing.com");

				EmailSender sender = new EmailSender();
				
				try
				{
					sender.sendMail(email);
				}
				catch( Exception notMuchWeCanDoButLogIt )
				{
					System.out.println("**********************************");
					System.out.println("Error calculating flags AND unable to send email");
					System.out.println("**********************************");
					
					System.out.println(notMuchWeCanDoButLogIt);
					notMuchWeCanDoButLogIt.printStackTrace();
				}

				
				
				if( ++errorCount == 10 )
				{
					break;
				}
			}
			
			
			if( (count++ % testValue) == 0 )
			{
				questionIDs = null;
				
				operatorDAO.close();
				contractorDAO.close();
				conAuditDAO.close();
				auditDataDAO.close();
				coFlagDAO.close();
				contractorDAO.close();
				
				System.gc();

				questionIDs = new ArrayList<Integer>();
				
				for(OperatorAccount operator : operators) {
					// Read the operator data from database
					operator.getFlagOshaCriteria();
					operator.getAudits();
					questionIDs.addAll(operator.getQuestionIDs());
				}

				long endTime = System.currentTimeMillis();
				
				float thisAvg = (endTime - startTime) / testValue; 
				
				System.out.println("one iteration finished:  batch size: " + testValue + " average time : " + thisAvg);
				
				if( thisAvg > lastAvg )
				{
					testValue -= 3;
					
					if( testValue < 5 )
					{
						testValue = 5;
					}
				}
				else
				{
					testValue += 5;

					if( testValue > 70 )
					{
						testValue = 20;
					}
				
				}
				lastAvg = thisAvg;
				count = 1;
				startTime = System.currentTimeMillis();
				
			}
			
			
		}
	}

	@Transactional
	protected void runCalc(List<Integer> questionIDs, Integer conID) {

		long startTime = System.currentTimeMillis();

		ContractorAccount contractor = contractorDAO.find(conID);


		//debug("FlagCalculator: Operator data ready...starting calculations");
		FlagCalculatorSingle calcSingle = new FlagCalculatorSingle();
		calcSingle.setDebug(debug);

		calcSingle.setContractor(contractor);
		calcSingle.setConAudits(conAuditDAO.findNonExpiredByContractor(contractor.getId()));
		calcSingle.setAuditAnswers(auditDataDAO.findAnswersByContractor(contractor.getId(), questionIDs));
		
		
		
		for(OperatorAccount operator : operators) {
		//OperatorAccount operator = operators.get(0);
			
			calcSingle.setOperator(operator);
			
			// Calculate the color of the flag right here
			
			FlagColor color = calcSingle.calculate();
			//debug(" - FlagColor returned: " + color);
			// Set the flag color on the object
			//em.refresh(contractor);
			ContractorOperatorFlag coFlag = null;
			
			
			try
			{
				coFlag = contractor.getFlags().get(operator);
			}
			catch( Exception e )
			{
				System.out.println(e);
			}
			

			if( coFlag == null)
			{
				for( ContractorOperatorFlag cof : contractor.getFlags().values() )
				{
					try
					{
						if( operator.getIdString().equals( cof.getOperatorAccount().getIdString() ) )
						{
							coFlag = cof;
						}
					}
					catch( Exception e )
					{
						if( ! ( e instanceof EntityNotFoundException ) ){
							System.out.println( e );	
						}
						
					}
				}
			}			
			
			if (coFlag == null) {
				// Add a new flag
				coFlag = new ContractorOperatorFlag();
				coFlag.setFlagColor(color);
				coFlag.setContractorAccount(contractor);
				coFlag.setOperatorAccount(operator);
				
				
				coFlagDAO.save(coFlag);
				contractor.getFlags().put(operator, coFlag);
			} else {
				if (!color.equals(coFlag.getFlagColor())) {
					coFlag.setFlagColor(color);
					coFlag.setLastUpdate(new Date());
				}
			}
			
		}
		contractorDAO.save(contractor);

		
		
		// destroy with a vengence!!!!
//		for(OperatorAccount operator : contractor.getFlags().keySet()) {
//			ContractorOperatorFlag flag =  contractor.getFlags().get(operator);
//			flag = null;
//		}
//		contractor.getFlags().clear();
//		contractor = null;
		
		//System.out.println(  "" + conID + " " + (System.currentTimeMillis() - startTime) );
	}
	
	protected void debug(String message) {
		if (!debug)
			return;
		Date now = new Date();
		System.out.println(now.toString() + message);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
/*
	@PersistenceContext
	public void setEntityManager( EntityManager em )
	{
		this.em = em;
	}
*/
	
	
}

