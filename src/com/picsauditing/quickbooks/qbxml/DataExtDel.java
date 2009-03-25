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
 *         &lt;choice>
 *           &lt;group ref="{}ListDataExt"/>
 *           &lt;group ref="{}TxnDataExt"/>
 *           &lt;element ref="{}OtherDataExtType"/>
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
    "ownerID",
    "dataExtName",
    "listDataExtType",
    "listObjRef",
    "txnDataExtType",
    "txnID",
    "txnLineID",
    "otherDataExtType"
})
@XmlRootElement(name = "DataExtDel")
public class DataExtDel {

    @XmlElement(name = "OwnerID", required = true)
    protected String ownerID;
    @XmlElement(name = "DataExtName", required = true)
    protected String dataExtName;
    @XmlElement(name = "ListDataExtType")
    protected String listDataExtType;
    @XmlElement(name = "ListObjRef")
    protected ListObjRef listObjRef;
    @XmlElement(name = "TxnDataExtType")
    protected String txnDataExtType;
    @XmlElement(name = "TxnID")
    protected String txnID;
    @XmlElement(name = "TxnLineID")
    protected String txnLineID;
    @XmlElement(name = "OtherDataExtType")
    protected String otherDataExtType;

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
     * Gets the value of the listDataExtType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListDataExtType() {
        return listDataExtType;
    }

    /**
     * Sets the value of the listDataExtType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListDataExtType(String value) {
        this.listDataExtType = value;
    }

    /**
     * Gets the value of the listObjRef property.
     * 
     * @return
     *     possible object is
     *     {@link ListObjRef }
     *     
     */
    public ListObjRef getListObjRef() {
        return listObjRef;
    }

    /**
     * Sets the value of the listObjRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListObjRef }
     *     
     */
    public void setListObjRef(ListObjRef value) {
        this.listObjRef = value;
    }

    /**
     * Gets the value of the txnDataExtType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnDataExtType() {
        return txnDataExtType;
    }

    /**
     * Sets the value of the txnDataExtType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnDataExtType(String value) {
        this.txnDataExtType = value;
    }

    /**
     * Gets the value of the txnID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnID() {
        return txnID;
    }

    /**
     * Sets the value of the txnID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnID(String value) {
        this.txnID = value;
    }

    /**
     * Gets the value of the txnLineID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnLineID() {
        return txnLineID;
    }

    /**
     * Sets the value of the txnLineID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnLineID(String value) {
        this.txnLineID = value;
    }

    /**
     * Gets the value of the otherDataExtType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherDataExtType() {
        return otherDataExtType;
    }

    /**
     * Sets the value of the otherDataExtType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherDataExtType(String value) {
        this.otherDataExtType = value;
    }

}
