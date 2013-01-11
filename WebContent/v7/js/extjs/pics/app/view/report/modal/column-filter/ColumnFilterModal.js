Ext.define('PICS.view.report.modal.column-filter.ColumnFilterModal', {
    extend: 'PICS.view.report.modal.ReportModal',
    alias: 'columnfiltermodal',
    
    requires: [
        'PICS.view.report.modal.column-filter.ColumnList',
        'PICS.view.report.modal.column-filter.FilterList'
    ],
    
    border: 0,
    dockedItems: [{
        xtype: 'toolbar',
        border: 0,
        cls: 'search',
        dock: 'top',
        height: 45,
        items: [{
            xtype: 'textfield',
            emptyText: 'Search',
            enableKeyEvents: true,
            fieldLabel: '<i class="icon-search icon-large"></i>',
            labelSeparator: '',
            name: 'search_box',
            labelWidth: 16,
            width: 245
        }],
        layout: {
            type: 'hbox',
            align: 'middle',
            pack: 'center'
        }
    }, {
        xtype: 'toolbar',
        border: 0,
        cls: 'footer',
        defaults: {
            margin: '0 10 0 0'
        },
        dock: 'bottom',
        height: 45,
        items: [{
            action: 'cancel',
            cls: 'default',
            height: 26,
            text: 'Cancel'
        }, {
            action: 'add',
            cls: 'primary',
            height: 26,
            text: 'Add'
        }],
        layout: {
            type: 'hbox',
            pack: 'end'
        }
    }],
    draggable: false,
    header: {
        height: 45
    },
    height: 500,
    layout: 'fit',
    modal: true,
    resizable: false,
    width: 600
});