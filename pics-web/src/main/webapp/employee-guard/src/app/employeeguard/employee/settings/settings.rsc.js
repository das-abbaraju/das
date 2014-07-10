angular.module('PICS.employeeguard')
.factory('ProfileSettingsService', function ($translate, SettingsResource) {
    var factory = {},
        settings;

    factory.getSettings = function(callback, forceReload) {
        if (forceReload || (typeof settings === 'undefined')) {
            return factory.fetchSettings(callback);
        } else {
            if (callback) {
                callback(settings);
            } else {
                return settings;
            }
        }
    };

    factory.setSettings = function(values) {
        settings = values;
        $translate.use(values.language.id);
    };

    factory.fetchSettings = function(callback) {
        console.log('fetch settings');
        return SettingsResource.get(function(settings) {
            $translate.use(settings.language.id);
            factory.setSettings(settings);
            if (callback) {
                callback(settings);
            }
        });
    };

    return factory;
})

.factory('SettingsResource', function($resource) {
    var live_url = '/employee-guard/employee/settings',
        dev_url = '/employee-guard/json/employee/settings/settings.json';

    return $resource(dev_url, {}, {
        update: { method: 'PUT'},
        get: { method: 'GET'}
    });
});