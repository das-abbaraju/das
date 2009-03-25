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
 * <p>Java class for ItemDiscountQueryRqType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ItemDiscountQueryRqType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{}ListQueryWithOwnerID"/>
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
@XmlType(name = "ItemDiscountQueryRqType", propOrder = {
    "listID",
    "fullName",
    "maxReturned",
    "activeStatus",
    "fromModifiedDate",
    "toModifiedDate",
    "nameFilter",
    "nameRangeFilter",
    "includeRetElement",
    "ownerID"
})
public class ItemDiscountQueryRqType {

    @XmlElement(name = "ListID")
    protected List<String> listID;
    @XmlElement(name = "FullName")
    protected List<String> fullName;
    @XmlElement(name = "MaxReturned")
    protected BigInteger maxReturned;
    @XmlElement(name = "ActiveStatus", defaultValue = "ActiveOnly")
    protected String activeStatus;
    @XmlElement(name = "FromModifiedDate")
    protected String fromModifiedDate;
    @XmlElement(name = "ToModifiedDate")
    protected String toModifiedDate;
    @XmlElement(name = "NameFilter")
    protected NameFilter nameFilter;
    @XmlElement(name = "NameRangeFilter")
    protected NameRangeFilter nameRangeFilter;
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
     * Gets the value of the listID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getListID() {
        if (listID == null) {
            listID = new ArrayList<String>();
        }
        return this.listID;
    }

    /**
     * Gets the value of the fullName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fullName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFullName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFullName() {
        if (fullName == null) {
            fullName = new ArrayList<String>();
        }
        return this.fullName;
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
     * Gets the value of the activeStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActiveStatus() {
        return activeStatus;
    }

    /**
     * Sets the value of the activeStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActiveStatus(String value) {
        this.activeStatus = value;
    }

    /**
     * Gets the value of the fromModifiedDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromModifiedDate() {
        return fromModifiedDate;
    }

    /**
     * Sets the value of the fromModifiedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromModifiedDate(String value) {
        this.fromModifiedDate = value;
    }

    /**
     * Gets the value of the toModifiedDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToModifiedDate() {
        return toModifiedDate;
    }

    /**
     * Sets the value of the toModifiedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToModifiedDate(String value) {
        this.toModifiedDate = value;
    }

    /**
     * Gets the value of the nameFilter property.
     * 
     * @return
     *     possible object is
     *     {@link NameFilter }
     *     
     */
    public NameFilter getNameFilter() {
        return nameFilter;
    }

    /**
     * Sets the value of the nameFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameFilter }
     *     
     */
    public void setNameFilter(NameFilter value) {
        this.nameFilter = value;
    }

    /**
     * Gets the value of the nameRangeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link NameRangeFilter }
     *     
     */
    public NameRangeFilter getNameRangeFilter() {
        return nameRangeFilter;
    }

    /**
     * Sets the value of the nameRangeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameRangeFilter }
     *     
     */
    public void setNameRangeFilter(NameRangeFilter value) {
        this.nameRangeFilter = value;
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
