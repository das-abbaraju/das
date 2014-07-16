angular.module('EmployeeGUARD', [
    'PICS.employeeguard',
    'PICS.employeeguard.skills',
    'ProfileService'
])
.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('noCacheInterceptor');
}])

.config(function ($translateProvider) {
    $translateProvider.useStaticFilesLoader({
        prefix: '/employee-guard/src/app/employeeguard/translations/locale-',
        suffix: '.json'
    });

    $translateProvider.fallbackLanguage(['en']);
})

.run(function($translate, ProfileService, $rootScope) {
    function setProfileLanguage(profile_settings) {
        if (profile_settings){
            if (profile_settings.language) {
                if (profile_settings.dialect) {
                    $translate.use(profile_settings.language.id + '_' + profile_settings.dialect.id);
                } else {
                    $translate.use(profile_settings.language.id);
                }
            }
        }
    }

    ProfileService.getSettings(setProfileLanguage);

    //Fix for failure of angular translate fallback for static files
    $rootScope.$on('$translateChangeError', function() {
        $translate.use('en');
    });
});