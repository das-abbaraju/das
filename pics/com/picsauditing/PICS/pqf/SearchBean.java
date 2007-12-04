package com.picsauditing.PICS.pqf;

import java.sql.*;
import java.util.*;
import com.picsauditing.PICS.*;

public class SearchBean extends DataBean {

//	public ContractorBean cBean = new ContractorBean();
	public ArrayList<String> searchResults = null;
	public ArrayList<String> tempSearchResults = null;
	String changed = "";
	String thisPage = "";
	String qIDs = "";
	public String searchType = "";
	public String selectedReport = "";
	public String selected_industry = "";
	public String selected_trade = "";
	public String selected_startsWith = "";
		public static final String DEFAULT_INDUSTRY = "- Industry -";
//	public static final String default_trade = TradesBean.DEFAULT_SELECT_TRADE_ID;
	int numResults = 0;		
	int beginResults = 0;
	int endResults = 0;
	int showPage = 1;
	public int showNum = 0;
	public String orderByColumn = "conID,pqfQuestions.questionID";	// the result set is sorted on this colunm, can be changed
		
	public static final String JOBS_IN_PROGRESS_QIDS = "664,665,666,667,668,814,669,670,671,672,673,815,674,675,676,677,678,816,679,680,681,682,683,817";
	public static final String JOBS_PAST_QIDS = "684,685,686,687,688,818,689,690,691,692,693,819,694,695,696,697,698,820,699,700,701,702,703,821";
		
	String[] REPORTS_ARRAY = {"InProgressJobs","In Progress Jobs","PastJobs","Past Jobs","AllJobs","In Progress and Past Jobs"};

