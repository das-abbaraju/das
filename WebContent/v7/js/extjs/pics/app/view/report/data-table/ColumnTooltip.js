Ext.define('PICS.view.report.data-table.ColumnTooltip', {
    extend: 'Ext.tip.ToolTip',
    alias: 'widget.reportcolumntooltip',
    
    anchor: 'bottom',
    showDelay: 0,
    tpl: '<div><h3>{name}</h3><p>{description}</p></div>'
});