<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<label>Select an Audit to be Created</label>
<s:hidden name="id" value="%{id}"/>
<s:select list="auditTypeList" listKey="id" listValue="auditName" name="selectedAudit"/>