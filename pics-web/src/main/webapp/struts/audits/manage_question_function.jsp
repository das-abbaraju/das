<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
<head>
    <title>Manage Function</title>

    <link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="js/jquery/mcdropdown/css/jquery.mcdropdown.min.css?v=${version}" />

    <s:include value="../jquery.jsp"/>

    <script type="text/javascript" src="js/jquery/jquery.bgiframe.min.js?v=${version}"></script>
    <script type="text/javascript" src="js/jquery/mcdropdown/jquery.mcdropdown.min.js?v=${version}"></script>

</head>
<body>
<a href="ManageQuestion.action?id=<s:property value="question.id" />">Back To Question</a>
<s:include value="../config_environment.jsp" />
<s:include value="../actionMessages.jsp" />

<s:form id="save" cssClass="form">
<s:hidden name="id" />
<s:hidden name="questionId" />

<fieldset class="form">
    <h2 class="formLegend">Question Function</h2>

    <ol>
        <li>
            <label>ID:</label>

            <s:if test="function.id > 0">
                <s:property value="function.id" />
            </s:if>
            <s:else>
                NEW
            </s:else>
        </li>
        <li>
            <label>Question Id:</label>
            <s:property value="question.id" />
        </li>
        <li>
            <label>Function Type:</label>
            <s:select list="@com.picsauditing.jpa.entities.QuestionFunctionType@values()"
                name="function.type"
                headerKey=""
                headerValue="" />
        </li>
        <li>
            <label>Function:</label>
            <s:select list="@com.picsauditing.jpa.entities.QuestionFunction@values()"
                      name="function.function"
                      headerKey=""
                      headerValue="" />
        </li>
        <li>
            <label>Overwrite:</label>
            <s:checkbox name="function.overwrite" />
        </li>
        <li>
            <label>Expression:</label>
            <s:textarea name="function.expression"></s:textarea>
        </li>
    </ol>
</fieldset>

<fieldset class="form submit">
    <div>
        <s:submit action="ManageQuestionFunction!save" cssClass="picsbutton positive" value="%{getText('button.Save')}" />
        <s:if test="function.id > 0">
            <s:submit action="ManageQuestionFunction!delete" cssClass="picsbutton negative" value="%{getText('button.Delete')}"
                      onclick="return confirm('Are you sure you want to delete this function?');"/>
        </s:if>
    </div>
</fieldset>
</s:form>
</body>
</html>