<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<a class="expand">+</a> <s:property value="fullNumber"/>. <a href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="name"/></a>
<div class="subs">
<ul class="list">
<s:iterator value="subCategories">
	<li class="draggable-category"><s:include value="manage_audit_type_hierarchy_category.jsp"/></li>
</s:iterator>
</ul>
</div>

<div class="questions">
<ul class="list">
<s:iterator value="questions">
	<li><s:property value="number"/>. <a href="ManageQuestion.action?id=<s:property value="id"/>"><s:property value="name"/></a></li>
</s:iterator>
</ul>
</div>