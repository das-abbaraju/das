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
            #report_filters reportfilterbasemultiselect,\
            #report_filters textfield,\
            #report_filters numberfield,\
            #report_filters datefield,\
            #report_filters checkbox': {
                blur: this.blurFilter,
                focus: this.focusFilter
            },

            // remove filter
            'reportfilteroptions button[action=remove-filter]': {
                click: this.removeFilter
            },

            'reportfilteroptions button[action=toggle-negate-operator]': {
                click: this.onNegateOperatorToggleButtonClick
            },

            'reportfilteroptions toggleslide': {
                change: this.onToggleSlideChange
            },

            /******************************************
             * SAVING EDITS TO FILTER STORE + REFRESH *
             ******************************************/

            /*
             * When the OPERATOR changes
             */

            // All filters with mutable operator values
            '#report_filters combobox[name=operator]': {
                select: this.selectOperator
            },

            /*
             * When the VALUE changes
             */

            // autocomplete and shortlist multiselect filters
            '\
            #report_filters reportfilterbasemultiselect combobox[name=value],\
            #report_filters reportfilterbaseautocomplete combobox[name=value]': {
                select: this.syncFormAndReportFromFormCmp,
                render: this.renderBoxselectValueField,
                change: this.changeBoxselectValueField
            },

            // date filters
            'reportfilterbasedate [name=value]': {
                select: this.syncFormAndReportFromFormCmp
            },

            '#report_filters datefield[name=value]': {
                render: this.renderDateField
            },

            // boolean filters
            '#report_filters checkbox': {
                change: this.syncFormAndReportFromFormCmp,
                render: this.renderCheckbox
            },

            // string, accountid, number, and userid filters
            '#report_filters [name=value]': {
                blur: this.submitTextValueFieldOnBlur,
                specialkey: this.submitValueFieldOnEnterKeypress
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

    toggleNegateOperator: function (cmp) {
        var filter_cmp = cmp.up('reportfilter'),
            base_filter_cmp = filter_cmp.down('reportfilterbasefilter'),
            value_field = filter_cmp.down('[name=value]'),
            has_value = value_field.value.length,
            negate_operator_field = filter_cmp.down('[name=negate_operator]'),
            old_tooltip_html = cmp.getTooltipHtml(),
            new_tooltip_html;

        if (filter_cmp.hasCls('negated')) {
            negate_operator_field.setValue(false);
            filter_cmp.removeCls('negated');

            if (old_tooltip_html) {
                new_tooltip_html = old_tooltip_html.replace('Include', 'Ignore');
                cmp.setTooltipHtml(new_tooltip_html);
            }
        } else {
            negate_operator_field.setValue(true);
            filter_cmp.addCls('negated');

            if (old_tooltip_html) {
                new_tooltip_html = old_tooltip_html.replace('Ignore', 'Include');
                cmp.setTooltipHtml(new_tooltip_html);
            }
        }

        if (has_value) {
            this.syncFormAndReport(base_filter_cmp);
        }
    },

    onNegateOperatorToggleButtonClick: function (cmp, eOpts) {
        this.toggleNegateOperator(cmp);
    },

    onToggleSlideChange: function (cmp, eOpts) {
        this.toggleNegateOperator(cmp);
    },

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

    changeBoxselectValueField: function (cmp, newValue, oldValue, eOpts) {
        // Select does not fire when the user clicks removes the last item.
        // TODO: Create a third method called by both change and select.
        if (!newValue) {
            this.syncFormAndReportFromFormCmp(cmp);
        }
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
        // attach the filter record to the filter view
        this.loadFilter(cmp);
    },

    blurFilter: function (cmp, event, eOpts) {
        var filter_view = cmp.up('reportfilter');

        filter_view.removeCls('x-form-focus');
    },

    focusFilter: function (cmp, event, eOpts) {
        var filter_view = cmp.up('reportfilter');

        filter_view.addCls('x-form-focus');
    },

    // must disable change event from being fired so extra loadRecord does not throw excess change events on loading
    // http://extjs-tutorials.blogspot.com/2012/02/extjs-loadrecord-disable-events.html
    loadFilter: function (cmp) {
        var filter_input = cmp.down('reportfilterbasefilter'),
            filter_input_form = filter_input.getForm();

        filter_input_form.getFields().each(function (item, index, length) {
            item.suspendCheckChange++;
        });

        // attach filter record to "filter view form"
        filter_input_form.loadRecord(cmp.filter);

        filter_input_form.getFields().each(function (item, index, length) {
            item.suspendCheckChange--;
        });
    },

    // this method is to default checkboxes lastValue. this would have normally been set by the form field by default
    // through chainable constructors, but we are suspending the change event from being thrown calling loadRecord
    renderCheckbox: function (cmp, eOpts) {
        cmp.lastValue = cmp.getValue();
    },

    // this method is to default checkboxes lastValue. this would have normally been set by the form field by default
    // through chainable constructors, but we are suspending the change event from being thrown calling select
    renderBoxselectValueField: function (cmp, eOpts) {
        var filter_view = cmp.up('reportfilter'),
            filter_input = cmp.up('reportfilterbasefilter'),
            filter_record = filter_view.filter;

        cmp.lastValue = cmp.getRawValue().split(', ');

        filter_input.updateValueFieldStore(filter_record);
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
        var filter_input_view = cmp.down('reportfilterbasefilter'),
            combobox = filter_input_view.down('[name="operator"]'),
            operator = combobox && combobox.value;

        // attach tooltip on the name of each filter
        cmp.createTooltip();

        // hide value field depending on operator selected
        if (filter_input_view && typeof filter_input_view.updateValueFieldFromOperatorValue == 'function') {
            filter_input_view.updateValueFieldFromOperatorValue(operator);
        }
    },

    selectOperator: function (cmp, records, eOpts) {
        var filter_input_view = cmp.up('reportfilterbasefilter'),
            filter_input_form = filter_input_view.getForm(),
            operator = cmp.value,
            value_field = cmp.next('[name=value]'),
            filter = filter_input_form.getRecord(),
            filter_value = filter.get('value'),
            is_empty = value_field.isHidden();

        // hide value field depending on operator selected
        if (filter_input_view && typeof filter_input_view.updateValueFieldFromOperatorValue == 'function') {
            filter_input_view.updateValueFieldFromOperatorValue(operator);
        }

        // update filter record
        filter_input_form.updateRecord();

        // refresh report if filter value present--whether explicit or implied.
        if (filter_value || (value_field && value_field.isHidden()) || is_empty) {
            PICS.data.ServerCommunication.loadData();
        }
    },

    syncFormAndReport: function (filter_input_view) {
        filter_input_view.getForm().updateRecord();
        PICS.data.ServerCommunication.loadData();
    },

    syncFormAndReportFromFormCmp: function (cmp, records, eOpts) {
        var filter_input_view = cmp.up('reportfilterbasefilter');
        this.syncFormAndReport(filter_input_view);
    },

    submitTextValueFieldOnBlur: function (cmp, event, eOpts) {
        var filter_input_view = cmp.up('reportfilterbasefilter'),
            filter_input_form_record_value = filter_input_view.getForm().getRecord().get('value');

        if (cmp.value != filter_input_form_record_value) {
            this.submitValueField(cmp);
        }
    },

    submitValueFieldOnEnterKeypress: function (cmp, event) {
        if (event.getKey() != event.ENTER) {
            return false;
        }

        this.submitValueField(cmp);
    },

    submitValueField: function (value_field_cmp) {
        var filter_input_view = value_field_cmp.up('reportfilterbasefilter'),
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