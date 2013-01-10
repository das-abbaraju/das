Ext.define('PICS.controller.report.Filter', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'filterFooter',
        selector: '#report_filter_options_footer'
    }, {
        ref: 'filterFormula',
        selector: 'reportfilteroptions reportfilterformula'
    }, {
        ref: 'filterFormulaTextfield',
        selector: 'reportfilteroptions reportfilterformula textfield[name=filter_formula]'
    }, {
        ref: 'filterHeader',
        selector: 'reportfilterheader'
    }, {
        ref: 'filterOptions',
        selector: 'reportfilteroptions'
    }, {
        ref: 'filters',
        selector: 'reportfilteroptions #report_filters'
    }, {
        ref: 'filterToolbar',
        selector: 'reportfilteroptions reportfiltertoolbar'
    }],

    stores: [
        'report.Reports'
    ],

    views: [
        'PICS.view.report.filter.Filters'
    ],

    init: function() {
        this.control({
            'reportfilter': {
                render: this.onFilterRender
            },
            
            // render filter options
            'reportfilteroptions': {
                afterlayout: this.onFilterOptionsAfterLayout,
                beforerender: this.onFilterOptionsBeforeRender
            },

            // collapse filter options
            '#report_filter_options_collapse': {
                click: this.onFilterOptionsCollapse
            },

            // expand filter options
            '#report_filter_options_expand': {
                click: this.onFilterOptionsExpand
            },

            // add filter
            'reportfilteroptions button[action=add-filter]': {
                click: this.onAddFilter
            },

            // show filter formula
            'reportfiltertoolbar button[action=show-filter-formula]': {
                click: this.onFilterFormulaShow
            },

            // load filter formula expression
            'reportfilterformula': {
                beforerender: this.onFilterFormulaBeforeRender
            },

            // save filter formula expression
            'reportfilterformula textfield[name=filter_formula]': {
                blur: this.onFilterFormulaBlur,
                specialkey: this.onFilterFormulaInputSpecialKey
            },

            // hide filter formula
            'reportfilterformula button[action=cancel]': {
                click: this.onFilterFormulaCancel
            },

            // show filter-number and remove-filter on filter focus
            '\
            #report_filters combobox,\
            #report_filters textfield,\
            #report_filters numberfield,\
            #report_filters datefield,\
            #report_filters checkbox\
            ': {
                blur: this.onFilterBlur,
                focus: this.onFilterFocus
            },

            // saving edits to filter store + refresh
            '#report_filters combobox[name=filter_value]': {
                select: this.onFilterValueSelect
            },

            '#report_filters combobox[name=operator]': {
                select: this.onFilterOperatorSelect
            },

            // saving edits to filter store + refresh
            'reportfilterbasedatefilter [name=filter_value]': {
                select: this.onFilterValueSelect
            },

            // saving edits to date filter store + refresh
            '#report_filters datefield[name=filter_value]': {
                blur: this.onFilterValueDateBlur,
                specialkey: this.onFilterValueDateSpecialKey
            },
            
            // saving edits to non-date filter store + refresh
            '#report_filters [name=filter_value]:not(datefield)': {
                blur: this.onFilterValueInputBlur,
                specialkey: this.onFilterValueInputSpecialKey
            },
            
            // saving edits to filter store + refresh
            'reportfilterbaseuseridfilter [name=filter_field_compare]': {
                blur: this.onFilterFieldCompareInputBlur
            },
            
            // saving edits to filter store + refresh
            '#report_filters checkbox': {
                change: this.onFilterValueSelect
            },

            // remove filter
            'reportfilteroptions button[action=remove-filter]': {
                click: this.onFilterRemove
            },

            // advanced filter
            'reportfilteroptions button[action=show-advanced-filter]': {
                click: this.onAdvancedFilterButtonClick
            }
         });

        this.application.on({
            refreshfilters: this.refreshFilters,
            scope: this
        });
    },

    /**
     * Filter Options
     */

    // TODO: This should be removed or refactored - pencil advanced filter is a hack
    // TODO: This should be removed or refactored - pencil advanced filter is a hack
    // TODO: This should be removed or refactored - pencil advanced filter is a hack
    onAdvancedFilterButtonClick: function (cmp, event, eOpts) {
        var filter_panel = cmp.up('reportfilter'),
            filter_content = filter_panel.down('reportfilterbaseuseridfilter'),
            filter = filter_panel.record,
            el = cmp.getEl(),
            advanced_button = el.down('.icon-pencil'),
            advanced_on = el.down('.icon-pencil.selected');

        if (advanced_on) {
            filter.set('fieldCompare', null);
            
            filter_content.createNumberfield(filter);
            
            advanced_button.removeCls('selected');
        } else {
            filter.set('value', null);
            
            advanced_button.addCls('selected');
            
            filter_content.createFieldSelect(filter);
        }
    },
    
    onFilterOptionsAfterLayout: function (cmp, eOpts) {
        var filters = this.getFilters();

        if (!filters) {
            return;
        }
        
        cmp.updateBodyHeight();
        
        cmp.updateFooterPosition();
    },

    onFilterOptionsBeforeRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore();
    
        if (!report_store.isLoaded()) {
            report_store.on('load', function (store, records, successful, eOpts) {
                var report = report_store.first(),
                    filter_expression = report.get('filterExpression');

                if (filter_expression != '') {
                    cmp.showFormula();
                }
                
                this.application.fireEvent('refreshfilters');
            }, this);
        } else {
            var report = report_store.first(),
                filter_expression = report.get('filterExpression');

            if (filter_expression != '') {
                cmp.showFormula();
            }
            
            this.application.fireEvent('refreshfilters');
        }
    },

    onFilterOptionsCollapse: function (cmp, event, eOpts) {
        var filter_options = this.getFilterOptions();

        filter_options.collapse();
    },

    onFilterOptionsExpand: function (cmp, event, eOpts) {
        var filter_options = this.getFilterOptions();

        filter_options.expand();
    },
    
    onFilterRender: function (cmp, eOpts) {
        // attach tooltip on the name of each filter
        cmp.createTooltip();
    },

    /**
     * Add Filter
     */

    onAddFilter: function (cmp, event, eOpts) {
        this.application.fireEvent('showavailablefieldmodal', 'filter');
    },

    /**
     * Filter Formula
     */

    onFilterFormulaShow: function (cmp, event, eOpts) {
        var filter_options = cmp.up('reportfilteroptions');
        
        filter_options.showFormula();
    },

    onFilterFormulaCancel: function (cmp, event, eOpts) {
        var filter_options = cmp.up('reportfilteroptions'),
            filters = this.getFilters();
        
        filter_options.showToolbar();
        
        filters.removeCls('x-active');
    },

    onFilterFormulaBeforeRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_formula_textfield = this.getFilterFormulaTextfield(),
            filters = this.getFilters(),
            filter_expression = report.get('filterExpression');
            
        if (filter_expression != '') {
            filter_formula_textfield.setValue(report.getFilterExpression());
        }

        if (filters) {
            filters.addCls('x-active');
        }
    },

    onFilterFormulaBlur: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_formula_textfield = this.getFilterFormulaTextfield(),
            filter_expression = filter_formula_textfield.getValue();
        
        report.setFilterExpression(filter_expression);
    },

    onFilterFormulaInputSpecialKey: function (cmp, event) {
        if (event.getKey() != event.ENTER) {
            return false;
        }
        
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_formula_textfield = this.getFilterFormulaTextfield(),
            filter_expression = filter_formula_textfield.getValue();
        
        report.setFilterExpression(filter_expression);

        this.application.fireEvent('refreshreport');
    },

    /**
     * Filters
     */

    refreshFilters: function () {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_store = report.filters(),
            filter_options = this.getFilterOptions();

        // remove all filters
        filter_options.removeAll();

        // create new list of filters
        var filters = Ext.create('PICS.view.report.filter.Filters', {
            store: filter_store
        });

        // add new filters
        filter_options.add(filters);
    },

    /**
     * Filter
     */

    onFilterBlur: function (cmp, event, eOpts) {
        var filter_panel = cmp.up('reportfilter');
        
        filter_panel.removeCls('x-form-focus');
    },

    onFilterFocus: function (cmp, event, eOpts) {
        var filter_panel = cmp.up('reportfilter');

        filter_panel.addCls('x-form-focus');
    },

    onFilterOperatorSelect: function (cmp, records, eOpts) {
        var filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record,
            filter_value_textfield = cmp.next('textfield, inputfield, numberfield'),
            operator_value = cmp.getSubmitValue();
        
        filter.set('operator', operator_value);
        
        if (operator_value == 'Empty') {
            filter_value_textfield.setValue('');
            filter_value_textfield.disable();
        } else {
            filter_value_textfield.enable();
        }

        if (filter.get('value') != '') {
            this.application.fireEvent('refreshreport');
        }
    },

    onFilterRemove: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_store = report.filters(),
            filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record;

        filter_store.remove(filter);

        this.application.fireEvent('refreshfilters');

        if (filter.get('value') != '') {
            this.application.fireEvent('refreshreport');
        }
    },

    onFilterValueDateBlur: function (cmp, event, eOpts) {
        var filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record,
            value = cmp.getValue(),
            // TODO: weird may need some unified date format
            date = Ext.Date.format(value, 'Y-m-d') || value;
        
        filter.set('value', date);
    },

    onFilterValueDateSpecialKey: function (cmp, event) {
        if (event.getKey() != event.ENTER) {
            return false;
        }
        
        var filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record,
            // TODO: weird may need some unified date format
            date = Ext.Date.format(value, 'Y-m-d') || value;
        
        filter.set('value', date);

        this.application.fireEvent('refreshreport');
    },
    
    onFilterValueInputBlur: function (cmp, event, eOpts) {
        var filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record,
            value = cmp.getSubmitValue();
        
        filter.set('value', value);
    },

    // TODO: TOTALLY WRONG THERE IS NO SUCH THING AS FIELDCOMPARE
    // TODO: TOTALLY WRONG THERE IS NO SUCH THING AS FIELDCOMPARE
    // TODO: TOTALLY WRONG THERE IS NO SUCH THING AS FIELDCOMPARE
    onFilterFieldCompareInputBlur: function (cmp, event, eOpts) {
        var filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record,
            value = cmp.getSubmitValue();
        
        filter.set('fieldCompare', value);
    },
    
    onFilterValueInputSpecialKey: function (cmp, event) {
        if (event.getKey() != event.ENTER) {
            return false;
        }
        
        var filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record,
            value = cmp.getSubmitValue();
        
        filter.set('value', value);

        this.application.fireEvent('refreshreport');
    },

    onFilterValueSelect: function (cmp, records, eOpts) {
        var filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record,
            value = cmp.getSubmitValue();
        
        filter.set('value', value);

        this.application.fireEvent('refreshreport');
    }
});