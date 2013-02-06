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
                beforerender: this.beforeFilterRender,
                render: this.renderFilter
            },
            
            // render filter options
            'reportfilteroptions': {
                afterlayout: this.afterFilterOptionsLayout,
                afterrender: this.afterFilterOptionsRender,
                beforerender: this.beforeFilterOptionsRender
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
                click: this.removeFilter
            }
         });

        this.application.on({
            refreshfilters: this.refreshFilters,
            scope: this
        });

        Ext.EventManager.onWindowResize(this.updateRemoveButtonPositions, this);
    },

    /**
     * Filter Options
     */

    // customizes the filter options view after it gets placed by the layout manager
    afterFilterOptionsLayout: function (cmp, eOpts) {
        // TODO: This is a workaround. Two afterlayouts get called. In the first of them, the view has no filters. Why?
        var filters_view = this.getFilters();

        if (!filters_view) {
            return;
        }

        cmp.updateBodyHeight();
        
        cmp.updateFooterPosition();

        this.updateRemoveButtonPositions();
    },

    // TODO: figure out if this should be here
    // add the filter formula view after the filter options have been generated
    afterFilterOptionsRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore(),
        report = report_store.first(),
        filter_expression = report.get('filter_expression');
        
        if (filter_expression) {
            cmp.showFormula();
        }
    },
    
    // add filters to the filter options panel before its rendered
    beforeFilterOptionsRender: function (cmp, eOpts) {
        this.application.fireEvent('refreshfilters');
    },
    
    refreshFilters: function () {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_store = report.filters(),
            filter_options_view = this.getFilterOptions(),
            filter_formula = this.getFilterFormula();

        // remove all filter items from the filter options view
        filter_options_view.removeAll();

        // create new list of filters
        var filters_view = Ext.create('PICS.view.report.filter.Filters', {
            store: filter_store
        });

        // add new filters
        filter_options_view.add(filters_view);

        if (filter_formula) {
            filters_view.showFilterNumbers();
        }

        this.updateRemoveButtonPositions();
    },
    
    beforeFilterRender: function (cmp, eOpts) {
        var filter_input = cmp.down('reportfilterbasefilter'),
            filter_input_form = filter_input.getForm();
            
        filter_input_form.loadRecord(cmp.filter);
        
        // if autocomplete or multiselect
        if (filter_input.down('combobox[name=value]')) {
            var field_id = cmp.filter.get('field_id');
            
            filter_input.updateValueFieldStore(field_id);
        }
    },
    
    renderFilter: function (cmp, eOpts) {
        // attach tooltip on the name of each filter
        cmp.createTooltip();
    },

    onFilterOptionsCollapse: function (cmp, event, eOpts) {
        var filter_options = this.getFilterOptions();

        filter_options.collapse();
    },

    onFilterOptionsExpand: function (cmp, event, eOpts) {
        var filter_options = this.getFilterOptions();

        filter_options.expand();
    },
    
    /**
     * Add Filter
     */

    onAddFilter: function (cmp, event, eOpts) {
        this.application.fireEvent('openfiltermodal');
    },

    /**
     * Filter Formula
     */

    onFilterFormulaShow: function (cmp, event, eOpts) {
        var filter_options = cmp.up('reportfilteroptions');
        
        filter_options.showFormula();
    },

    // TODO: can optimize this by not refreshing the filter if the filter hasn't changed (report dirty flag)
    onFilterFormulaCancel: function (cmp, event, eOpts) {
        var filter_options = cmp.up('reportfilteroptions'),
            filters_view = this.getFilters(),
            report_store = this.getReportReportsStore(),
            report = report_store.first();

        // Reset the filter expression to null.
        report.setFilterExpression(null);

        // Hide the filter expression field.
        filter_options.showToolbar();
        
        filters_view.hideFilterNumbers();

        // Refresh the report with the expression no longer applied.
        PICS.data.ServerCommunication.loadData();
    },

    onFilterFormulaBeforeRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_formula_textfield = this.getFilterFormulaTextfield(),
            filters_view = this.getFilters(),
            filter_expression = report.get('filter_expression');

        if (filter_expression) {
            filter_formula_textfield.setValue(report.getFilterExpression());
        }

        filters_view.showFilterNumbers();
    },

    onFilterFormulaBlur: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_formula_textfield = this.getFilterFormulaTextfield(),
            filter_expression = filter_formula_textfield.getValue();
        
        report.setFilterExpression(filter_expression);

        PICS.data.ServerCommunication.loadData();
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

        PICS.data.ServerCommunication.loadData();
    },

    /**
     * Filters
     */

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
        
        if (operator_value == 'Empty' || operator_value == 'CurrentAccount') {
            filter_value_textfield.setValue('');
            filter_value_textfield.hide();
        } else {
            filter_value_textfield.show();
        }

        if (filter.get('value') != '') {
            PICS.data.ServerCommunication.loadData();
        }
    },

    removeFilter: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_store = report.filters(),
            filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record;

        filter_store.remove(filter);

        this.application.fireEvent('refreshfilters');

        if (filter.get('value') != '') {
            PICS.data.ServerCommunication.loadData();
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

        PICS.data.ServerCommunication.loadData();
    },
    
    onFilterValueInputBlur: function (cmp, event, eOpts) {
        var filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record,
            value = cmp.getSubmitValue();

        filter.set('value', value);
    },

    onFilterValueInputSpecialKey: function (cmp, event) {
        if (event.getKey() != event.ENTER) {
            return false;
        }
        
        var filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record,
            value = cmp.getSubmitValue();

        filter.set('value', value);

        PICS.data.ServerCommunication.loadData();
    },

    selectFilterCheckbox: function (cmp, records, eOpts) {
        var filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record,
            yes_checkbox = filter_panel.down('checkbox[boxLabel=Yes]'),
            no_checkbox = filter_panel.down('checkbox[boxLabel=No]'),
            is_yes_checked = yes_checkbox.value,
            is_no_checked = no_checkbox.value,
            filter_value = '';
        
        // TODO: not verified in handshake
        if (is_yes_checked && is_no_checked) {
            filter_value = 'or';
        } else if (is_yes_checked) {
            filter_value = 'true';
        } else if (is_no_checked) {
            filter_value = 'false';
        } else if (cmp.boxLabel == 'Yes') {
            no_checkbox.setValue(true);
            
            filter_value = 'false';
        } else if (cmp.boxLabel == 'No') {
            yes_checkbox.setValue(true);
            
            filter_value = 'true';
        }

        filter.set('value', filter_value);

        PICS.data.ServerCommunication.loadData();
    },

    onFilterValueSelect: function (cmp, records, eOpts) {
        var filter_panel = cmp.up('reportfilter'),
            filter = filter_panel.record,
            value = cmp.getSubmitValue();

        filter.set('value', value);

        PICS.data.ServerCommunication.loadData();
    },

    // TODO: check requirements, but is here to fix view for filters that vertically go past browser height
    updateRemoveButtonPositions: function () {
        var remove_filter_elements = Ext.select('.remove-filter').elements;

        if (remove_filter_elements.length) {
            var filter_options = this.getFilterOptions(),
                scrollbar_width = Ext.getScrollbarSize().width,
                scrollbar_left = filter_options.width - scrollbar_width,
                scrollbar_visible = filter_options.body.dom.scrollHeight > filter_options.body.dom.clientHeight ? true : false,
                button_left = parseInt(remove_filter_elements[0].style.left),
                button_obscured = button_left + 7 >= scrollbar_left ? true : false;

            if (scrollbar_visible && button_obscured) {
                button_left = button_left - scrollbar_width;
                for (var i = 0; i < remove_filter_elements.length; i++) {
                    remove_filter_elements[i].style.left = button_left + 'px';
                }
            }
        }
    }
});