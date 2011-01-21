<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<div id="filterLoadData" style="display: none;">
	<div class="filterOption" id="select_categories">
		<a href="#" onclick="toggleBox('form1_categories'); return false;">Categories</a> =
		<span id="form1_categories_query">NONE</span>
		<br />
		<span id="form1_categories_select" style="display: none" class="clearLink">
			<s:select list="auditCategories" multiple="true" cssClass="forms"
				name="categoryIDs" id="form1_categories" listKey="id" listValue="name" />
			<br />
			
			<a class="clearLink" href="#" onclick="clearSelected('form1_categories'); return false;">Clear</a>
		</span>
	</div>
	<div class="clear"></div>
	<div class="filterOption" id="select_items">
		<a href="#" onclick="toggleBox('form1_items'); return false;"><s:if test="auditType.desktop">Services Performed</s:if><s:else>Competencies</s:else></a> =
		<span id="form1_items_query">NONE</span>
		<br />
		<span id="form1_items_select" style="display: none" class="clearLink">
			<s:if test="auditType.desktop">
				<s:select list="desktopQuestions" multiple="true" cssClass="forms"
					name="itemIDs" id="form1_items" listKey="id" listValue="name" />
			</s:if>
			<s:else>
				<s:select list="operatorCompetencies" multiple="true" cssClass="forms"
					name="itemIDs" id="form1_items" listKey="id" listValue="label" />
			</s:else>
			<br />
			<a class="clearLink" href="#" onclick="clearSelected('form1_items'); return false;">Clear</a>
		</span>
	</div>
	<div class="clear"></div>
	<div><input type="button" value="Update" class="picsbutton positive" onclick="addFilter(); return false;" /></div>
</div>
<script type="text/javascript">$('#filterLoad').html($('#filterLoadData').html()); updateQuery('form1_categories'); updateQuery('form1_items');</script>
<br />
<s:if test="selectedCategories.size > 0 && selectedItems.size > 0">
	<table class="report">
		<thead>
			<tr>
				<th><button onclick="toggleEdit(); return false;" class="picsbutton">Edit</button></th>
				<s:iterator value="selectedCategories">
					<th><s:property value="name" /></th>
				</s:iterator>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="selectedItems" var="item">
				<tr>
					<td><s:property value="name" /></td>
					<s:iterator value="selectedCategories" var="cat">
						<td class="center">
							<s:if test="matrix.get(#cat.id, #item.id)"><img alt="Checked" src="images/okCheck.gif" class="view" /></s:if>
							<input type="checkbox" class="edit"<s:if test="matrix.get(#cat.id, #item.id)"> checked="checked"</s:if> onclick="toggle(<s:property value="auditType.id" />, <s:property value="#item.id" />, <s:property value="#cat.id" />, this);" />
						</td>
					</s:iterator>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	<div class="info">Please select categories and <s:if test="auditType.desktop">services performed</s:if><s:else>competencies</s:else> to view the matrix.</div>
</s:else>