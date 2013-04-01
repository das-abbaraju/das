alter table `users`
add column `firstName` varchar(50) CHARSET utf8 COLLATE utf8_general_ci NULL after `email`,
add column `lastName` varchar(50) CHARSET utf8 COLLATE utf8_general_ci NULL after `firstName`;