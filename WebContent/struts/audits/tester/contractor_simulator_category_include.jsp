<%@ taglib prefix="s" uri="/struts-tags"%>
<li><s:property value="name" /></li>
<s:if test="subCategories.size > 0">
	<ul class="categories">
		<s:iterator value="subCategories">
			<s:include value="contractor_simulator_category_include.jsp" />
		</s:iterator>
	</ul>
</s:if>
