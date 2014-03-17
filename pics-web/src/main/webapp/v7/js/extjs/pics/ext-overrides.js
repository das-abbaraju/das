Ext.define('PICS.Overrides', {
    requires: [
        'Ext.data.writer.Json',
        'Ext.menu.Menu',
        'Ext.Ajax',
        'Ext.data.proxy.Server',
        'Ext.ux.form.field.BoxSelect'
    ]
}, function () {
    Ext.log = function (message) {
        console.log(message);
    };

    /*
     * Set AJAX requests to timeout after one minute.
     */
    Ext.override(Ext.Ajax, {
        timeout: 60000
    });

    Ext.override(Ext.data.proxy.Server, {
        timeout: Ext.Ajax.timeout
    });

    /*
     * Fix for bug in ExtJS selection model causing it to return random results
     * http://www.sencha.com/forum/showthread.php?208453-4.0.2-Selection-model-returns-wrong-record-when-Grouping-feature-is-enabled
     */
    Ext.override(Ext.data.Store, {
      sort:function(){
         var me = this, groups, g;

         me.callOverridden(arguments);
         groups = me.getGroups();

         me.data.clear();

         Ext.Array.each(groups, function (group) {
            Ext.Array.each(group.children, function (child) {
               me.data.add(child.internalId, child);
            });
         });
         me.fireGroupChange();
      }
   });

    /**
     * Override of BoxSelect's override of the same method
     * The additional conditional check prevents emptying of the boxselect store if no actual records were selected.
     */
    Ext.override(Ext.ux.form.field.BoxSelect, {
        onListSelectionChange: function(list, selectedRecords) {
            var me = this,
            valueStore = me.valueStore,
            mergedRecords = [],
            i;

            // Only react to selection if it is not called from setValue, and if our list is
            // expanded (ignores changes to the selection model triggered elsewhere)
            if (selectedRecords.length && me.ignoreSelection <= 0 && me.isExpanded) {
                // Pull forward records that were already selected or are now filtered out of the store
                valueStore.each(function(rec) {
                    if (Ext.Array.contains(selectedRecords, rec) || me.isFilteredRecord(rec)) {
                        mergedRecords.push(rec);
                    }
                });
                mergedRecords = Ext.Array.merge(mergedRecords, selectedRecords);

                i = Ext.Array.intersect(mergedRecords, valueStore.getRange()).length;
                if ((i != mergedRecords.length) || (i != me.valueStore.getCount())) {
                    me.setValue(mergedRecords, false);
                    if (!me.multiSelect || !me.pinList) {
                        Ext.defer(me.collapse, 1, me);
                    }
                    if (valueStore.getCount() > 0) {
                        me.fireEvent('select', me, valueStore.getRange());
                    }
                }
                me.inputEl.focus();
                if (!me.pinList) {
                    me.inputEl.dom.value = '';
                }
                if (me.selectOnFocus) {
                    me.inputEl.dom.select();
                }
            }
        }
    });

    /*
     * This function overrides the default implementation of
     * json writer. Any hasMany relationships will be submitted
     * as nested objects
     */
    Ext.override(Ext.data.writer.Json, {
        getRecordData: function(record) {
            var me = this, i, association, childStore, data = {};
            data = me.callParent([record]);

            /* Iterate over all the hasMany associations */
            for (i = 0; i < record.associations.length; i++) {
                association = record.associations.get(i);
                if (association.type == 'hasMany')  {
                    data[association.name] = [];
                    childStore = eval('record.'+association.name+'()');

                    //Iterate over all the children in the current association
                    childStore.each(function(childRecord) {

                        //Recursively get the record data for children (depth first)
                        var childData = this.getRecordData.call(this, childRecord);
                        if (childRecord.dirty | childRecord.phantom | (childData != null)){
                            data[association.name].push(childData);
                            record.setDirty();
                        }
                    }, me);
                }
            }
            return data;
        }
    });

    /*
     * Adds three-pixel separation between menu and toolbar.
     */
    Ext.override(Ext.menu.Menu, {
        showBy: function(cmp, pos, off) {
            var me = this,
                xy,
                region;

            if (me.floating && cmp) {
                me.layout.autoSize = true;

                // show off-screen first so that we can calc position without causing a visual jump
                me.doAutoRender();
                delete me.needsLayout;

                // Component or Element
                cmp = cmp.el || cmp;

                // Convert absolute to floatParent-relative coordinates if necessary.
                xy = me.el.getAlignToXY(cmp, pos || me.defaultAlign, off);
                if (me.floatParent) {
                    region = me.floatParent.getTargetEl().getViewRegion();
                    xy[0] -= region.x;
                    xy[1] -= region.y;
                }

                // custom menu positioning
                xy[1] += 3;

                me.showAt(xy);
            }
            return me;
        },

        doConstrain : function() {
            var me = this,
                y = me.el.getY(),
                max, full,
                vector,
                returnY = y, normalY, parentEl, scrollTop, viewHeight;

            delete me.height;
            me.setSize();
            full = me.getHeight();
            if (me.floating) {
                //if our reset css is scoped, there will be a x-reset wrapper on this menu which we need to skip
                parentEl = Ext.fly(me.el.getScopeParent());
                scrollTop = parentEl.getScroll().top;
                viewHeight = parentEl.getViewSize().height;
                //Normalize y by the scroll position for the parent element.  Need to move it into the coordinate space
                //of the view.
                normalY = y - scrollTop;
                max = me.maxHeight ? me.maxHeight : viewHeight - normalY;
                if (full > viewHeight) {
                    max = viewHeight;
                    //Set returnY equal to (0,0) in view space by reducing y by the value of normalY
                    returnY = y - normalY;
                } else if (max < full) {
                    returnY = y - (full - max);
                    max = full;
                }
            }else{
                max = me.getHeight();
            }
            // Always respect maxHeight
            if (me.maxHeight){
                max = Math.min(me.maxHeight, max);
            }
            if (full > max && max > 0){
                me.layout.autoSize = false;
                me.setHeight(max);
                if (me.showSeparator){
                    me.iconSepEl.setHeight(me.layout.getRenderTarget().dom.scrollHeight);
                }
            }
            vector = me.getConstrainVector(me.el.getScopeParent());
            if (vector) {
                me.setPosition(me.getPosition()[0] + vector[0]);
            }
            me.el.setY(returnY);
        }
    });    
});
