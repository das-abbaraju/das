//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.03.12 at 04:00:19 PM PDT 
//


package com.picsauditing.quickbooks.qbxml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AgingReportQueryRqType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AgingReportQueryRqType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{}AgingReportQuery"/>
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
@XmlType(name = "AgingReportQueryRqType", propOrder = {
    "agingReportType",
    "displayReport",
    "reportPeriod",
    "reportDateMacro",
    "reportAccountFilter",
    "reportEntityFilter",
    "reportItemFilter",
    "reportClassFilter",
    "reportTxnTypeFilter",
    "reportModifiedDateRangeFilter",
    "reportModifiedDateRangeMacro",
    "reportDetailLevelFilter",
    "reportPostingStatusFilter",
    "includeColumn",
    "includeAccounts",
    "reportAgingAsOf"
})
public class AgingReportQueryRqType {

    @XmlElement(name = "AgingReportType", required = true)
    protected String agingReportType;
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
    @XmlElement(name = "ReportTxnTypeFilter")
    protected ReportTxnTypeFilter reportTxnTypeFilter;
    @XmlElement(name = "ReportModifiedDateRangeFilter")
    protected ReportModifiedDateRangeFilter reportModifiedDateRangeFilter;
    @XmlElement(name = "ReportModifiedDateRangeMacro")
    protected String reportModifiedDateRangeMacro;
    @XmlElement(name = "ReportDetailLevelFilter", defaultValue = "All")
    protected String reportDetailLevelFilter;
    @XmlElement(name = "ReportPostingStatusFilter")
    protected String reportPostingStatusFilter;
    @XmlElement(name = "IncludeColumn")
    protected List<String> includeColumn;
    @XmlElement(name = "IncludeAccounts")
    protected String includeAccounts;
    @XmlElement(name = "ReportAgingAsOf", defaultValue = "ReportEndDate")
    protected String reportAgingAsOf;
    @XmlAttribute
    protected String requestID;

    /**
     * Gets the value of the agingReportType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgingReportType() {
        return agingReportType;
    }

    /**
     * Sets the value of the agingReportType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgingReportType(String value) {
        this.agingReportType = value;
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
     * Gets the value of the reportTxnTypeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ReportTxnTypeFilter }
     *     
     */
    public ReportTxnTypeFilter getReportTxnTypeFilter() {
        return reportTxnTypeFilter;
    }

    /**
     * Sets the value of the reportTxnTypeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportTxnTypeFilter }
     *     
     */
    public void setReportTxnTypeFilter(ReportTxnTypeFilter value) {
        this.reportTxnTypeFilter = value;
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
     * Gets the value of the includeColumn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includeColumn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludeColumn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIncludeColumn() {
        if (includeColumn == null) {
            includeColumn = new ArrayList<String>();
        }
        return this.includeColumn;
    }

    /**
     * Gets the value of the includeAccounts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeAccounts() {
        return includeAccounts;
    }

    /**
     * Sets the value of the includeAccounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeAccounts(String value) {
        this.includeAccounts = value;
    }

    /**
     * Gets the value of the reportAgingAsOf property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportAgingAsOf() {
        return reportAgingAsOf;
    }

    /**
     * Sets the value of the reportAgingAsOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportAgingAsOf(String value) {
        this.reportAgingAsOf = value;
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
