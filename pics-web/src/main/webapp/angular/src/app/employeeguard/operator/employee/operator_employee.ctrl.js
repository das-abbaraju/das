angular.module('PICS.employeeguard')

.controller('operatorEmployeeCtrl', function ($scope, $location, EmployeeCompanyInfo, SiteList, SkillModel, SkillList, $routeParams, $filter, WhoAmI) {
    var skillModel;

    var employee_info = EmployeeCompanyInfo.get(function(employee) {
        $scope.employee = employee;
        $scope.employeeStatusIcon = employee.status;
    });

    WhoAmI.get(function(user) {
        $scope.user = user.type.toLowerCase();

        if ($scope.user === 'corporate') {
            SiteList.query(function(sites) {
                $scope.siteList = sites;

                if (!$routeParams.siteId) {
                    $scope.initialState = true;
                    loadSkillList(sites[0].id);
                } else {
                    loadSkillList($routeParams.siteId);
                }
            });
        } else {
            employee_info.$promise.then(function(employee) {
                loadSkillList(employee.id);
            });
        }
    });

    function loadSkillList(id) {
        $scope.selected_site = id;

        $scope.skillList = SkillList.get({id: id}, function(result) {
            skillModel = new SkillModel(result);
            loadMenuItems();
            selectViewModel();
        });
    }

    $scope.loadSelectedSiteData = function(site_id) {
        //TODO: Remove initial state asap
        if ($scope.initialState) {
            $scope.initialState = false;
            return;
        }

        if (site_id !== 'null') {
            $location.path('/employee-guard/operators/employees/' + $routeParams.id +'/sites/' + site_id);
        }
    };

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
        $scope.skillGroup = model.skillGroup;
        $scope.selectedMenuItem = model.selectedMenuItem;
        $scope.viewTitle = model.viewTitle;

        if (model.employeeStatusIcon) {
            $scope.employeeStatusIcon = model.employeeStatusIcon;
        }
    }

    function getRoleModel() {
        var slugname = $routeParams.roleSlug,
            role = skillModel.getRoleBySlug(slugname);

        return {
            requiredSkills: skillModel.getSiteAndCorpRequiredSkills(),
            skillGroup: role,
            employeeStatusIcon: role.status,
            selectedMenuItem: slugname,
            viewTitle: skillModel.getRoleNameBySlug(slugname)
        };
    }

    function getProjectModel() {
        var slugname = $routeParams.projectSlug,
            project = skillModel.getProjectBySlug(slugname);

        return {
            requiredSkills: skillModel.getProjectAndSiteRequiredSkillsBySlug(slugname),
            skillGroup: project,
            employeeStatusIcon: project.status,
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
        loadSkillList: loadSkillList,
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