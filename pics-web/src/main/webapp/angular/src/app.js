angular.module('PICSApp', [
    'PICS.filters',
    'PICS.home',
    'PICS.employeeguard',
    'PICS.directives',
    'PICS.charts',
    'ui.select2',
    'PICS.services',
    'PICS.translations'
])

.run(function (translationsService) {
    translationsService.setLogKeysToConsole(false);
});