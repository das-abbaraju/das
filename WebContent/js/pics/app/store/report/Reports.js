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
    },
    
    /**
     * Get Report JSON
     *
     * Builds a jsonified version of the report to be sent to the server
     */
    getReportJSON: function () {
        var report_store = Ext.StoreManager.get('report.Reports');
        var report = report_store && report_store.first();
        var report_data;

        if (!report) {
            throw 'Data.getReportJSON missing report';
        }

        function convertStoreToDataObject(store) {
            var data = [];

            store.each(function (record) {
                var item = {};

                record.fields.each(function (field) {
                    item[field.name] = record.get(field.name);
                });

                data.push(item);
            });

            return data;
        }

        report_data = report.data;
        report_data.columns = convertStoreToDataObject(report.columns());
        report_data.filters = convertStoreToDataObject(report.filters());
        report_data.sorts = convertStoreToDataObject(report.sorts());

        return Ext.encode(report_data);
    },
    
    getReportParameters: function () {
        var report = this.first();

        if (!report) {
            throw 'Data.getReportJSON missing report';
        }

        var report_json = this.getReportJSON();
        var parameters = {};

        if (report && report.getId() > 0) {
            parameters['report'] = report.getId();
        } else {
            parameters['report.base'] = report.get('base');
        }

        parameters['report.parameters'] = report_json;

        parameters['report.name'] = report.get('name');

        parameters['report.description'] = report.get('description');

        parameters['report.rowsPerPage'] = report.get('rowsPerPage');
        
        return parameters;
    },
    
    getReportQueryString: function () {
        var parameters = this.getReportParameters();
        
        return Ext.Object.toQueryString(parameters);
        
    }
});