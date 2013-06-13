Ext.define('PICS.view.report.settings.ModelInfoSetting', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.reportmodelinfosetting',

    id: 'report_model_info',
    items: [{
        xtype: 'component',
        id: 'model_info_list',
        html: new Ext.Template([
            '<ul>',
                '<li>{first}</li>',
                '<li>{second}</li>',
            '</ul>'
        ])
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    },
    // custom config
    modal_title: PICS.text('Report.execute.modelInfoSetting.title'),

    update: function (target_el) {
        model_info_list_html = this.down('#model_info_list').html;

        model_info_list_html.compile();
        model_info_list_html.append(target_el, {first: 'test1', second: 'test2'});
    }
});