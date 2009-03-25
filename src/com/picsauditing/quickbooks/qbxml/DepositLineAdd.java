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
 *         &lt;choice>
 *           &lt;group ref="{}PaymentLine"/>
 *           &lt;group ref="{}DepositInfo"/>
 *         &lt;/choice>
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
    "paymentTxnID",
    "paymentTxnLineID",
    "entityRef",
    "accountRef",
    "memo",
    "checkNumber",
    "paymentMethodRef",
    "classRef",
    "amount"
})
@XmlRootElement(name = "DepositLineAdd")
public class DepositLineAdd {

    @XmlElement(name = "PaymentTxnID")
    protected com.picsauditing.quickbooks.qbxml.DepositLineMod.PaymentTxnID paymentTxnID;
    @XmlElement(name = "PaymentTxnLineID")
    protected com.picsauditing.quickbooks.qbxml.DepositLineMod.PaymentTxnLineID paymentTxnLineID;
    @XmlElement(name = "EntityRef")
    protected EntityRef entityRef;
    @XmlElement(name = "AccountRef")
    protected AccountRef accountRef;
    @XmlElement(name = "Memo")
    protected String memo;
    @XmlElement(name = "CheckNumber")
    protected String checkNumber;
    @XmlElement(name = "PaymentMethodRef")
    protected PaymentMethodRef paymentMethodRef;
    @XmlElement(name = "ClassRef")
    protected ClassRef classRef;
    @XmlElement(name = "Amount")
    protected String amount;
    @XmlAttribute
    protected String defMacro;

    /**
     * Gets the value of the paymentTxnID property.
     * 
     * @return
     *     possible object is
     *     {@link com.picsauditing.quickbooks.qbxml.DepositLineMod.PaymentTxnID }
     *     
     */
    public com.picsauditing.quickbooks.qbxml.DepositLineMod.PaymentTxnID getPaymentTxnID() {
        return paymentTxnID;
    }

    /**
     * Sets the value of the paymentTxnID property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.picsauditing.quickbooks.qbxml.DepositLineMod.PaymentTxnID }
     *     
     */
    public void setPaymentTxnID(com.picsauditing.quickbooks.qbxml.DepositLineMod.PaymentTxnID value) {
        this.paymentTxnID = value;
    }

    /**
     * Gets the value of the paymentTxnLineID property.
     * 
     * @return
     *     possible object is
     *     {@link com.picsauditing.quickbooks.qbxml.DepositLineMod.PaymentTxnLineID }
     *     
     */
    public com.picsauditing.quickbooks.qbxml.DepositLineMod.PaymentTxnLineID getPaymentTxnLineID() {
        return paymentTxnLineID;
    }

    /**
     * Sets the value of the paymentTxnLineID property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.picsauditing.quickbooks.qbxml.DepositLineMod.PaymentTxnLineID }
     *     
     */
    public void setPaymentTxnLineID(com.picsauditing.quickbooks.qbxml.DepositLineMod.PaymentTxnLineID value) {
        this.paymentTxnLineID = value;
    }

    /**
     * Gets the value of the entityRef property.
     * 
     * @return
     *     possible object is
     *     {@link EntityRef }
     *     
     */
    public EntityRef getEntityRef() {
        return entityRef;
    }

    /**
     * Sets the value of the entityRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityRef }
     *     
     */
    public void setEntityRef(EntityRef value) {
        this.entityRef = value;
    }

    /**
     * Gets the value of the accountRef property.
     * 
     * @return
     *     possible object is
     *     {@link AccountRef }
     *     
     */
    public AccountRef getAccountRef() {
        return accountRef;
    }

    /**
     * Sets the value of the accountRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountRef }
     *     
     */
    public void setAccountRef(AccountRef value) {
        this.accountRef = value;
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
     * Gets the value of the checkNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckNumber() {
        return checkNumber;
    }

    /**
     * Sets the value of the checkNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckNumber(String value) {
        this.checkNumber = value;
    }

    /**
     * Gets the value of the paymentMethodRef property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentMethodRef }
     *     
     */
    public PaymentMethodRef getPaymentMethodRef() {
        return paymentMethodRef;
    }

    /**
     * Sets the value of the paymentMethodRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentMethodRef }
     *     
     */
    public void setPaymentMethodRef(PaymentMethodRef value) {
        this.paymentMethodRef = value;
    }

    /**
     * Gets the value of the classRef property.
     * 
     * @return
     *     possible object is
     *     {@link ClassRef }
     *     
     */
    public ClassRef getClassRef() {
        return classRef;
    }

    /**
     * Sets the value of the classRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassRef }
     *     
     */
    public void setClassRef(ClassRef value) {
        this.classRef = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmount(String value) {
        this.amount = value;
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
