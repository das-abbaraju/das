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
 *         &lt;group ref="{}ListCore"/>
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
 *         &lt;element ref="{}DataExtRet" maxOccurs="unbounded" minOccurs="0"/>
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
    "timeCreated",
    "timeModified",
    "editSequence",
    "name",
    "isActive",
    "isUsedOnPurchaseTransaction",
    "itemDesc",
    "taxRate",
    "taxVendorRef",
    "salesTaxReturnLineNumber",
    "dataExtRet"
})
@XmlRootElement(name = "ItemSalesTaxRet")
public class ItemSalesTaxRet {

    @XmlElement(name = "ListID")
    protected String listID;
    @XmlElement(name = "TimeCreated")
    protected String timeCreated;
    @XmlElement(name = "TimeModified")
    protected String timeModified;
    @XmlElement(name = "EditSequence")
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
    @XmlElement(name = "DataExtRet")
    protected List<DataExtRet> dataExtRet;

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
     * Gets the value of the timeCreated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeCreated() {
        return timeCreated;
    }

    /**
     * Sets the value of the timeCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeCreated(String value) {
        this.timeCreated = value;
    }

    /**
     * Gets the value of the timeModified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeModified() {
        return timeModified;
    }

    /**
     * Sets the value of the timeModified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeModified(String value) {
        this.timeModified = value;
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

    /**
     * Gets the value of the dataExtRet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataExtRet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataExtRet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataExtRet }
     * 
     * 
     */
    public List<DataExtRet> getDataExtRet() {
        if (dataExtRet == null) {
            dataExtRet = new ArrayList<DataExtRet>();
        }
        return this.dataExtRet;
    }

}
