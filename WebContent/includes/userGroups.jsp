<%@ page language="java" import="com.picsauditing.mail.*;"%>
<%
boolean isSubmitted = (null != request.getParameter("action") && request.getParameter("action").equals("rsvp"));
if (isSubmitted) {
	String newline = "\r\n";
	
	String JESSE_EMAIL_FOOTER =
		"PICS - Pacific Industrial Contractor Screening"+newline+
		"P.O. Box 51387"+newline+
		"Irvine CA 92619-1387"+newline+
		"(949)387-1940"+newline+
	 	"fax: (949)269-9177"+newline+
		"info@picsauditing.com (Please add this email address to your address book to prevent it from being labeled as spam)"+newline+
		"http://www.picsauditing.com";
	
	String to = "meetings@picsauditing.com";
	String cc = "";
	String from = request.getParameter("email");
	String subject = request.getParameter("name") + " from " + request.getParameter("organization") + " registered";
	
	StringBuffer message = new StringBuffer();
	message.append("Info:\n");
	message.append("Name:          " + request.getParameter("name") + "\n");
	message.append("Organization:  " +request.getParameter("organization") + "\n");
	message.append("Phone:         " +request.getParameter("phone") + " " + request.getParameter("ext") + "\n");
	message.append("Email:         " +request.getParameter("email") + "\n");
	message.append("Number:        " +request.getParameter("howmany") + "\n");
	message.append("Attendees:     " +request.getParameter("attendees") + "\n");
	message.append("Special Needs: " +request.getParameter("specialneeds") + "\n");
	
	EmailSender.send(from, to, cc, subject, message.toString());
	
	to = request.getParameter("email");
	from = "meetings@picsauditing.com";
	subject = "Registration confirmation for Contractor User Group Meeting";
	message = new StringBuffer();
	
	message.append("Hi, " + request.getParameter("name") + "\n\n\n");
	message.append("Thanks for registering for PICS Contractor User Group Meeting 2008.\nIf you have any questions about your registration, please contact Whitney Curry\nat (949)387-1940 x 714 or wcurry@picsauditing.com.");
	message.append("\n\nHave a great day,\nJesse Cota\n\n");
	message.append(JESSE_EMAIL_FOOTER);
	
	EmailSender.send(from, to, cc, subject, message.toString());
}
%>