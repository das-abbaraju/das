/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
 * This plugin provides drag and/or drop functionality for a GridView.
 *
 * It creates a specialized instance of {@link Ext.dd.DragZone DragZone} which knows how to drag out of a {@link
 * Ext.grid.View GridView} and loads the data object which is passed to a cooperating {@link Ext.dd.DragZone DragZone}'s
 * methods with the following properties:
 *
 * - `copy` : Boolean
 *
 *   The value of the GridView's `copy` property, or `true` if the GridView was configured with `allowCopy: true` _and_
 *   the control key was pressed when the drag operation was begun.
 *
 * - `view` : GridView
 *
 *   The source GridView from which the drag originated.
 *
 * - `ddel` : HtmlElement
 *
 *   The drag proxy element which moves with the mouse
 *
 * - `item` : HtmlElement
 *
 *   The GridView node upon which the mousedown event was registered.
 *
 * - `records` : Array
 *
 *   An Array of {@link Ext.data.Model Model}s representing the selected data being dragged from the source GridView.
 *
 * It also creates a specialized instance of {@link Ext.dd.DropZone} which cooperates with other DropZones which are
 * members of the same ddGroup which processes such data objects.
 *
 * Adding this plugin to a view means that two new events may be fired from the client GridView, `{@link #beforedrop
 * beforedrop}` and `{@link #drop drop}`
 *
 *     @example
 *     Ext.create('Ext.data.Store', {
 *         storeId:'simpsonsStore',
 *         fields:['name'],
 *         data: [["Lisa"], ["Bart"], ["Homer"], ["Marge"]],
 *         proxy: {
 *             type: 'memory',
 *             reader: 'array'
 *         }
 *     });
 *
 *     Ext.create('Ext.grid.Panel', {
 *         store: 'simpsonsStore',
 *         columns: [
 *             {header: 'Name',  dataIndex: 'name', flex: true}
 *         ],
 *         viewConfig: {
 *             plugins: {
 *                 ptype: 'gridviewdragdrop',
 *                 dragText: 'Drag and drop to reorganize'
 *             }
 *         },
 *         height: 200,
 *         width: 400,
 *         renderTo: Ext.getBody()
 *     });
 */
