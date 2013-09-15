<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<script type="text/javascript">
$(function() {
	updateModes('<s:property value="mode.toLowerCase()"/>');
});
</script>

<s:if test="mode == 'ViewQ' || mode == 'ViewAll' || viewBlanks == false">
	<s:iterator value="categoryNodes">
		<s:set name="category" value="category"/>
		
		<s:include value="audit_cat_view.jsp" />
	</s:iterator>
</s:if>
<s:else>
	<s:set name="category" value="categoryData.category"/>
	
	<s:include value="audit_cat_view.jsp" />

	<div class="buttons" id="cat-nav-buttons">
		<div class="next">
			<a href="#" id="next_cat" class="picsbutton right">
				<s:text name="button.Next" />
			</a>
		</div>
		<div class="last">
			<a href="#" id="done" class="picsbutton right">
				<s:text name="button.Done" />
			</a>
		</div>
	</div>
</s:else>