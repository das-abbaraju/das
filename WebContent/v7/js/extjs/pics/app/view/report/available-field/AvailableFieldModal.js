Ext.define('PICS.view.report.available-field.AvailableFieldModal', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportavailablefieldmodal'],

    requires: [
        'PICS.view.report.available-field.AvailableFieldList'
    ],

    border: 0,
    dockedItems: [{
        xtype: 'toolbar',
        border: 0,
        dock: 'top',
        height: 45,
        id: 'available_field_search',
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
        id: 'available_field_modal_footer',
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
    id: 'available_field_modal',
    items: [{
        xtype: 'reportavailablefieldlist'
    }],
    layout: 'fit',
    modal: true,
    resizable: false,
    width: 600,

    initComponent: function () {
        // type is used to determine context of modal - will add fields to filter or column store
        if (this.type != 'filter' && this.type != 'column') {
            throw 'Invalid type:' + this.type + ' - must be (filter|column)';
        }

        this.title = this.getTitle(this.type);

        this.callParent();
    },

    getTitle: function (type) {
        if (type == 'column') {
            return 'Add Column';
        } else if (type == 'filter') {
            return 'Add Filter';
        }
    }
});