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
 *         &lt;choice>
 *           &lt;group ref="{}TxnDateFilter"/>
 *           &lt;element ref="{}DateMacro"/>
 *         &lt;/choice>
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
    "fromTxnDate",
    "toTxnDate",
    "dateMacro"
})
@XmlRootElement(name = "TxnDateRangeFilter")
public class TxnDateRangeFilter {

    @XmlElement(name = "FromTxnDate")
    protected String fromTxnDate;
    @XmlElement(name = "ToTxnDate")
    protected String toTxnDate;
    @XmlElement(name = "DateMacro")
    protected String dateMacro;

    /**
     * Gets the value of the fromTxnDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromTxnDate() {
        return fromTxnDate;
    }

    /**
     * Sets the value of the fromTxnDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromTxnDate(String value) {
        this.fromTxnDate = value;
    }

    /**
     * Gets the value of the toTxnDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToTxnDate() {
        return toTxnDate;
    }

    /**
     * Sets the value of the toTxnDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToTxnDate(String value) {
        this.toTxnDate = value;
    }

    /**
     * Gets the value of the dateMacro property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateMacro() {
        return dateMacro;
    }

    /**
     * Sets the value of the dateMacro property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateMacro(String value) {
        this.dateMacro = value;
    }

}
