angular.module('PICS.employeeguard')

.controller('operatorProjectListCtrl', function ($scope, SkillList, WhoAmI) {
    $scope.projects = SkillList.query();
    $scope.user = WhoAmI.get();

    $scope.requiredSkills = [];

    $scope.user.$promise.then(function(user) {
        if (user.type.toLowerCase() === 'corporate') {
            $scope.orderByField = 'site';
        }
    });
});