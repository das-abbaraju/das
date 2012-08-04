/**
 * Available Fields Store
 *
 * List of all fields associated to a given report
 * Contains miscellaneous information regarding columns, filters, sorts for each field
 * Dynamically loads Available Fields By Category Store
 */
Ext.define('PICS.store.report.AvailableFields', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.AvailableField',

	autoLoad: true,
	listeners: {
        load: function (store, records, successful, operation, options) {
            var available_fields_by_category_store = Ext.StoreManager.get('report.AvailableFieldsByCategory');

            available_fields_by_category_store.data = store.data;
        }
    },
	proxy: {
	    reader: {
            root: 'fields',
            type: 'json'
        },
        timeout: 3000,
        type: 'ajax'
    },

    constructor: function () {
        var request_parameters = Ext.Object.fromQueryString(document.location.search);
        var report_id = request_parameters.report;

        this.proxy.url = 'ReportDynamic!availableFields.action?report=' + report_id;

        this.callParent(arguments);
    }
});