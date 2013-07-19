Ext.define('PICS.view.report.settings.SubscribeSetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportsubscribesetting',

    border: 0,
    id: 'report_subscribe',
    items: [{
            xtype: 'component',
            html:  new Ext.Template([
                '<p class="coming-soon">' + PICS.text('Report.execute.subscribeSetting.ComingSoon') + '</p>'
            ])
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    },

    // custom config
    modal_title: PICS.text('Report.execute.subscribeSetting.title'),
    title: '<i class="icon-envelope icon-large"></i>' + PICS.text('Report.execute.subscribeSetting.tabName')
});