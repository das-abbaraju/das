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
 *         &lt;element ref="{}OwnerID"/>
 *         &lt;element name="DataExtName">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="31"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}DataExtValue"/>
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
    "ownerID",
    "dataExtName",
    "dataExtValue"
})
@XmlRootElement(name = "DataExt")
public class DataExt {

    @XmlElement(name = "OwnerID", required = true)
    protected String ownerID;
    @XmlElement(name = "DataExtName", required = true)
    protected String dataExtName;
    @XmlElement(name = "DataExtValue", required = true)
    protected String dataExtValue;

    /**
     * Gets the value of the ownerID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnerID() {
        return ownerID;
    }

    /**
     * Sets the value of the ownerID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnerID(String value) {
        this.ownerID = value;
    }

    /**
     * Gets the value of the dataExtName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataExtName() {
        return dataExtName;
    }

    /**
     * Sets the value of the dataExtName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataExtName(String value) {
        this.dataExtName = value;
    }

    /**
     * Gets the value of the dataExtValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataExtValue() {
        return dataExtValue;
    }

    /**
     * Sets the value of the dataExtValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataExtValue(String value) {
        this.dataExtValue = value;
    }

}
