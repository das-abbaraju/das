<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<label>
	<s:text name="RequestNewContractor.OperatorTags" />
</label>
<s:optiontransferselect
	label="RequestNewContractor.OperatorTags"
	name="operatorTags"
	list="operatorTags"
	listKey="id"
	listValue="tag"
	doubleName="requestedTags"
	doubleList="requestedTags"
	doubleListKey="id"
	doubleListValue="tag"
	leftTitle="%{getText('RequestNewContractor.AvailableTags')}"
	rightTitle="%{getText('RequestNewContractor.AssignedTags')}"
	addToLeftLabel="%{getText('RequestNewContractor.Remove')}"
	addToRightLabel="%{getText('RequestNewContractor.Assign')}"
	allowAddAllToLeft="false"
	allowAddAllToRight="false"
	allowSelectAll="false"
	allowUpDownOnLeft="false"
	allowUpDownOnRight="false"
	buttonCssClass="arrow"
	theme="pics"
/>
<div class="fieldhelp">
	<h3><s:text name="RequestNewContractor.OperatorTags" /></h3>
	<s:text name="RequestNewContractor.OperatorTags.fieldhelp" />
</div>