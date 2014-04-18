angular.module('PICS.employeeguard')

.controller('operatorEmployeeCtrl', function ($scope, EmployeeCompanyInfo, SkillModel, SkillList, $routeParams, $filter) {
    var skillModel;

    // $scope.employee = EmployeeCompanyInfo.get();
    EmployeeCompanyInfo.get(function(employee) {
        $scope.employee = employee;
        $scope.employeeStatusIcon = employee.status;
    });

    $scope.skillList = SkillList.get(function(result) {
        skillModel = new SkillModel(result);
        loadMenuItems();
        selectViewModel();
    });

    function loadAllRequiredSkills() {
        $scope.requiredSkills = skillModel.getAllRequiredSkills();
    }

    function loadMenuItems() {
        $scope.projects = skillModel.getProjects();
        $scope.roles = skillModel.getRoles();
    }

    function selectViewModel() {
        var model;

        if ($routeParams.roleSlug) {
            model = getRoleModel();
            setScopeModel(model);
        } else if ($routeParams.projectSlug) {
            model = getProjectModel();
            setScopeModel(model);
        } else {
            model = getDefaultModel();
            setScopeModel(model);
        }
    }

    function setScopeModel(model) {
        $scope.requiredSkills = model.requiredSkills;
        $scope.skillList = model.skillList;
        $scope.employeeStatusIcon = model.employeeStatusIcon;
        $scope.selectedMenuItem = model.selectedMenuItem;
        $scope.viewTitle = model.viewTitle;
    }

    function getRoleModel() {
        var slugname = $routeParams.roleSlug;

        return {
            requiredSkills: skillModel.getSiteAndCorpRequiredSkills(),
            skillList: skillModel.getRoleBySlug(slugname),
            employeeStatusIcon: skillModel.getRoleBySlug(slugname).status,
            selectedMenuItem: slugname,
            viewTitle: skillModel.getRoleNameBySlug(slugname)
        };
    }

    function getProjectModel() {
        var slugname = $routeParams.projectSlug;

        return {
            requiredSkills: skillModel.getProjectAndSiteRequiredSkillsBySlug(slugname),
            skillList: skillModel.getProjectBySlug(slugname),
            employeeStatusIcon: skillModel.getProjectBySlug(slugname).status,
            selectedMenuItem: slugname,
            viewTitle: skillModel.getProjectNameBySlug(slugname)
        };
    }

    function getDefaultModel() {
        return {
            requiredSkills: skillModel.getAllRequiredSkills(),
            selectedMenuItem: 'all'
        };
    }

    $scope.getSelectedView = function() {
        var view;

        if ($routeParams.roleSlug) {
            view = 'role';
        } else if ($routeParams.projectSlug) {
            view = 'project';
        } else {
            view = 'all';
        }

        return view;
    };

    angular.extend($scope, {
        loadMenuItems: loadMenuItems,
        selectViewModel: selectViewModel,
        getRoleModel: getRoleModel,
        getProjectModel: getProjectModel,
        getDefaultModel: getDefaultModel
    });
})

.filter('removeInvalidCharactersFromUrl', function () {
        return function (text) {

            var str = text.replace(/\s+/g, '-').toLowerCase();
            return str;
        };
});