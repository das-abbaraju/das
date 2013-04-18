Ext.define('PICS.data.Translate', {
    statics: {
        text: function (key) {
            var args = arguments;
            
            return PICS.i18n[key] ? PICS.i18n[key].replace(/{([0-9]+)}/g, function (match, p1) {
                return args[parseInt(p1) + 1];
            }) : key;
        }
    }
});