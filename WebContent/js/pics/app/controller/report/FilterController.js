Ext.define('PICS.controller.report.FilterController', {
    extend: 'Ext.app.Controller',
    refs: [{
        ref: 'filterOptions',
        selector: 'reportoptionsfilters #options'
    }],

    filterStyle: null,
    showOptionsPanel: null,    

    init: function() {
        this.control({
            "reportoptionsfilters gridpanel":  {
                itemclick: this.showFilterOptions
            },
            'basefilter button[action=apply]': {
                click: function () {
                    this.application.fireEvent('refreshreport');
                }                
            }
        });
    },
    showFilterOptions: function (view, record, item, index, e, options) {
        //TODO remove option panel only if 'removed' 
        //if record deleted, don't display options
        if (!record.store) {
            if (this.showOptionsPanel !== null) {
                this.showOptionsPanel.destroy();
            }
            return;
        }
        if (this.showOptionsPanel !== null){
            this.showOptionsPanel.destroy();
        }
        this.filterType = record.data.field.data.filterType;
        
        if (this.filterType === "String") {
            this.showOptionsPanel = Ext.create('PICS.view.report.filter.StringFilter');
        } else if (this.filterType === "AccountName") {
            this.showOptionsPanel = Ext.create('PICS.view.report.filter.StringFilter');
        } else if (this.filterType === "Boolean") {
            this.showOptionsPanel = Ext.create('PICS.view.report.filter.BooleanFilter');
        } else if (this.filterType === "Float") {
            this.showOptionsPanel = Ext.create('PICS.view.report.filter.FloatFilter');
        } else if (this.filterType === "Number") {
            this.showOptionsPanel = Ext.create('PICS.view.report.filter.NumberFilter');
        } else if (this.filterType === "Integer") {
            this.showOptionsPanel = Ext.create('PICS.view.report.filter.NumberFilter');
        } else if (this.filterType === "AccountType") {
            this.showOptionsPanel = Ext.create('PICS.view.report.filter.AccountTypeFilter');
        } else if (this.filterType === "AccountStatus") {
            this.showOptionsPanel = Ext.create('PICS.view.report.filter.AccountStatusFilter');
        } else if (this.filterType === "StateProvince") {
            this.showOptionsPanel = Ext.create('PICS.view.report.filter.StateFilter');            
        } else if (this.filterType === "Country") {
            this.showOptionsPanel = Ext.create('PICS.view.report.filter.CountryFilter');
        } else if (this.filterType === "Date") {
            this.showOptionsPanel = Ext.create('PICS.view.report.filter.DateFilter');
        } else {
            console.log(this.filterType + " is not supported at this time");
            return;
        }
        this.showOptionsPanel.setRecord(record);
        this.getFilterOptions().add(this.showOptionsPanel);
    }
});
