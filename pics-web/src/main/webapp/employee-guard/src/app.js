angular.module('EmployeeGUARD', [
    'pascalprecht.translate',
    'PICS.employeeguard'
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

    // load 'en' table on startup
    $translateProvider.preferredLanguage('de');
});