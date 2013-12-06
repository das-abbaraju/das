/**
 * ParsedInputType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.AddressDoctor.validator2.addBatch.Batch;

public class ParsedInputType implements java.io.Serializable {
    private String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected ParsedInputType(String value) {
        _value_ = value;
        _table_.put(_value_, this);
    }

    public static final String _NEVER = "NEVER";
    public static final String _ONLY_FOR_P = "ONLY_FOR_P";
    public static final String _ONLY_PARSED = "ONLY_PARSED";
    public static final ParsedInputType NEVER = new ParsedInputType(_NEVER);
    public static final ParsedInputType ONLY_FOR_P = new ParsedInputType(_ONLY_FOR_P);
    public static final ParsedInputType ONLY_PARSED = new ParsedInputType(_ONLY_PARSED);

    public String getValue() {
        return _value_;
    }

    public static ParsedInputType fromValue(String value)
            throws IllegalArgumentException {
        ParsedInputType enumeration = (ParsedInputType)
                _table_.get(value);
        if (enumeration == null) throw new IllegalArgumentException();
        return enumeration;
    }

    public static ParsedInputType fromString(String value)
            throws IllegalArgumentException {
        return fromValue(value);
    }

    public boolean equals(Object obj) {
        return (obj == this);
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        return _value_;
    }

    public Object readResolve() throws java.io.ObjectStreamException {
        return fromValue(_value_);
    }

    public static org.apache.axis.encoding.Serializer getSerializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.EnumSerializer(
                        _javaType, _xmlType);
    }

    public static org.apache.axis.encoding.Deserializer getDeserializer(
            String mechType,
            Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return
                new org.apache.axis.encoding.ser.EnumDeserializer(
                        _javaType, _xmlType);
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(ParsedInputType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "ParsedInputType"));
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
