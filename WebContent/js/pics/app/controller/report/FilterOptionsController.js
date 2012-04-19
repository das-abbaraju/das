Ext.define('PICS.controller.report.FilterOptionsController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportColumnSelector',
        selector: 'reportcolumnselector'
    }, {
        ref: 'reportOptionsColumnsGrid',
        selector: 'reportoptionscolumns grid'
    }, {
        ref: 'reportOptionsFiltersGrid',
        selector: 'reportoptionsfilters grid'
    }],
    stores: [
        'report.AvailableFieldsByCategory'
    ],

    init: function() {
        this.control({
            'filteroptions button[action=add-filter]': {
                click: this.showColumnSelector
            }
        });
    },
    showColumnSelector: function(component, e, options) {
        var window = this.getReportColumnSelector();

        if (!window) {
            var store = this.getReportAvailableFieldsByCategoryStore();
            store.clearFilter();

            window = Ext.create('PICS.view.report.ColumnSelector');
            window._column_type = 'filter';
            window.show();
        }
    }
});
