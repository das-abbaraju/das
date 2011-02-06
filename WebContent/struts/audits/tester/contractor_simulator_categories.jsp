<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<ul>
	<s:iterator value="categories">
		<li><s:property value="name" /></li>
		<s:if test="subCategories.size > 0">
		<ul>
			<s:iterator value="subCategories">
				<li><s:property value="name" /></li>
			</s:iterator>
		</ul>
		</s:if>
	</s:iterator>
</ul>
