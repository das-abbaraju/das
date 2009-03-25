//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.03.12 at 04:00:19 PM PDT 
//


package com.picsauditing.quickbooks.qbxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}HoursAvailable" minOccurs="0"/>
 *         &lt;element ref="{}AccrualPeriod" minOccurs="0"/>
 *         &lt;element ref="{}HoursAccrued" minOccurs="0"/>
 *         &lt;element ref="{}MaximumHours" minOccurs="0"/>
 *         &lt;element ref="{}IsResettingHoursEachNewYear" minOccurs="0"/>
 *         &lt;element ref="{}HoursUsed" minOccurs="0"/>
 *         &lt;element ref="{}AccrualStartDate" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "hoursAvailable",
    "accrualPeriod",
    "hoursAccrued",
    "maximumHours",
    "isResettingHoursEachNewYear",
    "hoursUsed",
    "accrualStartDate"
})
@XmlRootElement(name = "VacationHours")
public class VacationHours {

    @XmlElement(name = "HoursAvailable")
    protected String hoursAvailable;
    @XmlElement(name = "AccrualPeriod")
    protected String accrualPeriod;
    @XmlElement(name = "HoursAccrued")
    protected String hoursAccrued;
    @XmlElement(name = "MaximumHours")
    protected String maximumHours;
    @XmlElement(name = "IsResettingHoursEachNewYear")
    protected String isResettingHoursEachNewYear;
    @XmlElement(name = "HoursUsed")
    protected String hoursUsed;
    @XmlElement(name = "AccrualStartDate")
    protected String accrualStartDate;

    /**
     * Gets the value of the hoursAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHoursAvailable() {
        return hoursAvailable;
    }

    /**
     * Sets the value of the hoursAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoursAvailable(String value) {
        this.hoursAvailable = value;
    }

    /**
     * Gets the value of the accrualPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccrualPeriod() {
        return accrualPeriod;
    }

    /**
     * Sets the value of the accrualPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccrualPeriod(String value) {
        this.accrualPeriod = value;
    }

    /**
     * Gets the value of the hoursAccrued property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHoursAccrued() {
        return hoursAccrued;
    }

    /**
     * Sets the value of the hoursAccrued property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoursAccrued(String value) {
        this.hoursAccrued = value;
    }

    /**
     * Gets the value of the maximumHours property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaximumHours() {
        return maximumHours;
    }

    /**
     * Sets the value of the maximumHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaximumHours(String value) {
        this.maximumHours = value;
    }

    /**
     * Gets the value of the isResettingHoursEachNewYear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsResettingHoursEachNewYear() {
        return isResettingHoursEachNewYear;
    }

    /**
     * Sets the value of the isResettingHoursEachNewYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsResettingHoursEachNewYear(String value) {
        this.isResettingHoursEachNewYear = value;
    }

    /**
     * Gets the value of the hoursUsed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHoursUsed() {
        return hoursUsed;
    }

    /**
     * Sets the value of the hoursUsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHoursUsed(String value) {
        this.hoursUsed = value;
    }

    /**
     * Gets the value of the accrualStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccrualStartDate() {
        return accrualStartDate;
    }

    /**
     * Sets the value of the accrualStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccrualStartDate(String value) {
        this.accrualStartDate = value;
    }

}
