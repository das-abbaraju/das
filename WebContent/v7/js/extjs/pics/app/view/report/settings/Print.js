Ext.define('PICS.view.report.settings.Print', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingsprint'],

    border: 0,
    id: 'report_print',
    /*items: [{
        text: '<i class="icon-print icon-large"></i>Print'
    }],*/
    items: [{
        cls: 'coming-soon',
        html: new Ext.Template([
            '<i class="icon-wrench icon-large"></i>',
            '<p>Coming Soon</p>'
        ])
    }],
    title: '<i class="icon-print icon-large"></i>Print',
    // custom config
    modal_title: 'Print Report'
});