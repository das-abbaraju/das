<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table id="directory_table" class="table raw">
    <thead>
        <tr>
            <th class="field-id">
                fieldId
            </th>
            <th>
                modelType
            </th>
            <th>
                category
            </th>
            <th>
                name
            </th>
            <th>
                help
            </th>
            <th>
                isVisible
            </th>
            <th>
                isSortable
            </th>
            <th>
                isFilterable
            </th>
        </tr>
    </thead>
    <tbody>
    <s:iterator value="allModelFieldInfos" var="field">

        <s:set var="is_visible" value="%{#field.isVisible() ? 'Yes' : 'No'}" />
        <s:set var="is_sortable" value="%{#field.isSortable() ? 'Yes' : 'No'}" />
        <s:set var="is_filterable" value="%{#field.isFilterable() ? 'Yes' : 'No'}" />

        <tr>
            <td class="field-id">
                ${field.fieldId}
            </td>
            <td>
                ${field.modelType}
            </td>
            <td>
                ${field.category}
            </td>
            <td>
                ${field.name}
            </td>
            <td>
                ${field.help}
            </td>
            <td>
                ${is_visible}
            </td>
            <td>
                ${is_sortable}
            </td>
            <td>
                ${is_filterable}
            </td>
        </tr>
    </s:iterator>
    </tbody>
    <tfoot>
        <tr>
            <th class="field-id">
                fieldId
            </th>
            <th>
                modelType
            </th>
            <th>
                category
            </th>
            <th>
                name
            </th>
            <th>
                help
            </th>
            <th>
                isVisible
            </th>
            <th>
                isSortable
            </th>
            <th>
                isFilterable
            </th>
        </tr>
    </tfoot>
</table>
</div>