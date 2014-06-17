--liquibase formatted sql

--changeset rbeaini:20
UPDATE `email_template` SET `body`='Attn: ${displayName} <br /> This is an automatically generated email that will allow you to set or reset your password. Please click the following link and set your password on the following page. <br /><br /> ${confirmLink} <br /><br /> If you did not request that this email be sent to you or if you have any questions, please contact us. <br /> <CountrySpecificPicsName> <br> <CSRPhone> <br> <CSREmail> <br> <CountrySpecificOfficeAddress>  ' WHERE `id`='85';
UPDATE `app_translation` SET `msgValue`='Attn: ${displayName} This is an automatically generated email that will allow you to set or reset your password. Please click the following link and set your password on the following page. ${confirmLink} If you did not request that this email be sent to you or if you have any questions, please contact us. <PICSSignature>' WHERE `id`='83504';
