<%@ page language="java" errorPage="exception_handler.jsp" import="com.picsauditing.PICS.*, java.util.*"%>
<%
	////////////////////////
	// AJAX Functionality //
	////////////////////////
	String action = request.getParameter("action");
	if (action != null) {
		if (action.equals("pricing")) {
			BillContractor billing = new BillContractor();
			
			billing.getContractor().riskLevel = request.getParameter("riskLevel");
			billing.getContractor().oqEmployees = request.getParameter("oqEmployees");
			
			String facilities = request.getParameter("facilities");
			ArrayList<String> selectedFacilities = new ArrayList<String>();
			String[] facilityArray = facilities.split(",");;
			for(String facility: facilityArray) {
				selectedFacilities.add(facility);
			}
			billing.setSelectedFacilities(selectedFacilities);
			%>$<%=billing.calculatePrice()%><br/>
			<% if(billing.calculatePrice() > 0) { %>
			$<%= 99%>
			<% } else { %>$<%= 0%><% } %>
			<%
			return;
		}
	}
%>