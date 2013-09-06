<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<title>
	Manage <s:property value="country.name"/>
</title>

<div>
	<h1>
        Manage <s:property value="country.name"/>
	</h1>

    <a href="ManageCountries.action">
        Back to Manage Countries
    </a>

    <s:include value="../actionMessages.jsp" />

    <s:form id="save" method="POST" enctype="multipart/form-data">
        <s:hidden name="countryContact.id"/>
        <s:hidden name="countryContact.country.isoCode"/>
        <s:hidden name="country.isoCode"/>

        <table width="100%">
            <tr>
                <td style="vertical-align: top; width: 50%;">
                    <fieldset class="form">
                        <h2 class="formLegend">Country</h2>
                        <ol>
                            <li><label>Country:</label><s:property value="countryContact.country.name"/></li>
                            <li><label>Country Code:</label><s:property value="countryContact.country.isoCode"/></li>
                        </ol>
                    </fieldset>
                    <fieldset class="form">
                        <h2 class="formLegend">CSR Information</h2>
                        <ol>
                            <li><label>Phone: </label><s:textfield name="countryContact.csrPhone" /></li>
                            <li><label>Fax: </label><s:textfield name="countryContact.csrFax" /></li>
                            <li><label>Email: </label><s:textfield name="countryContact.csrEmail" /></li>
                            <li><label>Address: </label><s:textfield name="countryContact.csrAddress" size="35" /></li>
                            <li><label>City: </label><s:textfield name="countryContact.csrCity"  size="20" /></li>
                            <li><label>Subdivision: </label>
                                <s:select id="csr_subdivision"
                                          name="countryContact.csrCountrySubdivision.isoCode"
                                          value="countryContact.csrCountrySubdivision.isoCode"
                                          list="countrySubdivisionList"
                                          listKey="isoCode"
                                          listValue="name"
                                          headerKey=""
                                          headerValue=" - Subdivision - "/>
                            </li>
                            <li><label>Zip: </label><s:textfield name="countryContact.csrZip" size="7" /></li>
                        </ol>
                    </fieldset>
                    <fieldset class="form">
                        <h2 class="formLegend">ISR Information</h2>
                        <ol>
                            <li><label>Phone: </label><s:textfield name="countryContact.isrPhone" /></li>
                            <li><label>Fax: </label><s:textfield name="countryContact.isrFax" /></li>
                            <li><label>Email: </label><s:textfield name="countryContact.isrEmail" /></li>
                            <li><label>Address: </label><s:textfield name="countryContact.isrAddress" size="35" /></li>
                            <li><label>City: </label><s:textfield name="countryContact.isrCity"  size="20" /></li>
                            <li><label>Subdivision: </label>
                                <s:select id="isr_subdivision"
                                          name="countryContact.isrCountrySubdivision.isoCode"
                                          value="countryContact.isrCountrySubdivision.isoCode"
                                          list="countrySubdivisionList"
                                          listKey="isoCode"
                                          listValue="name"
                                          headerKey=""
                                          headerValue=" - Subdivision - "/>
                            </li>
                            <li><label>Zip: </label><s:textfield name="countryContact.isrZip" size="7" /></li>
                        </ol>
                    </fieldset>
                    <fieldset class="form">
                        <h2 class="formLegend">Business Unit</h2>
                        <ol>
                            <li><label>Business Unit: </label>
                                <s:select id="country_business_unit"
                                          name="countryContact.businessUnit.id"
                                          value="countryContact.businessUnit.id"
                                          list="businessUnitList"
                                          listKey="id"
                                          listValue="businessUnit"
                                          headerKey=""
                                          headerValue=" - Business Unit - "/>
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