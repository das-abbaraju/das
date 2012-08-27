/**
 * Available Fields Store
 *
 * List of all fields associated to a given report
 * Contains miscellaneous information regarding columns, filters, sorts for each field
 * Dynamically loads Available Fields By Category Store
 */
Ext.define('PICS.store.report.AvailableFields', {
    extend : 'PICS.store.report.base.Store',
	model : 'PICS.model.report.AvailableField',

	autoLoad: true,
	proxy: {
	    reader: {
            root: 'fields',
            type: 'json'
        },
        timeout: 10000,
        type: 'ajax'
    },

    constructor: function () {
        var request_parameters = Ext.Object.fromQueryString(document.location.search);
        var report_id = request_parameters.report;

        this.proxy.url = 'ReportDynamic!availableFields.action?report=' + report_id;

        this.callParent(arguments);
        
        this.bindOnLoadEvent();
    },
    
    bindOnLoadEvent: function () {
        this.on('load', function (store, records, successful, options) {
            var available_fields_by_category_store = Ext.StoreManager.get('report.AvailableFieldsByCategory');

            available_fields_by_category_store.data = store.data;
        });
    }
});