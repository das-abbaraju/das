Ext.define('PICS.view.report.settings.ReportInfoSetting', {
    extend: 'Ext.Component',
    alias: 'widget.reportinfosetting',

    cls: 'report-info',
    tpl: new Ext.XTemplate([
        '<ul id="report_info_list">',
            '<li>',
                '<label>' + PICS.text('Report.execute.reportInfoSetting.modelLable') + ':</label><span>{model}</span>',
            '</li>',
            '<li>',
                '<label>' + PICS.text('Report.execute.reportInfoSetting.sharesLabel') + ':</label><span>{shares}</span>',
            '</li>',
            '<li>',
                '<label>' + PICS.text('Report.execute.reportInfoSetting.favoritesLabel') + ':</span>',
            '</li>',
            '<li>',
                '<label>' + PICS.text('Report.execute.reportInfoSetting.subscribersLabel') + ':</label><span>{subscribers}</span>',
            '</li>',
            '<li class="update-info">',
                '<label>' + PICS.text('Report.execute.reportInfoSetting.updatedDateLabel') + ':</label>',
                '<span>',
                    '{updated}',
                    '<tpl if="updated_by">',
                        '<br />(' + PICS.text('Report.execute.reportInfoSetting.updatedNameLabel') + ': {updated_by})',
                    '</tpl>',
                '</span>',
            '</li>',
            '<li>',
                '<label>' + PICS.text('Report.execute.reportInfoSetting.ownerLabel') + ':</label><span>{owner}</span>',
            '</li>',
        '</ul>'
    ]),
    layout: {
        type: 'vbox',
        align: 'center'
    },
    // custom config
    modal_title: PICS.text('Report.execute.reportInfoSetting.title'),

    update: function (values) {
        this.callParent([values]);
    }
});