<%@ page language="java" contentType="application/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<%
	// usage: translate('ContractorAccount.name', [params (optional)])
%>

var translate = (function(window, document, undefined) {
	var translations = <s:property value="translations" escape="false" />;
    
	return function(key, args) {
		if (translations[key]) {
			if (args !== undefined && args.length > 0) {
				var t = translations[key];
				t = t.replace(/\{(\d+)\}/g, function(match, index) {
					return args[index];
				});
				return t;
			} else {
				return translations[key];
			}
		}
		else {
			return "Translation Missing";
		}
	}
})(window, document);