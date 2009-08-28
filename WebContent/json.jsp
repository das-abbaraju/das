
<%@page import="org.json.simple.JSONArray"%>
<%@page import="java.util.List"%><%@page import="com.picsauditing.jpa.entities.User"%>
<%@page import="com.picsauditing.dao.UserDAO"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="org.json.simple.JSONObject"%>
<%
	UserDAO userDAO = (UserDAO)SpringUtils.getBean("UserDAO");
//	User trevor = userDAO.find(941);
//	out.print(trevor.getJSON(true));
	
	List<User> pics = userDAO.findByAccountID(1100, "Yes", "");
	JSONArray jsonArray = new JSONArray();
	for(User user : pics)
		jsonArray.add(user.getJSON(false));
	
	out.print(jsonArray);

	out.flush();
%>