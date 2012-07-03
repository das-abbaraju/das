Ext.define('PICS.view.report.SettingsEdit', {
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
            cls: 'cancel default',
            height: 28,
            text: 'Cancel'
        }, {
            cls: 'edit primary',
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
    title: '<i class="icon-cog icon-large"></i>Settings'
});