Ext.define('PICS.view.report.settings.Export', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingsexport'],

    border: 0,
    id: 'report_export',
    /*items: [{
        text: '<i class="icon-table icon-large"></i>Spread Sheet'
    }, {
        text: '<i class="icon-file icon-large"></i>PDF'
    }, {
        text: '<i class="icon-home icon-large"></i>To Dashboard'
    }],*/
    items: [{
        cls: 'coming-soon',
        html: new Ext.Template([
            '<i class="icon-wrench icon-large"></i>',
            '<p>Coming Soon</p>'
        ])
    }],
    // custom config
    modal_title: 'Export Report',
    title: '<i class="icon-eject icon-large"></i>Export'
});