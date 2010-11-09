<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<ol class="subcat-list" id="subcat_<s:property value="#subcat.id" />">
	<s:iterator value="#subcat.subCategories" id="subcat">
		<li>
			<s:property value="#subcat.name" />
			<s:if test="#subcat.subCategories.size > 0">
				<a href="#" onclick="return toggleCategory(<s:property value="#subcat.id" />);">
					<img src="images/arrow-blue-down.png" class="arrow_<s:property value="#subcat.id" />" alt="Expand" />
							<img src="images/arrow-blue-right.png" class="arrow_<s:property value="#subcat.id" />" alt="Collapse" style="display: none;" />
				</a>
			</s:if>
			<s:if test="#subcat.subCategories.size > 0">
				<s:include value="op_config_subcat.jsp"/>
			</s:if>
		</li>
	</s:iterator>
</ol>