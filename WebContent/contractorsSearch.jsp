<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.access.OpPerms" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@page import="com.picsauditing.mail.*"%>
<!-- %@page import="com.picsauditing.PICS.EmailBean"% -->
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>

<%
if (permissions.isContractor()) throw new com.picsauditing.access.NoRightsException("Not Contractor");
try{
	com.picsauditing.PICS.pqf.QuestionTypeList statesLicensedInList = new com.picsauditing.PICS.pqf.QuestionTypeList();
	tBean.setFromDB();
	String action = request.getParameter("action");
	String actionID = request.getParameter("actionID");
	String searchName = request.getParameter("name");
	String changed = request.getParameter("changed");
	String filter = "&entireDB=Y";
	if (null==searchName)
		searchName = "";
	else
		filter += "&name="+searchName;
	String searchTradeID = request.getParameter("trade");
	if (null==searchTradeID)
		searchTradeID = "";
	else
		filter += "&trade="+searchTradeID;
	boolean isSearchNameOK = !("".equals(searchName)) && !sBean.DEFAULT_NAME.equals(searchName) && !(searchName.length()<sBean.MIN_NAME_SEARCH_LENGTH);
	boolean isSearchTradeIDOK = !("".equals(searchTradeID)) && !tBean.DEFAULT_SELECT_TRADE_ID.equals(searchTradeID);
	boolean doSearch = "0".equals(changed) || isSearchNameOK || isSearchTradeIDOK;
	if ("Add".equals(action) && pBean.oBean.canAddContractors()){
		if (pBean.oBean.isCorporate) {
			response.sendRedirect("con_selectFacilities.jsp?id="+actionID);
			return;
		}
		if (pBean.oBean.addSubContractor(permissions.getAccountId(), actionID)) {
			pBean.canSeeSet.add(actionID);
			doSearch = true;
			
			AccountBean aBean = new AccountBean();
			aBean.setFromDB(permissions.getAccountIdString());
			
			// Send the contractors an email that the operator added them
			EmailContractorBean emailer = new EmailContractorBean();
			emailer.setData(actionID, permissions);
			emailer.setMerge(EmailTemplates.contractoradded);
			User currentUser = new User();
			currentUser.setFromDB(permissions.getUserIdString());
			emailer.addTokens("opName", currentUser.userDO.accountName);
			emailer.addTokens("opUser", currentUser.userDO.name);
			emailer.sendMail();
			emailer.addNote(currentUser.userDO.name+" from "+currentUser.userDO.accountName+" added "+aBean.name+", email sent to: "+ emailer.getSentTo());
	
			ContractorBean cBean = new ContractorBean();
			cBean.setFromDB(actionID);
			cBean.addNote(actionID, permissions.getUsername(), "Added this Contractor to "+aBean.name+"'s db", DateBean.getTodaysDateTime());
			cBean.writeToDB();
			cBean.buildAudits();
			//EmailBean.sendUpdateDynamicPQFEmail(actionID);
		}
	}
	
	if ("Remove".equals(action) && pBean.oBean.canAddContractors()){
		if (pBean.oBean.removeSubContractor(permissions.getAccountId(), actionID)) {
			pBean.canSeeSet.remove(actionID);
			ContractorBean cBean = new ContractorBean();
			cBean.setFromDB(actionID);
			AccountBean aBean = new AccountBean();
			aBean.setFromDB(pBean.userID);
			cBean.addNote(actionID, "", permissions.getUsername()+" from "+aBean.name+" removed contractor from its db", DateBean.getTodaysDateTime());
			cBean.writeToDB();
			cBean.buildAudits();
		}
	}//if
	sBean.orderBy = "name";
	sBean.setCanSeeSet(pBean.canSeeSet);
	if (doSearch)
		sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
	String showPage = request.getParameter("showPage");
	if (showPage == null)	showPage = "1";
%>
<html>
<head>
<title>Search for Contractors</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
  <script language="JavaScript">
	function addContractor( cid )
	{	
		var form = document.getElementById('form1');
		form['action'].value="Add";		
		form['actionID'].value=cid;		
		form['showPage'].value=<%= showPage %>;		
		
					
		form.submit();

		return false;	
	}
	function removeContractor( cid )
	{
		var form = document.getElementById('form1');
		form['action'].value="Remove";
		form['actionID'].value=cid;
		form['showPage'].value=<%= showPage %>;
		
		form.submit();
	
		return false;
	}  
  </script>
</head>
<body>
<h1>Search For New Contractors</h1>
            <table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td height="70" colspan="2" align="center" class="blueMain">
<%	if (!doSearch){%>
                  <table border="0" cellpadding="0" cellspacing="0">
                    <tr align="left">
                      <td>
                        <span class="redMain"><strong>* For a valid search, you must either select a trade<br>
                        or enter part of the name (at least 3 characters long)</strong></span>
                      </td>
                    </tr>
                  </table>
<%	}//if %>
				<div id="search">
				<div id="showSearch" style="display: none"><a href="#" onclick="showSearch()">Show Filter Options</a></div>
				<div id="hideSearch" ><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
                  <form id='form1' name="form1" method="post">
                  <table border="0" cellpadding="2" cellspacing="0">
                    <tr>
                      <td>
                        <input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)">
                       <% if (permissions.isOperator()) {%>
                        <%=Inputs.inputSelect("flagStatus","forms", sBean.selected_flagStatus,SearchBean.FLAG_STATUS_ARRAY)%>
						<% } %> 
                        <input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0" onClick="runSearch('form1');">
                        </td>
                       </tr>
                       <tr><td> 
                        <%=tBean.getTradesSelect("trade", "forms", sBean.selected_trade)%>
                        <%=Inputs.inputSelect("performedBy","forms",sBean.selected_performedBy,TradesBean.PERFORMED_BY_ARRAY)%>
                       	<%=Inputs.inputSelect2First("state", "forms", sBean.selected_state, Inputs.STATE_ARRAY, "",SearchBean.DEFAULT_STATE)%>
                        <input name="taxID" type="text" class="forms" value="<%=sBean.selected_taxID%>" size="9" onFocus="clearText(this)"><span class=redMain>*must be 9 digits</span>
                      </td>
                    </tr>
                    <tr>
                      <td class="blueMain" colspan="2" align="left"><%=Inputs.getCheckBoxInput("searchCorporate", "forms",sBean.searchCorporate,"Y")%>
                        Check to limit search to contractors already working within my parent corporation
                      </td>
                    </tr>
                  </table>
                  <input type="hidden" name="entireDB" value="Y">
                  <input type="hidden" name="actionID" value="0">
                  <input type="hidden" name="action" value="">
     
     
     			  <input type="hidden" name="showPage" value="1"/>
		          <input type="hidden" name="startsWith" value=""/>
		          <input type="hidden" name="orderBy"  value="name"/>
                  </form>
                 </div> 
                </td>
              </tr>
            </table>
