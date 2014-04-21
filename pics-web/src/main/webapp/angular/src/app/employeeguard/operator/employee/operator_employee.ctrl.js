angular.module('PICS.employeeguard')

.controller('operatorEmployeeCtrl', function ($scope, $location, EmployeeCompanyInfo, SiteList, SkillModel, SkillList, $routeParams, $filter, WhoAmI) {
    var skillModel;

    EmployeeCompanyInfo.get(function(employee) {
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
            loadSkillList();
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
        $scope.skillList = model.skillList;
        $scope.selectedMenuItem = model.selectedMenuItem;
        $scope.viewTitle = model.viewTitle;

        if (model.employeeStatusIcon) {
            $scope.employeeStatusIcon = model.employeeStatusIcon;
        }
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