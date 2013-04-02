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
                click: this.collapseFilterOptions
            },

            // expand filter options
            '#report_filter_options_expand': {
                click: this.expandFilterOptions
            },

            // add filter
            'reportfilteroptions button[action=add-filter]': {
                click: this.addFilter
            },

            // show filter formula
            'reportfiltertoolbar button[action=show-filter-formula]': {
                click: this.showFilterFormula
            },

            // load filter formula expression
            'reportfilterformula': {
                beforerender: this.beforeFilterFormulaRender
            },

            // save filter formula expression
            'reportfilterformula textfield[name=filter_formula]': {
                blur: this.blurFilterFormula,
                specialkey: this.submitFilterFormula
            },

            // hide filter formula
            'reportfilterformula button[action=cancel]': {
                click: this.cancelFilterFormula
            },

            // show filter-number and remove-filter on filter focus
            '\
            #report_filters combobox,\
            #report_filters textfield,\
            #report_filters numberfield,\
            #report_filters datefield,\
            #report_filters checkbox\
            ': {
                blur: this.blurFilter,
                focus: this.focusFilter
            },

            // saving edits to filter store + refresh
            '#report_filters combobox[name=value]': {
                // Unlike "select," the change event also fires when the user removes the last item.
                change: this.selectValueField
            },

            '#report_filters combobox[name=operator]': {
                select: this.selectOperator
            },

            // saving edits to filter store + refresh
            'reportfilterbasedate [name=value]': {
                select: this.selectValueField
            },

            // saving edits to date filter store + refresh
            '#report_filters datefield[name=value]': {
                render: this.renderDateField
            },
            
            // saving edits to non-date filter store + refresh
            '#report_filters [name=value]': {
                blur: this.blurValueField,
                specialkey: this.submitValueField
            },
            
            // saving edits to filter store + refresh
            '#report_filters checkbox': {
                change: this.selectValueField
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

        // TODO: ???
        this.updateRemoveButtonPositions();
    },

    // TODO: figure out if this should be here
    // TODO: figure out if this should be here
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
    
    collapseFilterOptions: function (cmp, event, eOpts) {
        var filter_options_view = this.getFilterOptions();

        filter_options_view.collapse();
    },

    expandFilterOptions: function (cmp, event, eOpts) {
        var filter_options_view = this.getFilterOptions();

        filter_options_view.expand();
    },
    
    /**
     * Filter Formula
     */

    beforeFilterFormulaRender: function (cmp, eOpts) {
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

    blurFilterFormula: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_formula_textfield = this.getFilterFormulaTextfield(),
            filter_expression = filter_formula_textfield.getValue(),
            is_new_filter_expression = report.isNewFilterExpression(filter_expression);
        
        if (is_new_filter_expression) {
            report.setFilterExpression(filter_expression);

            PICS.data.ServerCommunication.loadData();
        }
    },
    
    // TODO: can optimize this by not refreshing the filter if the filter hasn't changed (report dirty flag)
    // TODO: can optimize this by not refreshing the filter if the filter hasn't changed (report dirty flag)
    // TODO: can optimize this by not refreshing the filter if the filter hasn't changed (report dirty flag)
    cancelFilterFormula: function (cmp, event, eOpts) {
        var filter_options = cmp.up('reportfilteroptions'),
            filters_view = this.getFilters(),
            report_store = this.getReportReportsStore(),
            report = report_store.first(),
            current_expression = report.get('filter_expression');

        // Hide the filter expression field.
        filter_options.showToolbar();
        
        filters_view.hideFilterNumbers();

        // Clear the filter expression and reload the report if it isn't already cleared.
        if (current_expression != '') {
            report.setFilterExpression('');

            PICS.data.ServerCommunication.loadData();
        }
    },
    
    showFilterFormula: function (cmp, event, eOpts) {
        var filter_options = cmp.up('reportfilteroptions');
        
        filter_options.showFormula();
    },

    submitFilterFormula: function (cmp, event) {
        if (event.getKey() != event.ENTER) {
            return false;
        }
        
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_formula_textfield = this.getFilterFormulaTextfield(),
            filter_expression = filter_formula_textfield.getValue(),
            is_new_filter_expression = report.isNewFilterExpression(filter_expression);
        
        if (is_new_filter_expression) {
            report.setFilterExpression(filter_expression);
            PICS.data.ServerCommunication.loadData();
        }
    },

    /**
     * Filter
     */
    
    addFilter: function (cmp, event, eOpts) {
        this.application.fireEvent('openfiltermodal');
    },
    
    beforeFilterRender: function (cmp, eOpts) {
        var filter_input = cmp.down('reportfilterbasefilter'),
            filter_input_form = filter_input.getForm(),
            is_autocomplete = cmp.down('reportfilterbaseautocomplete'),
            is_multiselect = cmp.down('reportfilterbasemultiselect');
        
        // attach filter record to "filter view form"
        filter_input_form.loadRecord(cmp.filter);
        
        // dynamically load value store for multiselect and autocomplete
        if (is_autocomplete) {
            filter_input.updateValueFieldStore(cmp.filter);
        } else if (is_multiselect) {
            filter_input.updateValueFieldStore(cmp.filter);
        }
    },
    
    blurFilter: function (cmp, event, eOpts) {
        var filter_view = cmp.up('reportfilter');
        
        filter_view.removeCls('x-form-focus');
    },

    blurValueField: function (cmp, event, eOpts) {
        var filter_input_view = cmp.up('reportfilterbasefilter'),
            filter_input_form = filter_input_view.getForm();
    
        filter_input_form.updateRecord();
    },
    
    focusFilter: function (cmp, event, eOpts) {
        var filter_view = cmp.up('reportfilter');

        filter_view.addCls('x-form-focus');
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

        // TODO: ???
        this.updateRemoveButtonPositions();
    },
    
    removeFilter: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_store = report.filters(),
            filter_view = cmp.up('reportfilter'),
            filter = filter_view.filter,
            filter_value = filter.get('value');

        filter_store.remove(filter);

        this.application.fireEvent('refreshfilters');

        if (filter_value != '') {
            PICS.data.ServerCommunication.loadData();
        }
    },

    renderDateField: function (cmp, eOpts) {
        var filter_input_view = cmp.up('reportfilterbasefilter'),
            filter_input_form = filter_input_view.getForm(),
            filter = filter_input_form.getRecord(),
            filter_value = filter.get('value');

        // by-pass setValue validation by modifying dom directly
        cmp.el.down('input[name="value"]').dom.value = filter_value;
    },

    renderFilter: function (cmp, eOpts) {
        // attach tooltip on the name of each filter
        cmp.createTooltip();
    },
    
    selectOperator: function (cmp, records, eOpts) {
        var filter_input_view = cmp.up('reportfilterbasefilter'),
            filter_input_form = filter_input_view.getForm(),
            operator = cmp.value,
            value_field = cmp.next('[name=value]'),
            filter = filter_input_form.getRecord(),
            filter_value = filter.get('value');

        // hide value field depending on operator selected
        if (typeof filter_input_view.updateValueFieldFromOperatorValue == 'function') {
            filter_input_view.updateValueFieldFromOperatorValue(operator);
        }
        
        // update filter record
        filter_input_form.updateRecord();

        // refresh report if filter value present--whether explicit or implied.
        if ((value_field && value_field.isHidden()) || (filter_value != null && filter_value != '')) {
            PICS.data.ServerCommunication.loadData();
        }
    },
    
    selectValueField: function (cmp, newValue, oldValue, eOpts) {
        var filter_input_view = cmp.up('reportfilterbasefilter'),
            filter_input_form = filter_input_view.getForm();
    
        // Abort if we are pre-selecting. (It will be undefined.)
        if (!oldValue) {
            return;
        }

        filter_input_form.updateRecord();

        PICS.data.ServerCommunication.loadData();
    },

    submitValueField: function (cmp, event) {
        if (event.getKey() != event.ENTER) {
            return false;
        }
        
        var filter_input_view = cmp.up('reportfilterbasefilter'),
            filter_input_form = filter_input_view.getForm();
    
        filter_input_form.updateRecord();

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