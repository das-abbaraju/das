<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp" pageEncoding="UTF-8"%>
<ul class="subcat-list">
	<s:iterator value="#subcat.subCategories" id="subcat">
		<li <s:if test="#subcat.subCategories.size() > 0">class="parentCategory"</s:if>>
			<a href="#categoryID=<s:property value="#subcat.topParent.id"/>&subCat=<s:property value="#subcat.id"/>"><s:property value="#subcat.name" /></a>
			<s:iterator value="#subcat.subCategories" id="subcat">
				<s:include value="con_audit_sidebar_subcat.jsp"/>
			</s:iterator>
		</li>
	</s:iterator>
</ul>