<%	if (doSearch){%>
            <table>
              <tr> 
                <td height="30"></td>
                <td><%=sBean.getLinksWithDynamicForm()%></td>
              </tr>
            </table>
            <table class="report">
            <thead>
              <tr> 
                <td></td>
                <td>Contractor</td>
                <td>Address</td>
                <td align="center" bgcolor="#6699CC">Contact</td>
                <td align="center" bgcolor="#336699">Phone</td>
                <td align="center" bgcolor="#336699">Performed By</td>
                <td align="center" bgcolor="#336699">Flag</td>
                <td align="center" bgcolor="#336699"></td>
              </tr>
              </thead>
<%		while (sBean.isNextRecord()){
			String thisClass = "cantSee";
			if ((pBean.canSeeSet.contains(sBean.aBean.id)))
				thisClass = ""; // TODO add in the FlagColor here
%>            <span id="con_<%=sBean.aBean.id%>"><tr <%=sBean.getBGColor()%> class=<%=thisClass%>>
               <td class="right"><%=sBean.count-1%></td>
                <td>
                  <a href="ContractorView.action?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class=<%=thisClass%>><%=sBean.aBean.name%></a>
                </td>
                <td><%=sBean.aBean.city%>, <%=sBean.aBean.state%></td>
                <td align="center"><%=sBean.aBean.contact%></td>
                <td align="center"><%=sBean.aBean.phone%><br><%=sBean.aBean.phone2%></td>
                <td align="center"><%=sBean.tradePerformedBy%></td>
                <td align="center">
                	<%=permissions.isCorporate() 
                	? "<a href='con_selectFacilities.jsp?id="+sBean.aBean.id+"'>Show</a>" 
                	: sBean.getFlagLink()%>
                </td>
                <td align="center">
<%			if (pBean.oBean.canAddContractors()) {
				if (!pBean.canSeeSet.contains(sBean.aBean.id) || pBean.isCorporate()){
					if (permissions.hasPermission(OpPerms.AddContractors)){%>
                  <input name="action" type="submit" class="buttons" value="Add" onClick="return addContractor(<%=sBean.aBean.id%>);">
<%				}//if
				}else{
					if (permissions.hasPermission(OpPerms.RemoveContractors)){%>
                  <input name="action" type="submit" class="buttons" value="Remove" onClick="return removeContractor(<%=sBean.aBean.id%>);">
<%					}//if
				}//else
			}//if%>
                </td>
              </tr></span>
<%		}//while %>
            </table><br>
		    <div><%=sBean.getLinksWithDynamicForm()%></div>
<%	}//if %>
<%}finally{
	sBean.closeSearch();
}//finally
%>
</body>
</html>
