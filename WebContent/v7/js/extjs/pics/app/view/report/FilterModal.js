Ext.define('PICS.view.report.FilterModal', {
    extend: 'PICS.view.report.ReportModal',
    alias: 'widget.filtermodal',

    requires: [
        'PICS.view.report.FilterList'
    ],
    
    border: 0,
    dockedItems: [{
        xtype: 'toolbar',
        border: 0,
        dock: 'top',
        height: 45,
        id: 'filter_search',
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
        defaults: {
            margin: '0 10 0 0'
        },
        dock: 'bottom',
        height: 45,
        id: 'filter_modal_footer',
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
    id: 'filter_modal',
    items: [{
        xtype: 'filterlist'
    }],
    layout: 'fit',
    modal: true,
    resizable: false,
    width: 600,

    initComponent: function () {
        this.setTitle('Add Filter');

        this.callParent(arguments);
    }
});