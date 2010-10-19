<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp" pageEncoding="UTF-8"%>
<ul class="subcat-list">
	<s:iterator value="#subcatNode.subCategories" id="subcatNode">
		<li <s:if test="#subcatNode.subCategories.size() > 0">class="parentCategory"</s:if>>
			<a href="#categoryID=<s:property value="#catNode.category.id"/>&subCat=<s:property value="#subcatNode.category.id"/>"><s:property value="#subcatNode.category.name" /></a>
			<s:if test="#subcatNode.subCategories.size() > 0">
				<s:include value="con_audit_sidebar_subcat.jsp"/>
			</s:if>
		</li>
	</s:iterator>
</ul>
