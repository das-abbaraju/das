<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage Question Transform Option</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
</head>
<body>
<s:include value="../actionMessages.jsp" />

<a href="ManageQuestion.action?id=<s:property value="id"/>">&lt Back</a>

<s:form id="save" cssClass="form">
	<s:hidden name="id" />
	<s:hidden name="transformId"/>
	<fieldset class="form">
	<h2 class="formLegend">Transform Definition</h2>
	<ol>
		<li><label>ID:</label>
			<s:if test="option.id > 0">
				<s:property value="option.id" />
			</s:if>
			<s:else>
				NEW
			</s:else>
		</li>
		<li><label>Destination PQF Question ID:</label>
			<s:textfield name="destinationId" size="65"/>
		<pics:fieldhelp title="PQF Question ID">
			<p>Specify the PQF question ID for the destination of the response</p>
		</pics:fieldhelp>
		</li>
		<li><label>Use Response as Comment:</label>
			<s:checkbox name="option.commentResponse"/>
			<pics:fieldhelp title="Use Response as Comment">
				<p>The response will be used as the comment for the answer rather than the answer.</p>
			</pics:fieldhelp>
		</li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Search for Text Option</h2>
	<ol>
	<li><label>Search Text:</label>
		<s:textfield name="option.searchValue" size="65"/>
		<pics:fieldhelp title="Search Text">
			<p>A result of  "Yes" is returned if the text is found and "No" if the text is not found.</p>
		</pics:fieldhelp>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Extract Text Option</h2>
	<ol>
	<li><label>Extraction Pattern:</label>
		<s:textfield name="option.extractPattern" size="65"/>
		<pics:fieldhelp title="Extraction Pattern">
			<p>Used to pull some text from the response </p>
			<p>Use a pattern such as "{0} is {1}". Use "\\n" for a new line.</p>
		</pics:fieldhelp>
	</li>
	<li><label>Extraction Number</label>
		<s:textfield name="option.extractIndex"/>
		<pics:fieldhelp title="Extraction Pattern">
			<p>This is the {#} number to be the response.</p>
		</pics:fieldhelp>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Date Transformation Option</h2>
	<ol>
	<li><label>Original Date Pattern:</label>
		<s:textfield name="option.dateFromPattern" size="65"/>
		<pics:fieldhelp title="Date Pattern">
			<p>Specify a pattern such as "yyyy-MM-dd" for dates like "2010-01-31".</p>
			<p>use "yyyy" for a 4-digit year, "MM" for a 1 or 2-digit month, and "dd" for a 1 or 2-digit day.</p>
		</pics:fieldhelp>
	</li>
	<li><label>New Date Pattern:</label>
		<s:textfield name="option.dateToPattern" size="65"/>
	</li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Number Transformation Option</h2>
	<ol>
	<li><label>Format Response as a Number:</label>
		<s:checkbox name="formatNumber"/>
		<pics:fieldhelp title="Format Response as a Number">
			<p>The response will be formated as a number.</p>
		</pics:fieldhelp>
	</li>
	<li><label>Decimal Places:</label>
		<s:textfield name="decimalPlaces" size="65"/>
		<pics:fieldhelp title="Decimal Places">
			<p>Specify the number of decimal places.</p>
		</pics:fieldhelp>
	</li>
	<li><label>Multiplier:</label>
		<s:textfield name="option.multiplier" size="65"/>
		<pics:fieldhelp title="Multiplier">
			<p>Specify the number should be multiplied by.</p>
		</pics:fieldhelp>
	</li>
	<li><label>Check Against Level:</label>
		<s:checkbox name="hasLevel"/>
		<pics:fieldhelp title="Format Response as a Number">
			<p>The response will be checked against the level value and return "Yes" if it is greater than or equal to the level, otherwise it will return "No".</p>
		</pics:fieldhelp>
	</li>
	<li><label>Level:</label>
		<s:textfield name="level" size="65"/>
	</li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Reformat Response Option</h2>
	<ol>
	<li><label>Original Format Pattern:</label>
		<s:textfield name="option.reformatFromPattern" size="65"/>
		<pics:fieldhelp title="Format Pattern">
			<p>Specify a pattern such as "({0}) {1}-{2}" for phone numbers like "(555) 123-4567"</p>
		</pics:fieldhelp>
	</li>
	<li><label>New Format Pattern:</label>
		<s:textfield name="option.reformatToPattern" size="65"/>
	</li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Map Response Option</h2>
	<ol>
	<li><label>Mapping Options:</label>
		<s:textfield name="option.answerMapOptions" size="65"/>
		<pics:fieldhelp title="Mapping Options">
			<p>Specify a comma separated list of original responses and new responses.</p>
			<p>For example a value of "Y,Yes,N,No,N/A,NA" will map a response of "Y" to "Yes", "N" to "No", and "N/A" to "NA".</p>
			<p>If mapping options are defined then ALL mappings must be defined. Responses that do not have a mapping will be mapped to no response.</p>
		</pics:fieldhelp>
	</li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Response Found Options</h2>
	<ol>
	<li><label>Response Found:</label>
		<s:checkbox name="option.somethingOrNothing"/>
		<pics:fieldhelp title="Response Found">
			<p>If the question has any response, a response of "Yes" will be returned otherwise "No" is returned.</p>
		</pics:fieldhelp>
	</li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Comparison Options</h2>
	<ol>
	<li><label>Compare Against Other Responses:</label>
		<s:select name="comparison" list="comparisonOptions" value="comparison"></s:select>
		<pics:fieldhelp title="Comparison Options">
			<p>This specifies how the responses are compared.</p>
			<p>Select a value of "None" for no comparison</>
			<p>A value of "Same" will return "Yes" if this and the other questions to compare are the same. Otherwise, "No" is returned.</p>
			<p>A value of "Or" will return "Yes" if any of the questions is "Yes". Otherwise, "No" is returned.</p>
		</pics:fieldhelp>
	</li>
	<li><label>Question IDs to Compare:</label>
		<s:textfield name="option.comparisonQuestions" />
		<pics:fieldhelp title="Question IDs to Compare">
			<p>Enter a comma separated list of question IDs  to compare the response to this question with.</p>
		</pics:fieldhelp>
	</li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
		<div>
			<button class="picsbutton positive" name="button" type="submit" value="save">Save</button>
			<s:if test="option.id > 0">
				<input type="submit" name="button" class="picsbutton negative" value="Delete"
					onclick="return confirm('Are you sure you want to delete this transform?');" />
			</s:if>
		</div>
	</fieldset>
</s:form>
</body>
</html>