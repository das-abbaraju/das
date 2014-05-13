angular.module('PICS.employeeguard')

.controller('employeeDashboardCtrl', function ($scope, $location, $filter, EmployeeInfo, EmployeeAssignment, SkillStatus) {
    $scope.employee = EmployeeInfo.get(function (employee) {
        setSlug();
    });

    $scope.assignments = EmployeeAssignment.query();
    $scope.getSkillClass = SkillStatus.getClassNameFromStatus;

    $scope.viewAssignedSkills = function(assignment) {
        var siteSlug,
            projectSlug;

        if (assignment.site) {
            siteSlug = $filter('removeInvalidCharactersFromUrl')(assignment.site);
            projectSlug = $filter('removeInvalidCharactersFromUrl')(assignment.name);
            $location.path('/employee-guard/employee/skills/sites/' + siteSlug + '/projects/' + projectSlug);
        } else {
            siteSlug = $filter('removeInvalidCharactersFromUrl')(assignment.name);
            $location.path('/employee-guard/employee/skills/sites/' + siteSlug);
        }
    };

    function setSlug() {
        if (!$scope.employee.slug) {
            $scope.employee.slug = $scope.employee.email;
        }
    }

    angular.extend($scope, {
        setSlug: setSlug
    });
});