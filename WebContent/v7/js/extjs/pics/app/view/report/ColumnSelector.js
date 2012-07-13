Ext.define('PICS.view.report.ColumnSelector', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportcolumnselector'],
    requires: ['PICS.view.report.ColumnSelectorGrid'],
    
    buttons: [{
        action: 'add',
        scope: this,
        text: 'Add'
    }, {
        action: 'close',
        text: 'Cancel'
    }],
    columnSelectorType: null,    
    height: 500,
    items: [{
        xtype: 'reportcolumnselectorgrid'
    }],
    layout: 'fit',
    modal: true,
    tbar: [{
        xtype: 'textfield',

        enableKeyEvents: true,
        fieldLabel: 'Search',
        labelWidth: 40
    }, {
        xtype: 'checkboxfield',

        boxLabel: 'Hide All Selected',
        checked: true,
        inputValue: true,
        name: 'hide_selected_column'
    }],
    title: 'Select Report Columns',
    width: 400
});
