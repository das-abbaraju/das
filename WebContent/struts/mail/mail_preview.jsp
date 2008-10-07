<%@ taglib prefix="s" uri="/struts-tags"%>
<label>Subject:</label> <s:property value="templateSubject"/> <br />

<s:textarea name="emailPreview" value="%{templateBody}" rows="25"></s:textarea>
