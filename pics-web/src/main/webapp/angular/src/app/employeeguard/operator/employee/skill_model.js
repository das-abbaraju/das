angular.module('PICS.employeeguard')

.factory('SkillModel', function ($filter) {
    var Model = function (data) {
        this.data = Model.generateURLSlug(data);
    };

    Model.generateURLSlug = function (data) {
        var projects = data.projects;
        var roles = data.roles;

        for (var i in projects) {
            projects[i].slug = $filter('removeInvalidCharactersFromUrl')(projects[i].name);
        }

        for (var j in roles) {
            roles[j].slug = $filter('removeInvalidCharactersFromUrl')(roles[j].name);
        }

        return data;
    };

    Model.prototype.getData = function () {
        return this.data;
    };

    Model.prototype.getRoles = function () {
        return this.getData().roles;
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

    Model.prototype.getRoleBySlug = function (slug) {
        var data = this.getData(),
            roles = data.roles;

        for (var i = 0; i < roles.length; i++) {
            if (roles[i].slug == slug) {
                return roles[i];
            }
        }
    };

    Model.prototype.getRoleNameBySlug = function (slug) {
        var data = this.getData(),
            roles = data.roles;

        for (var i = 0; i < roles.length; i++) {
            if (roles[i].slug == slug) {
                return roles[i].name;
            }
        }
    };

    Model.prototype.getProjectBySlug = function (slug) {
        var data = this.getData(),
            projects = data.projects;

        for (var i = 0; i < projects.length; i++) {
            if (projects[i].slug == slug) {
                return projects[i];
            }
        }
    };

    Model.prototype.getProjectNameBySlug = function (slug) {
        var data = this.getData(),
            projects = data.projects;

        for (var i = 0; i < projects.length; i++) {
            if (projects[i].slug == slug) {
                return projects[i].name;
            }
        }
    };

    return Model;
});