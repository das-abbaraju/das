<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div id="who_<s:property value="#o"/>"
	onclick="getElement('who_<s:property value="#o"/>').style.display = 'none'"
	title="<s:text name="WhoIs.ClickToHide" />"
	style="cursor: pointer; position: absolute; border: 1px solid gray; z-index: 10000; background-color: rgb(238, 238, 238); text-align: left; display: none;" >


<!-- Extraneous information: Nobody is currently using it.
<s:text name="WhoIs.CreatedBy"><s:param><s:property value="#o.createdBy.name"/></s:param><s:param><s:property value="#o.createdBy.account.name"/></s:param><s:param><s:date name="#o.creationDate"/></s:param></s:text>
<s:if test="#o.createdBy.id != #o.updatedBy.id || #o.updateDate.after(#o.creationDate)">
	<br /><s:text name="WhoIs.LastUpdatedBy"><s:param><s:property value="#o.updatedBy.name"/></s:param><s:param><s:property value="#o.updatedBy.account.name"/></s:param><s:param><s:date name="#o.updateDate"/></s:param></s:text>
</s:if>
<s:if test="#addBy != null && #addDate != null" >
	<br /><s:text name="WhoIs.AddBy" ><s:param><s:property value="#addBy.name"/></s:param><s:param><s:property value="#addDate" /></s:param></s:text>
</s:if>
-->

</div>
<img class="noprint" src="images/help.gif" width="12" height="12" 
	title='<s:text name="WhoIs.ShowWho" />' style="cursor: pointer;" 
	onclick="getElement('who_<s:property value="#o"/>').style.display = 'block'; return false;" />
