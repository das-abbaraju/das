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
 * <p>Java class for ARRefundCreditCardQueryRqType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ARRefundCreditCardQueryRqType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{}TxnQuery"/>
 *       &lt;/sequence>
 *       &lt;attribute name="requestID" type="{}STRTYPE" />
 *       &lt;attribute name="metaData" default="NoMetaData">
 *         &lt;simpleType>
 *           &lt;restriction base="{}STRTYPE">
 *             &lt;enumeration value="NoMetaData"/>
 *             &lt;enumeration value="MetaDataOnly"/>
 *             &lt;enumeration value="MetaDataAndResponseData"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="iterator">
 *         &lt;simpleType>
 *           &lt;restriction base="{}STRTYPE">
 *             &lt;enumeration value="Start"/>
 *             &lt;enumeration value="Continue"/>
 *             &lt;enumeration value="Stop"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="iteratorID" type="{}STRTYPE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ARRefundCreditCardQueryRqType", propOrder = {
    "txnID",
    "refNumber",
    "refNumberCaseSensitive",
    "maxReturned",
    "modifiedDateRangeFilter",
    "txnDateRangeFilter",
    "entityFilter",
    "accountFilter",
    "refNumberFilter",
    "refNumberRangeFilter",
    "includeLineItems",
    "includeRetElement",
    "ownerID"
})
public class ARRefundCreditCardQueryRqType {

    @XmlElement(name = "TxnID")
    protected List<String> txnID;
    @XmlElement(name = "RefNumber")
    protected List<String> refNumber;
    @XmlElement(name = "RefNumberCaseSensitive")
    protected List<String> refNumberCaseSensitive;
    @XmlElement(name = "MaxReturned")
    protected BigInteger maxReturned;
    @XmlElement(name = "ModifiedDateRangeFilter")
    protected ModifiedDateRangeFilter modifiedDateRangeFilter;
    @XmlElement(name = "TxnDateRangeFilter")
    protected TxnDateRangeFilter txnDateRangeFilter;
    @XmlElement(name = "EntityFilter")
    protected EntityFilter entityFilter;
    @XmlElement(name = "AccountFilter")
    protected AccountFilter accountFilter;
    @XmlElement(name = "RefNumberFilter")
    protected RefNumberFilter refNumberFilter;
    @XmlElement(name = "RefNumberRangeFilter")
    protected RefNumberRangeFilter refNumberRangeFilter;
    @XmlElement(name = "IncludeLineItems")
    protected String includeLineItems;
    @XmlElement(name = "IncludeRetElement")
    protected List<String> includeRetElement;
    @XmlElement(name = "OwnerID")
    protected List<String> ownerID;
    @XmlAttribute
    protected String requestID;
    @XmlAttribute
    protected String metaData;
    @XmlAttribute
    protected String iterator;
    @XmlAttribute
    protected String iteratorID;

    /**
     * Gets the value of the txnID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the txnID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTxnID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTxnID() {
        if (txnID == null) {
            txnID = new ArrayList<String>();
        }
        return this.txnID;
    }

    /**
     * Gets the value of the refNumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the refNumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRefNumber().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRefNumber() {
        if (refNumber == null) {
            refNumber = new ArrayList<String>();
        }
        return this.refNumber;
    }

    /**
     * Gets the value of the refNumberCaseSensitive property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the refNumberCaseSensitive property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRefNumberCaseSensitive().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRefNumberCaseSensitive() {
        if (refNumberCaseSensitive == null) {
            refNumberCaseSensitive = new ArrayList<String>();
        }
        return this.refNumberCaseSensitive;
    }

    /**
     * Gets the value of the maxReturned property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxReturned() {
        return maxReturned;
    }

    /**
     * Sets the value of the maxReturned property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxReturned(BigInteger value) {
        this.maxReturned = value;
    }

    /**
     * Gets the value of the modifiedDateRangeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ModifiedDateRangeFilter }
     *     
     */
    public ModifiedDateRangeFilter getModifiedDateRangeFilter() {
        return modifiedDateRangeFilter;
    }

    /**
     * Sets the value of the modifiedDateRangeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModifiedDateRangeFilter }
     *     
     */
    public void setModifiedDateRangeFilter(ModifiedDateRangeFilter value) {
        this.modifiedDateRangeFilter = value;
    }

    /**
     * Gets the value of the txnDateRangeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link TxnDateRangeFilter }
     *     
     */
    public TxnDateRangeFilter getTxnDateRangeFilter() {
        return txnDateRangeFilter;
    }

    /**
     * Sets the value of the txnDateRangeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link TxnDateRangeFilter }
     *     
     */
    public void setTxnDateRangeFilter(TxnDateRangeFilter value) {
        this.txnDateRangeFilter = value;
    }

    /**
     * Gets the value of the entityFilter property.
     * 
     * @return
     *     possible object is
     *     {@link EntityFilter }
     *     
     */
    public EntityFilter getEntityFilter() {
        return entityFilter;
    }

    /**
     * Sets the value of the entityFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityFilter }
     *     
     */
    public void setEntityFilter(EntityFilter value) {
        this.entityFilter = value;
    }

    /**
     * Gets the value of the accountFilter property.
     * 
     * @return
     *     possible object is
     *     {@link AccountFilter }
     *     
     */
    public AccountFilter getAccountFilter() {
        return accountFilter;
    }

    /**
     * Sets the value of the accountFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountFilter }
     *     
     */
    public void setAccountFilter(AccountFilter value) {
        this.accountFilter = value;
    }

    /**
     * Gets the value of the refNumberFilter property.
     * 
     * @return
     *     possible object is
     *     {@link RefNumberFilter }
     *     
     */
    public RefNumberFilter getRefNumberFilter() {
        return refNumberFilter;
    }

    /**
     * Sets the value of the refNumberFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link RefNumberFilter }
     *     
     */
    public void setRefNumberFilter(RefNumberFilter value) {
        this.refNumberFilter = value;
    }

    /**
     * Gets the value of the refNumberRangeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link RefNumberRangeFilter }
     *     
     */
    public RefNumberRangeFilter getRefNumberRangeFilter() {
        return refNumberRangeFilter;
    }

    /**
     * Sets the value of the refNumberRangeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link RefNumberRangeFilter }
     *     
     */
    public void setRefNumberRangeFilter(RefNumberRangeFilter value) {
        this.refNumberRangeFilter = value;
    }

    /**
     * Gets the value of the includeLineItems property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeLineItems() {
        return includeLineItems;
    }

    /**
     * Sets the value of the includeLineItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeLineItems(String value) {
        this.includeLineItems = value;
    }

    /**
     * Gets the value of the includeRetElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includeRetElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludeRetElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIncludeRetElement() {
        if (includeRetElement == null) {
            includeRetElement = new ArrayList<String>();
        }
        return this.includeRetElement;
    }

    /**
     * Gets the value of the ownerID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ownerID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOwnerID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getOwnerID() {
        if (ownerID == null) {
            ownerID = new ArrayList<String>();
        }
        return this.ownerID;
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
     * Gets the value of the metaData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetaData() {
        if (metaData == null) {
            return "NoMetaData";
        } else {
            return metaData;
        }
    }

    /**
     * Sets the value of the metaData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetaData(String value) {
        this.metaData = value;
    }

    /**
     * Gets the value of the iterator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIterator() {
        return iterator;
    }

    /**
     * Sets the value of the iterator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIterator(String value) {
        this.iterator = value;
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
