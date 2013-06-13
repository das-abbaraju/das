Ext.define('PICS.view.report.settings.ReportInfoSetting', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.reportreportinfosetting',

    id: 'report_report_info',
    items: [{
        xtype: 'component',
        id: 'report_info_list',
        html: new Ext.Template([
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
                        '(by {updated_by})',
                    '</span>',
                '</li>',
                '<li>',
                    '<label>Owner:</label><span>{owner}</span>',
                '</li>',
            '</ul>'
        ])
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    },
    // custom config
    modal_title: PICS.text('Report.execute.reportInfoSetting.title'),

    update: function (target_el) {
        report_info_list_html = this.down('#report_info_list').html;

        report_info_list_html.compile();
        report_info_list_html.append(target_el, {
            model: 'Contractors',
            shares: '136',
            favorites: '1,242',
            updated: '2012-12-07 @ 08:22 PST',
            updated_by: 'Matt DeSio',
            owner: 'Trevor Allred',
        });
    }
});