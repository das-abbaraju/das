Ext.define('PICS.view.report.settings.Copy', {
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
            action: 'cancel',
            cls: 'cancel default',
            height: 28,
            text: 'Cancel'
        }, {
            action: 'copy',
            cls: 'copy primary',
            formBind: true,
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
        allowBlank: false,
        fieldLabel: 'Report Name',
        labelAlign: 'right',
        name: 'report_name'
    }, {
        xtype: 'textarea',
        allowBlank: false,
        fieldLabel: 'Description',
        labelAlign: 'right',
        name: 'report_description'
    }],
    layout: 'form',
    // custom config
    modal_title: 'Duplicate Report',
    title: '<i class="icon-copy icon-large"></i>Duplicate'
});