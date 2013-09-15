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
 *         &lt;element ref="{}ItemInventoryAssemblyRef"/>
 *         &lt;element ref="{}TxnDate" minOccurs="0"/>
 *         &lt;element name="RefNumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="11"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Memo" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="4095"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}QuantityToBuild"/>
 *         &lt;element ref="{}MarkPendingIfRequired" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="defMacro" type="{}MACROTYPE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "itemInventoryAssemblyRef",
    "txnDate",
    "refNumber",
    "memo",
    "quantityToBuild",
    "markPendingIfRequired"
})
@XmlRootElement(name = "BuildAssemblyAdd")
public class BuildAssemblyAdd {

    @XmlElement(name = "ItemInventoryAssemblyRef", required = true)
    protected ItemInventoryAssemblyRef itemInventoryAssemblyRef;
    @XmlElement(name = "TxnDate")
    protected String txnDate;
    @XmlElement(name = "RefNumber")
    protected String refNumber;
    @XmlElement(name = "Memo")
    protected String memo;
    @XmlElement(name = "QuantityToBuild", required = true)
    protected String quantityToBuild;
    @XmlElement(name = "MarkPendingIfRequired")
    protected String markPendingIfRequired;
    @XmlAttribute
    protected String defMacro;

    /**
     * Gets the value of the itemInventoryAssemblyRef property.
     * 
     * @return
     *     possible object is
     *     {@link ItemInventoryAssemblyRef }
     *     
     */
    public ItemInventoryAssemblyRef getItemInventoryAssemblyRef() {
        return itemInventoryAssemblyRef;
    }

    /**
     * Sets the value of the itemInventoryAssemblyRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemInventoryAssemblyRef }
     *     
     */
    public void setItemInventoryAssemblyRef(ItemInventoryAssemblyRef value) {
        this.itemInventoryAssemblyRef = value;
    }

    /**
     * Gets the value of the txnDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnDate() {
        return txnDate;
    }

    /**
     * Sets the value of the txnDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnDate(String value) {
        this.txnDate = value;
    }

    /**
     * Gets the value of the refNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefNumber() {
        return refNumber;
    }

    /**
     * Sets the value of the refNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefNumber(String value) {
        this.refNumber = value;
    }

    /**
     * Gets the value of the memo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMemo() {
        return memo;
    }

    /**
     * Sets the value of the memo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMemo(String value) {
        this.memo = value;
    }

    /**
     * Gets the value of the quantityToBuild property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuantityToBuild() {
        return quantityToBuild;
    }

    /**
     * Sets the value of the quantityToBuild property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuantityToBuild(String value) {
        this.quantityToBuild = value;
    }

    /**
     * Gets the value of the markPendingIfRequired property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMarkPendingIfRequired() {
        return markPendingIfRequired;
    }

    /**
     * Sets the value of the markPendingIfRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarkPendingIfRequired(String value) {
        this.markPendingIfRequired = value;
    }

    /**
     * Gets the value of the defMacro property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefMacro() {
        return defMacro;
    }

    /**
     * Sets the value of the defMacro property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefMacro(String value) {
        this.defMacro = value;
    }

}