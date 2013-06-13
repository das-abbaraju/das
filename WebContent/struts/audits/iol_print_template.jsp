<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<s:set value="{13224,13225,13227,13229,13232,13234}" name="rmpSection" />

<head>
     <title><s:text name="%{audit.auditType.getI18nKey('name')}" /> for <s:property value="audit.contractorAccount.name" /></title>

    <link rel="stylesheet" type="text/css" href="css/style.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" href="css/print.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" href="v7/css/vendor/bootstrap.css?v=${version}" />
    <link rel="stylesheet" type="text/css" href="v7/css/vendor/bootstrap-responsive.css?v=${version}" />

</head>
<body id="${actionName}_${methodName}_page" class="${actionName}-page page iol-print-template">

    <div class="row">
        <button id="print" class="btn btn-primary span1 offset11" href="#" onclick="window.print(); return false;"><s:text name="global.print"/></button>
    </div>


    <header>
        <h1>Products and Chemicals Contractor Safety Management Practice</h1>
        <p><s:text name="%{audit.auditType.getI18nKey('name')}" /></p>
        <p>RMP Template</p>
    </header>
    <hr />

    <section>
        <div class="row">
            <div class="span4">
                <p>
                    <label>Company:</label>
                    <span><s:property value="%{audit.contractorAccount.name}" /></span>
                </p>
                <p>
                    <label>Developed By:</label>
                    <span>Captain Underpants, Snoopy, John Smith</span>
                </p>
            </div>
            <div class="span4">
                <p>
                    <label>Risk level:</label>
                    <span>54</span>
                </p>
            </div>
            <div class="span4">
                <p>
                    <label>Work Level:</label>
                    <span>2</span>
                </p>
                <p>
                    <label>Review Date:</label>
                    <span>02/02/14</span>
                </p>
            </div>
        </div>
    </section>

    <section>
        <h1>Executive Summary</h1>
        <div class="executive-summary table-striped">
            <div>
                <label><s:text name="%{getQuestion(13207).getI18nKey('name')}" /></label>
                <p><s:property value="%{getAnswer(13207).answer}" /></p>
            </div>
            <div>
                <label><s:text name="%{getQuestion(13208).getI18nKey('name')}" /></label>
                <p><s:property value="%{getAnswer(13208).answer}" /></p>
            </div>
            <div>
                <label><s:text name="%{getQuestion(13209).getI18nKey('name')}" /></label>
                <p><s:property value="%{getAnswer(13209).answer}" /></p>
            </div>
            <div>
                <label><s:text name="%{getQuestion(13210).getI18nKey('name')}" /></label>
                <p><s:property value="%{getAnswer(13210).answer}" /></p>
            </div>
            <div>
                <label><s:text name="%{getQuestion(13211).getI18nKey('name')}" /></label>
                <p><s:property value="%{getAnswer(13211).answer}" /></p>
            </div>
            <div class="row rmp-info">
                <div class="span4">
                    <p>
                        <label>RMP Owner:</label>
                        <span>Mr. Maggot</span>
                    </p>
                    <p>
                        <label>BU/Site:</label>
                        <span>Inferno</span>
                    </p>
                    <p>
                        <label>Name:</label>
                    <span>Sgt. Spandex</span>
                    </p>
                </div>
                <div class="span4">
                    <p>
                        <label>RMP User Contacts: BU/Site:</label>
                        <ul>
                            <li>hi</li>
                            <li>hi</li>
                            <li>hi</li>
                        </ul>
                    </p>
                </div>
                <div class="span3">
                    <p>
                        <label>Name:</label>
                        <ul>
                            <li>hi</li>
                            <li>hi</li>
                            <li>hi</li>
                            <li>hi</li>
                        </ul>
                    </p>
                </div>
            </div>
        </div>
    </section>

    <section>
        <h1>Risk Mitigation Plan (RMP)</h1>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Specific Improvment Items</th>
                    <%--<th>Resp.</th> --%>
                    <%--<th>Target Date</th> --%>
                    <th>Comments/Status</th>
                </tr>
            </thead>
            <tbody>
                    <s:iterator value="rmpSection" var="questionID" status="loopvar">
                        <s:set name="answer" value="%{getAnswer(#questionID).answer}" />

                        <s:if test="!#answer.isEmpty()">
                            <tr>
                                <td>${loopvar.index}</td>
                                <td>${answer}</td>
                                <td><s:property value="%{getAnswer(#questionID).comment}" /></td>
                            </tr>
                        </s:if>
                    </s:iterator>
            </tbody>
        </table>
    </section>

    <section>
        <label><s:text name="%{getQuestion(13212).getI18nKey('name')}" /></label>
        <p><s:property value="%{getAnswer(13212).answer}" /></p>
    </section>

    <section>
        <h1>RMP Stewardship</h1>
        <s:set var="frequency_of_review_answer" value="%{getAnswer(13213).answer}" />
        <table class="table table-striped">
            <thead>
                <tr>
                    <th></th>
                    <th>Monthly</th>
                    <th>Quarterly</th>
                    <th>Semi-Annually</th>
                    <th>Annually</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <strong><s:text name="%{getQuestion(13213).getI18nKey('name')}" /></strong>
                        <p>
                            <small>Check off appropriate frequency</small>
                        </p>
                    </td>
                    <td id="monthly-review">
                        <s:if test="#frequency_of_review_answer == 1260">X</s:if>
                    </td>
                    <td id="quarterly-review">
                        <s:if test="#frequency_of_review_answer == 1261">X</s:if>
                    </td>
                    <td id="semi-annual-review">
                        <s:if test="#frequency_of_review_answer == 1262">X</s:if>
                    </td>
                    <td id="annual-review">
                        <s:if test="#frequency_of_review_answer == 1263">X</s:if>
                    </td>
                </tr>
            </tbody>
        </table>
    </section>

    <section id="signatures">
        <h1>Planned Date of Next Annual RMP Review/Update</h1>
        <label>Date:</label>
        <span>02/02/52</span>

        <ol>
            <li>
                <label><s:text name="%{getQuestion(13214).getI18nKey('name')}" /></label>
                <div class="row">
                    <div class="span8">
                        <label>Name/Title:</label>
                        <span><s:property value="%{getAnswer(13214).answer}" /></span>
                    </div>
                    <div class="span4">
                        <label>Date:</label>
                        <span>02/02/17</span>
                    </div>
                </div>
            </li>
            <li>
                <label><s:text name="%{getQuestion(13215).getI18nKey('name')}" /></label>
                <div class="row">
                    <div class="span8">
                        <label>Name/Title:</label>
                        <span><s:property value="%{getAnswer(13215).answer}" /></span>
                    </div>
                    <div class="span4">
                        <label>Date:</label>
                        <span>02/02/17</span>
                    </div>
                </div>
            </li>
            <li>
                <label><s:text name="%{getQuestion(13216).getI18nKey('name')}" /></label>
                <div class="row">
                    <div class="span8">
                        <label>Name/Title:</label>
                        <span><s:property value="%{getAnswer(13216).answer}" /></span>
                    </div>
                    <div class="span4">
                        <label>Date:</label>
                        <span>02/02/17</span>
                    </div>
                </div>
            </li>
            <li>
                <label><s:text name="%{getQuestion(13217).getI18nKey('name')}" /></label>
                <div class="row">
                    <div class="span8">
                        <label>Name/Title:</label>
                        <span><s:property value="%{getAnswer(13217).answer}" /></span>
                    </div>
                    <div class="span4">
                        <label>Date:</label>
                        <span>02/02/17</span>
                    </div>
                </div>
            </li>
            <li>
                <label><s:text name="%{getQuestion(13218).getI18nKey('name')}" /></label>
                <div class="row">
                    <div class="span8">
                        <label>Name/Title:</label>
                        <span><s:property value="%{getAnswer(13218).answer}" /></span>
                    </div>
                    <div class="span4">
                        <label>Date:</label>
                        <span>02/02/17</span>
                    </div>
                </div>
            </li>
        </ol>
    </section>
</body>