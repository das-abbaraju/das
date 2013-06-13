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
                    '<label>Shares:</label><a href="{shares_url}">{shares}</a>',
                '</li>',
                '<li>',
                    '<label>Favorites:</label><span>{favorites}</span>',
                '</li>',
                '<li class="update-info">',
                    '<label>Updated:</label>',
                    '<span>',
                        '{updated}<br />',
                        '(by <a href="{updated_by_url}">{updated_by}</a>',
                    '</span>',
                '</li>',
                '<li>',
                    '<label>Owner:</label><a href="{owner_url}">{owner})</a>',
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
            shares_url: 'something.Action',
            favorites: '1,242',
            updated: '2012-12-07 @ 08:22 PST',
            updated_by: 'Matt DeSio',
            updated_by_url: 'something.Action',
            owner: 'Trevor Allred',
            owner_url: 'something.Action'
        });
    }
});