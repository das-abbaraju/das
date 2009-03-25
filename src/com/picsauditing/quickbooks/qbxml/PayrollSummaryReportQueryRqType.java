//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.03.12 at 04:00:19 PM PDT 
//


package com.picsauditing.quickbooks.qbxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PayrollSummaryReportQueryRqType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PayrollSummaryReportQueryRqType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{}PayrollSummaryReportQuery"/>
 *       &lt;/sequence>
 *       &lt;attribute name="requestID" type="{}STRTYPE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayrollSummaryReportQueryRqType", propOrder = {
    "payrollSummaryReportType",
    "displayReport",
    "reportPeriod",
    "reportDateMacro",
    "reportAccountFilter",
    "reportEntityFilter",
    "reportItemFilter",
    "reportClassFilter",
    "reportModifiedDateRangeFilter",
    "reportModifiedDateRangeMacro",
    "reportDetailLevelFilter",
    "reportPostingStatusFilter",
    "summarizeColumnsBy",
    "includeSubcolumns",
    "reportCalendar",
    "returnRows",
    "returnColumns"
})
public class PayrollSummaryReportQueryRqType {

    @XmlElement(name = "PayrollSummaryReportType", required = true)
    protected String payrollSummaryReportType;
    @XmlElement(name = "DisplayReport")
    protected String displayReport;
    @XmlElement(name = "ReportPeriod")
    protected ReportPeriod reportPeriod;
    @XmlElement(name = "ReportDateMacro")
    protected String reportDateMacro;
    @XmlElement(name = "ReportAccountFilter")
    protected ReportAccountFilter reportAccountFilter;
    @XmlElement(name = "ReportEntityFilter")
    protected ReportEntityFilter reportEntityFilter;
    @XmlElement(name = "ReportItemFilter")
    protected ReportItemFilter reportItemFilter;
    @XmlElement(name = "ReportClassFilter")
    protected ReportClassFilter reportClassFilter;
    @XmlElement(name = "ReportModifiedDateRangeFilter")
    protected ReportModifiedDateRangeFilter reportModifiedDateRangeFilter;
    @XmlElement(name = "ReportModifiedDateRangeMacro")
    protected String reportModifiedDateRangeMacro;
    @XmlElement(name = "ReportDetailLevelFilter", defaultValue = "All")
    protected String reportDetailLevelFilter;
    @XmlElement(name = "ReportPostingStatusFilter")
    protected String reportPostingStatusFilter;
    @XmlElement(name = "SummarizeColumnsBy")
    protected String summarizeColumnsBy;
    @XmlElement(name = "IncludeSubcolumns")
    protected String includeSubcolumns;
    @XmlElement(name = "ReportCalendar")
    protected String reportCalendar;
    @XmlElement(name = "ReturnRows")
    protected String returnRows;
    @XmlElement(name = "ReturnColumns")
    protected String returnColumns;
    @XmlAttribute
    protected String requestID;

    /**
     * Gets the value of the payrollSummaryReportType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPayrollSummaryReportType() {
        return payrollSummaryReportType;
    }

    /**
     * Sets the value of the payrollSummaryReportType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPayrollSummaryReportType(String value) {
        this.payrollSummaryReportType = value;
    }

    /**
     * Gets the value of the displayReport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayReport() {
        return displayReport;
    }

    /**
     * Sets the value of the displayReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayReport(String value) {
        this.displayReport = value;
    }

    /**
     * Gets the value of the reportPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link ReportPeriod }
     *     
     */
    public ReportPeriod getReportPeriod() {
        return reportPeriod;
    }

    /**
     * Sets the value of the reportPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportPeriod }
     *     
     */
    public void setReportPeriod(ReportPeriod value) {
        this.reportPeriod = value;
    }

    /**
     * Gets the value of the reportDateMacro property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportDateMacro() {
        return reportDateMacro;
    }

    /**
     * Sets the value of the reportDateMacro property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportDateMacro(String value) {
        this.reportDateMacro = value;
    }

    /**
     * Gets the value of the reportAccountFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ReportAccountFilter }
     *     
     */
    public ReportAccountFilter getReportAccountFilter() {
        return reportAccountFilter;
    }

