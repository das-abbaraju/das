<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
    <head>
        <title>
            <s:property value="contractor.name" /> Credit Memo <s:property value="creditMemo.id" />
        </title>
    </head>

    <body>
        <div>

            <s:form id="save_refund" method="POST" enctype="multipart/form-data">
                <s:set var="credit_memo_id" value="creditMemo.id" />
                <s:hidden name="creditmemo.id" value="%{credit_memo_id}" />
                <table width="100%">
                    <tr>
                        <td>
                            <fieldset class="form">
                                <h2 class="formLegend">Refund Details</h2>

                                <ol>
                                    <li>
                                        <label>Refund Method: </label>
                                        <s:select
                                                list="@com.picsauditing.jpa.entities.PaymentMethod@values()"
                                                name="paymentMethod"
                                                headerKey=""
                                                headerValue="- Refund Method -" />
                                    </li>
                                </ol>
                            </fieldset>
                            <fieldset class="form submit">
                                <s:submit cssClass="picsbutton positive" method="save" value="%{getText('button.Save')}" />
                            </fieldset>
                        </td>
                    </tr>
                </table>
            </s:form>
        </div>
    </body>
</html>
