Ext.define('PICS.view.report.settings.Share', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingsshare'],

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
            cls: 'share primary',
            height: 28,
            text: 'Share'
        }],
        layout: {
            pack: 'end'
        },
        ui: 'footer'
    }],
    id: 'report_share',
    items: [
        Ext.create('PICS.view.report.settings.UserSearch')
    ],
//    items: [{
//        xtype: 'textfield',
//        fieldLabel: 'Report Name',
//        labelAlign: 'right',
//        name: 'report_name'
//    }, {
//        xtype: 'textarea',
//        fieldLabel: 'Description',
//        labelAlign: 'right',
//        name: 'report_description'
//    }],
    layout: 'form',
    // custom config
    modal_title: 'Share Report',
    title: '<i class="icon-share icon-large"></i>Share'
});