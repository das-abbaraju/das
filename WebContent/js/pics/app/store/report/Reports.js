/**
 * Report Store
 *
 * load backend report into local report database via ajax
 * sends backend report from local to server
 */
Ext.define('PICS.store.report.Reports', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.Report',

	autoLoad: false,
    proxy: {
        reader: {
            root: 'report',
            type: 'json'
        },
        type: 'ajax'
    },

    constructor: function () {
        var url = Ext.Object.fromQueryString(document.location.search);

        this.proxy.url = 'ReportDynamic!getReportParameters.action?report=' + url.report;

        this.callParent(arguments);
    }
});