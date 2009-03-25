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
 *         &lt;group ref="{}ListCoreMod"/>
 *         &lt;element name="Name" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="31"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}IsActive" minOccurs="0"/>
 *         &lt;element ref="{}IsUsedOnPurchaseTransaction" minOccurs="0"/>
 *         &lt;element name="ItemDesc" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="4095"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}TaxRate" minOccurs="0"/>
 *         &lt;element ref="{}TaxVendorRef" minOccurs="0"/>
 *         &lt;element ref="{}SalesTaxReturnLineNumber" minOccurs="0"/>
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
    "listID",
    "editSequence",
    "name",
    "isActive",
    "isUsedOnPurchaseTransaction",
    "itemDesc",
    "taxRate",
    "taxVendorRef",
    "salesTaxReturnLineNumber"
})
@XmlRootElement(name = "ItemSalesTaxMod")
public class ItemSalesTaxMod {

    @XmlElement(name = "ListID", required = true)
    protected String listID;
    @XmlElement(name = "EditSequence", required = true)
    protected String editSequence;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "IsActive")
    protected String isActive;
    @XmlElement(name = "IsUsedOnPurchaseTransaction")
    protected String isUsedOnPurchaseTransaction;
    @XmlElement(name = "ItemDesc")
    protected String itemDesc;
    @XmlElement(name = "TaxRate")
    protected String taxRate;
    @XmlElement(name = "TaxVendorRef")
    protected TaxVendorRef taxVendorRef;
    @XmlElement(name = "SalesTaxReturnLineNumber")
    protected String salesTaxReturnLineNumber;

    /**
     * Gets the value of the listID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListID() {
        return listID;
    }

    /**
     * Sets the value of the listID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListID(String value) {
        this.listID = value;
    }

    /**
     * Gets the value of the editSequence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEditSequence() {
        return editSequence;
    }

    /**
     * Sets the value of the editSequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEditSequence(String value) {
        this.editSequence = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsActive(String value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the isUsedOnPurchaseTransaction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsUsedOnPurchaseTransaction() {
        return isUsedOnPurchaseTransaction;
    }

    /**
     * Sets the value of the isUsedOnPurchaseTransaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsUsedOnPurchaseTransaction(String value) {
        this.isUsedOnPurchaseTransaction = value;
    }

    /**
     * Gets the value of the itemDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemDesc() {
        return itemDesc;
    }

    /**
     * Sets the value of the itemDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemDesc(String value) {
        this.itemDesc = value;
    }

    /**
     * Gets the value of the taxRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxRate() {
        return taxRate;
    }

    /**
     * Sets the value of the taxRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxRate(String value) {
        this.taxRate = value;
    }

    /**
     * Gets the value of the taxVendorRef property.
     * 
     * @return
     *     possible object is
     *     {@link TaxVendorRef }
     *     
     */
    public TaxVendorRef getTaxVendorRef() {
        return taxVendorRef;
    }

    /**
     * Sets the value of the taxVendorRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxVendorRef }
     *     
     */
    public void setTaxVendorRef(TaxVendorRef value) {
        this.taxVendorRef = value;
    }

    /**
     * Gets the value of the salesTaxReturnLineNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalesTaxReturnLineNumber() {
        return salesTaxReturnLineNumber;
    }

    /**
     * Sets the value of the salesTaxReturnLineNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalesTaxReturnLineNumber(String value) {
        this.salesTaxReturnLineNumber = value;
    }

}
