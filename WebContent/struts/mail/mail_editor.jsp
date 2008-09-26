<%@ taglib prefix="s" uri="/struts-tags"%>

<s:hidden name="templateID" />
<label>Subject:</label> <s:textfield name="templateSubject" size="40" cssClass="forms" />
<s:select cssClass="forms" id="tokens" name="tokens" headerKey="0"
	headerValue="- Add Field to Email -" listKey="tokenName"
	listValue="tokenName" list="tokens" onchange="addToken(this);" />
<br />
<s:textarea name="templateBody" rows="20" />
