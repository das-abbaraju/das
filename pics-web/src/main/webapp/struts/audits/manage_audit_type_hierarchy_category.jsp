<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<a href="#"><s:property value="name"/></a>
<ul>
<s:iterator value="subCategories">
	<li class="category"><s:include value="manage_audit_type_hierarchy_category.jsp"/></li>
</s:iterator>

<s:iterator value="questions" id="q">
	<li class="jstree-leaf question"><a href="#"><s:property value="number"/>. <s:property value="name"/></a></li>
</s:iterator>
</ul>