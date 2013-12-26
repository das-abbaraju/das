/**
 * AddressVerificationListingWithStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.strikeiron.www.api;

public class AddressVerificationListingWithStatus extends SIWsResultWithStatus implements java.io.Serializable {
    private Listing listing;

    public AddressVerificationListingWithStatus() {
    }

    public AddressVerificationListingWithStatus(
            SIWsStatus itemStatus,
            Listing listing) {
        super(
                itemStatus);
        this.listing = listing;
    }


    /**
     * Gets the listing value for this AddressVerificationListingWithStatus.
     *
     * @return listing
     */
    public Listing getListing() {
        return listing;
    }


    /**
     * Sets the listing value for this AddressVerificationListingWithStatus.
     *
     * @param listing
     */
    public void setListing(Listing listing) {
        this.listing = listing;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof AddressVerificationListingWithStatus)) return false;
        AddressVerificationListingWithStatus other = (AddressVerificationListingWithStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) &&
                ((this.listing == null && other.getListing() == null) ||
                        (this.listing != null &&
                                this.listing.equals(other.getListing())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getListing() != null) {
            _hashCode += getListing().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(AddressVerificationListingWithStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "AddressVerificationListingWithStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listing");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.strikeiron.com", "Listing"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.strikeiron.com", "Listing"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.BeanSerializer(
                        _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.BeanDeserializer(
                        _javaType, _xmlType, typeDesc);
    }

}
