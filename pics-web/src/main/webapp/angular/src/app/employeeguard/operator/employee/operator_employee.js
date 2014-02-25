var test;

angular.module('PICS.employeeguard')

.controller('operatorEmployeeCtrl', function ($scope, $http, $filter) {
    $scope.partialname = 'all';

    $http.get('json/dummyData.json').success(function(data) {
        $scope.employee = data;

        $scope.projectSkills = $scope.getAllProjectSkills($scope.employee.projects);

        $scope.projectRoles = $scope.getAllProjectRoles($scope.employee.projects);

        $scope.highlightedStatus = $scope.employee.overallStatus;
    });

    $scope.getAllProjectSkills = function (projects) {
        var projectSkills = [];
        //loop over projects
        for (var x in projects) {
            //loop over project skills
            for (var y in projects[x].skills) {
                projectSkills.push(projects[x].skills[y]);
            }
        }

        return projectSkills;
    };

    $scope.getAllProjectRoles = function (projects) {
        var projectRoles = [];

        for (var x in projects) {
            for (var y in projects[x].roles) {
                projectRoles.push(projects[x].roles[y]);
            }
        }

        return projectRoles;
    };

    $scope.updatePartial = function (view, role, project) {
        $scope.partialname = view;
        $scope.role = role;
        $scope.project = project;

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
                console.log(projects[x]);
                return projects[x];
            }
        }
    };
});