    /**
     * Sets the value of the reportAccountFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportAccountFilter }
     *     
     */
    public void setReportAccountFilter(ReportAccountFilter value) {
        this.reportAccountFilter = value;
    }

    /**
     * Gets the value of the reportEntityFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ReportEntityFilter }
     *     
     */
    public ReportEntityFilter getReportEntityFilter() {
        return reportEntityFilter;
    }

    /**
     * Sets the value of the reportEntityFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportEntityFilter }
     *     
     */
    public void setReportEntityFilter(ReportEntityFilter value) {
        this.reportEntityFilter = value;
    }

    /**
     * Gets the value of the reportItemFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ReportItemFilter }
     *     
     */
    public ReportItemFilter getReportItemFilter() {
        return reportItemFilter;
    }

    /**
     * Sets the value of the reportItemFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportItemFilter }
     *     
     */
    public void setReportItemFilter(ReportItemFilter value) {
        this.reportItemFilter = value;
    }

    /**
     * Gets the value of the reportClassFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ReportClassFilter }
     *     
     */
    public ReportClassFilter getReportClassFilter() {
        return reportClassFilter;
    }

    /**
     * Sets the value of the reportClassFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportClassFilter }
     *     
     */
    public void setReportClassFilter(ReportClassFilter value) {
        this.reportClassFilter = value;
    }

    /**
     * Gets the value of the reportModifiedDateRangeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ReportModifiedDateRangeFilter }
     *     
     */
    public ReportModifiedDateRangeFilter getReportModifiedDateRangeFilter() {
        return reportModifiedDateRangeFilter;
    }

    /**
     * Sets the value of the reportModifiedDateRangeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportModifiedDateRangeFilter }
     *     
     */
    public void setReportModifiedDateRangeFilter(ReportModifiedDateRangeFilter value) {
        this.reportModifiedDateRangeFilter = value;
    }

    /**
     * Gets the value of the reportModifiedDateRangeMacro property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportModifiedDateRangeMacro() {
        return reportModifiedDateRangeMacro;
    }

    /**
     * Sets the value of the reportModifiedDateRangeMacro property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportModifiedDateRangeMacro(String value) {
        this.reportModifiedDateRangeMacro = value;
    }

    /**
     * Gets the value of the reportDetailLevelFilter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportDetailLevelFilter() {
        return reportDetailLevelFilter;
    }

    /**
     * Sets the value of the reportDetailLevelFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportDetailLevelFilter(String value) {
        this.reportDetailLevelFilter = value;
    }

    /**
     * Gets the value of the reportPostingStatusFilter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportPostingStatusFilter() {
        return reportPostingStatusFilter;
    }

    /**
     * Sets the value of the reportPostingStatusFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportPostingStatusFilter(String value) {
        this.reportPostingStatusFilter = value;
    }

    /**
     * Gets the value of the summarizeColumnsBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSummarizeColumnsBy() {
        return summarizeColumnsBy;
    }

    /**
     * Sets the value of the summarizeColumnsBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSummarizeColumnsBy(String value) {
        this.summarizeColumnsBy = value;
    }

    /**
     * Gets the value of the includeSubcolumns property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeSubcolumns() {
        return includeSubcolumns;
    }

    /**
     * Sets the value of the includeSubcolumns property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeSubcolumns(String value) {
        this.includeSubcolumns = value;
    }

    /**
     * Gets the value of the reportCalendar property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportCalendar() {
        return reportCalendar;
    }

    /**
     * Sets the value of the reportCalendar property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportCalendar(String value) {
        this.reportCalendar = value;
    }

    /**
     * Gets the value of the returnRows property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnRows() {
        return returnRows;
    }

    /**
     * Sets the value of the returnRows property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnRows(String value) {
        this.returnRows = value;
    }

    /**
     * Gets the value of the returnColumns property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnColumns() {
        return returnColumns;
    }

    /**
     * Sets the value of the returnColumns property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnColumns(String value) {
        this.returnColumns = value;
    }

    /**
     * Gets the value of the requestID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Sets the value of the requestID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestID(String value) {
        this.requestID = value;
    }

}
