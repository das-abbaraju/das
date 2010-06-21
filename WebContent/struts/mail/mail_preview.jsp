<%@ taglib prefix="s" uri="/struts-tags"%>
<label>To:</label> <s:property value="emailPreview.toAddresses"/> <br />
<label>Cc:</label> <s:property value="emailPreview.ccAddresses"/> <br />
<label>Subject:</label> <s:property value="emailPreview.subject"/> <br />

<s:textarea name="emailPreviewBody" value="%{emailPreview.body}" rows="25" cols="75"></s:textarea>
