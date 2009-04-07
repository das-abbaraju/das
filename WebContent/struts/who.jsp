<%@ taglib prefix="s" uri="/struts-tags"%>
<div id="who_<s:property value="#o"/>"
	onclick="getElement('who_<s:property value="#o"/>').style.display = 'none'"
	title="Click to Hide"
	style="cursor: pointer; position: absolute; border: 1px solid gray; z-index: 10000; background-color: rgb(238, 238, 238); text-align: left; display: none;" >

Created by <s:property value="#o.createdBy.name"/> from <s:property value="#o.createdBy.account.name"/>
at <s:date name="#o.creationDate"/>

<s:if test="#o.createdBy.id != #o.updatedBy.id || #o.updateDate.after(#o.creationDate)">
	<br />Last updated by <s:property value="#o.updatedBy.name"/> from <s:property value="#o.updatedBy.account.name"/>
	at <s:date name="#o.updateDate"/>
</s:if>

</div>
<img class="noprint" src="images/help.gif" width="12" height="12" 
	title="Show who created this record and when" style="cursor: pointer;" 
	onclick="getElement('who_<s:property value="#o"/>').style.display = 'block'; return false;" />
