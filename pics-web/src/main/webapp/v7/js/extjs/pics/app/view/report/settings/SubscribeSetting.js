Ext.define('PICS.view.report.settings.SubscribeSetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportsubscribesetting',

    border: 0,

    id: 'report_subscribe',

    items: [{
        xtype: 'fieldset',
        title: PICS.text('Report.execute.subscribeSetting.legendTitle') + ':',
        items: [{
            xtype: 'radiogroup',
            defaults: {
                flex: 1,
                name: 'subscription_frequency',
                margin: '0 0 10 0'
            },
            layout: 'vbox',
            align: 'left',
            items: [{
                boxLabel: PICS.text('Report.execute.subscribeSetting.labelNever'),
                inputValue: 'None'
            }, {
                boxLabel: PICS.text('Report.execute.subscribeSetting.labelDaily'),
                inputValue: 'Daily'
            }, {
                boxLabel: PICS.text('Report.execute.subscribeSetting.labelWeekly'),
                inputValue: 'Weekly'
            }, {
                boxLabel: PICS.text('Report.execute.subscribeSetting.labelMonthly'),
                    inputValue: 'Monthly'
            }]
        }]
    }, {
        xtype: 'component',
        html: new Ext.Template([
            '<p class="subscription-info">',
                    PICS.text('Report.execute.subscribeSetting.subscriptionInfo2') + '<br />',
                    PICS.text('Report.execute.subscribeSetting.subscriptionInfo1'),
            '</p>'
        ])
    }],

    layout: {
        type: 'form'
    },

    margin: '0 20 0 20',

    // custom config
    modal_title: PICS.text('Report.execute.subscribeSetting.title'),
    title: '<i class="icon-envelope icon-large"></i>' + PICS.text('Report.execute.subscribeSetting.tabName')
});