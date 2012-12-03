Ext.define('PICS.view.report.settings.FavoriteToggle', {
    extend: 'Ext.form.field.Display',
    alias: ['widget.favoritetoggle'],
    
    fieldLabel: '<i class="favorite icon-star"></i>',
    labelAlign: 'right',
    labelSeparator: '',
    value: 'Report <strong class="favorite-text">is not</strong> a Favorite',

    listeners: {
        afterrender: function(cmp, eOpts) {
            var config = PICS.app.configuration;

            if (config.isFavorite()) {
                this.toggleFavoriteStatus();
            }
            
            this.mon(this.el,'click', this.toggleFavoriteStatus, this, {
                delegate: '.icon-star'
            });            
        }
    },

    isFavoriteOn: function () {
        var favorite_elements = this.getFavoriteElements();

        return favorite_elements.icon.hasCls('selected');
    },

    getFavoriteElements: function () {
        var element = this.getEl();

        return {
            icon: element.down('.icon-star'),
            text: element.down('.favorite-text')
        };
    },

    saveFavoriteStatus: function (status) {
        var event_name = status ? 'favorite' : 'unfavorite',
            config = PICS.app.configuration;

        config.setIsFavorite(status);

        this.fireEvent(event_name);
    },

    toggleFavoriteOn: function () {
        var favorite_elements = this.getFavoriteElements();

        favorite_elements.icon.addCls('selected');
        favorite_elements.text.setHTML('is');
    },

    toggleFavoriteOff: function () {
        var favorite_elements = this.getFavoriteElements();

        favorite_elements.icon.removeCls('selected');
        favorite_elements.text.setHTML('is not');
    },

    toggleFavoriteStatus: function () {
        var is_favorite_on = this.isFavoriteOn(),
            config = PICS.app.configuration,
            is_editable = config && config.isEditable(),
            duplicating = Ext.getClassName(this) == 'PICS.view.report.settings.CopySettings';

        is_favorite_on ? this.toggleFavoriteOff() : this.toggleFavoriteOn();

        // The Duplicate tab and the Edit tab of editable reports have Apply buttons.
        // Without an apply button, we need to save immediately.
        if (!is_editable && !duplicating) {
            this.saveFavoriteStatus(!is_favorite_on);
        }
    }
});