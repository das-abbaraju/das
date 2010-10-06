<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<div style="width: 48%; padding: 0; margin: 0 10px 0 0; vertical-align: top; float: left;">
	<table class="report" style="border-collapse: collapse; width: 100%;">
		<thead>
			<tr>
				<th style="width: 85%;">Applied Categories</th>
				<th style="width: 15%;">Remove</th>
			</tr>
		</thead>
		<s:if test="categories.keySet().size > 1">
			<s:iterator value="categories" var="currentCat">
				<s:if test="#currentCat.key.parent == NULL && #currentCat.value.applies">
					<tr>
						<td style="width: 80%;"><s:property value="#currentCat.key.name"/></td>
						<td style="width: 20%;" class="center"><a style="cursor: pointer;" class="remove removeCat" id="category_<s:property value="#currentCat.key.id"/>"></a></td>
					</tr>
				</s:if>
			</s:iterator>
		</s:if>
	</table>
</div>
<div style="width: 48%; padding: 0; margin: 0; vertical-align: top; float: right;">
	<table class="report" style="border-collapse: collapse; width: 100%;">
		<thead>
			<tr>
				<th style="width: 15%;">Add</th>
				<th style="width: 85%;">N/A Categories</th>
			</tr>
		</thead>
		<s:if test="categories.keySet().size > 1">
			<s:iterator value="categories" var="currentCat">
				<s:if test="#currentCat.key.parent == NULL && !#currentCat.value.applies && permissions.picsEmployee">
					<tr>
						<td style="width: 20%;" class="center"><a style="cursor: pointer;"  class="add addCat" id="category_<s:property value="#currentCat.key.id"/>"></a></td>
						<td style="width: 80%;"><s:property value="#currentCat.key.name"/></td>
					</tr>
				</s:if>
			</s:iterator>
		</s:if>
	</table>
</div>	