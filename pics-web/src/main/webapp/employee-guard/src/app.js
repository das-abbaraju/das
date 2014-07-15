angular.module('EmployeeGUARD', [
    'PICS.employeeguard',
    'PICS.employeeguard.skills'
    'ProfileService'
])
.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('noCacheInterceptor');
}])

.config(function ($translateProvider) {
    // configures staticFilesLoader
    $translateProvider.useStaticFilesLoader({
        prefix: '/employee-guard/src/app/employeeguard/translations/locale-',
        suffix: '.json'
    });

    $translateProvider.fallbackLanguage(['en']);
})

.run(function($translate, ProfileService) {
    function setProfileLanguage(profile_settings) {
        if (profile_settings){
            if (profile_settings.language) {
                $translate.use(profile_settings.language.id);
            }
        }
    }

    ProfileService.getSettings(setProfileLanguage);
});