Ext.define('Ext.grid.plugin.DragDrop', {
    extend: 'Ext.AbstractPlugin',
    alias: 'plugin.gridviewdragdrop',

    uses: [
        'Ext.view.DragZone',
        'Ext.grid.ViewDropZone'
    ],

    /**
     * @event beforedrop
     * **This event is fired through the GridView. Add listeners to the GridView object**
     *
     * Fired when a drop gesture has been triggered by a mouseup event in a valid drop position in the GridView.
     *
     * @param {HTMLElement} node The GridView node **if any** over which the mouse was positioned.
     *
     * Returning `false` to this event signals that the drop gesture was invalid, and if the drag proxy will animate
     * back to the point from which the drag began.
     *
     * Returning `0` To this event signals that the data transfer operation should not take place, but that the gesture
     * was valid, and that the repair operation should not take place.
     *
     * Any other return value continues with the data transfer operation.
     *
     * @param {Object} data The data object gathered at mousedown time by the cooperating {@link Ext.dd.DragZone
     * DragZone}'s {@link Ext.dd.DragZone#getDragData getDragData} method it contains the following properties:
     *
     * - copy : Boolean
     *
     *   The value of the GridView's `copy` property, or `true` if the GridView was configured with `allowCopy: true` and
     *   the control key was pressed when the drag operation was begun
     *
     * - view : GridView
     *
     *   The source GridView from which the drag originated.
     *
     * - ddel : HtmlElement
     *
     *   The drag proxy element which moves with the mouse
     *
     * - item : HtmlElement
     *
     *   The GridView node upon which the mousedown event was registered.
     *
     * - records : Array
     *
     *   An Array of {@link Ext.data.Model Model}s representing the selected data being dragged from the source GridView.
     *
     * @param {Ext.data.Model} overModel The Model over which the drop gesture took place.
     *
     * @param {String} dropPosition `"before"` or `"after"` depending on whether the mouse is above or below the midline
     * of the node.
     *
     * @param {Function} dropFunction
     *
     * A function to call to complete the data transfer operation and either move or copy Model instances from the
     * source View's Store to the destination View's Store.
     *
     * This is useful when you want to perform some kind of asynchronous processing before confirming the drop, such as
     * an {@link Ext.window.MessageBox#confirm confirm} call, or an Ajax request.
     *
     * Return `0` from this event handler, and call the `dropFunction` at any time to perform the data transfer.
     */

    /**
     * @event drop
     * **This event is fired through the GridView. Add listeners to the GridView object** Fired when a drop operation
     * has been completed and the data has been moved or copied.
     *
     * @param {HTMLElement} node The GridView node **if any** over which the mouse was positioned.
     *
     * @param {Object} data The data object gathered at mousedown time by the cooperating {@link Ext.dd.DragZone
     * DragZone}'s {@link Ext.dd.DragZone#getDragData getDragData} method it contains the following properties:
     *
     * - copy : Boolean
     *
     *   The value of the GridView's `copy` property, or `true` if the GridView was configured with `allowCopy: true` and
     *   the control key was pressed when the drag operation was begun
     *
     * - view : GridView
     *
     *   The source GridView from which the drag originated.
     *
     * - ddel : HtmlElement
     *
     *   The drag proxy element which moves with the mouse
     *
     * - item : HtmlElement
     *
     *   The GridView node upon which the mousedown event was registered.
     *
     * - records : Array
     *
     *   An Array of {@link Ext.data.Model Model}s representing the selected data being dragged from the source GridView.
     *
     * @param {Ext.data.Model} overModel The Model over which the drop gesture took place.
     *
     * @param {String} dropPosition `"before"` or `"after"` depending on whether the mouse is above or below the midline
     * of the node.
     */

    dragText : '{0} selected row{1}',

    /**
     * @cfg {String} ddGroup
     * A named drag drop group to which this object belongs. If a group is specified, then both the DragZones and
     * DropZone used by this plugin will only interact with other drag drop objects in the same group.
     */
    ddGroup : "GridDD",

    /**
     * @cfg {String} dragGroup
     * The ddGroup to which the DragZone will belong.
     *
     * This defines which other DropZones the DragZone will interact with. Drag/DropZones only interact with other
     * Drag/DropZones which are members of the same ddGroup.
     */

    /**
     * @cfg {String} dropGroup
     * The ddGroup to which the DropZone will belong.
     *
     * This defines which other DragZones the DropZone will interact with. Drag/DropZones only interact with other
     * Drag/DropZones which are members of the same ddGroup.
     */

    /**
     * @cfg {Boolean} enableDrop
     * False to disallow the View from accepting drop gestures.
     */
    enableDrop: true,

    /**
     * @cfg {Boolean} enableDrag
     * False to disallow dragging items from the View.
     */
    enableDrag: true,

    init : function(view) {
        view.on('render', this.onViewRender, this, {single: true});
    },

    /**
     * @private
     * AbstractComponent calls destroy on all its plugins at destroy time.
     */
    destroy: function() {
        Ext.destroy(this.dragZone, this.dropZone);
    },

    enable: function() {
        var me = this;
        if (me.dragZone) {
            me.dragZone.unlock();
        }
        if (me.dropZone) {
            me.dropZone.unlock();
        }
        me.callParent();
    },

    disable: function() {
        var me = this;
        if (me.dragZone) {
            me.dragZone.lock();
        }
        if (me.dropZone) {
            me.dropZone.lock();
        }
        me.callParent();
    },

    onViewRender : function(view) {
        var me = this;

        if (me.enableDrag) {
            me.dragZone = Ext.create('Ext.view.DragZone', {
                view: view,
                ddGroup: me.dragGroup || me.ddGroup,
                dragText: me.dragText
            });
        }

        if (me.enableDrop) {
            me.dropZone = Ext.create('Ext.grid.ViewDropZone', {
                view: view,
                ddGroup: me.dropGroup || me.ddGroup
            });
        }
    }
});
