angular.module('PICS.employeeguard')

.factory('EmployeeSkillModel', function ($filter) {
    var Model = function (data) {
        this.data = Model.generateURLSlug(data);
    };

    Model.generateURLSlug = function (data) {
        var sites = data.sites;

        for (var i in sites) {
            var site = sites[i];
            site.slug = $filter('removeInvalidCharactersFromUrl')(site.name);

            for (var j in site.projects) {
                site.projects[j].slug = $filter('removeInvalidCharactersFromUrl')(site.projects[j].name);
            }
        }

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

    Model.prototype.getAllRequiredSkills = function () {
        var siteAndCorpSkills,
            projectSkills,
            Model = this;

        siteAndCorpSkills = Model.getSiteAndCorpRequiredSkills();
        projectSkills = Model.getAllProjectRequiredSkills();

        return siteAndCorpSkills.concat(projectSkills);
    };

    Model.prototype.getAllProjectRequiredSkills = function () {
        var data = this.getData(),
            requiredSkills = [];

        for (var j in data.projects) {
            requiredSkills = requiredSkills.concat(data.projects[j].required.skills);
        }

        return requiredSkills;
    };

    Model.prototype.getProjectRequiredSkillsBySlug = function (slug) {
        var data = this.getData(),
            Model = this,
            projectSkills,
            siteAndCorpSkills;

        for (var j in data.projects) {
            var project = data.projects[j];

            if (project.slug === slug) {
                projectSkills = project.required.skills;
            }
        }
        return projectSkills;
    };

    Model.prototype.getProjectAndSiteRequiredSkillsBySlug = function (slug) {
        var data = this.getData(),
            Model = this,
            projectSkills,
            siteAndCorpSkills;

        //Add site and corp to list
        siteAndCorpSkills = Model.getSiteAndCorpRequiredSkills();
        projectSkills = Model.getProjectRequiredSkillsBySlug(slug);

        return siteAndCorpSkills.concat(projectSkills);
    };

    Model.prototype.getSiteAndCorpRequiredSkills = function () {
        var data = this.getData();

        return data.required.skills;
    };

    Model.prototype.getSiteBySlug = function (slug) {
        var data = this.getData(),
            sites = data.sites;

        for (var i = 0; i < sites.length; i++) {
            if (sites[i].slug == slug) {
                return sites[i];
            }
        }
    };

    Model.prototype.getSiteNameBySlug = function (slug) {
        var data = this.getData(),
            sites = data.sites;

        for (var i = 0; i < sites.length; i++) {
            if (sites[i].slug == slug) {
                return sites[i].name;
            }
        }
    };

    Model.prototype.getProjectBySlug = function (slug) {
        var data = this.getData(),
            sites = data.sites;

        for (var i in data.sites) {
            var site = data.sites[i];

            for (var j in site.projects) {
                if (site.projects[j].slug == slug) {
                    return site.projects[j];
                }
            }
        }
    };

    Model.prototype.getProjectNameBySlug = function (slug) {
        var data = this.getData();

        for (var i in data.sites) {
            var site = data.sites[i];

            for (var j in site.projects) {
                if (site.projects[j].slug == slug) {
                    return site.projects[j].name;
                }
            }
        }
    };

    return Model;
});