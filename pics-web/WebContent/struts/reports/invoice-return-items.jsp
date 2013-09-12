<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="id" value="invoice.id" />
<html>
    <head>
        <title>
            <s:property value="contractor.name" /> Invoice <s:property value="invoice.id" />
        </title>
    </head>

    <body>
        <s:set name="invoice_id" value="invoice.id" />

        <s:include value="/struts/layout/_page-header.jsp">
            <s:param name="title"><s:text name="InvoiceReturnItems.pageHeader.title" /></s:param>
            <s:param name="subtitle"><s:text name="InvoiceReturnItems.pageHeader.subtitle" /></s:param>
        </s:include>

        <div id="main" class="container">

            <s:include value="/struts/_action-messages.jsp" />

            <form action="InvoiceReturnItems!doReturn.action?invoice.id=${invoice.id}" method="post" id="" name="" class="form-horizontal">

                <div class="control-group">
                    <table class="table table-striped table-bordered table-hover">
                        <thead>
                            <tr>
                                <th>
                                    &nbsp;
                                </th>
                                <th>
                                    <s:text name="InvoiceReturnItems.table.headerNameAndDescription" />
                                </th>
                                <th>
                                    <s:text name="InvoiceReturnItems.table.headerFee" />
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <s:iterator value="invoice.items">

                                <s:set name="o" value="[0]" />
                                <s:set name="item_name" value="invoiceFee.fee" />
                                <s:set name="item_description" value="description" />
                                <s:set name="item_price" value="amount" />
                                <s:set name="item_id" value="id" />
                                <s:set name="price_effective_date" value="InvoiceDetail.Effective" />
                                <s:set name="item_expiration_date" value="paymentExpires" />
                                <s:set name="invoice_currency" value="invoice.currency" />

                                <tr>
                                    <td>
                                        <input type="checkbox" name="refunds" value="${item_id}" />
                                    </td>
                                    <td>
                                        <s:include value="../who.jsp" />
                                        ${item_name}
                                        <s:if test="paymentExpires != null">
                                            <span>
                                                <s:if test="invoiceFee.membership">
                                                    <s:text name="InvoiceDetail.Expires" />
                                                </s:if>
                                                <s:else>
                                                    ${price_effective_date}
                                                    <s:text name="InvoiceDetail.Effective" />
                                                </s:else>
                                                ${item_expiration_date}
                                            </span>
                                        </s:if>
                                    </td>
                                    <td class="number">
                                        ${item_price}
                                        ${invoice_currency}
                                    </td>
                                </tr>

                            </s:iterator>
                        </tbody>
                    </table>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <button type="submit" class="btn">Submit</button>
                        <button type="reset" class="btn">Reset</button>
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>
