angular.module('ProfileService', [
    'pascalprecht.translate',
    'ngResource'
])

.factory('Profile', function ($resource, $q, profileResource) {
    var profile = {},
        profile_settings_cache;

    profile.get = function(force_reload) {
        var deferred = $q.defer();

        if (!profile_settings_cache || force_reload) {
            fetchSettings().$promise.then(function(response) {
                profile_settings_cache = response;
                deferred.resolve(response);
            });
        } else {
            deferred.resolve(profile_settings_cache);
        }

        return deferred.promise;
    };

    profile.save = function(values) {
        profileResource.update(values);
    };

    function fetchSettings() {
        return profileResource.get();
    }

    return profile;
})

.factory('profileResource', function($resource) {
    return $resource('/employee-guard/api/settings', {}, {
        update: { method: 'PUT'},
        get: { method: 'GET'}
    });
});