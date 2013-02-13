<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:form cssClass="form">
	<ol>
		<li>
			<label>Limit</label>
			<s:textfield name="limit" />
		</li>
		<li>
			<label>Locale To</label>
			<s:select name="localeTo"
                      list="supportedLanguages.unifiedLanguageList"
                      listValue="displayName"
            />
		</li>
		<li>
			<s:submit method="count" value="Show Count" class="picsbutton"/>
			<s:submit method="process" value="Process" class="picsbutton positive"/>
		</li>
	</ol>
</s:form>

Count: ${count}