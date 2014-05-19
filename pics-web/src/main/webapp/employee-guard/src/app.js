angular.module('PICSApp', [
    'PICS.employeeguard'
])
.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('noCacheInterceptor');
}]);