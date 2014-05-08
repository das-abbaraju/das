angular.module('PICS.employeeguard')

.controller('operatorEmployeeCtrl', function ($scope, $location, EmployeeCompanyInfo, SiteList, SkillModel, SkillList, $routeParams, $filter, WhoAmI) {
    var skillModel;

    var employee_info = EmployeeCompanyInfo.get({id: $routeParams.id}, function(employee) {
        $scope.employee = employee;
        $scope.employeeStatusIcon = employee.status;
    });

    var user = WhoAmI.get(function(user) {
        $scope.user = user.type.toLowerCase();

        if ($scope.user === 'corporate') {
            SiteList.query(function(sites) {
                $scope.siteList = sites;

                //load first site if none specified in url. Remove when no longer an issue
                if (!$routeParams.siteId) {
                    $scope.initialState = true;
                    loadEmployeeSkills(sites[0].id);
                } else {
                    loadEmployeeSkills($routeParams.siteId);
                }
            });
        } else {
            loadEmployeeSkills();
        }
    });

    function loadEmployeeSkills(site_id) {
        $scope.selected_site = site_id;

        //make sure the employee info has populated first
        employee_info.$promise.then(function(employee) {
            SkillList.get({siteId: site_id, id: employee.id}, function(result) {
                skillModel = new SkillModel(result);
                loadMenuItems();
                selectViewModel();
            });
        });
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

    function setScopeModel(model) {
        $scope.requiredSkills = model.requiredSkills;
        $scope.skillGroup = model.skillGroup;
        $scope.selectedMenuItem = model.selectedMenuItem;
        $scope.viewTitle = model.viewTitle;

        if (model.employeeStatusIcon) {
            $scope.employeeStatusIcon = model.employeeStatusIcon;
        }
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
        loadEmployeeSkills: loadEmployeeSkills,
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