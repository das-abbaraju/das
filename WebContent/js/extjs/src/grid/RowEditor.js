/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

Commercial Usage
Licensees holding valid commercial licenses may use this file in accordance with the Commercial Software License Agreement provided with the Software or, alternatively, in accordance with the terms contained in a written agreement between you and Sencha.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
// Currently has the following issues:
// - Does not handle postEditValue
// - Fields without editors need to sync with their values in Store
// - starting to edit another record while already editing and dirty should probably prevent it
// - aggregating validation messages
// - tabIndex is not managed bc we leave elements in dom, and simply move via positioning
// - layout issues when changing sizes/width while hidden (layout bug)

/**
 * @class Ext.grid.RowEditor
 * @extends Ext.form.Panel
 *
 * Internal utility class used to provide row editing functionality. For developers, they should use
 * the RowEditing plugin to use this functionality with a grid.
 *
 * @ignore
 */
Ext.define('Ext.grid.RowEditor', {
    extend: 'Ext.form.Panel',
    requires: [
        'Ext.tip.ToolTip',
        'Ext.util.HashMap',
        'Ext.util.KeyNav'
    ],

    saveBtnText  : 'Update',
    cancelBtnText: 'Cancel',
    errorsText: 'Errors',
    dirtyText: 'You need to commit or cancel your changes',

    lastScrollLeft: 0,
    lastScrollTop: 0,

    border: false,
    
    // Change the hideMode to offsets so that we get accurate measurements when
    // the roweditor is hidden for laying out things like a TriggerField.
    hideMode: 'offsets',

    initComponent: function() {
        var me = this,
            form;

        me.cls = Ext.baseCSSPrefix + 'grid-row-editor';

        me.layout = {
            type: 'hbox',
            align: 'middle'
        };

        // Maintain field-to-column mapping
        // It's easy to get a field from a column, but not vice versa
        me.columns = Ext.create('Ext.util.HashMap');
        me.columns.getKey = function(columnHeader) {
            var f;
            if (columnHeader.getEditor) {
                f = columnHeader.getEditor();
                if (f) {
                    return f.id;
                }
            }
            return columnHeader.id;
        };
        me.mon(me.columns, {
            add: me.onFieldAdd,
            remove: me.onFieldRemove,
            replace: me.onFieldReplace,
            scope: me
        });

        me.callParent(arguments);

        if (me.fields) {
            me.setField(me.fields);
            delete me.fields;
        }

        form = me.getForm();
        form.trackResetOnLoad = true;
    },

    onFieldChange: function() {
        var me = this,
            form = me.getForm(),
            valid = form.isValid();
        if (me.errorSummary && me.isVisible()) {
            me[valid ? 'hideToolTip' : 'showToolTip']();
        }
        if (me.floatingButtons) {
            me.floatingButtons.child('#update').setDisabled(!valid);
        }
        me.isValid = valid;
    },

    afterRender: function() {
        var me = this,
            plugin = me.editingPlugin;

        me.callParent(arguments);
        me.mon(me.renderTo, 'scroll', me.onCtScroll, me, { buffer: 100 });

        // Prevent from bubbling click events to the grid view
        me.mon(me.el, {
            click: Ext.emptyFn,
            stopPropagation: true
        });

        me.el.swallowEvent([
            'keypress',
            'keydown'
        ]);

        me.keyNav = Ext.create('Ext.util.KeyNav', me.el, {
            enter: plugin.completeEdit,
            esc: plugin.onEscKey,
            scope: plugin
        });

        me.mon(plugin.view, {
            beforerefresh: me.onBeforeViewRefresh,
            refresh: me.onViewRefresh,
            scope: me
        });
    },

    onBeforeViewRefresh: function(view) {
        var me = this,
            viewDom = view.el.dom;

        if (me.el.dom.parentNode === viewDom) {
            viewDom.removeChild(me.el.dom);
        }
    },

    onViewRefresh: function(view) {
        var me = this,
            viewDom = view.el.dom,
            context = me.context,
            idx;

        viewDom.appendChild(me.el.dom);

        // Recover our row node after a view refresh
        if (context && (idx = context.store.indexOf(context.record)) >= 0) {
            context.row = view.getNode(idx);
            me.reposition();
            if (me.tooltip && me.tooltip.isVisible()) {
                me.tooltip.setTarget(context.row);
            }
        } else {
            me.editingPlugin.cancelEdit();
        }
    },

    onCtScroll: function(e, target) {
        var me = this,
            scrollTop  = target.scrollTop,
            scrollLeft = target.scrollLeft;

        if (scrollTop !== me.lastScrollTop) {
            me.lastScrollTop = scrollTop;
            if ((me.tooltip && me.tooltip.isVisible()) || me.hiddenTip) {
                me.repositionTip();
            }
        }
        if (scrollLeft !== me.lastScrollLeft) {
            me.lastScrollLeft = scrollLeft;
            me.reposition();
        }
    },

    onColumnAdd: function(column) {
        this.setField(column);
    },

    onColumnRemove: function(column) {
        this.columns.remove(column);
    },

    onColumnResize: function(column, width) {
        column.getEditor().setWidth(width - 2);
        if (this.isVisible()) {
            this.reposition();
        }
    },

    onColumnHide: function(column) {
        column.getEditor().hide();
        if (this.isVisible()) {
            this.reposition();
        }
    },

    onColumnShow: function(column) {
        var field = column.getEditor();
        field.setWidth(column.getWidth() - 2).show();
        if (this.isVisible()) {
            this.reposition();
        }
    },

    onColumnMove: function(column, fromIdx, toIdx) {
        var field = column.getEditor();
        if (this.items.indexOf(field) != toIdx) {
            this.move(fromIdx, toIdx);
        }
    },

    onFieldAdd: function(map, fieldId, column) {
        var me = this,
            colIdx = me.editingPlugin.grid.headerCt.getHeaderIndex(column),
            field = column.getEditor({ xtype: 'displayfield' });

        me.insert(colIdx, field);
    },

    onFieldRemove: function(map, fieldId, column) {
        var me = this,
            field = column.getEditor(),
            fieldEl = field.el;
        me.remove(field, false);
        if (fieldEl) {
            fieldEl.remove();
        }
    },

    onFieldReplace: function(map, fieldId, column, oldColumn) {
        var me = this;
        me.onFieldRemove(map, fieldId, oldColumn);
    },

    clearFields: function() {
        var me = this,
            map = me.columns;
        map.each(function(fieldId) {
            map.removeAtKey(fieldId);
        });
    },

    getFloatingButtons: function() {
        var me = this,
            cssPrefix = Ext.baseCSSPrefix,
            btnsCss = cssPrefix + 'grid-row-editor-buttons',
            plugin = me.editingPlugin,
            btns;

        if (!me.floatingButtons) {
            btns = me.floatingButtons = Ext.create('Ext.Container', {
                renderTpl: [
                    '<div class="{baseCls}-ml"></div>',
                    '<div class="{baseCls}-mr"></div>',
                    '<div class="{baseCls}-bl"></div>',
                    '<div class="{baseCls}-br"></div>',
                    '<div class="{baseCls}-bc"></div>'
                ],

                renderTo: me.el,
                baseCls: btnsCss,
                layout: {
                    type: 'hbox',
                    align: 'middle'
                },
                defaults: {
                    margins: '0 1 0 1'
                },
                items: [{
                    itemId: 'update',
                    flex: 1,
                    xtype: 'button',
                    handler: plugin.completeEdit,
                    scope: plugin,
                    text: me.saveBtnText,
                    disabled: !me.isValid
                }, {
                    flex: 1,
                    xtype: 'button',
                    handler: plugin.cancelEdit,
                    scope: plugin,
                    text: me.cancelBtnText
                }]
            });

            // Prevent from bubbling click events to the grid view
            me.mon(btns.el, {
                // BrowserBug: Opera 11.01
                //   causes the view to scroll when a button is focused from mousedown
                mousedown: Ext.emptyFn,
                click: Ext.emptyFn,
                stopEvent: true
            });
        }
        return me.floatingButtons;
    },

    reposition: function(animateConfig) {
        var me = this,
            context = me.context,
            row = context && Ext.get(context.row),
            btns = me.getFloatingButtons(),
            btnEl = btns.el,
            grid = me.editingPlugin.grid,
            viewEl = grid.view.el,
            scroller = grid.verticalScroller,

            // always get data from ColumnModel as its what drives
            // the GridView's sizing
            mainBodyWidth = grid.headerCt.getFullWidth(),
            scrollerWidth = grid.getWidth(),

            // use the minimum as the columns may not fill up the entire grid
            // width
            width = Math.min(mainBodyWidth, scrollerWidth),
            scrollLeft = grid.view.el.dom.scrollLeft,
            btnWidth = btns.getWidth(),
            left = (width - btnWidth) / 2 + scrollLeft,
            y, rowH, newHeight,

            invalidateScroller = function() {
                if (scroller) {
                    scroller.invalidate();
                    btnEl.scrollIntoView(viewEl, false);
                }
                if (animateConfig && animateConfig.callback) {
                    animateConfig.callback.call(animateConfig.scope || me);
                }
            };

        // need to set both top/left
        if (row && Ext.isElement(row.dom)) {
            // Bring our row into view if necessary, so a row editor that's already
            // visible and animated to the row will appear smooth
            row.scrollIntoView(viewEl, false);

            // Get the y position of the row relative to its top-most static parent.
            // offsetTop will be relative to the table, and is incorrect
            // when mixed with certain grid features (e.g., grouping).
            y = row.getXY()[1] - 5;
            rowH = row.getHeight();
            newHeight = rowH + 10;

            // IE doesn't set the height quite right.
            // This isn't a border-box issue, it even happens
            // in IE8 and IE7 quirks.
            // TODO: Test in IE9!
            if (Ext.isIE) {
                newHeight += 2;
            }

            // Set editor height to match the row height
            if (me.getHeight() != newHeight) {
                me.setHeight(newHeight);
                me.el.setLeft(0);
            }

            if (animateConfig) {
                var animObj = {
                    to: {
                        y: y
                    },
                    duration: animateConfig.duration || 125,
                    listeners: {
                        afteranimate: function() {
                            invalidateScroller();
                            y = row.getXY()[1] - 5;
                            me.el.setY(y);
                        }
                    }
                };
                me.animate(animObj);
            } else {
                me.el.setY(y);
                invalidateScroller();
            }
        }
        if (me.getWidth() != mainBodyWidth) {
            me.setWidth(mainBodyWidth);
        }
        btnEl.setLeft(left);
    },

    getEditor: function(fieldInfo) {
        var me = this;

        if (Ext.isNumber(fieldInfo)) {
            // Query only form fields. This just future-proofs us in case we add
            // other components to RowEditor later on.  Don't want to mess with
            // indices.
            return me.query('>[isFormField]')[fieldInfo];
        } else if (fieldInfo instanceof Ext.grid.column.Column) {
            return fieldInfo.getEditor();
        }
    },

    removeField: function(field) {
        var me = this;

        // Incase we pass a column instead, which is fine
        field = me.getEditor(field);
        me.mun(field, 'validitychange', me.onValidityChange, me);

        // Remove field/column from our mapping, which will fire the event to
        // remove the field from our container
        me.columns.removeKey(field.id);
    },

    setField: function(column) {
        var me = this,
            field;

        if (Ext.isArray(column)) {
            Ext.Array.forEach(column, me.setField, me);
            return;
        }

        // Get a default display field if necessary
        field = column.getEditor(null, {
            xtype: 'displayfield',
            // Default display fields will not return values. This is done because
            // the display field will pick up column renderers from the grid.
            getModelData: function() {
                return null;
            }
        });
        field.margins = '0 0 0 2';
        field.setWidth(column.getDesiredWidth() - 2);
        me.mon(field, 'change', me.onFieldChange, me);

        // Maintain mapping of fields-to-columns
        // This will fire events that maintain our container items
        me.columns.add(field.id, column);
        if (column.hidden) {
            me.onColumnHide(column);
        }
        if (me.isVisible() && me.context) {
            me.renderColumnData(field, me.context.record);
        }
    },

    loadRecord: function(record) {
        var me = this,
            form = me.getForm();
        form.loadRecord(record);
        if (form.isValid()) {
            me.hideToolTip();
        } else {
            me.showToolTip();
        }

        // render display fields so they honor the column renderer/template
        Ext.Array.forEach(me.query('>displayfield'), function(field) {
            me.renderColumnData(field, record);
        }, me);
    },

    renderColumnData: function(field, record) {
        var me = this,
            grid = me.editingPlugin.grid,
            headerCt = grid.headerCt,
            view = grid.view,
            store = view.store,
            column = me.columns.get(field.id),
            value = record.get(column.dataIndex);

        // honor our column's renderer (TemplateHeader sets renderer for us!)
        if (column.renderer) {
            var metaData = { tdCls: '', style: '' },
                rowIdx = store.indexOf(record),
                colIdx = headerCt.getHeaderIndex(column);

            value = column.renderer.call(
                column.scope || headerCt.ownerCt,
                value,
                metaData,
                record,
                rowIdx,
                colIdx,
                store,
                view
            );
        }

        field.setRawValue(value);
        field.resetOriginalValue();
    },

    beforeEdit: function() {
        var me = this;

        if (me.isVisible() && !me.autoCancel && me.isDirty()) {
            me.showToolTip();
            return false;
        }
    },

    /**
     * Start editing the specified grid at the specified position.
     * @param {Ext.data.Model} record The Store data record which backs the row to be edited.
     * @param {Ext.data.Model} columnHeader The Column object defining the column to be edited.
     */
    startEdit: function(record, columnHeader) {
        var me = this,
            grid = me.editingPlugin.grid,
            view = grid.getView(),
            store = grid.store,
            context = me.context = Ext.apply(me.editingPlugin.context, {
                view: grid.getView(),
                store: store
            });

        // make sure our row is selected before editing
        context.grid.getSelectionModel().select(record);

        // Reload the record data
        me.loadRecord(record);

        if (!me.isVisible()) {
            me.show();
            me.focusContextCell();
        } else {
            me.reposition({
                callback: this.focusContextCell
            });
        }
    },

    // Focus the cell on start edit based upon the current context
    focusContextCell: function() {
        var field = this.getEditor(this.context.colIdx);
        if (field && field.focus) {
            field.focus();
        }
    },

    cancelEdit: function() {
        var me = this,
            form = me.getForm();

        me.hide();
        form.clearInvalid();
        form.reset();
    },

    completeEdit: function() {
        var me = this,
            form = me.getForm();

        if (!form.isValid()) {
            return;
        }

        form.updateRecord(me.context.record);
        me.hide();
        return true;
    },

    onShow: function() {
        var me = this;
        me.callParent(arguments);
        me.reposition();
    },

    onHide: function() {
        var me = this;
        me.callParent(arguments);
        me.hideToolTip();
        me.invalidateScroller();
        if (me.context) {
            me.context.view.focus();
            me.context = null;
        }
    },

    isDirty: function() {
        var me = this,
            form = me.getForm();
        return form.isDirty();
    },

    getToolTip: function() {
        var me = this,
            tip;

        if (!me.tooltip) {
            tip = me.tooltip = Ext.createWidget('tooltip', {
                cls: Ext.baseCSSPrefix + 'grid-row-editor-errors',
                title: me.errorsText,
                autoHide: false,
                closable: true,
                closeAction: 'disable',
                anchor: 'left'
            });
        }
        return me.tooltip;
    },

    hideToolTip: function() {
        var me = this,
            tip = me.getToolTip();
        if (tip.rendered) {
            tip.disable();
        }
        me.hiddenTip = false;
    },

    showToolTip: function() {
        var me = this,
            tip = me.getToolTip(),
            context = me.context,
            row = Ext.get(context.row),
            viewEl = context.grid.view.el;

        tip.setTarget(row);
        tip.showAt([-10000, -10000]);
        tip.body.update(me.getErrors());
        tip.mouseOffset = [viewEl.getWidth() - row.getWidth() + me.lastScrollLeft + 15, 0];
        me.repositionTip();
        tip.doLayout();
        tip.enable();
    },

    repositionTip: function() {
        var me = this,
            tip = me.getToolTip(),
            context = me.context,
            row = Ext.get(context.row),
            viewEl = context.grid.view.el,
            viewHeight = viewEl.getHeight(),
            viewTop = me.lastScrollTop,
            viewBottom = viewTop + viewHeight,
            rowHeight = row.getHeight(),
            rowTop = row.dom.offsetTop,
            rowBottom = rowTop + rowHeight;

        if (rowBottom > viewTop && rowTop < viewBottom) {
            tip.show();
            me.hiddenTip = false;
        } else {
            tip.hide();
            me.hiddenTip = true;
        }
    },

    getErrors: function() {
        var me = this,
            dirtyText = !me.autoCancel && me.isDirty() ? me.dirtyText + '<br />' : '',
            errors = [];

        Ext.Array.forEach(me.query('>[isFormField]'), function(field) {
            errors = errors.concat(
                Ext.Array.map(field.getErrors(), function(e) {
                    return '<li>' + e + '</li>';
                })
            );
        }, me);

        return dirtyText + '<ul>' + errors.join('') + '</ul>';
    },

    invalidateScroller: function() {
        var me = this,
            context = me.context,
            scroller = context.grid.verticalScroller;

        if (scroller) {
            scroller.invalidate();
        }
    }
});
