angular.module('PICS.employeeguard')

.factory('ProjectList', function($resource) {
    return $resource('/angular/json/operator/project_list_corp.json');
});