Ext.define('PICS.view.report.settings.ReportInfoSetting', {
    extend: 'Ext.Component',
    alias: 'widget.reportinfosetting',

    cls: 'report-info',
    tpl: new Ext.XTemplate([
        '<ul id="report_info_list">',
            '<li>',
                '<label>Model:</label><span>{model}</span>',
            '</li>',
            '<li>',
                '<label>Shares:</label><span>{shares}</span>',
            '</li>',
            '<li>',
                '<label>Favorites:</label><span>{favorites}</span>',
            '</li>',
            '<li class="update-info">',
                '<label>Updated:</label>',
                '<span>',
                    '{updated}<br />',
                    '<tpl if="updated_by">',
                        '(by {updated_by})',
                    '</tpl>',
                '</span>',
            '</li>',
            '<li>',
                '<label>Owner:</label><span>{owner}</span>',
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