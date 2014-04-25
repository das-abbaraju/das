angular.module('PICS.employeeguard')

.controller('employeeSkillListCtrl', function ($scope, EmployeeSkillList, EmployeeSkillModel, $routeParams, $filter) {
    var skillModel;

    $scope.skillList = EmployeeSkillList.get(function(result) {
        $scope.overallStatus = result.status;
        skillModel = new EmployeeSkillModel(result);
        loadMenuItems();
        selectViewModel();
    });

    function loadMenuItems() {
        $scope.sites = skillModel.getSites();
    }

    function selectViewModel() {
        var model;

        if ($routeParams.projectSlug && $routeParams.siteSlug) {
            model = getProjectModel();
            setScopeModel(model);
        } else if ($routeParams.siteSlug) {
            model = getSiteModel();
            setScopeModel(model);
        } else {
            model = getDefaultModel();
            setScopeModel(model);
        }
    }

    function getSiteModel() {
        var slugname = $routeParams.siteSlug;

        return {
            skillList: skillModel.getAllSiteAndProjectSkillsBySlug(slugname),
            selectedMenuItem: slugname,
            viewTitle: skillModel.getSiteNameBySlug(slugname)
        };
    }

    function getProjectModel() {
        var slugname = $routeParams.projectSlug,
            site_name = skillModel.getSiteNameByProjectSlug(slugname),
            skillList = skillModel.getProjectBySlug(slugname),
            viewTitle = site_name + ': ' + skillList.name;

        return {
            skillList: skillList,
            selectedMenuItem: slugname,
            viewTitle: viewTitle
        };
    }

    function getDefaultModel() {
        return {
            selectedMenuItem: 'all',
            skillList: skillModel.getAllSiteAndProjectSkills()
        };
    }

    function setScopeModel(model) {
        $scope.skillList = model.skillList;
        $scope.selectedMenuItem = model.selectedMenuItem;
        $scope.viewTitle = model.viewTitle;
    }

    $scope.getSelectedView = function() {
        var view;

        if ($routeParams.projectSlug && $routeParams.siteSlug) {
            view = 'project';
        } else if ($routeParams.siteSlug) {
            view = 'site';
        } else {
            view = 'all';
        }

        return view;
    };

    angular.extend($scope, {
        loadMenuItems: loadMenuItems,
        selectViewModel: selectViewModel,
        getSiteModel: getSiteModel,
        getProjectModel: getProjectModel,
        getDefaultModel: getDefaultModel,
        setScopeModel: setScopeModel
    });
});