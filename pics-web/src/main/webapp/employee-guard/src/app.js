angular.module('EmployeeGUARD', [
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

    $translateProvider.addInterpolation('$translateMessageFormatInterpolation');

    // load 'en' table on startup
    $translateProvider.preferredLanguage('en');
});