<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<%@ page import="org.apache.commons.collections.CollectionUtils" %>

<s:if test="#insureguardCertificates.size > 0">

    <h3>
        <s:text name="ConInsureGUARD.NumCertificates">
            <s:param><s:property value="#certType" /></s:param>
        </s:text>
    </h3>

    <table class="report">
        <thead>
            <tr>
                <td>
                    <s:text name="global.Filename" />
                </td>
                <td>
                    <s:text name="global.ExpirationDate" />
                </td>
                <td>
                    <s:text name="global.View" />
                </td>
                <td>
                    <s:text name="global.Edit" />
                </td>

                <s:if test="#certType != 'Uploaded'">
                    <td>
                        <s:text name="ConInsureGUARD.UsedBy" />
                    </td>
                </s:if>
            </tr>
        </thead>

        <tbody>
            <s:iterator value="#insureguardCertificates.keySet()" var="certificate">
                <tr>
                    <td>
                        <s:property value="#certificate.description" />
                    </td>
                    <td class="center">
                        <s:date name="expirationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
                    </td>
                    <td class="center">
                        <a href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="id"/>&button=download" target="_BLANK">
                            <img src="images/icon_insurance.gif" />
                        </a>
                    </td>
                    <td>
                        <s:if test="permissions.userId == createdBy.Id || permissions.admin">
                            <a class="edit" href="#" onclick="showCertUpload(<s:property value="contractor.id"/>, <s:property value="id" />); return false;" title="Opens in new window (please disable your popup blocker)">
                                <s:text name="global.Edit" />
                            </a>
                        </s:if>
                    </td>
                    <s:if test="#certType != 'Uploaded'">
                        <td>
                            <s:if test="!permissions.operatorCorporate || permissions.insuranceOperatorID == operator.id">
                                <!-- The Java Set.toString() method is almost perfect, but we need to trim the square brackets -->
                                <s:property value="#insureguardCertificates.get(#certificate).toString().replace(\"[\", \"\").replace(\"]\", \"\")" />
                            </s:if>
                        </td>
                    </s:if>
                </tr>
            </s:iterator>
        </tbody>
    </table>

    <br />

</s:if>
