/**
 * Available Fields By Category Store
 *
 * Loaded dynamically by the Available Fields Store
 * Categorizes available fields
 * Organizes column data for selection purposes
 */
Ext.define('PICS.store.report.AvailableFieldsByCategory', {
    extend : 'PICS.store.report.base.Store',
    model : 'PICS.model.report.AvailableField',

	autoLoad: false,
    groupField: 'category',
    sorters: [{
        property: 'category',
        direction: 'ASC'
    }]
});