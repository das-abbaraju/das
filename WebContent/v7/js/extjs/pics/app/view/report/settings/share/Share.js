Ext.define('PICS.view.report.settings.share.Share', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportsharesetting',

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
        Ext.create('PICS.view.report.settings.share.UserSearch')
    ],
    layout: 'form',
    // custom config
    modal_title: 'Share Report',
    title: '<i class="icon-share icon-large"></i>Share'
});