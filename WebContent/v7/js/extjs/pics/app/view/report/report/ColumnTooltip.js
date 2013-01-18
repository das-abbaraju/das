Ext.define('PICS.view.report.report.ColumnTooltip', {
    extend: 'Ext.tip.ToolTip',
    alias: 'widget.columntooltip',
    
    anchor: 'bottom',
    showDelay: 0,
    tpl: '<div><h3>{name}</h3><p>{description}</p></div>'
});