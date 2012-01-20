<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
	<head>
		<title>
			Manage Translations
		</title>
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<style type="text/css">
			td.saving,
			td.saving textarea,
			td.saving button
			{
				cursor: wait;
			}
			
			table.report td .edit
			{
				display: none;
			}
			
			table.report td.editMode .edit
			{
				display: inherit;
			}
			
			table.report td.editMode .view
			{
				display: none;
			}
			
			span.view {
				padding-left: 10px;
			}
		</style>
	</head>
	<body>
		<h1>
			Manage Translations
		</h1>
		<s:include value="../actionMessages.jsp" />
		<s:include value="../config_environment.jsp" />
		<s:if test="tracingOn">
			<div class="alert">
				<s:if test="showDoneButton">
					<div class="right">
						<input type="button" class="picsbutton positive" value="Done with this page" id="doneButton" />
					</div>
				</s:if>
				Text Tracing for Internationalization is turned ON.
				<s:form id="formTracingOff">
					<s:hidden name="button" value="tracingOff" />
					<s:submit value="Turn Tracing Off" />
				</s:form>
				<s:form id="formTracingClear">
					<s:hidden name="button" value="tracingClear" />
					<s:submit value="Clear Tracing Cache" />
				</s:form>
			</div>
		</s:if>
		<s:else>
			<s:form id="formTracingOn">
				<s:hidden name="button" value="tracingOn" />
				<s:submit value="Turn Tracing On" />
			</s:form>
		</s:else>
		
		<div id="search">
		<s:form id="form1">
			<s:hidden name="filter.ajax" value="false" />
			<s:hidden name="showPage" value="1" />
			From:
			<s:select
				list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()"
				name="localeFrom"
				listValue="displayName" />
			To:
			<s:select
				list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()"
				name="localeTo"
				listValue="displayName" />
			Key:
			<s:textfield name="key" />
			Search:
			<s:textfield name="search" />
			<br />
			Custom:
			<s:select headerKey="" headerValue=""
				list="#{
					'Common':'Commonly Used '+localeFrom.displayName+' Phrases', 
					'MissingTo':'Missing '+localeTo.displayName+' Translations', 
					'MissingFrom':'Missing '+localeFrom.displayName+' Translations', 
					'Updated':'Recently Updated '+localeFrom.displayName+' Phrases', 
					'Unused':'Unused Keys'}"
				name="searchType" />
			<br /> 
			<s:submit name="button" id="searchfilter" value="Search" />
		</s:form>
		</div>
			<div class="right">
				<a
					class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
					href="javascript: download('ManageTranslations');"
				 	title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>">
				 	<s:text name="global.Download" />
				</a>
			</div>
		<div>
			<s:property value="report.pageLinksWithDynamicForm" escape="false" />
		</div>
		<table class="report" style="width: 100%">
			<thead>
				<tr>
					<td width="20%">
						Key
					</td>
					<td width="40">
						<s:property value="localeFrom.displayName"/>
					</td>
					<td width="40%">
						<s:property value="localeTo.displayName"/>
					</td>
				</tr>
			</thead>
			<s:iterator value="list">
				<tr class="translate" id="row<s:property value="from.id"/>">
					<td>
						<s:property value="from.key" />
					</td>
					<s:iterator value="items">
						<td class="phrase">
							<form onsubmit="return false;" class="translationValue">
								<input type="hidden" name="translation" value="<s:property value="id"/>">
								<s:hidden name="localeTo" />
								<s:hidden name="localeFrom" />
								<s:if test="!(id > 0)">
									<s:hidden name="translation.locale" value="%{localeTo}" />
									<s:hidden name="translation.key" value="%{from.key}" />
									<a href="#" class="view suggestTranslation">
										Suggest
									</a>
								</s:if>
								<input type="hidden" name="button" value="save">
								<a href="#" class="showEdit view">
									Edit
								</a>
								<span class="view">
									<s:property value="value"/>
								</span>
								<div class="edit">
									<s:textarea name="translation.value" value="%{value}" cssStyle="width: 90%" rows="5" />
									<br/>
									<button name="button" class="save">Save</button>
									<button class="cancel">Cancel</button>
								</div>
								<s:if test="(updatedBy == null || updatedBy.id == 1) && locale != 'en'">
									<button class="right" name="button" class="save">Approve</button>
								</s:if>
								<s:radio
									list="@com.picsauditing.jpa.entities.TranslationQualityRating@values()"
									name="translation.qualityRating"
									theme="pics"
									cssClass="inline qualityRating"
									value="%{qualityRating}"
								/>
							</form>
						</td>
					</s:iterator>
				</tr>
			</s:iterator>
		</table>
		<div>
			<s:property value="report.pageLinksWithDynamicForm" escape="false" />
		</div>
		
		<s:form>
			<input type="hidden" name="translation.locale" value="<s:property value="localeFrom"/>">
			Add New Key <s:textfield name="translation.key" /> for <s:property value="localeFrom.displayName"/>
			<br />
			<s:textarea name="translation.value" cols="50" />
			<br />
			<button name="button" class="save" value="save">Save</button>
		</s:form>
		<script type="text/javascript" src="js/jquery/translate/jquery.translate-1.4.7-debug-all.js"></script>
		<script type="text/javascript" src="js/Search.js?v=<s:property value="version"/>"></script>
		<script type="text/javascript">
			$(function(){
				$('table.report .showEdit').click(function() {
					$(this).closest("td").addClass("editMode");
					return false;
				});
			
				$('table.report .suggestTranslation').click(function() {
					// http://code.google.com/p/jquery-translate/
					var textarea = $(this).closest("td").find("textarea");
					$(textarea).val( $(this).closest("td").prev().find("textarea").val() );
					$(textarea).translate('<s:property value="localeFrom"/>', '<s:property value="localeTo"/>');
					$(this).closest("td").addClass("editMode");
					$(this).hide();
					return false;
				});
			
				$('table.report button.cancel').click(function() {
					$(this).closest("td").removeClass("editMode");
				});
			
				$('table.report button.save').click(function() {
					var that = $(this).closest("td");
					that.addClass("saving");
					
					var params = $(this).closest("form").serialize();
					
					$.post('ManageTranslationsAjax.action', params, function(result) {
						if (result.success) {
							that.find("input[name|='translation']").val(result.id);
							that.find("span").html(that.find("textarea").val());
							that.removeClass("editMode");
						} else {
							alert(result.reason);
						}
						that.removeClass("saving");
					}, "json");
				});
				
				$('table.report form ul.qualityRating input').click(function() {
					var qualityForm = $(this).closest("form");
					var qualityRating = $(this).val();
					var ulElement = $(this).closest("ul");
					
					$.ajax({
						url: "ManageTranslationsAjax!updateQualityRating.action",
						data: {
							translation: qualityForm.find("input[name=translation]").val(),
							"translation.qualityRating": qualityRating
						},
						success: function() {
							ulElement.effect('highlight', {color: '#FFFF11'}, 1000);
						}
					});
				});
				
				$('#doneButton').bind('click', function() {
					self.close();
				});
			});
		</script>
	</body>
</html>