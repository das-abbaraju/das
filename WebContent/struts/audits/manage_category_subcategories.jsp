<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="subCategories.size() > 0">
	<ul>
		<s:iterator value="subCategories">
			<li rel="<s:property value="id" />"><s:property value="number" />. <s:property value="name" />
				<s:include value="manage_category_subcategories.jsp" />
			</li>
		</s:iterator>
	</ul>
</s:if>