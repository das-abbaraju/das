angular.module('ProfileService', [
    'pascalprecht.translate',
    'ngResource'
])

.factory('ProfileService', function (ProfileResource) {
    var factory = {},
        profile_settings;

    factory.getSettings = function(callback, forceReload) {
        if (forceReload || (typeof profile_settings === 'undefined')) {
            return factory.fetchSettings(callback);
        } else {
            if (callback) {
                callback(profile_settings);
            } else {
                return profile_settings;
            }
        }
    };

    factory.cacheProfileSettings = function(values) {
        profile_settings = values;
    };

    factory.save = function(values) {
        ProfileResource.update(values);
    };

    factory.fetchSettings = function(callback) {
        return ProfileResource.get(function(profile_settings) {
            factory.cacheProfileSettings(profile_settings);
            if (callback) {
                callback(profile_settings);
            }
        });
    };

    return factory;
})

.factory('ProfileResource', function($resource) {
    var live_url = '/employee-guard/api/settings',
        dev_url = '/employee-guard/json/employee/settings/settings.json';

    return $resource(live_url, {}, {
        update: { method: 'PUT'},
        get: { method: 'GET'}
    });
});