<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<figure class="employee-image img-thumbnail">
    <img src="${image_url}" class="img-responsive" alt="${alt_text}" />

    <div class="overlay-container">
        <div class="overlay"></div>
        <span class="edit-text"><strong>Select to edit...</strong></span>
    </div>
    <tw:input inputName="photo" type="file" class="default-file-import" />
    <input type="submit" class="hidden" />
</figure>
