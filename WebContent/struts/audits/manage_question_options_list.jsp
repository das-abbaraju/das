<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<script type="text/javascript">
$(function() {
	var sortList = $('#questionOptions table.report tbody').sortable({
		helper: function(e, tr) {
		  var $originals = tr.children();
		  var $helper = tr.clone();
		  $helper.children().each(function(index) {
			  $(this).width($originals.eq(index).width())
		  });
		  
		  return $helper;
		},
		update: function() {
			$('#questionOptions-info').load('OrderAuditChildrenAjax.action?id=<s:property value="type.id"/>&type=AuditQuestionOption', 
				sortList.sortable('serialize').replace(/\[|\]/g,''), 
				function() {
					startThinking({div: questionOptions, message: "Loading updated list..."});
					$('#questionOptions').load('ManageQuestionOption!listAjax.action?typeID=<s:property value="type.id" />');
				}
			);
		}
	}).disableSelection();
});
</script>
<table class="report">
	<thead>
		<tr>
			<th><s:text name="AuditQuestionOption.number" /></th>
			<th><s:text name="AuditQuestionOption.name" /></th>
			<th><s:text name="AuditQuestionOption.visible" /></th>
			<th><s:text name="AuditQuestionOption.score" /></th>
			<th><s:text name="AuditQuestionOption.uniqueCode" /></th>
			<th><s:text name="button.Edit" /></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="type.questionOptions">
			<tr id="item_<s:property value="id" />">
				<td class="optionNumber right"><s:property value="number" /></td>
				<td class="optionName"><s:property value="name" /></td>
				<td class="center optionVisible"><s:if test="visible"><img src="images/okCheck.gif" /></s:if></td>
				<td class="optionScore right"><s:property value="score" /></td>
				<td class="optionUniqueCode"><s:property value="uniqueCode" /></td>
				<td class="optionEdit"><a href="#" onclick="loadEdit(<s:property value="id" />); return false;" class="edit"></a></td>
			</tr>
		</s:iterator>
		<s:if test="type.questionOptions.size == 0">
			<tr>
				<td colspan="7" class="center">No question options found</td>
			</tr>
		</s:if>
	</tbody>
</table>