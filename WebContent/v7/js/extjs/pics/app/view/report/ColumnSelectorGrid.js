Ext.define('PICS.view.report.ColumnSelectorGrid', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportcolumnselectorgrid'],
    store: 'report.AvailableFieldsByCategory',
    
    columns: [{
        dataIndex: 'category',
        text: 'Category',
        width: 160
    }, {
        dataIndex: 'text',
        text: 'Column Name',
        flex: 1
    }],
    enableColumnHide: false,
    features: Ext.create('Ext.grid.feature.Grouping', {
        groupHeaderTpl: 'Category: {name} ({rows.length} Item{[values.rows.length != 1 ? "s" : ""]})'
    }),
    
    initComponent: function () {
        this.selModel = Ext.create('Ext.selection.CheckboxModel', {
            // remove ability to select all checkbox for performance -- hack
            onHeaderClick: function (headerCt, header, e) {
                return false;
            }
        });
        
        this.callParent();
    },
    listeners: {
        // remove ability to select all checkbox for performance -- hack
        afterRender: function (component, eOpts) {
            var component_element = component.getEl();
            
            Ext.get(Ext.query('.x-column-header-checkbox div:first', component_element.dom)).hide();
        }
    }
});