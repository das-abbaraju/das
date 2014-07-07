<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<section class="swatch">
    <h1>Default</h1>
    <div class="row">
        <div class="col-md-1 default-platinum">
            <dl>
                <dt>platinum</dt>
                <dd>f1f1f2</dd>
            </dl>
        </div>
        <div class="col-md-1 default-mercury">
            <dl>
                <dt>mercury</dt>
                <dd>e6e6e6</dd>
            </dl>
        </div>
        <div class="col-md-1 default-silver">
            <dl>
                <dt>silver</dt>
                <dd>cccccc</dd>
            </dl>
        </div>
        <div class="col-md-1 default-pewter">
            <dl>
                <dt>pewter</dt>
                <dd>b3b3b3</dd>
            </dl>
        </div>
        <div class="col-md-1 default-aluminum">
            <dl>
                <dt>aluminum</dt>
                <dd>999999</dd>
            </dl>
        </div>
        <div class="col-md-1 default-nickel">
            <dl>
                <dt>nickel</dt>
                <dd>808285</dd>
            </dl>
        </div>
        <div class="col-md-1 default-tin">
            <dl>
                <dt>tin</dt>
                <dd>666666</dd>
            </dl>
        </div>
        <div class="col-md-1 default-steel">
            <dl>
                <dt>steel</dt>
                <dd>454545</dd>
            </dl>
        </div>
        <div class="col-md-1 default-iron">
            <dl>
                <dt>iron</dt>
                <dd>333333</dd>
            </dl>
        </div>
        <div class="col-md-1 default-tungsten">
            <dl>
                <dt>tungsten</dt>
                <dd>252525</dd>
            </dl>
        </div>
        <div class="col-md-1 default-lead">
            <dl>
                <dt>lead</dt>
                <dd>1a1a1a</dd>
            </dl>
        </div>
    </div>
</section>

<s:include value="/struts/frontend-style-guide/colors/_basic-color-swatch.jsp">
    <s:param name="heading">Primary</s:param>
    <s:param name="class_prefix">primary</s:param>
    <s:param name="tint_hex">d6eaf8</s:param>
    <s:param name="light_hex">3498db</s:param>
    <s:param name="med_hex">2980b9</s:param>
    <s:param name="dark_hex">105093</s:param>
</s:include>

<s:include value="_basic-color-swatch.jsp">
    <s:param name="heading">Info</s:param>
    <s:param name="class_prefix">info</s:param>
    <s:param name="tint_hex">ebf5fe</s:param>
    <s:param name="light_hex">9cccf9</s:param>
    <s:param name="med_hex">73bbe8</s:param>
    <s:param name="dark_hex">4496d8</s:param>
</s:include>

<s:include value="_basic-color-swatch.jsp">
    <s:param name="heading">Success</s:param>
    <s:param name="class_prefix">success</s:param>
    <s:param name="tint_hex">d6f0e4</s:param>
    <s:param name="light_hex">33b679</s:param>
    <s:param name="med_hex">0f9d58</s:param>
    <s:param name="dark_hex">05702e</s:param>
</s:include>

<s:include value="_basic-color-swatch.jsp">
    <s:param name="heading">Warning</s:param>
    <s:param name="class_prefix">warning</s:param>
    <s:param name="tint_hex">fe5d9</s:param>
    <s:param name="light_hex">fbcb43</s:param>
    <s:param name="med_hex">f4b400</s:param>
    <s:param name="dark_hex">ec8c00</s:param>
</s:include>

<s:include value="_basic-color-swatch.jsp">
    <s:param name="heading">Danger</s:param>
    <s:param name="class_prefix">danger</s:param>
    <s:param name="tint_hex">fadb8</s:param>
    <s:param name="light_hex">e74c3c</s:param>
    <s:param name="med_hex">c0392b</s:param>
    <s:param name="dark_hex">800a06</s:param>
</s:include>