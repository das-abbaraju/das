<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp" pageEncoding="UTF-8"%>
<s:if test="conAudit.auditType.scoreable">
	<div id="auditScore_refresh">
		<s:property value="conAudit.score"/>
	</div>
</s:if>
<div id="cao_table_refresh">
	<s:include value="caoTable.jsp"/>
</div>
<div id="audit_sidebar_refresh">
	<s:include value="../audits/con_audit_sidebar.jsp"/>
</div>