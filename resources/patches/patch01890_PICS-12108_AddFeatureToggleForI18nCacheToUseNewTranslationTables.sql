-- Initialize Toggle.TranslationServiceAdapter to off, to prevent log spamming
insert ignore into `app_properties` (`property`, `value`) values('Toggle.UseNewTranslationsDataSource','false');
