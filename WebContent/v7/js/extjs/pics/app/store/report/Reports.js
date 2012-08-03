/**
 * Report Store
 *
 * load backend report into local report database via ajax
 * sends backend report from local to server
 */
Ext.define('PICS.store.report.Reports', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.Report',

	autoLoad: true,
    proxy: {
        reader: {
            root: 'report',
            type: 'json'
        },
        timeout: 3000,
        type: 'ajax'
    },

    constructor: function () {
        var request_parameters = Ext.Object.fromQueryString(document.location.search);
        var report_id = request_parameters.report;

        this.proxy.url = 'ReportDynamic!report.action?report=' + report_id;

        this.callParent(arguments);
    }
});