angular.module('PICS.employeeguard')

.controller('operatorEmployeeCtrl', function ($scope, EmployeeCompanyInfo, SkillModel, SkillList, $routeParams) {
    var skillModel;

    $scope.employee = EmployeeCompanyInfo.get();
    $scope.skillList = SkillList.get();

    $scope.employee.$promise.then(function(employee) {
        $scope.employeeStatusIcon = employee.status;
    });

    $scope.skillList.$promise.then(function (result) {
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
        if ($routeParams.roleSlug) {
            loadRoleModel();
        } else if ($routeParams.projectSlug) {
            loadProjectModel();
        } else {
            loadDefaultModel();
        }
    }

    function loadRoleModel() {
        var slugname = $routeParams.roleSlug;

        $scope.requiredSkills = skillModel.getSiteAndCorpRequiredSkills();
        $scope.skillList = skillModel.getRoleBySlug(slugname);
        setEmployeeStatusIcon($scope.skillList.status);
        setSelectedMenuItem(slugname);
        setViewTitle(skillModel.getRoleNameBySlug(slugname));
    }

    function loadProjectModel() {
        var slugname = $routeParams.projectSlug;

        $scope.requiredSkills = skillModel.getProjectAndSiteRequiredSkillsBySlug(slugname);
        $scope.skillList = skillModel.getProjectBySlug(slugname);
        setEmployeeStatusIcon($scope.skillList.status);
        setSelectedMenuItem(slugname);
        setViewTitle(skillModel.getProjectNameBySlug(slugname));
    }

    function loadDefaultModel() {
        $scope.requiredSkills = skillModel.getAllRequiredSkills();
        setSelectedMenuItem('all');
    }

    function setEmployeeStatusIcon(status) {
        $scope.employeeStatusIcon = status;
    }

    function setSelectedMenuItem(item) {
        $scope.selectedMenuItem = item;
    }

    function setViewTitle(name) {
        $scope.viewTitle = name;
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
        loadRoleModel: loadRoleModel,
        loadProjectModel: loadProjectModel,
        setEmployeeStatusIcon: setEmployeeStatusIcon,
        setSelectedMenuItem: setSelectedMenuItem,
        setViewTitle: setViewTitle
    });
})

.filter('removeInvalidCharactersFromUrl', function () {
        return function (text) {

            var str = text.replace(/\s+/g, '');
            return str;
        };
});