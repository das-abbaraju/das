angular.module('PICS.employeeguard')

.controller('operatorEmployeeCtrl', function ($scope, $filter, EmployeeSkills, Model) {
    $scope.partialname = 'all';

    $scope.employee = EmployeeSkills.get();

    $scope.employee.$promise.then(function (result) {
        var model = new Model(result);

        $scope.projectSkills = model.getAllProjectSkills();
        $scope.projectRoles = model.getAllProjectRoles();

        $scope.highlightedStatus = $scope.employee.overallStatus;
    });

    $scope.updatePartial = function (view, role, project) {
        $scope.partialname = view;
        $scope.role = role;
        $scope.project = project;

        if (view == 'all') {
            $scope.highlightedStatus = $scope.employee.overallStatus;
        }

        if (project) {
            $scope.currentProject = $scope.getProject();
        }
    };

    $scope.roleFilter = function (role) {
        if (role.name == $scope.role) {
            $scope.highlightedStatus = role.status;
            return role;
        }
    };

    $scope.getProject = function () {
        var projects = $scope.employee.projects;

        for (var x = 0; x < projects.length; x++) {
            if (projects[x].name == $scope.project) {
                $scope.highlightedStatus = projects[x].status;
                return projects[x];
            }
        }
    };
});