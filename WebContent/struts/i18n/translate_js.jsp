<%@ page language="java" contentType="application/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<%
	// usage: translate('ContractorAccount.name')
%>

var translate = (function() {
	var translations = <s:property value="translations" escape="false" />;
	return function(key) {
		if (translations[key])
			return translations[key];
		else
			return "Translation Missing";
	}
})();