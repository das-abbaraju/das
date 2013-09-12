<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<li>
	<a href="javascript: chooseTemplate(-1);">
		<nobr><s:text name="EditEmailTemplate.StartWithBlank" /></nobr>
	</a>
</li>
<s:iterator value="emailTemplates">
	<li id="li_template<s:property value="id"/>">
		<nobr>
			<a href="javascript: chooseTemplate(<s:property value="id"/>);">
				<s:property value="templateName" />
			</a>
			<pics:permission perm="EmailTemplates" type="Delete">
				<a href="javascript: deleteTemplate(<s:property value="id"/>);" title="<s:text name="EmailWizard.RemoveThisTemplate" />">
					<img src="images/cross.png" />
				</a>
			</pics:permission>
		</nobr>
	</li>
</s:iterator>
