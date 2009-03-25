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
 *         &lt;element ref="{}ItemRef"/>
 *         &lt;choice>
 *           &lt;group ref="{}ORCustomPrice"/>
 *           &lt;group ref="{}PriceLevelAdjustment"/>
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
    "itemRef",
    "customPrice",
    "customPricePercent",
    "adjustPercentage",
    "adjustRelativeTo"
})
@XmlRootElement(name = "PriceLevelPerItem")
public class PriceLevelPerItem {

    @XmlElement(name = "ItemRef", required = true)
    protected ItemRef itemRef;
    @XmlElement(name = "CustomPrice")
    protected String customPrice;
    @XmlElement(name = "CustomPricePercent")
    protected String customPricePercent;
    @XmlElement(name = "AdjustPercentage")
    protected String adjustPercentage;
    @XmlElement(name = "AdjustRelativeTo")
    protected String adjustRelativeTo;

    /**
     * Gets the value of the itemRef property.
     * 
     * @return
     *     possible object is
     *     {@link ItemRef }
     *     
     */
    public ItemRef getItemRef() {
        return itemRef;
    }

    /**
     * Sets the value of the itemRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemRef }
     *     
     */
    public void setItemRef(ItemRef value) {
        this.itemRef = value;
    }

    /**
     * Gets the value of the customPrice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomPrice() {
        return customPrice;
    }

    /**
     * Sets the value of the customPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomPrice(String value) {
        this.customPrice = value;
    }

    /**
     * Gets the value of the customPricePercent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomPricePercent() {
        return customPricePercent;
    }

    /**
     * Sets the value of the customPricePercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomPricePercent(String value) {
        this.customPricePercent = value;
    }

    /**
     * Gets the value of the adjustPercentage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdjustPercentage() {
        return adjustPercentage;
    }

    /**
     * Sets the value of the adjustPercentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdjustPercentage(String value) {
        this.adjustPercentage = value;
    }

    /**
     * Gets the value of the adjustRelativeTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdjustRelativeTo() {
        return adjustRelativeTo;
    }

    /**
     * Sets the value of the adjustRelativeTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdjustRelativeTo(String value) {
        this.adjustRelativeTo = value;
    }

}
