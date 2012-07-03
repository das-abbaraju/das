Ext.define('PICS.view.report.SettingsCopy', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingscopy'],

    border: 0,
    dockedItems: [{
        xtype: 'toolbar',
        defaults: {
            margin: '0 0 0 5'
        },
        dock: 'bottom',
        items: [{
            cls: 'cancel default',
            height: 28,
            text: 'Cancel'
        }, {
            cls: 'copy primary',
            height: 28,
            text: 'Duplicate'
        }],
        layout: {
            pack: 'end'
        },
        ui: 'footer'
    }],
    id: 'report_copy',
    items: [{
        xtype: 'textfield',
        fieldLabel: 'Report Name',
        labelAlign: 'right',
        name: 'report_name'
    }, {
        xtype: 'textarea',
        fieldLabel: 'Description',
        labelAlign: 'right',
        name: 'report_description'
    }],
    layout: 'form',
    title: '<i class="icon-copy icon-large"></i>Duplicate'
});