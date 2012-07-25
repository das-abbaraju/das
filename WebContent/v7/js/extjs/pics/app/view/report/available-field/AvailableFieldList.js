Ext.define('PICS.view.report.available-field.AvailableFieldList', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportavailablefieldlist'],

    store: 'report.AvailableFieldsByCategory',

    border: 0,
    columns: [{
        dataIndex: 'text',
        text: 'Column Name',
        flex: 1
    }],
    enableColumnHide: false,
    hideHeaders: true,
    id: 'available_field_list',
    features: Ext.create('Ext.grid.feature.Grouping', {
        groupHeaderTpl: 'Category: {name} <span class="number-of-items">({rows.length} item{[values.rows.length != 1 ? "s" : ""]})</span>'
    }),
    rowLines: false,
    selModel: Ext.create('Ext.selection.CheckboxModel')
});