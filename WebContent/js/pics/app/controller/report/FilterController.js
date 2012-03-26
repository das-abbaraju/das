Ext.define('PICS.controller.report.FilterController', {
    extend: 'Ext.app.Controller',
    refs: [{
        ref: 'filterOptions',
        selector: 'reportoptionsfilters #options'
    },{
        ref: 'dockFilter',
        selector: 'reportdatagrid #filterToolbar'
    }],

    stores: [
        'report.ReportsFilter'
    ],
    filterStyle: null,

    toolbarDockedItemsMax: 2,    
    
    dockFilter: function (target) {
        var filterPanel = target.up('basefilter'),
            toolbar = this.getDockFilter(),
            baseObj = this;
        
        if (!filterPanel.position) {
            filterPanel.position = 'docked' + filterPanel.id
            addToToolbar();
        } else {
            removeFromToolbar();
        }
        function addToToolbar () {
            var type = filterPanel.record.data.field.data.filterType,
                panel = baseObj.setFilterPanelType(type),
                dockedFilter = Ext.create(panel, {displayMode: 'docked', id: filterPanel.position});
            
            dockedFilter.record = filterPanel.record;
            dockedFilter.setTitle(true);
            toolbar.add(dockedFilter);
        }
        function removeFromToolbar() {
            toolbar.remove(filterPanel.position);
            filterPanel.position = null;
        }
    },
    init: function() {
        this.control({
            "reportoptionsfilters gridpanel":  {
                itemclick: this.showFilterOptions
            },
            'basefilter button[action=apply]': {
                click: function () {
                    this.application.fireEvent('refreshreport');
                }                
            },
            'basefilter checkbox[name=dockFilter]': {
                change: this.dockFilter
            }
        });
    },
    setFilterPanelType: function (type) {
        var filterPanel = '';
        console.log(type);
        if (type === "String") {
            filterPanel = 'PICS.view.report.filter.StringFilter';
        } else if (type === "AccountName") {
            filterPanel = 'PICS.view.report.filter.StringFilter';
        } else if (type === "Boolean") {
            filterPanel = 'PICS.view.report.filter.BooleanFilter';
        } else if (type === "Float") {
            filterPanel = 'PICS.view.report.filter.FloatFilter';
        } else if (type === "Number") {
            filterPanel = 'PICS.view.report.filter.NumberFilter';
        } else if (type === "Integer") {
            filterPanel = 'PICS.view.report.filter.NumberFilter';
        } else if (type === "AccountType") {
            filterPanel = 'PICS.view.report.filter.AccountTypeFilter';
        } else if (type === "AccountStatus") {
            filterPanel = 'PICS.view.report.filter.AccountStatusFilter';
        } else if (type === "StateProvince") {
            filterPanel = 'PICS.view.report.filter.StateFilter';            
        } else if (type === "Country") {
            filterPanel = 'PICS.view.report.filter.CountryFilter';
        } else if (type === "Date") {
            filterPanel = 'PICS.view.report.filter.DateFilter';
        } else {
            console.log(type + " is not supported at this time");
        }
        return filterPanel;
        
    },
    showFilterOptions: function (view, record, item, index, e, options) {
        var filterPanel = Ext.ComponentQuery.query('#filterPanel' + index)[0],
            filterOptions = this.getFilterOptions(),
            baseObj = this;
            
        if (!record.store) {
            filterOptions.removeAll();
            updateDockedFilterIds();
            return;
        }
        
        hideOpenFilters();
        
        if (filterPanel) {
            filterPanel.show();
        } else {
            createFilterOptionsPanel();
        }
        function updateDockedFilterIds() {
            var filterName = '',
                recordName = '',
                store = baseObj.getReportReportsFilterStore(),
                filterToolbar = baseObj.getDockFilter();
                
            store.each(function(item, recordIndex) {
                recordName = item.data.field.get('text');
                for (y = 0; y < filterToolbar.items.items.length; y++) {
                    filterName = filterToolbar.items.items[y].record.data.field.get('text');
                    if (filterName === recordName) {
                        filterToolbar.items.items[y].id = 'dockedfilterPanel' + recordIndex;
                    }
                }
            });
        }
        function hideOpenFilters() {
            for (x = 0; x < filterOptions.items.items.length; x++) {
                filterOptions.items.items[x].hide();
            }
        }        
        function createFilterOptionsPanel() {
            var type = record.data.field.data.filterType,
                panel = baseObj.setFilterPanelType(type),
                filterPanel = Ext.create(panel, {id: 'filterPanel' + index});
            
            filterPanel.setRecord(record);
            filterPanel.setTitle();
            filterOptions.add(filterPanel);
        }
    }
});
