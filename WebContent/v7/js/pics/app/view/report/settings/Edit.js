Ext.define('PICS.view.report.settings.Edit', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingsedit'],

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
            action: 'edit',
            cls: 'edit primary',
            formBind: true,
            height: 28,
            text: 'Apply'
        }],
        layout: {
            pack: 'end'
        },
        ui: 'footer'
    }],
    id: 'report_edit',
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
    modal_title: 'Edit Report',
    title: '<i class="icon-cog icon-large"></i>Settings'
});