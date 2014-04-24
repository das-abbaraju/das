angular.module('PICS.employeeguard')

.factory('EmployeeSkillModel', function ($filter) {
    var Model = function (data) {
        this.data = Model.generateURLSlug(data);
    };

    Model.generateURLSlug = function (data) {
        angular.forEach(data.sites, function(site) {
            site.slug = $filter('removeInvalidCharactersFromUrl')(site.name);

            angular.forEach(site.projects, function(project) {
                project.slug = $filter('removeInvalidCharactersFromUrl')(project.name);
            });
        });

        return data;
    };

    Model.prototype.getData = function () {
        return this.data;
    };

    Model.prototype.getSites = function () {
        return this.getData().sites;
    };

    Model.prototype.getProjects = function () {
        return this.getData().projects;
    };

    Model.prototype.getSiteBySlug = function (site_slug) {
        var data = this.getData(),
        sites = $filter('filter')(data.sites, { slug: site_slug})[0];

        if (sites) {
            return sites;
        }
    };

    Model.prototype.getSiteNameBySlug = function (site_slug) {
        var data = this.getData(),
        sites = $filter('filter')(data.sites, { slug: site_slug})[0];

        if (sites) {
            return sites.name;
        }
    };

    Model.prototype.getAllSiteAndProjectSkills = function () {
        var sites = this.getSites(),
            skills = [];

        angular.forEach(sites, function(site) {
            if (site.required) {
                site.skills = site.required.skills;
            } else {
                site.skills = [];
            }
            angular.forEach(site.projects, function(project) {
                site.skills = site.skills.concat(project.skills);
                site.skills = $filter('removeDuplicateItemsFromArray')(site.skills);
            });
        });


        return sites;
    };

    Model.prototype.getAllSiteAndProjectSkillsBySlug = function (site_slug) {
        var site = this.getSiteBySlug(site_slug),
            projectSkills = [];

        angular.forEach(site.projects, function(project) {
            projectSkills = projectSkills.concat(project.skills);
        });

        if (site.required) {
            site.skills = site.required.skills.concat(projectSkills);
        } else {
            site.skills = projectSkills;
        }

        site.skills = $filter('removeDuplicateItemsFromArray')(site.skills);

        return site;
    };

    Model.prototype.getProjectBySlug = function (project_slug) {
        var data = this.getData(),
            selected_project;

        angular.forEach(data.sites, function(site) {
            angular.forEach(site.projects, function(project) {
                if (project.slug === project_slug) {
                    selected_project = project;
                    if (site.required) {
                        selected_project.skills = site.required.skills.concat(project.skills);
                    }
                }
            });
        });

        selected_project.skills = $filter('removeDuplicateItemsFromArray')(selected_project.skills);

        return selected_project;
    };

    Model.prototype.getSiteNameByProjectSlug = function (project_slug) {
        var data = this.getData(),
            site_name;

        angular.forEach(data.sites, function(site) {
            angular.forEach(site.projects, function(project) {
                if (project.slug === project_slug) {
                    site_name = site.name;
                }
            });
        });

        return site_name;
    };

    Model.prototype.getProjectNameBySlug = function (project_slug) {
        var data = this.getData(),
            projectArray = [],
            site_name;

        angular.forEach(data.sites, function(site) {
            projectArray = projectArray.concat(site.projects);
        });

        projects = $filter('filter')(projectArray, { slug: project_slug})[0];

        if (projects) {
            return projects.name;
        }
    };

    return Model;
});