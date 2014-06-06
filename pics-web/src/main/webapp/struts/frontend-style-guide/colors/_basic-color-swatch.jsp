<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="heading">${param.heading}</s:set>
<s:set var="class_prefix">${param.class_prefix}</s:set>
<s:set var="tint_hex">${param.tint_hex}</s:set>
<s:set var="light_hex">${param.light_hex}</s:set>
<s:set var="med_hex">${param.med_hex}</s:set>
<s:set var="dark_hex">${param.dark_hex}</s:set>

<section class="swatch">
    <h1>${heading}</h1>
    <div class="row swatches">
        <div class="col-md-1 ${class_prefix}-tint">
            <dl>
                <dt>tint</dt>
                <dd>${tint_hex}</dd>
            </dl>
        </div>
        <div class="col-md-1 ${class_prefix}-light">
            <dl>
                <dt>light</dt>
                <dd>${light_hex}</dd>
            </dl>
        </div>
        <div class="col-md-1 ${class_prefix}-med">
            <dl>
                <dt>med</dt>
                <dd>${med_hex}</dd>
            </dl>
        </div>
        <div class="col-md-1 ${class_prefix}-dark">
            <dl>
                <dt>dark</dt>
                <dd>${dark_hex}</dd>
            </dl>
        </div>
    </div>
</section>