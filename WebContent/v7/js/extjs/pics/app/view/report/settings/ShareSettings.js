Ext.define('PICS.view.report.settings.Share', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingsshare'],

    border: 0,
    items: [{
        cls: 'coming-soon',
        html: new Ext.Template([
            '<i class="icon-wrench icon-large"></i>',
            '<p>Coming Soon</p>'
        ])
    }],
    // custom config
    modal_title: 'Share Report',
    title: '<i class="icon-share icon-large"></i>Share'
});