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

    $translateProvider.fallbackLanguage(['en_GB']);
})

.config(function ($routeProvider) {
    $routeProvider
        .when('/employee-guard/profile/settings', {
            templateUrl: '/employee-guard/src/common/profile/settings.tpl.html'
        });
})


.run(function($translate, $rootScope, Profile) {
    function setProfileLanguage(language, dialect) {
        if (dialect) {
            $translate.use(language.id + '_' + dialect.id);
        } else {
            $translate.use(language.id);
        }
    }

    Profile.get().then(function(profile) {
        setProfileLanguage(profile.language, profile.dialect);
    });

    //Fix for failure of angular translate fallback for static files
    $rootScope.$on('$translateChangeError', function() {
        $translate.use('en_GB');
    });
});