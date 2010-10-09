<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>

<s:set name="category" value="categoryData.category"/>
<script type="text/javascript">
$(function() {
	updateModes('<s:property value="mode.toLowerCase()"/>');
});
</script>

<s:include value="audit_cat_view.jsp" />