	public void doPQFSearch(javax.servlet.http.HttpServletRequest r,String questionIDs) throws Exception {
		tempSearchResults = new ArrayList<String>();
		changed = r.getParameter("changed");
		selectedReport =  r.getParameter("report");
		selected_startsWith = r.getParameter("startsWith");
		selected_industry = r.getParameter("industry");
		selected_trade = r.getParameter("trade");
		if ((null == changed) || ("1".equals(changed))) 
			showPage = 1;
		else
			showPage = Integer.parseInt(r.getParameter("showPage"));
		
		if ("InProgressJobs".equals(selectedReport)) {
				qIDs = JOBS_IN_PROGRESS_QIDS;
				searchType = "WorkHistory";
			} else if ("PastJobs".equals(selectedReport)){
				qIDs = JOBS_PAST_QIDS;
				searchType = "WorkHistory";
			} else if ("AllJobs".equals(selectedReport)) {
				qIDs = JOBS_IN_PROGRESS_QIDS + "," + JOBS_PAST_QIDS;
				searchType = "WorkHistory";
			} else
				qIDs = questionIDs;
	
		String selectQuery = "SELECT pqfQuestions.*, pqfData.*, accounts.name FROM pqfQuestions INNER JOIN pqfData USING(questionID) INNER JOIN accounts on pqfData.conID = accounts.ID WHERE pqfQuestions.questionID in (" + qIDs + ") AND answer <> '' ";
			if (null !=selected_industry   && !DEFAULT_INDUSTRY.equals(selected_industry))
				selectQuery+= "AND industry = '" + selected_industry + "' ";
		selectQuery+= " ORDER BY " + orderByColumn + ";";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				tempSearchResults.add(SQLResult.getString("question"));
				tempSearchResults.add(SQLResult.getString("answer"));	
				tempSearchResults.add(SQLResult.getString("conID"));
				tempSearchResults.add(SQLResult.getString("name"));
				numResults++;
			}//while
			SQLResult.close();
			DBClose();
		} catch (Exception ex) {
			DBClose();
			throw ex;
		}//catch
		//sort/manipulate the data if neccessary
		if ("WorkHistory".equals(searchType)) 
			numResults = sortWorkHistory();
		else
			searchResults = new ArrayList<String>(tempSearchResults);
		if (!"".equals(selected_startsWith) &&  null!=selected_startsWith) {
			numResults = 0;
			ArrayList<String> searchResultsStartsWith = new ArrayList<String>();	
			ListIterator li2 = searchResults.listIterator();
			while (li2.hasNext()) {
				if (li2.next().toString().substring(0,1).equals(selected_startsWith)) {
					searchResultsStartsWith.add((String)li2.next());
					numResults++;
				}//if 
			}//while
			searchResults = new ArrayList<String>(searchResultsStartsWith);
		}//if		
		beginResults = (showPage-1)*showNum + 1;
		endResults = showPage*showNum;
		if (numResults == 0) {
			beginResults = 0;
			endResults = 0;
		}//if	
		if (endResults > numResults)
			endResults = numResults;
		thisPage = r.getServletPath();
	}//doPQFSearch
	
	public String displayResults() {
		String []  thisCompany = null;
		String tempStr = ""; 
			for (int c=beginResults;c<endResults; c+=1) {
				if ("WorkHistory".equals(searchType)) {
						//split and display each array element (a company)
						thisCompany = searchResults.get(c).toString().split(";");
					 	if (thisCompany.length>1) {		
							tempStr += "<tr><td" +getBGColor(c) + "><b>";
							for (int x=0;!(x>=thisCompany.length-3);x+=1) {
								tempStr += thisCompany[x];
								if ((x%2) == 0)
									tempStr += "</b><br>";
								else
									tempStr += " <b>";
		 					}//for 
							tempStr += "</td><td"+getBGColor(c)+" valign='top'>Referred by <a href='pqf_view.jsp?catID=6&id=" + thisCompany[thisCompany.length-1] + "'>" + thisCompany[thisCompany.length-2] + "</a></td></tr>";
						}//if
				} else 
					//generic results display
					tempStr += "<tr><td>" + searchResults.get(c) + "</td></tr>";
			}//for
		return  tempStr;
	} //displayResults
	
	public int sortWorkHistory() throws Exception {	
		ArrayList<String> jobs = new ArrayList<String>();	
		String tempStr = "";
		String thisConID = "";
		String thisConName = "";
		String thisAnswer = "";
		String thisQuestion = "";
		ListIterator li = tempSearchResults.listIterator();
		//create array to sort
		while (li.hasNext()) {
			thisQuestion = (String)li.next();
			if (thisQuestion.equals("Customer/Location")) {
				tempStr +=  thisConName + ";" + thisConID;
				jobs.add(tempStr.trim());
				tempStr = "";
			} else {
				tempStr +=thisQuestion + ";";
			}//if
			thisAnswer = (String)li.next();
			thisConID = (String)li.next();
			thisConName = (String)li.next();
			tempStr += thisAnswer +";";				
		}//while			
		tempStr +=  thisConName + ";" + thisConID;
		jobs.add(tempStr.trim());
		Collections.sort(jobs);
		searchResults = new ArrayList<String>(jobs);			
		return jobs.size();		
	}//sortWorkHistory

	public String getLinks() {
		int prevPage = showPage - 1;
		int nextPage = showPage + 1;
		int lastPage = (numResults - 1) / showNum + 1;
		String temp = "<span class=\"redMain\">";

		temp += "Showing " + beginResults + "-" + endResults + " of <b>" + numResults + "</b> results ";
		for (int i=1;i<=lastPage;i++) {
			if (i == showPage)
				temp += "Page " + i + " ";
			else {
				if (null != orderByColumn && !"".equals(orderByColumn))
					temp += "<A HREF=\"" + thisPage + "?changed=0&orderBy=" + orderByColumn + "&report="+selectedReport+"&showPage="+i+"\">"+i+"</A> ";
				else
					temp += "<A HREF=\"" + thisPage + "?changed=0&report="+selectedReport+"&showPage="+i+"\">"+i+"</A> ";
			}//else
		}//for
		temp += "</span>";
		return temp;
	}//getLinks

	public String getStartsWithLinks() {
		String temp = "<span class=\"blueMain\">Starts with: ";
		for (char c = 'A';c<='Z';c++)
			temp += "<a href=\"" + thisPage + "?startsWith=" + c + "&changed=1&report="+selectedReport+"\" class=\"blueMain\">" + c + "</a> ";
		temp +="</span>";
		return temp;
	}//getStartsWithLinks
	
	public String getBGColor(int count) {
		if ((count % 2) == 1)	return " bgcolor=\"#FFFFFF\"";
		else	return "";
	}//getBGColor
	
	public String getPQFReportSelect(String selectedReport) {
		String name = "report";
		String classType = "blueMain";
		return Inputs.inputSelect2FirstSubmit(name,classType,selectedReport, REPORTS_ARRAY, "Select a Report", "");
	}//getPQFReportSelect
} //SearchBean