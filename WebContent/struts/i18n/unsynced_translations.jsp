<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<head>
	<title>Unsynced Translations</title>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	
	<script type="text/javascript" src="js/ReportSearch.js?v=<s:property value="version"/>"></script>
	<script type="text/javascript" src="js/filters.js?v=<s:property value="version"/>"></script>
</head>
<body>
	<h1>Unsynced Translations</h1>
	
	<s:include value="../actionMessages.jsp" />
	<s:include value="../reports/filters_translation.jsp" />
	
	<div id="${actionName}-page">
		<s:if test="report.allRows == 0">
			<div class="alert">
				<s:text name="Report.message.NoRowsFound" />
			</div>
		</s:if>
		<s:else>
			<s:form id="unsynced_translations">
				<s:submit
					action="UnsyncedTranslations"
					method="sendToTarget"
					value="Send to Target"
					cssClass="picsbutton"
				/>
				
				<div>
					<s:property value="report.pageLinksWithDynamicForm" escape="false" />
				</div>
				
				<table class="report">
					<thead>
						<tr>
							<th>
							</th>
							<th>
								<input
				    				type="checkbox"
				    				class="master"
				    			/>
							</th>
							<th>
								Key
							</th>
							<th>
								Locale
							</th>
							<th>
								<s:property value="local" /> Translation
							</th>
							<th>
								<s:property value="target" /> Translation
							</th>
						</tr>
					</thead>
				    <tbody>
				    	<s:iterator value="data" status="stat">
					    	<tr>
					    		<td class="right">
					    			<s:property value="#stat.index + report.firstRowNumber" />
					    		</td>
					    		<td>
					    			<input
					    				type="checkbox"
					    				name="translationsToTransfer"
					    				value="<s:property value="get('id')" />"
					    				class="selectable"
					    			/>
					    		</td>
					    		<td>
					    			<s:property value="get('msgKey')" />
					    		</td>
					    		<td>
					    			<s:property value="get('locale')" />
					    		</td>
					    		<td>
					    			<s:property value="get('translationLocal')" />
					    		</td>
					    		<td>
					    			<s:property value="get('translationTarget')" />
					    		</td>
					    	</tr>
				    	</s:iterator>
				    </tbody>
				</table>
				
				<div>
					<s:property value="report.pageLinksWithDynamicForm" escape="false" />
				</div>
				<s:submit
					action="UnsyncedTranslations"
					method="sendToTarget"
					value="Send to Target"
					cssClass="picsbutton"
				/>
			</s:form>
		</s:else>
	</div>
</body>