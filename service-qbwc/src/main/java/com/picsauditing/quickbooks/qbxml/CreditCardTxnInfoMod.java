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
 *         &lt;element ref="{}CreditCardTxnInputInfoMod" minOccurs="0"/>
 *         &lt;element ref="{}CreditCardTxnResultInfoMod" minOccurs="0"/>
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
    "creditCardTxnInputInfoMod",
    "creditCardTxnResultInfoMod"
})
@XmlRootElement(name = "CreditCardTxnInfoMod")
public class CreditCardTxnInfoMod {

    @XmlElement(name = "CreditCardTxnInputInfoMod")
    protected CreditCardTxnInputInfoMod creditCardTxnInputInfoMod;
    @XmlElement(name = "CreditCardTxnResultInfoMod")
    protected CreditCardTxnResultInfoMod creditCardTxnResultInfoMod;

    /**
     * Gets the value of the creditCardTxnInputInfoMod property.
     * 
     * @return
     *     possible object is
     *     {@link CreditCardTxnInputInfoMod }
     *     
     */
    public CreditCardTxnInputInfoMod getCreditCardTxnInputInfoMod() {
        return creditCardTxnInputInfoMod;
    }

    /**
     * Sets the value of the creditCardTxnInputInfoMod property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditCardTxnInputInfoMod }
     *     
     */
    public void setCreditCardTxnInputInfoMod(CreditCardTxnInputInfoMod value) {
        this.creditCardTxnInputInfoMod = value;
    }

    /**
     * Gets the value of the creditCardTxnResultInfoMod property.
     * 
     * @return
     *     possible object is
     *     {@link CreditCardTxnResultInfoMod }
     *     
     */
    public CreditCardTxnResultInfoMod getCreditCardTxnResultInfoMod() {
        return creditCardTxnResultInfoMod;
    }

    /**
     * Sets the value of the creditCardTxnResultInfoMod property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditCardTxnResultInfoMod }
     *     
     */
    public void setCreditCardTxnResultInfoMod(CreditCardTxnResultInfoMod value) {
        this.creditCardTxnResultInfoMod = value;
    }

}