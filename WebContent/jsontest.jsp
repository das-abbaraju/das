<%@page import="com.picsauditing.util.JSONCallback"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.picsauditing.jpa.entities.Webcam"%>
<%@page import="java.util.List"%>
<%@page import="org.json.simple.JSONArray"%><%@page import="java.io.Writer"%>
<%
JSONArray dataBlock = new JSONArray();

List<Webcam> list = new ArrayList<Webcam>();
list.add(new Webcam());
list.get(0).setMake("Trevor");
list.get(0).setModel("Kyle");

JSONArray jsonArray = new JSONArray();
for (Webcam obj : list)
	jsonArray.add(obj.toJSON(false));

JSONCallback callback = new JSONCallback(jsonArray);
callback.setCallbackFunction();

response.setContentType("text/javascript");
Writer writer = response.getWriter();
writer.write(callback.toString());
%>