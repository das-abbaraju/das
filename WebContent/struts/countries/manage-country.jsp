<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<title>
	Manage <s:property value="country.name"/>
</title>

<div>
	<h1>
        Manage <s:property value="country.name"/>
	</h1>

    <a href="Report.action?report=1010">
        Back to Manage Countries
    </a>

    <s:include value="../actionMessages.jsp" />

    <s:form id="save" method="POST" enctype="multipart/form-data">
        <s:hidden name="country.id"/>
        <s:hidden name="country.isoCode"/>
        <s:hidden name="country.businessUnit"/>

        <table width="100%">
            <tr>
                <td style="vertical-align: top; width: 50%;">
                    <fieldset class="form">
                        <h2 class="formLegend">Country</h2>
                        <ol>
                            <li><label>Country:</label><s:property value="country.name"/></li>
                            <li><label>Country Code:</label><s:property value="country.isoCode"/></li>
                        </ol>
                    </fieldset>
                    <fieldset class="form">
                        <h2 class="formLegend">Billing</h2>
                        <ol>
                            <li><label>Currency: </label>
                                <s:select id="country_currency"
                                          name="country.currency"
                                          value="country.currency"
                                          list="@com.picsauditing.jpa.entities.Currency@values()"/>
                            </li>
                            <li><label>Accepts Proforma?</label>
                                <s:radio
                                        list="#{'true':'Yes','false':'No'}"
                                        name="country.proforma"
                                        value="country.proforma"
                                        theme="pics"
                                        cssClass="inline"
                                        />
                            </li>
                        </ol>
                    </fieldset>
                    <fieldset class="form">
                        <h2 class="formLegend">CSR Information</h2>
                        <ol>
                            <li><label>Phone: </label><s:textfield name="country.csrPhone" /></li>
                            <li><label>Fax: </label><s:textfield name="country.csrFax" /></li>
                            <li><label>Email: </label><s:textfield name="country.csrEmail" /></li>
                            <li><label>Address: </label><s:textfield name="country.csrAddress" size="35" /></li>
                            <li><label>City: </label><s:textfield name="country.csrCity"  size="20" /></li>
                            <li><label>Subdivision: </label>
                                <s:select id="csr_subdivision"
                                          name="country.csrCountrySubdivision.isoCode"
                                          value="country.csrCountrySubdivision.isoCode"
                                          list="countrySubdivisionList"
                                          listKey="isoCode"
                                          listValue="name"
                                          headerKey=""
                                          headerValue=" - Subdivision - "/>
                            </li>
                            <li><label>Zip: </label><s:textfield name="country.csrZip" size="7" /></li>
                        </ol>
                    </fieldset>
                    <fieldset class="form">
                        <h2 class="formLegend">ISR Information</h2>
                        <ol>
                            <li><label>Phone: </label><s:textfield name="country.isrPhone" /></li>
                            <li><label>Fax: </label><s:textfield name="country.isrFax" /></li>
                            <li><label>Email: </label><s:textfield name="country.isrEmail" /></li>
                            <li><label>Address: </label><s:textfield name="country.isrAddress" size="35" /></li>
                            <li><label>City: </label><s:textfield name="country.isrCity"  size="20" /></li>
                            <li><label>Subdivision: </label>
                                <s:select id="isr_subdivision"
                                          name="country.isrCountrySubdivision.isoCode"
                                          value="country.isrCountrySubdivision.isoCode"
                                          list="countrySubdivisionList"
                                          listKey="isoCode"
                                          listValue="name"
                                          headerKey=""
                                          headerValue=" - Subdivision - "/>
                            </li>
                            <li><label>Zip: </label><s:textfield name="country.isrZip" size="7" /></li>
                        </ol>
                    </fieldset>
                    <fieldset class="form">
                        <h2 class="formLegend">Business Unit</h2>
                        <ol>
                            <li><label>Business Unit: </label>
                                <s:property value="country.businessUnit.businessUnit"/>
                                <%--<s:select id="country_business_unit"--%>
                                          <%--name="country.businessUnit.id"--%>
                                          <%--value="country.businessUnit.id"--%>
                                          <%--list="businessUnitList"--%>
                                          <%--listKey="id"--%>
                                          <%--listValue="businessUnit"--%>
                                          <%--headerKey=""--%>
                                          <%--headerValue=" - Business Unit - "/>--%>
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