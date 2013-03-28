<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>

<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />

<script type="text/javascript" src="TranslateJS.action"></script>

<!-- Grab Google CDN's jQuery, with a protocol relative URL; fall back to local if necessary -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script>window.jQuery || document.write('<script src="js/jquery/jquery-1.7.1.min.js">\x3C/script>')</script>

<script type="text/javascript" src="v7/js/pics/core/core.js?v=${version}"></script>
<script type="text/javascript" src="js/audit_cat_edit.js?v=<s:property value="version"/>"></script>

<div id="main">
    <div id="bodyholder">
        <div id="content">
            <div id="${actionName}_${methodName}_page" class="${actionName}-page page">
                <h1>
                    <s:text name="AuditDataUpload.UploadFile">
                       <s:param>${conAudit.auditFor}</s:param>
                       <s:param>
                            <s:if test="auditData.question.columnHeader.exists" >
                                ${auditData.question.columnHeader.toString()}
                            </s:if>
                       </s:param>
                    </s:text>
                    <span class="sub">
                        <s:iterator value="auditData.question.category.ancestors" status="stat">
                            ${name}<s:if test="!#stat.last">&gt;</s:if>
                        </s:iterator>
                    </span>
                </h1>

                <s:include value="../actionMessages.jsp" />

                <b>${auditData.question.expandedNumber}</b>&nbsp;&nbsp;
                <s:property value="auditData.question.name" escape="false"/>


                <s:form enctype="multipart/form-data" method="POST">
                    <s:hidden name="auditID" />
                    <s:hidden name="divId" />
                    <s:hidden name="auditData.question.id" />
                    <table>
                        <tr>
                            <td style="text-align:center;vertical-align:top;width: 45%">
                                <h3>
                                    <s:text name="AuditDataUpload.UploadNew" />
                                </h3>

                                <s:if test="file != null && file.exists()">
                                    <s:text name="AuditDataUpload.WillReplaceFile" />
                                </s:if>

                                <div style="margin-bottom:20px;">
                                    <s:text name="global.maxFileUploadBytes">
                                        <s:param>${maxFileUploadSize}</s:param>
                                    </s:text>
                                </div>

                                <s:file name="file" size="15%"></s:file>

                                <div>
                                    <s:submit method="uploadFile" value="%{getText('button.UploadFile')}" cssClass="picsbutton positive" id="uploadFile"></s:submit>
                                </div>
                            </td>
                            <%--
                            <s:if test="file != null && file.exists()">
                                <td style="text-align:center;vertical-align:top; width: 45%;border-left: 1px solid #eee;">
                                    <h3 style="margin-top:0px;"><s:text name="AuditDataUpload.ViewFile"></s:text></h3>
                                    <a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&auditData.question.id=<s:property value="auditData.question.id"/>"
                                        target="_BLANK"><s:text name="AuditDataUpload.OpenExisting"><s:param><s:property value="fileSize" /></s:param></s:text></a>
                                    <br/><br/>

                                    <s:submit method="deleteFile" value="%{getText('button.DeleteFile')}" cssClass="picsbutton positive" onclick="return confirm(translate('JS.ConfirmDeletion'));"></s:submit>
                                    <br clear="all" />
                                </td>
                            </s:if>
                            --%>

                                <td style="text-align:center;vertical-align:top; width: 45%;border-left: 1px solid #eee;">
                                    <h3 style="margin-top:0px;"><s:text name="AuditDataUpload.ViewFile"></s:text></h3>
                                    <a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&auditData.question.id=<s:property value="auditData.question.id"/>"
                                        target="_BLANK"><s:text name="AuditDataUpload.OpenExisting"><s:param><s:property value="fileSize" /></s:param></s:text></a>
                                    <br/><br/>

                                    <s:submit method="deleteFile" value="%{getText('button.DeleteFile')}" cssClass="picsbutton positive" id="deleteFile"></s:submit>
                                    <br clear="all" />
                                </td>

                        </tr>
                    </table>
                </s:form>

                <button id="close_page" style="width:100%" class="picsbutton" name="button" value="Close"><s:text name="button.CloseReturn" /></button>
            </div>
        </div>
    </div>
</div>
