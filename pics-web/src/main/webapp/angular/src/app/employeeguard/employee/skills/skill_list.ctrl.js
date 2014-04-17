angular.module('PICS.employeeguard')

.controller('employeeSkillListCtrl', function ($scope, EmployeeSkillList, EmployeeSkillModel, $routeParams) {
    var skillModel;

    console.log($routeParams);

    $scope.skillList = EmployeeSkillList.get();

    $scope.skillList.$promise.then(function (result) {
        skillModel = new EmployeeSkillModel(result);
        $scope.overallStatus = skillModel.status;

        loadMenuItems();
        selectViewModel();
    });

    function selectViewModel() {
        if ($routeParams.siteSlug) {
            loadSiteModel();
        } else if ($routeParams.projectSlug) {
            loadProjectModel();
        } else {
            loadDefaultModel();
        }
    }

    function loadMenuItems() {
        $scope.sites = skillModel.getSites();
    }

    function loadSiteModel() {
        var slugname = $routeParams.siteSlug;

        // $scope.requiredSkills = skillModel.getSiteAndCorpRequiredSkills();
        $scope.skillList = skillModel.getSiteBySlug(slugname);
        setSelectedMenuItem(slugname);
        setViewTitle(skillModel.getSiteNameBySlug(slugname));

        // $scope.currentSiteProjects = skillModel.getSiteBySlug(slugname);
    }

    function loadProjectModel() {
        var slugname = $routeParams.projectSlug;

        // $scope.requiredSkills = skillModel.getProjectAndSiteRequiredSkillsBySlug(slugname);
        $scope.skillList = skillModel.getProjectBySlug(slugname);
        setSelectedMenuItem(slugname);
        setViewTitle(skillModel.getProjectNameBySlug(slugname));
    }

    function loadDefaultModel() {
        // $scope.requiredSkills = skillModel.getAllRequiredSkills();
        setSelectedMenuItem('all');
    }

    function setSelectedMenuItem(item) {
        $scope.selectedMenuItem = item;
    }

    function setViewTitle(name) {
        $scope.viewTitle = name;
    }

    $scope.getSelectedView = function() {
        var view;

        if ($routeParams.siteSlug) {
            view = 'site';
        } else if ($routeParams.projectSlug) {
            view = 'project';
        } else {
            view = 'all';
        }

        return view;
    };

});