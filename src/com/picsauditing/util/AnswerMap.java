package com.picsauditing.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.picsauditing.jpa.entities.AccountName;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;

/**
 * Two level map of questionID and rowID
 * 
 * @author Trevor
 * 
 */
public class AnswerMap {
	private Map<Integer, Map<Integer, List<AuditData>>> list = new HashMap<Integer, Map<Integer, List<AuditData>>>();

	
	public AnswerMap() { }
	
	public AnswerMap( AnswerMap toCopy ) {
		for( Integer a : toCopy.list.keySet() ) {
			Map<Integer, List<AuditData>> temp = toCopy.list.get(a);
			
			for( Integer b : temp.keySet() ) {
				for(AuditData value : temp.get(b) ) {
					getDataList(a, b).add(value);					
				}
			}
		}
	}
	
	public AnswerMap( AnswerMap toCopy, OperatorAccount operator ) {
		this( toCopy );
		
		
		//we'll build this collection by going through our main data, then after all the iteration is complete,
		//we call the remove() on each of the entries.  This avoids any ConcurrentModificationExceptions
		List<AuditData> toRemove = new Vector<AuditData>();
		
		for( Integer questionID : list.keySet() ) {
			Map<Integer, List<AuditData>> temp = list.get(questionID );
			
			for( Integer rowId : temp.keySet() ) {
				List<AuditData> data = temp.get(rowId);
				
				for( AuditData answer : data ) {
					if( answer.getQuestion().getUniqueCode() != null 
							&& answer.getQuestion().getUniqueCode().equals("aiName") 
							&& answer.getAnswer() != null 
							&& answer.getAnswer().length() > 0) {

						List<AccountName> operatorNames = operator.getNames();
						boolean matched = false;
						if( operatorNames != null ) {
							for( AccountName name : operatorNames ) {
								if( answer.getAnswer().equalsIgnoreCase( name.getName())) {
									matched = true;
									break;
								}
							}
						}
						
						if( !matched ) {
							toRemove.add(answer);
						}
					}
				}
			}
		}
	
		for( AuditData ad : toRemove ) {
			remove( ad );
		}		
	}
	
	
	public AnswerMap copy( AnswerMap other ) {
		return new AnswerMap( this );
	}
	public AnswerMap copy( AnswerMap other, OperatorAccount operator ) {
		return new AnswerMap( other, operator );
	}
	
	
	/** *************** Fill the AnswerMap ***************** */
	public void add(AuditData answer) {
		if (answer == null)
			return;
		AuditQuestion question = answer.getQuestion();
		AuditData parent = answer.getParentAnswer();
		if (parent == null)
			add(question.getId(), answer);
		else
			add(question.getId(), parent.getId(), answer);
	}

	public void add(int questionID, int rowID, AuditData answer) {
		getDataList(questionID, rowID).add(answer);
	}

	public void add(int questionID, AuditData answer) {
		add(questionID, 0, answer);
	}

	/** *************** Read the AnswerMap ***************** */

	public AuditData get(int questionID) {
		return get(questionID, 0);
	}

	public AuditData get(AuditQuestion question, AuditData parent) {
		if (parent == null)
			return get(question.getId());
		return get(question.getId(), parent.getId());
	}

	/**
	 * Return the answer for this question. If no answer exists for this tuple,
	 * then try to return the answer to this question from the 0 tuple if it
	 * exists. This is useful when trying to access "parent" data when on a
	 * child row.
	 * 
	 * @param questionID
	 *            question.id
	 * @param rowID
	 *            Parent answer.id that is the anchor for this tuple
	 * @return
	 */
	public AuditData get(int questionID, int rowID) {
		if (rowID > 0 && getRows(questionID).get(rowID) == null)
			return get(questionID);

		List<AuditData> dataList = getDataList(questionID, rowID);
		if (dataList.size() == 0)
			return null;
		
		if (dataList.size() > 1)
			System.out.println("WARNING! Returning the first entry, but multiple values were found in list.");
		
		return dataList.get(0);
	}

	public List<AuditData> getAnswerList(int questionID) {
		return getAnswerList(questionID, 0);
	}

	public List<AuditData> getAnswerList(int questionID, int rowID) {
		List<AuditData> orderedList = new ArrayList<AuditData>();

		for (AuditData childData : getDataList(questionID, rowID)) {
			orderedList.add(childData);
		}
		return orderedList;
	}

	/** *************** Helper methods ***************** */

	/**
	 * Return a Map of rowIDs to List of AuditDatas. If no map exists, one will
	 * be created and returned. This is useful to avoid null errors.
	 */
	private Map<Integer, List<AuditData>> getRows(int questionID) {
		Map<Integer, List<AuditData>> row = list.get(questionID);
		if (row == null) {
			row = new TreeMap<Integer, List<AuditData>>();
			list.put(questionID, row);
		}
		return row;
	}

	/**
	 * Return a List of AuditDatas. If no List exists, one will be created and
	 * returned. This is useful to avoid null errors.
	 */
	private List<AuditData> getDataList(int questionID, int rowID) {
		List<AuditData> dataList = getRows(questionID).get(rowID);
		if (dataList == null) {
			dataList = new ArrayList<AuditData>();
			getRows(questionID).put(rowID, dataList);
		}
		return dataList;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		for (Integer questionID : list.keySet()) {
			for (Integer rowID : list.get(questionID).keySet()) {
				for(AuditData data : list.get(questionID).get(rowID)) {
					output.append("Q:").append(questionID).append(" ");
					output.append("P:").append(rowID).append(" ");
					output.append("Answer:").append(data).append("\n");
				}
			}
		}
		return output.toString();
	}
	
	
	/**
	 * 1.) recursively removes all children of this auditData, then 2.) removes this auditData itself.
	 * @param auditData
	 */
	
	public void remove( AuditData auditData ) {
		
		//1
		List<AuditQuestion> childQuestions = auditData.getQuestion().getChildQuestions();
		if( childQuestions != null ) {
			for( AuditQuestion question : childQuestions ) {

				List<AuditData> children = getAnswerList( question.getId(), auditData.getId());
				
				if( children != null ) {
					for( AuditData child : children ) {
						remove( child );
					}
				}
				
			}
		}
		
		
		//2
		List<AuditData> possibleMatches = null;
		
		if( auditData.getParentAnswer() != null ) {
			possibleMatches = getDataList(auditData.getQuestion().getId(), auditData.getParentAnswer().getId());
		}
		else {
			possibleMatches = getDataList(auditData.getQuestion().getId(),0);
		}
		
		for( Iterator<AuditData> iterator = possibleMatches.iterator(); iterator.hasNext();) {
			AuditData possible = iterator.next();
			if( possible.compareTo(auditData) == 0 ) {

								
				iterator.remove();
				break;
			}
		}		
		
		
	}
	
	public void resetFlagColors() {
		// The flag colors should always start Green, but sometimes they
		// are still set from the previous operator's loop
		
		for( Integer questionID : list.keySet() ) {
			Map<Integer, List<AuditData>> temp = list.get(questionID );
			
			for( Integer rowId : temp.keySet() ) {
				List<AuditData> data = temp.get(rowId);
				
				for( AuditData answer : data ) {
					answer.setFlagColor(null);
				}
			}
		}
	}

}
