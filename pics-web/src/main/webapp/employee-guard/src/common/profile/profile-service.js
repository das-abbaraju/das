angular.module('ProfileService', [
    'pascalprecht.translate',
    'ngResource'
])

.factory('Profile', function ($resource, $q, $translate) {
    var settings = {},
        profile_settings_cache,
        settings_promise;

    var profileResource = $resource('/employee-guard/api/settings', {}, {
        update: { method: 'PUT'},
        get: { method: 'GET'}
    });

    settings.get = function(force_reload) {
        var deferred = $q.defer();

        if (!force_reload) {
            if(settings_promise) {
                settings_promise.then(function(profile) {
                    deferred.resolve(profile);
                });
                return deferred.promise;
            } else if (profile_settings_cache) {
                deferred.resolve(profile_settings_cache);
                return deferred.promise;
            }
        }

        settings_promise = fetchSettings(function(profile) {
            profile_settings_cache = profile;
            deferred.resolve(profile);
        }, function() { deferred.reject();});

        return deferred.promise;
    };

    settings.save = function(profile) {
        profileResource.update(profile, function(value, responseHeaders) {
            profile_settings_cache = profile;
        });
    };

    settings.setTranslatedLanguage = function(language, dialect) {
        if (language && dialect) {
            $translate.use(language.id + '_' + dialect.id);
        } else if (language && !dialect) {
            $translate.use(language.id);
        } else {
            $translate.use('en_GB');
        }
    };

    function fetchSettings(onSuccess, onError) {
        return profileResource.get(onSuccess, onError).$promise;
    }

    return settings;
});