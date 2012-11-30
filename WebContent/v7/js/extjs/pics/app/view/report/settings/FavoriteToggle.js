Ext.define('PICS.view.report.settings.FavoriteToggle', {
    extend: 'Ext.form.field.Display',
    alias: ['widget.favoritetoggle'],
    
    fieldLabel: '<i class="favorite icon-star"></i>',
    labelAlign: 'right',
    labelSeparator: '',
    value: 'Report <strong class="favorite-text">is not</strong> a Favorite',

    initComponent: function () {
        this.callParent(arguments);
    },

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

    isFavoriteOn: function (favorite_elements) {
        var favorite_elements = favorite_elements || this.getFavoriteElements();

        return favorite_elements.icon.hasCls('selected');
    },

    getFavoriteElements: function () {
        var parentCmp = this.up('panel'),
            parentEl = parentCmp.getEl();

        return {
            parentCmp: parentCmp,
            icon: parentEl.down('.icon-star'),
            text: parentEl.down('.favorite-text')
        };
    },

    saveFavoriteStatus: function (status) {
        var event_name = status ? 'favorite' : 'unfavorite',
            config = PICS.app.configuration;

        config.setIsFavorite(status);
        this.fireEvent(event_name);
    },

    toggleFavoriteOn: function (favorite_elements) {
        var favorite_elements = favorite_elements || this.getFavoriteElements();

        favorite_elements.icon.addCls('selected');
        favorite_elements.text.setHTML('is');
    },

    toggleFavoriteOff: function (favorite_elements) {
        var favorite_elements = favorite_elements || this.getFavoriteElements();

        favorite_elements.icon.removeCls('selected');
        favorite_elements.text.setHTML('is not');
    },

    toggleFavoriteStatus: function () {
        var favorite_elements = favorite_elements || this.getFavoriteElements(),
            is_favorite_on = this.isFavoriteOn(favorite_elements),
            is_editable = PICS.app.configuration.isEditable(),
            duplicating = Ext.getClassName(favorite_elements.parentCmp) == 'PICS.view.report.settings.CopySettings';

        is_favorite_on ? this.toggleFavoriteOff(favorite_elements) : this.toggleFavoriteOn(favorite_elements);

        if (!is_editable && !duplicating) {
            this.saveFavoriteStatus(!is_favorite_on);
        }
    }
});