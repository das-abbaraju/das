Ext.define('PICS.view.report.settings.ReportInfoSetting', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.reportreportinfosetting',

    items: [{
        xtype: 'component',
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

    update: function (container_el, values) {
        report_info_list_html = this.down('component').html;

        report_info_list_html.compile();
        report_info_list_html.append(container_el, values);
    }
});