//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.03.12 at 04:00:19 PM PDT 
//


package com.picsauditing.quickbooks.qbxml;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VendorQueryRsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VendorQueryRsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}VendorRet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="requestID" type="{}STRTYPE" />
 *       &lt;attribute name="statusCode" use="required" type="{}INTTYPE" />
 *       &lt;attribute name="statusSeverity" use="required" type="{}STRTYPE" />
 *       &lt;attribute name="statusMessage" type="{}STRTYPE" />
 *       &lt;attribute name="retCount" type="{}INTTYPE" />
 *       &lt;attribute name="iteratorRemainingCount" type="{}INTTYPE" />
 *       &lt;attribute name="iteratorID" type="{}STRTYPE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VendorQueryRsType", propOrder = {
    "vendorRet"
})
public class VendorQueryRsType {

    @XmlElement(name = "VendorRet")
    protected List<VendorRet> vendorRet;
    @XmlAttribute
    protected String requestID;
    @XmlAttribute(required = true)
    protected BigInteger statusCode;
    @XmlAttribute(required = true)
    protected String statusSeverity;
    @XmlAttribute
    protected String statusMessage;
    @XmlAttribute
    protected BigInteger retCount;
    @XmlAttribute
    protected BigInteger iteratorRemainingCount;
    @XmlAttribute
    protected String iteratorID;

    /**
     * Gets the value of the vendorRet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vendorRet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVendorRet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VendorRet }
     * 
     * 
     */
    public List<VendorRet> getVendorRet() {
        if (vendorRet == null) {
            vendorRet = new ArrayList<VendorRet>();
        }
        return this.vendorRet;
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

    /**
     * Gets the value of the statusCode property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the value of the statusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStatusCode(BigInteger value) {
        this.statusCode = value;
    }

    /**
     * Gets the value of the statusSeverity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusSeverity() {
        return statusSeverity;
    }

    /**
     * Sets the value of the statusSeverity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusSeverity(String value) {
        this.statusSeverity = value;
    }

    /**
     * Gets the value of the statusMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the value of the statusMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusMessage(String value) {
        this.statusMessage = value;
    }

    /**
     * Gets the value of the retCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRetCount() {
        return retCount;
    }

    /**
     * Sets the value of the retCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRetCount(BigInteger value) {
        this.retCount = value;
    }

    /**
     * Gets the value of the iteratorRemainingCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIteratorRemainingCount() {
        return iteratorRemainingCount;
    }

    /**
     * Sets the value of the iteratorRemainingCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIteratorRemainingCount(BigInteger value) {
        this.iteratorRemainingCount = value;
    }

    /**
     * Gets the value of the iteratorID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIteratorID() {
        return iteratorID;
    }

    /**
     * Sets the value of the iteratorID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIteratorID(String value) {
        this.iteratorID = value;
    }

}
