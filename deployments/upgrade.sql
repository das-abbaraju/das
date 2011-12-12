-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO NON-CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- data conversion
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgradeConfig.sql FOR CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-3350
/*
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 2.9 WHERE `code` = 11;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.7 WHERE `code` = 111;
UPDATE `naics` SET `trir` = 7.4, `lwcr` = 3.7 WHERE `code` = 1111;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 3.2 WHERE `code` = 1112;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.6 WHERE `code` = 1113;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.7 WHERE `code` = 1114;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.2 WHERE `code` = 1119;
UPDATE `naics` SET `trir` = 6.9, `lwcr` = 3.6 WHERE `code` = 112;
UPDATE `naics` SET `trir` = 5.7, `lwcr` = 2.7 WHERE `code` = 1121;
UPDATE `naics` SET `trir` = 6.5, `lwcr` = 3.3 WHERE `code` = 11211;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 2.5 WHERE `code` = 11212;
UPDATE `naics` SET `trir` = 6.4, `lwcr` = 5.2 WHERE `code` = 1123;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.8 WHERE `code` = 1125;
UPDATE `naics` SET `trir` = 7.5, `lwcr` = 4.3 WHERE `code` = 1129;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 1.8 WHERE `code` = 113;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 1.9 WHERE `code` = 1133;
UPDATE `naics` SET `trir` = 0.9, `lwcr` = 0.6 WHERE `code` = 114;
UPDATE `naics` SET `trir` = 1, `lwcr` = 0.7 WHERE `code` = 1141;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.8 WHERE `code` = 115;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.8 WHERE `code` = 1151;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.8 WHERE `code` = 11511;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 2.1 WHERE `code` = 115112;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 2.8 WHERE `code` = 115113;
UPDATE `naics` SET `trir` = 7.2, `lwcr` = 3.5 WHERE `code` = 115114;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 2.4 WHERE `code` = 115115;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.8 WHERE `code` = 115116;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2.7 WHERE `code` = 1152;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2 WHERE `code` = 1153;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.5 WHERE `code` = 21;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.9 WHERE `code` = 211;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.9 WHERE `code` = 2111;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.9 WHERE `code` = 21111;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.9 WHERE `code` = 211111;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 2.2 WHERE `code` = 212;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.8 WHERE `code` = 2121;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.8 WHERE `code` = 21211;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.3 WHERE `code` = 212111;
UPDATE `naics` SET `trir` = 5.9, `lwcr` = 4.1 WHERE `code` = 212112;
UPDATE `naics` SET `trir` = 5.6, `lwcr` = 4.9 WHERE `code` = 212113;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.6 WHERE `code` = 2122;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.7 WHERE `code` = 21221;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.2 WHERE `code` = 21222;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 1.1 WHERE `code` = 212221;
UPDATE `naics` SET `trir` = 6, `lwcr` = 3 WHERE `code` = 212222;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.8 WHERE `code` = 21223;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2 WHERE `code` = 212231;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.8 WHERE `code` = 212234;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.6 WHERE `code` = 21229;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.5 WHERE `code` = 212299;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.7 WHERE `code` = 2123;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.8 WHERE `code` = 21231;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.9 WHERE `code` = 212311;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.8 WHERE `code` = 212312;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0.9 WHERE `code` = 212313;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 2.2 WHERE `code` = 212319;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.5 WHERE `code` = 21232;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.4 WHERE `code` = 212321;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.9 WHERE `code` = 212324;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 2 WHERE `code` = 212325;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.9 WHERE `code` = 21239;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 2.4 WHERE `code` = 212391;
UPDATE `naics` SET `trir` = 1.7, `lwcr` = 1.2 WHERE `code` = 212392;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 2.3 WHERE `code` = 212393;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.7 WHERE `code` = 212399;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.3 WHERE `code` = 213;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.3 WHERE `code` = 2131;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.3 WHERE `code` = 21311;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1 WHERE `code` = 213112;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.3 WHERE `code` = 23;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.9 WHERE `code` = 236;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2.1 WHERE `code` = 2361;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 1.7 WHERE `code` = 2362;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 2.2 WHERE `code` = 237;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 2.4 WHERE `code` = 2371;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 3.2 WHERE `code` = 23711;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 1 WHERE `code` = 23712;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.3 WHERE `code` = 23713;
UPDATE `naics` SET `trir` = 2, `lwcr` = 0.8 WHERE `code` = 2372;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.4 WHERE `code` = 2373;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.4 WHERE `code` = 2379;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.5 WHERE `code` = 238;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 3 WHERE `code` = 2381;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3.1 WHERE `code` = 23811;
UPDATE `naics` SET `trir` = 5.6, `lwcr` = 3.1 WHERE `code` = 23812;
UPDATE `naics` SET `trir` = 5.6, `lwcr` = 2.8 WHERE `code` = 23813;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.6 WHERE `code` = 23814;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 3.1 WHERE `code` = 23815;
UPDATE `naics` SET `trir` = 5.7, `lwcr` = 3.4 WHERE `code` = 23816;
UPDATE `naics` SET `trir` = 7.2, `lwcr` = 3.9 WHERE `code` = 23817;
UPDATE `naics` SET `trir` = 0, `lwcr` = 1.5 WHERE `code` = 23819;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.3 WHERE `code` = 2382;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.2 WHERE `code` = 23821;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 2.5 WHERE `code` = 23822;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.2 WHERE `code` = 23829;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.7 WHERE `code` = 2383;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 3.3 WHERE `code` = 23831;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.5 WHERE `code` = 23832;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.7 WHERE `code` = 23833;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 2.6 WHERE `code` = 23834;
UPDATE `naics` SET `trir` = 5.7, `lwcr` = 3.3 WHERE `code` = 23835;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.9 WHERE `code` = 23839;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.4 WHERE `code` = 2389;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 2.2 WHERE `code` = 23891;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.5 WHERE `code` = 23899;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.3 WHERE `code` = 31;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.3 WHERE `code` = 32;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.3 WHERE `code` = 33;
UPDATE `naics` SET `trir` = 5.7, `lwcr` = 3.6 WHERE `code` = 311;
UPDATE `naics` SET `trir` = 5, `lwcr` = 3 WHERE `code` = 3111;
UPDATE `naics` SET `trir` = 5, `lwcr` = 3 WHERE `code` = 31111;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2 WHERE `code` = 311111;
UPDATE `naics` SET `trir` = 5.8, `lwcr` = 3.6 WHERE `code` = 311119;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.6 WHERE `code` = 3112;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3.1 WHERE `code` = 31121;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 3.3 WHERE `code` = 311211;
UPDATE `naics` SET `trir` = 6.5, `lwcr` = 2.7 WHERE `code` = 311212;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.3 WHERE `code` = 31122;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1 WHERE `code` = 311221;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2 WHERE `code` = 311222;
UPDATE `naics` SET `trir` = 6.4, `lwcr` = 4.3 WHERE `code` = 311225;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.6 WHERE `code` = 31123;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 3 WHERE `code` = 3113;
UPDATE `naics` SET `trir` = 7.5, `lwcr` = 3.8 WHERE `code` = 31131;
UPDATE `naics` SET `trir` = 6.5, `lwcr` = 2.6 WHERE `code` = 311311;
UPDATE `naics` SET `trir` = 10, `lwcr` = 5.2 WHERE `code` = 311313;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 3.7 WHERE `code` = 31132;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.3 WHERE `code` = 31133;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 3 WHERE `code` = 31134;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 3 WHERE `code` = 3114;
UPDATE `naics` SET `trir` = 5, `lwcr` = 3.1 WHERE `code` = 31141;
UPDATE `naics` SET `trir` = 6.6, `lwcr` = 3.6 WHERE `code` = 311411;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.7 WHERE `code` = 311412;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 3 WHERE `code` = 31142;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 2.8 WHERE `code` = 311421;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.5 WHERE `code` = 311422;
UPDATE `naics` SET `trir` = 7, `lwcr` = 4.3 WHERE `code` = 311423;
UPDATE `naics` SET `trir` = 6.6, `lwcr` = 4.3 WHERE `code` = 3115;
UPDATE `naics` SET `trir` = 6.9, `lwcr` = 4.5 WHERE `code` = 31151;
UPDATE `naics` SET `trir` = 8.4, `lwcr` = 5.6 WHERE `code` = 311511;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 4.5 WHERE `code` = 311512;
UPDATE `naics` SET `trir` = 5.8, `lwcr` = 3.7 WHERE `code` = 311513;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.4 WHERE `code` = 311514;
UPDATE `naics` SET `trir` = 5, `lwcr` = 3.2 WHERE `code` = 31152;
UPDATE `naics` SET `trir` = 6.9, `lwcr` = 4.6 WHERE `code` = 3116;
UPDATE `naics` SET `trir` = 6.9, `lwcr` = 4.6 WHERE `code` = 31161;
UPDATE `naics` SET `trir` = 9.3, `lwcr` = 6.3 WHERE `code` = 311611;
UPDATE `naics` SET `trir` = 6.6, `lwcr` = 4.4 WHERE `code` = 311612;
UPDATE `naics` SET `trir` = 7, `lwcr` = 3.8 WHERE `code` = 311613;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 3.7 WHERE `code` = 311615;
UPDATE `naics` SET `trir` = 6.3, `lwcr` = 3.5 WHERE `code` = 3117;
UPDATE `naics` SET `trir` = 6.3, `lwcr` = 3.5 WHERE `code` = 31171;
UPDATE `naics` SET `trir` = 8.6, `lwcr` = 5.7 WHERE `code` = 311711;
UPDATE `naics` SET `trir` = 6, `lwcr` = 3.2 WHERE `code` = 311712;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.9 WHERE `code` = 3118;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.9 WHERE `code` = 31181;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.9 WHERE `code` = 311811;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 3.7 WHERE `code` = 311812;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 2.9 WHERE `code` = 311813;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.8 WHERE `code` = 31182;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 2.8 WHERE `code` = 311821;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.6 WHERE `code` = 311822;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 2.8 WHERE `code` = 311823;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 3.7 WHERE `code` = 31183;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.8 WHERE `code` = 3119;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.8 WHERE `code` = 31191;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 2.9 WHERE `code` = 311911;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.8 WHERE `code` = 311919;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.4 WHERE `code` = 31192;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.1 WHERE `code` = 31193;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 3.1 WHERE `code` = 31194;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.3 WHERE `code` = 311941;
UPDATE `naics` SET `trir` = 5, `lwcr` = 3.5 WHERE `code` = 311942;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 3.2 WHERE `code` = 31199;
UPDATE `naics` SET `trir` = 5.9, `lwcr` = 3.9 WHERE `code` = 311991;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 2.4 WHERE `code` = 311999;
UPDATE `naics` SET `trir` = 6.4, `lwcr` = 4.6 WHERE `code` = 312;
UPDATE `naics` SET `trir` = 6.7, `lwcr` = 4.9 WHERE `code` = 3121;
UPDATE `naics` SET `trir` = 8.2, `lwcr` = 6.3 WHERE `code` = 31211;
UPDATE `naics` SET `trir` = 9.1, `lwcr` = 7 WHERE `code` = 312111;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3.9 WHERE `code` = 312113;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 2 WHERE `code` = 31212;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 3.1 WHERE `code` = 31213;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3 WHERE `code` = 31214;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.9 WHERE `code` = 3122;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.7 WHERE `code` = 31222;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.5 WHERE `code` = 312221;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.6 WHERE `code` = 313;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.6 WHERE `code` = 3131;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.6 WHERE `code` = 31311;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.6 WHERE `code` = 313111;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0 WHERE `code` = 313113;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.4 WHERE `code` = 3132;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.5 WHERE `code` = 31321;
UPDATE `naics` SET `trir` = 4, `lwcr` = 1.4 WHERE `code` = 31322;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 1.5 WHERE `code` = 313221;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.6 WHERE `code` = 31323;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.9 WHERE `code` = 31324;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 0.9 WHERE `code` = 313241;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 0.9 WHERE `code` = 313249;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 2 WHERE `code` = 3133;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.7 WHERE `code` = 31331;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.7 WHERE `code` = 313311;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.6 WHERE `code` = 313312;
UPDATE `naics` SET `trir` = 4, `lwcr` = 3.2 WHERE `code` = 31332;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.9 WHERE `code` = 314;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.9 WHERE `code` = 3141;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.9 WHERE `code` = 31411;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 1.9 WHERE `code` = 31412;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 1.7 WHERE `code` = 314121;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2 WHERE `code` = 314129;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 1.9 WHERE `code` = 3149;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 1.6 WHERE `code` = 31491;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 0.6 WHERE `code` = 314911;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2 WHERE `code` = 314912;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.2 WHERE `code` = 31499;
UPDATE `naics` SET `trir` = 7.5, `lwcr` = 3.6 WHERE `code` = 314992;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.7 WHERE `code` = 314999;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.3 WHERE `code` = 315;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.5 WHERE `code` = 3151;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 1.8 WHERE `code` = 31511;
UPDATE `naics` SET `trir` = 1.7, `lwcr` = 1 WHERE `code` = 315111;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.3 WHERE `code` = 315119;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.1 WHERE `code` = 31519;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.1 WHERE `code` = 315191;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.2 WHERE `code` = 315192;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.3 WHERE `code` = 3152;
UPDATE `naics` SET `trir` = 1.1, `lwcr` = 0.6 WHERE `code` = 31521;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.4 WHERE `code` = 315211;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2 WHERE `code` = 31522;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 2.6 WHERE `code` = 315221;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.8 WHERE `code` = 315225;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.3 WHERE `code` = 315228;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.2 WHERE `code` = 31523;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 3.6 WHERE `code` = 315231;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 1.7 WHERE `code` = 315239;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 2 WHERE `code` = 31529;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 1.9 WHERE `code` = 315299;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.6 WHERE `code` = 3159;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.6 WHERE `code` = 31599;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2 WHERE `code` = 315991;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.5 WHERE `code` = 315999;
UPDATE `naics` SET `trir` = 6.2, `lwcr` = 3.4 WHERE `code` = 316;
UPDATE `naics` SET `trir` = 9.6, `lwcr` = 7.9 WHERE `code` = 3161;
UPDATE `naics` SET `trir` = 7.4, `lwcr` = 4.3 WHERE `code` = 316211;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 3.5 WHERE `code` = 316219;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 1.7 WHERE `code` = 3169;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 1.7 WHERE `code` = 31699;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.1 WHERE `code` = 316991;
UPDATE `naics` SET `trir` = 6.5, `lwcr` = 3.3 WHERE `code` = 321;
UPDATE `naics` SET `trir` = 7, `lwcr` = 3.6 WHERE `code` = 3211;
UPDATE `naics` SET `trir` = 7, `lwcr` = 3.6 WHERE `code` = 32111;
UPDATE `naics` SET `trir` = 7.1, `lwcr` = 3.6 WHERE `code` = 321113;
UPDATE `naics` SET `trir` = 6.2, `lwcr` = 3.8 WHERE `code` = 321114;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.4 WHERE `code` = 3212;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.4 WHERE `code` = 32121;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.1 WHERE `code` = 321211;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 2 WHERE `code` = 321212;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.3 WHERE `code` = 321213;
UPDATE `naics` SET `trir` = 7.1, `lwcr` = 3.6 WHERE `code` = 321214;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.8 WHERE `code` = 321219;
UPDATE `naics` SET `trir` = 6.9, `lwcr` = 3.5 WHERE `code` = 3219;
UPDATE `naics` SET `trir` = 6.6, `lwcr` = 3.4 WHERE `code` = 32191;
UPDATE `naics` SET `trir` = 5.8, `lwcr` = 3.2 WHERE `code` = 321911;
UPDATE `naics` SET `trir` = 6.6, `lwcr` = 2.6 WHERE `code` = 321912;
UPDATE `naics` SET `trir` = 7.7, `lwcr` = 3.8 WHERE `code` = 321918;
UPDATE `naics` SET `trir` = 7.7, `lwcr` = 3.8 WHERE `code` = 32192;
UPDATE `naics` SET `trir` = 6.6, `lwcr` = 3.5 WHERE `code` = 32199;
UPDATE `naics` SET `trir` = 7.4, `lwcr` = 3.8 WHERE `code` = 321991;
UPDATE `naics` SET `trir` = 6.5, `lwcr` = 3 WHERE `code` = 321992;
UPDATE `naics` SET `trir` = 5.7, `lwcr` = 3.7 WHERE `code` = 321999;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.8 WHERE `code` = 322;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.4 WHERE `code` = 3221;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.2 WHERE `code` = 32211;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.5 WHERE `code` = 32212;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.4 WHERE `code` = 322121;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 2.1 WHERE `code` = 322122;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.4 WHERE `code` = 32213;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 2 WHERE `code` = 3222;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.9 WHERE `code` = 32221;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 2 WHERE `code` = 322211;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.8 WHERE `code` = 322212;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1 WHERE `code` = 322213;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 0.5 WHERE `code` = 322214;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 2.1 WHERE `code` = 32222;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.5 WHERE `code` = 322221;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.3 WHERE `code` = 322222;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 2.3 WHERE `code` = 322223;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 2 WHERE `code` = 322224;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2.5 WHERE `code` = 322225;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.7 WHERE `code` = 32223;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.4 WHERE `code` = 322231;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 2.9 WHERE `code` = 322232;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 2.2 WHERE `code` = 322233;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.9 WHERE `code` = 32229;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.9 WHERE `code` = 322291;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 1.9 WHERE `code` = 322299;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.6 WHERE `code` = 323;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.6 WHERE `code` = 3231;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.6 WHERE `code` = 32311;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.7 WHERE `code` = 323110;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1 WHERE `code` = 323111;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 2.2 WHERE `code` = 323112;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.6 WHERE `code` = 323113;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 1 WHERE `code` = 323114;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.6 WHERE `code` = 323115;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 3 WHERE `code` = 323116;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 2 WHERE `code` = 323117;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.4 WHERE `code` = 323119;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1 WHERE `code` = 32312;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.5 WHERE `code` = 323121;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0.5 WHERE `code` = 323122;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.9 WHERE `code` = 324;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.9 WHERE `code` = 3241;
UPDATE `naics` SET `trir` = 1, `lwcr` = 0.5 WHERE `code` = 32411;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.6 WHERE `code` = 32412;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.8 WHERE `code` = 324121;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.4 WHERE `code` = 324122;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.7 WHERE `code` = 32419;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.9 WHERE `code` = 324199;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.4 WHERE `code` = 325;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 1.1 WHERE `code` = 3251;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.3 WHERE `code` = 32511;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.9 WHERE `code` = 32513;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 2.4 WHERE `code` = 325131;
UPDATE `naics` SET `trir` = 2, `lwcr` = 0.9 WHERE `code` = 32518;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 1.3 WHERE `code` = 325181;
UPDATE `naics` SET `trir` = 2, `lwcr` = 0.8 WHERE `code` = 325188;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.4 WHERE `code` = 32519;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2.9 WHERE `code` = 325192;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.9 WHERE `code` = 325199;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.4 WHERE `code` = 3252;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.6 WHERE `code` = 32521;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.4 WHERE `code` = 325211;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 2.3 WHERE `code` = 325212;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 1.1 WHERE `code` = 32522;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.8 WHERE `code` = 325221;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 1.3 WHERE `code` = 325222;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.8 WHERE `code` = 3253;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.9 WHERE `code` = 32531;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 0.9 WHERE `code` = 325311;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.1 WHERE `code` = 325312;
UPDATE `naics` SET `trir` = 6.8, `lwcr` = 3.5 WHERE `code` = 325314;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.6 WHERE `code` = 32532;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.1 WHERE `code` = 3254;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.1 WHERE `code` = 32541;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.3 WHERE `code` = 325411;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.1 WHERE `code` = 325412;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.3 WHERE `code` = 325413;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 1.1 WHERE `code` = 325414;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.9 WHERE `code` = 3255;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.7 WHERE `code` = 32551;
UPDATE `naics` SET `trir` = 3, `lwcr` = 2.3 WHERE `code` = 32552;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 2.1 WHERE `code` = 3256;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 2 WHERE `code` = 32561;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.3 WHERE `code` = 325611;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 2.4 WHERE `code` = 325612;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 2.2 WHERE `code` = 32562;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.3 WHERE `code` = 3259;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 0.7 WHERE `code` = 32591;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.6 WHERE `code` = 32592;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.3 WHERE `code` = 32599;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.6 WHERE `code` = 325991;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.3 WHERE `code` = 325992;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.2 WHERE `code` = 325998;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.7 WHERE `code` = 326;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.5 WHERE `code` = 3261;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.4 WHERE `code` = 32611;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 2.7 WHERE `code` = 326111;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.7 WHERE `code` = 326112;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.4 WHERE `code` = 326113;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.3 WHERE `code` = 32612;
UPDATE `naics` SET `trir` = 5, `lwcr` = 3 WHERE `code` = 326121;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.8 WHERE `code` = 326122;
UPDATE `naics` SET `trir` = 4, `lwcr` = 1.7 WHERE `code` = 32613;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.4 WHERE `code` = 32614;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 2.2 WHERE `code` = 32615;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.3 WHERE `code` = 32616;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.7 WHERE `code` = 32619;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3.1 WHERE `code` = 326191;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.7 WHERE `code` = 326192;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.7 WHERE `code` = 326199;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 3.3 WHERE `code` = 3262;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 3.5 WHERE `code` = 32621;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 3.6 WHERE `code` = 326211;
UPDATE `naics` SET `trir` = 5.6, `lwcr` = 3.5 WHERE `code` = 326212;
UPDATE `naics` SET `trir` = 5.9, `lwcr` = 3.8 WHERE `code` = 32622;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2.9 WHERE `code` = 32629;
UPDATE `naics` SET `trir` = 5.6, `lwcr` = 2.9 WHERE `code` = 326291;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.9 WHERE `code` = 326299;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3 WHERE `code` = 327;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 3.1 WHERE `code` = 3271;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 2.3 WHERE `code` = 32711;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.6 WHERE `code` = 327111;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.8 WHERE `code` = 327112;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.3 WHERE `code` = 327113;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 3.6 WHERE `code` = 32712;
UPDATE `naics` SET `trir` = 8.3, `lwcr` = 4.7 WHERE `code` = 327122;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 3.6 WHERE `code` = 327124;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.8 WHERE `code` = 3272;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.8 WHERE `code` = 32721;
UPDATE `naics` SET `trir` = 6, `lwcr` = 2.9 WHERE `code` = 327212;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 3.3 WHERE `code` = 327213;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.4 WHERE `code` = 327215;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 3.3 WHERE `code` = 3273;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 3.4 WHERE `code` = 32732;
UPDATE `naics` SET `trir` = 6, `lwcr` = 3.3 WHERE `code` = 32733;
UPDATE `naics` SET `trir` = 6.1, `lwcr` = 3.3 WHERE `code` = 327331;
UPDATE `naics` SET `trir` = 5.8, `lwcr` = 3.4 WHERE `code` = 327332;
UPDATE `naics` SET `trir` = 5.6, `lwcr` = 3.5 WHERE `code` = 32739;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.6 WHERE `code` = 3274;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 0.9 WHERE `code` = 32742;
UPDATE `naics` SET `trir` = 5.8, `lwcr` = 2.8 WHERE `code` = 3279;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.8 WHERE `code` = 32791;
UPDATE `naics` SET `trir` = 5.9, `lwcr` = 2.8 WHERE `code` = 32799;
UPDATE `naics` SET `trir` = 7.8, `lwcr` = 3.6 WHERE `code` = 327991;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.5 WHERE `code` = 327992;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2.3 WHERE `code` = 327993;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.4 WHERE `code` = 327999;
UPDATE `naics` SET `trir` = 6.2, `lwcr` = 3.2 WHERE `code` = 331;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.8 WHERE `code` = 3311;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.8 WHERE `code` = 33111;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.8 WHERE `code` = 331111;
UPDATE `naics` SET `trir` = 7.6, `lwcr` = 3.5 WHERE `code` = 3312;
UPDATE `naics` SET `trir` = 9.5, `lwcr` = 5.2 WHERE `code` = 33121;
UPDATE `naics` SET `trir` = 6, `lwcr` = 2.2 WHERE `code` = 33122;
UPDATE `naics` SET `trir` = 6.2, `lwcr` = 2.2 WHERE `code` = 331221;
UPDATE `naics` SET `trir` = 5.6, `lwcr` = 2.3 WHERE `code` = 331222;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.7 WHERE `code` = 3313;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.7 WHERE `code` = 33131;
UPDATE `naics` SET `trir` = 5, `lwcr` = 3.7 WHERE `code` = 331312;
UPDATE `naics` SET `trir` = 6.8, `lwcr` = 4.5 WHERE `code` = 331314;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.1 WHERE `code` = 331315;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 2.9 WHERE `code` = 331316;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 2.4 WHERE `code` = 331319;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 3 WHERE `code` = 3314;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 2.1 WHERE `code` = 33141;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.1 WHERE `code` = 331411;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 2.1 WHERE `code` = 331419;
UPDATE `naics` SET `trir` = 5.7, `lwcr` = 3.4 WHERE `code` = 33142;
UPDATE `naics` SET `trir` = 7.3, `lwcr` = 4.3 WHERE `code` = 331421;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.6 WHERE `code` = 331422;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2.9 WHERE `code` = 33149;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 3.1 WHERE `code` = 331491;
UPDATE `naics` SET `trir` = 8.7, `lwcr` = 4.6 WHERE `code` = 3315;
UPDATE `naics` SET `trir` = 9.9, `lwcr` = 5 WHERE `code` = 33151;
UPDATE `naics` SET `trir` = 11.3, `lwcr` = 5.4 WHERE `code` = 331511;
UPDATE `naics` SET `trir` = 8.2, `lwcr` = 4.9 WHERE `code` = 331512;
UPDATE `naics` SET `trir` = 7.7, `lwcr` = 4.1 WHERE `code` = 331513;
UPDATE `naics` SET `trir` = 7.1, `lwcr` = 4.1 WHERE `code` = 33152;
UPDATE `naics` SET `trir` = 7.2, `lwcr` = 3.4 WHERE `code` = 331521;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 2.2 WHERE `code` = 331522;
UPDATE `naics` SET `trir` = 9, `lwcr` = 5.9 WHERE `code` = 331524;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 3.1 WHERE `code` = 331525;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 2.6 WHERE `code` = 332;
UPDATE `naics` SET `trir` = 6.6, `lwcr` = 3.3 WHERE `code` = 3321;
UPDATE `naics` SET `trir` = 6.6, `lwcr` = 3.3 WHERE `code` = 33211;
UPDATE `naics` SET `trir` = 7.6, `lwcr` = 4.1 WHERE `code` = 332111;
UPDATE `naics` SET `trir` = 3, `lwcr` = 2 WHERE `code` = 332115;
UPDATE `naics` SET `trir` = 6.3, `lwcr` = 3 WHERE `code` = 332116;
UPDATE `naics` SET `trir` = 6.7, `lwcr` = 3.4 WHERE `code` = 332117;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 1.8 WHERE `code` = 3322;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 1.8 WHERE `code` = 33221;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 1.9 WHERE `code` = 332211;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 1.5 WHERE `code` = 332212;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 1.8 WHERE `code` = 332213;
UPDATE `naics` SET `trir` = 8.3, `lwcr` = 5.2 WHERE `code` = 332214;
UPDATE `naics` SET `trir` = 6.7, `lwcr` = 3.3 WHERE `code` = 3323;
UPDATE `naics` SET `trir` = 6.9, `lwcr` = 3.3 WHERE `code` = 33231;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 2 WHERE `code` = 332311;
UPDATE `naics` SET `trir` = 7.9, `lwcr` = 3.9 WHERE `code` = 332312;
UPDATE `naics` SET `trir` = 6.9, `lwcr` = 2.8 WHERE `code` = 332313;
UPDATE `naics` SET `trir` = 6.5, `lwcr` = 3.2 WHERE `code` = 33232;
UPDATE `naics` SET `trir` = 5.8, `lwcr` = 3.3 WHERE `code` = 332321;
UPDATE `naics` SET `trir` = 6.5, `lwcr` = 3.1 WHERE `code` = 332322;
UPDATE `naics` SET `trir` = 7.6, `lwcr` = 3.5 WHERE `code` = 332323;
UPDATE `naics` SET `trir` = 5.6, `lwcr` = 2.8 WHERE `code` = 3324;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.2 WHERE `code` = 33241;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.7 WHERE `code` = 33242;
UPDATE `naics` SET `trir` = 6.7, `lwcr` = 3.4 WHERE `code` = 33243;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.6 WHERE `code` = 332431;
UPDATE `naics` SET `trir` = 8.6, `lwcr` = 4.3 WHERE `code` = 332439;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.5 WHERE `code` = 3325;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.3 WHERE `code` = 3326;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.3 WHERE `code` = 33261;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.8 WHERE `code` = 332611;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 1.9 WHERE `code` = 332612;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.4 WHERE `code` = 332618;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.1 WHERE `code` = 3327;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2 WHERE `code` = 33271;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.3 WHERE `code` = 33272;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 1.8 WHERE `code` = 332721;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.8 WHERE `code` = 332722;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.8 WHERE `code` = 3328;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.8 WHERE `code` = 33281;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2 WHERE `code` = 332811;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.7 WHERE `code` = 332812;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 3.2 WHERE `code` = 332813;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.2 WHERE `code` = 3329;
UPDATE `naics` SET `trir` = 4, `lwcr` = 1.8 WHERE `code` = 33291;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2 WHERE `code` = 332911;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.7 WHERE `code` = 332912;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 1.5 WHERE `code` = 332913;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2.1 WHERE `code` = 332919;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2.4 WHERE `code` = 33299;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.6 WHERE `code` = 332991;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.1 WHERE `code` = 332992;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.9 WHERE `code` = 332993;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.9 WHERE `code` = 332994;
UPDATE `naics` SET `trir` = 2, `lwcr` = 0.7 WHERE `code` = 332995;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2.5 WHERE `code` = 332996;
UPDATE `naics` SET `trir` = 6.1, `lwcr` = 2.1 WHERE `code` = 332998;
UPDATE `naics` SET `trir` = 7.1, `lwcr` = 3.5 WHERE `code` = 332999;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2 WHERE `code` = 333;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.3 WHERE `code` = 3331;
UPDATE `naics` SET `trir` = 6.5, `lwcr` = 2.9 WHERE `code` = 33311;
UPDATE `naics` SET `trir` = 7.1, `lwcr` = 3.2 WHERE `code` = 333111;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2 WHERE `code` = 333112;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.4 WHERE `code` = 33312;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.7 WHERE `code` = 33313;
UPDATE `naics` SET `trir` = 7.8, `lwcr` = 2.8 WHERE `code` = 333131;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.5 WHERE `code` = 333132;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 1.6 WHERE `code` = 3332;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 1.7 WHERE `code` = 33321;
UPDATE `naics` SET `trir` = 4, `lwcr` = 1.2 WHERE `code` = 33322;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.6 WHERE `code` = 33329;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 1.5 WHERE `code` = 333291;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.1 WHERE `code` = 333293;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.1 WHERE `code` = 333294;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.7 WHERE `code` = 333295;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2 WHERE `code` = 333298;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.9 WHERE `code` = 3333;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.9 WHERE `code` = 33331;
UPDATE `naics` SET `trir` = 6.8, `lwcr` = 3.3 WHERE `code` = 333311;
UPDATE `naics` SET `trir` = 7.8, `lwcr` = 3 WHERE `code` = 333312;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 1 WHERE `code` = 333313;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.3 WHERE `code` = 333314;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 2.1 WHERE `code` = 333315;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 2.1 WHERE `code` = 333319;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2.5 WHERE `code` = 3334;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2.5 WHERE `code` = 33341;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 2.1 WHERE `code` = 333411;
UPDATE `naics` SET `trir` = 7.1, `lwcr` = 3.9 WHERE `code` = 333412;
UPDATE `naics` SET `trir` = 6.9, `lwcr` = 3.2 WHERE `code` = 333414;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.3 WHERE `code` = 333415;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 1.7 WHERE `code` = 3335;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 1.7 WHERE `code` = 33351;
UPDATE `naics` SET `trir` = 4, `lwcr` = 1.6 WHERE `code` = 333511;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.5 WHERE `code` = 333512;
UPDATE `naics` SET `trir` = 5.7, `lwcr` = 3.5 WHERE `code` = 333513;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 1.5 WHERE `code` = 333514;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 1.4 WHERE `code` = 333515;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 1.3 WHERE `code` = 333516;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 1.9 WHERE `code` = 333518;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 1.5 WHERE `code` = 3336;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 1.5 WHERE `code` = 33361;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 1.2 WHERE `code` = 333611;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.1 WHERE `code` = 333612;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.3 WHERE `code` = 333613;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.3 WHERE `code` = 333618;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 2.1 WHERE `code` = 3339;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2 WHERE `code` = 33391;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.3 WHERE `code` = 333911;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.6 WHERE `code` = 333912;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.7 WHERE `code` = 333913;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.4 WHERE `code` = 33392;
UPDATE `naics` SET `trir` = 7, `lwcr` = 2.5 WHERE `code` = 333921;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.1 WHERE `code` = 333922;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.9 WHERE `code` = 333923;
UPDATE `naics` SET `trir` = 5.8, `lwcr` = 2.3 WHERE `code` = 333924;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2 WHERE `code` = 33399;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.2 WHERE `code` = 333991;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.2 WHERE `code` = 333993;
UPDATE `naics` SET `trir` = 5, `lwcr` = 1.9 WHERE `code` = 333994;
UPDATE `naics` SET `trir` = 6.2, `lwcr` = 2.8 WHERE `code` = 333995;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.2 WHERE `code` = 333996;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.4 WHERE `code` = 333999;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.8 WHERE `code` = 334;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.4 WHERE `code` = 3341;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.4 WHERE `code` = 33411;
UPDATE `naics` SET `trir` = 0.7, `lwcr` = 0.3 WHERE `code` = 334111;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.5 WHERE `code` = 334112;
UPDATE `naics` SET `trir` = 0.7, `lwcr` = 0.1 WHERE `code` = 334113;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.5 WHERE `code` = 334119;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.8 WHERE `code` = 3342;
UPDATE `naics` SET `trir` = 1.3, `lwcr` = 0.5 WHERE `code` = 33421;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.8 WHERE `code` = 33422;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 1.2 WHERE `code` = 33429;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 0.6 WHERE `code` = 3343;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0.9 WHERE `code` = 3344;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0.9 WHERE `code` = 33441;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.3 WHERE `code` = 334411;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.4 WHERE `code` = 334412;
UPDATE `naics` SET `trir` = 1, `lwcr` = 0.4 WHERE `code` = 334413;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.5 WHERE `code` = 334414;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.5 WHERE `code` = 334415;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 2.1 WHERE `code` = 334416;
UPDATE `naics` SET `trir` = 1.7, `lwcr` = 0.7 WHERE `code` = 334418;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.7 WHERE `code` = 334419;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0.8 WHERE `code` = 3345;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0.8 WHERE `code` = 33451;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.6 WHERE `code` = 334510;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.6 WHERE `code` = 334511;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1 WHERE `code` = 334512;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1 WHERE `code` = 334513;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.6 WHERE `code` = 334514;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 0.6 WHERE `code` = 334515;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1 WHERE `code` = 334516;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.6 WHERE `code` = 334517;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0 WHERE `code` = 334518;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2.1 WHERE `code` = 334519;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 1.1 WHERE `code` = 3346;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 1.1 WHERE `code` = 33461;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.8 WHERE `code` = 334612;
UPDATE `naics` SET `trir` = 1.7, `lwcr` = 1.2 WHERE `code` = 334613;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.8 WHERE `code` = 335;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2 WHERE `code` = 3351;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.5 WHERE `code` = 33511;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.1 WHERE `code` = 33512;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.2 WHERE `code` = 335121;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2.1 WHERE `code` = 335122;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 2.1 WHERE `code` = 335129;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.7 WHERE `code` = 3352;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.5 WHERE `code` = 33521;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.6 WHERE `code` = 335211;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.8 WHERE `code` = 33522;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.1 WHERE `code` = 335222;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.2 WHERE `code` = 335228;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 2 WHERE `code` = 3353;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 2 WHERE `code` = 33531;
UPDATE `naics` SET `trir` = 6.7, `lwcr` = 4.3 WHERE `code` = 335311;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2.4 WHERE `code` = 335312;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.6 WHERE `code` = 335313;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.7 WHERE `code` = 335314;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.6 WHERE `code` = 3359;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.1 WHERE `code` = 33591;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 2 WHERE `code` = 335911;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 2.1 WHERE `code` = 335912;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.3 WHERE `code` = 33592;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.8 WHERE `code` = 335929;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.8 WHERE `code` = 33593;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.6 WHERE `code` = 335931;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.2 WHERE `code` = 335932;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.3 WHERE `code` = 33599;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 2.4 WHERE `code` = 335991;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.1 WHERE `code` = 335999;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.7 WHERE `code` = 336;
UPDATE `naics` SET `trir` = 7.8, `lwcr` = 3.8 WHERE `code` = 3361;
UPDATE `naics` SET `trir` = 7.9, `lwcr` = 3.8 WHERE `code` = 33611;
UPDATE `naics` SET `trir` = 7.3, `lwcr` = 3.9 WHERE `code` = 336111;
UPDATE `naics` SET `trir` = 9.4, `lwcr` = 3.6 WHERE `code` = 336112;
UPDATE `naics` SET `trir` = 7.4, `lwcr` = 3.7 WHERE `code` = 33612;
UPDATE `naics` SET `trir` = 8.2, `lwcr` = 3.7 WHERE `code` = 3362;
UPDATE `naics` SET `trir` = 8.2, `lwcr` = 3.7 WHERE `code` = 33621;
UPDATE `naics` SET `trir` = 7.5, `lwcr` = 3.8 WHERE `code` = 336211;
UPDATE `naics` SET `trir` = 8.4, `lwcr` = 3.5 WHERE `code` = 336212;
UPDATE `naics` SET `trir` = 7.6, `lwcr` = 2.5 WHERE `code` = 336213;
UPDATE `naics` SET `trir` = 10.2, `lwcr` = 4.1 WHERE `code` = 336214;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.6 WHERE `code` = 3363;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.4 WHERE `code` = 33631;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.3 WHERE `code` = 336311;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.5 WHERE `code` = 336312;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.7 WHERE `code` = 33632;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.5 WHERE `code` = 336321;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.5 WHERE `code` = 336322;
UPDATE `naics` SET `trir` = 6.5, `lwcr` = 3.2 WHERE `code` = 33633;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.9 WHERE `code` = 33634;
UPDATE `naics` SET `trir` = 5.9, `lwcr` = 2.6 WHERE `code` = 33635;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3.2 WHERE `code` = 33636;
UPDATE `naics` SET `trir` = 6.8, `lwcr` = 2.9 WHERE `code` = 33637;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.5 WHERE `code` = 33639;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.5 WHERE `code` = 336391;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.5 WHERE `code` = 336399;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.8 WHERE `code` = 3364;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.8 WHERE `code` = 33641;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2.2 WHERE `code` = 336411;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.4 WHERE `code` = 336412;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.1 WHERE `code` = 336413;
UPDATE `naics` SET `trir` = 1, `lwcr` = 0.6 WHERE `code` = 336414;
UPDATE `naics` SET `trir` = 1.7, `lwcr` = 0.8 WHERE `code` = 336415;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.7 WHERE `code` = 336419;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.4 WHERE `code` = 3365;
UPDATE `naics` SET `trir` = 7.8, `lwcr` = 4.5 WHERE `code` = 3366;
UPDATE `naics` SET `trir` = 7.8, `lwcr` = 4.5 WHERE `code` = 33661;
UPDATE `naics` SET `trir` = 7.8, `lwcr` = 4.8 WHERE `code` = 336611;
UPDATE `naics` SET `trir` = 7.7, `lwcr` = 3.6 WHERE `code` = 336612;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.4 WHERE `code` = 3369;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.4 WHERE `code` = 33699;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.6 WHERE `code` = 336991;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2.3 WHERE `code` = 336992;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.4 WHERE `code` = 336999;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.7 WHERE `code` = 337;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 2.8 WHERE `code` = 3371;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.1 WHERE `code` = 33711;
UPDATE `naics` SET `trir` = 6.4, `lwcr` = 3.4 WHERE `code` = 33712;
UPDATE `naics` SET `trir` = 6.4, `lwcr` = 3.4 WHERE `code` = 337121;
UPDATE `naics` SET `trir` = 7.4, `lwcr` = 3.9 WHERE `code` = 337122;
UPDATE `naics` SET `trir` = 5.7, `lwcr` = 2.9 WHERE `code` = 337127;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.7 WHERE `code` = 337129;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.4 WHERE `code` = 3372;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.4 WHERE `code` = 33721;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2 WHERE `code` = 337211;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 2.9 WHERE `code` = 337212;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.5 WHERE `code` = 337214;
UPDATE `naics` SET `trir` = 6.4, `lwcr` = 2.7 WHERE `code` = 337215;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.5 WHERE `code` = 3379;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.7 WHERE `code` = 33791;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 2.4 WHERE `code` = 33792;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 1.6 WHERE `code` = 339;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.2 WHERE `code` = 3391;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.2 WHERE `code` = 33911;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.1 WHERE `code` = 339112;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.6 WHERE `code` = 339113;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.6 WHERE `code` = 339115;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 0.4 WHERE `code` = 339116;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2 WHERE `code` = 3399;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.2 WHERE `code` = 33991;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.3 WHERE `code` = 339911;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.7 WHERE `code` = 339912;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 1.8 WHERE `code` = 33992;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 0.8 WHERE `code` = 33993;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1 WHERE `code` = 339932;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.8 WHERE `code` = 33994;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 2.2 WHERE `code` = 339941;
UPDATE `naics` SET `trir` = 5, `lwcr` = 4.6 WHERE `code` = 339942;
UPDATE `naics` SET `trir` = 4, `lwcr` = 1.5 WHERE `code` = 339943;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.2 WHERE `code` = 33995;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2.1 WHERE `code` = 33999;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.8 WHERE `code` = 339991;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.6 WHERE `code` = 339992;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 3.1 WHERE `code` = 339993;
UPDATE `naics` SET `trir` = 7.4, `lwcr` = 5.6 WHERE `code` = 339994;
UPDATE `naics` SET `trir` = 8, `lwcr` = 2.7 WHERE `code` = 339995;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.4 WHERE `code` = 339999;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 2 WHERE `code` = 42;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 1.7 WHERE `code` = 423;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 2.2 WHERE `code` = 4231;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.6 WHERE `code` = 4232;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.8 WHERE `code` = 4233;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 1.1 WHERE `code` = 4234;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3.1 WHERE `code` = 4235;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 1 WHERE `code` = 4236;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.5 WHERE `code` = 4237;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.6 WHERE `code` = 4238;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 2.1 WHERE `code` = 4239;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.9 WHERE `code` = 424;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.7 WHERE `code` = 4241;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.4 WHERE `code` = 4242;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 1.1 WHERE `code` = 4243;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 4 WHERE `code` = 4244;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.2 WHERE `code` = 4245;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.6 WHERE `code` = 4246;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.8 WHERE `code` = 4247;
UPDATE `naics` SET `trir` = 7.7, `lwcr` = 5.4 WHERE `code` = 4248;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2.1 WHERE `code` = 4249;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.2 WHERE `code` = 44;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.2 WHERE `code` = 45;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 1.7 WHERE `code` = 441;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.5 WHERE `code` = 4411;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 1.5 WHERE `code` = 44111;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 1.1 WHERE `code` = 44112;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.3 WHERE `code` = 4412;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 1.5 WHERE `code` = 44121;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.2 WHERE `code` = 44122;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 2.5 WHERE `code` = 4413;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.3 WHERE `code` = 44131;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2.7 WHERE `code` = 44132;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.3 WHERE `code` = 442;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 2.5 WHERE `code` = 4421;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.1 WHERE `code` = 4422;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 0.9 WHERE `code` = 44221;
UPDATE `naics` SET `trir` = 5.7, `lwcr` = 2.9 WHERE `code` = 44229;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0.8 WHERE `code` = 443;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0.8 WHERE `code` = 4431;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1 WHERE `code` = 44311;
UPDATE `naics` SET `trir` = 0.7, `lwcr` = 0.5 WHERE `code` = 44312;
UPDATE `naics` SET `trir` = 1.1, `lwcr` = 1 WHERE `code` = 44313;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 3.3 WHERE `code` = 444;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 3.4 WHERE `code` = 4441;
UPDATE `naics` SET `trir` = 6.5, `lwcr` = 4.2 WHERE `code` = 44411;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 2 WHERE `code` = 44412;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.4 WHERE `code` = 44413;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2.6 WHERE `code` = 44419;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.3 WHERE `code` = 4442;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.5 WHERE `code` = 44421;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.9 WHERE `code` = 44422;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.8 WHERE `code` = 445;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 3 WHERE `code` = 4451;
UPDATE `naics` SET `trir` = 5.6, `lwcr` = 3.1 WHERE `code` = 44511;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.2 WHERE `code` = 4452;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 1.2 WHERE `code` = 44521;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.2 WHERE `code` = 44522;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.4 WHERE `code` = 44523;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 1.1 WHERE `code` = 44529;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.9 WHERE `code` = 4453;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1 WHERE `code` = 446;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1 WHERE `code` = 4461;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1 WHERE `code` = 44611;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 1.5 WHERE `code` = 44619;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.5 WHERE `code` = 447;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.5 WHERE `code` = 4471;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.5 WHERE `code` = 44711;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.5 WHERE `code` = 44719;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.1 WHERE `code` = 448;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 1.2 WHERE `code` = 4481;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.9 WHERE `code` = 44811;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 0.8 WHERE `code` = 44812;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.2 WHERE `code` = 44813;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 1.6 WHERE `code` = 44814;
UPDATE `naics` SET `trir` = 1.3, `lwcr` = 0.7 WHERE `code` = 44815;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.6 WHERE `code` = 44819;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.4 WHERE `code` = 4483;
UPDATE `naics` SET `trir` = 0.7, `lwcr` = 0.3 WHERE `code` = 44831;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.5 WHERE `code` = 44832;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1 WHERE `code` = 451;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.2 WHERE `code` = 4511;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.1 WHERE `code` = 45111;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 1.4 WHERE `code` = 45112;
UPDATE `naics` SET `trir` = 4, `lwcr` = 1.5 WHERE `code` = 45113;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 0.7 WHERE `code` = 45114;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.6 WHERE `code` = 4512;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 0.5 WHERE `code` = 45121;
UPDATE `naics` SET `trir` = 1, `lwcr` = 0.8 WHERE `code` = 45122;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3.1 WHERE `code` = 452;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.6 WHERE `code` = 4521;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 3.6 WHERE `code` = 4529;
UPDATE `naics` SET `trir` = 5.7, `lwcr` = 3.8 WHERE `code` = 45291;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.8 WHERE `code` = 45299;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 1.6 WHERE `code` = 453;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.6 WHERE `code` = 4531;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.4 WHERE `code` = 4532;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.3 WHERE `code` = 45321;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.6 WHERE `code` = 45322;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 2.7 WHERE `code` = 4533;
UPDATE `naics` SET `trir` = 6.1, `lwcr` = 1.4 WHERE `code` = 4539;
UPDATE `naics` SET `trir` = 13.6, `lwcr` = 2.1 WHERE `code` = 45391;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.8 WHERE `code` = 45393;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 1 WHERE `code` = 45399;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 1.9 WHERE `code` = 454;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 1.2 WHERE `code` = 4541;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 1.2 WHERE `code` = 45411;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 1 WHERE `code` = 454111;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.3 WHERE `code` = 454113;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.7 WHERE `code` = 4542;
UPDATE `naics` SET `trir` = 6.2, `lwcr` = 3.2 WHERE `code` = 4543;
UPDATE `naics` SET `trir` = 6.6, `lwcr` = 3 WHERE `code` = 45431;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 3.5 WHERE `code` = 45439;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3.5 WHERE `code` = 48;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3.5 WHERE `code` = 49;
UPDATE `naics` SET `trir` = 8.5, `lwcr` = 6.5 WHERE `code` = 481;
UPDATE `naics` SET `trir` = 9.3, `lwcr` = 7.1 WHERE `code` = 4811;
UPDATE `naics` SET `trir` = 9.3, `lwcr` = 7.1 WHERE `code` = 48111;
UPDATE `naics` SET `trir` = 9.5, `lwcr` = 7.3 WHERE `code` = 481111;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 1 WHERE `code` = 481112;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.5 WHERE `code` = 4812;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.6 WHERE `code` = 482;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.7 WHERE `code` = 483;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.3 WHERE `code` = 4831;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.3 WHERE `code` = 48311;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 2.8 WHERE `code` = 483113;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.8 WHERE `code` = 483114;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 2.2 WHERE `code` = 4832;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 2.2 WHERE `code` = 48321;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 2.4 WHERE `code` = 483211;
UPDATE `naics` SET `trir` = 0.7, `lwcr` = 0.6 WHERE `code` = 483212;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 3 WHERE `code` = 484;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 3 WHERE `code` = 4841;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.4 WHERE `code` = 48411;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 3.2 WHERE `code` = 48412;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 3 WHERE `code` = 4842;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3.4 WHERE `code` = 48421;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2.8 WHERE `code` = 48422;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 3.1 WHERE `code` = 48423;
UPDATE `naics` SET `trir` = 5, `lwcr` = 3.1 WHERE `code` = 485;
UPDATE `naics` SET `trir` = 7.9, `lwcr` = 5.3 WHERE `code` = 4851;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 3.7 WHERE `code` = 4852;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.8 WHERE `code` = 4853;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.1 WHERE `code` = 48531;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.5 WHERE `code` = 48532;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 2.5 WHERE `code` = 4854;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 3.3 WHERE `code` = 4855;
UPDATE `naics` SET `trir` = 5.8, `lwcr` = 3.7 WHERE `code` = 4859;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.7 WHERE `code` = 486;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 0.9 WHERE `code` = 4862;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 2 WHERE `code` = 487;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 2.4 WHERE `code` = 4871;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.2 WHERE `code` = 4872;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.7 WHERE `code` = 488;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.7 WHERE `code` = 4881;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.9 WHERE `code` = 4882;
UPDATE `naics` SET `trir` = 6.9, `lwcr` = 4.7 WHERE `code` = 4883;
UPDATE `naics` SET `trir` = 7.2, `lwcr` = 4.7 WHERE `code` = 48832;
UPDATE `naics` SET `trir` = 5.3, `lwcr` = 3.4 WHERE `code` = 48833;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.9 WHERE `code` = 48839;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2.8 WHERE `code` = 4884;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 2.2 WHERE `code` = 48841;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 3.7 WHERE `code` = 48849;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.4 WHERE `code` = 4885;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 3.7 WHERE `code` = 4889;
UPDATE `naics` SET `trir` = 7.2, `lwcr` = 4.7 WHERE `code` = 492;
UPDATE `naics` SET `trir` = 7.6, `lwcr` = 4.9 WHERE `code` = 4921;
UPDATE `naics` SET `trir` = 3.2, `lwcr` = 2.5 WHERE `code` = 4922;
UPDATE `naics` SET `trir` = 5.9, `lwcr` = 4.3 WHERE `code` = 493;
UPDATE `naics` SET `trir` = 5.9, `lwcr` = 4.3 WHERE `code` = 4931;
UPDATE `naics` SET `trir` = 6, `lwcr` = 4.4 WHERE `code` = 49311;
UPDATE `naics` SET `trir` = 6.4, `lwcr` = 4.5 WHERE `code` = 49312;
UPDATE `naics` SET `trir` = 4, `lwcr` = 1.9 WHERE `code` = 49313;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 3.1 WHERE `code` = 49319;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.8 WHERE `code` = 22;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.8 WHERE `code` = 221;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.5 WHERE `code` = 2211;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.2 WHERE `code` = 22111;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.8 WHERE `code` = 221111;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 1.7 WHERE `code` = 221112;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.3 WHERE `code` = 221113;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.3 WHERE `code` = 221119;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2 WHERE `code` = 22112;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.5 WHERE `code` = 2212;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.5 WHERE `code` = 2213;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 2.5 WHERE `code` = 22131;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.8 WHERE `code` = 22133;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 1 WHERE `code` = 51;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.7 WHERE `code` = 511;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 1.1 WHERE `code` = 5111;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.7 WHERE `code` = 51111;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.2 WHERE `code` = 51112;
UPDATE `naics` SET `trir` = 1, `lwcr` = 0.5 WHERE `code` = 51113;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.5 WHERE `code` = 51114;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.8 WHERE `code` = 51119;
UPDATE `naics` SET `trir` = 0.5, `lwcr` = 0.1 WHERE `code` = 5112;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 0.6 WHERE `code` = 512;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 0.7 WHERE `code` = 5121;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 0.6 WHERE `code` = 51211;
UPDATE `naics` SET `trir` = 7.3, `lwcr` = 0.8 WHERE `code` = 51213;
UPDATE `naics` SET `trir` = 1.1, `lwcr` = 0.1 WHERE `code` = 5122;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 1.1 WHERE `code` = 51229;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.1 WHERE `code` = 515;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.5 WHERE `code` = 5151;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.3 WHERE `code` = 51511;
UPDATE `naics` SET `trir` = 1.7, `lwcr` = 0.7 WHERE `code` = 51512;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.4 WHERE `code` = 5152;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1.5 WHERE `code` = 517;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.8 WHERE `code` = 5171;
UPDATE `naics` SET `trir` = 1, `lwcr` = 0.7 WHERE `code` = 5172;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.1 WHERE `code` = 5179;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.2 WHERE `code` = 518;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.2 WHERE `code` = 5182;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.2 WHERE `code` = 519;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.2 WHERE `code` = 5191;
UPDATE `naics` SET `trir` = 0.4, `lwcr` = 0 WHERE `code` = 51911;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 0.4 WHERE `code` = 51912;
UPDATE `naics` SET `trir` = 0.3, `lwcr` = 0.1 WHERE `code` = 51913;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.2 WHERE `code` = 52;
UPDATE `naics` SET `trir` = 1, `lwcr` = 0.5 WHERE `code` = 521;
UPDATE `naics` SET `trir` = 1, `lwcr` = 0.2 WHERE `code` = 522;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.3 WHERE `code` = 5221;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.2 WHERE `code` = 52211;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.3 WHERE `code` = 52212;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0.5 WHERE `code` = 52213;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.2 WHERE `code` = 5222;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.2 WHERE `code` = 52221;
UPDATE `naics` SET `trir` = 0.5, `lwcr` = 0.2 WHERE `code` = 52222;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.2 WHERE `code` = 52229;
UPDATE `naics` SET `trir` = 0.9, `lwcr` = 0.2 WHERE `code` = 5223;
UPDATE `naics` SET `trir` = 0.1, `lwcr` = 0 WHERE `code` = 52231;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0.3 WHERE `code` = 52232;
UPDATE `naics` SET `trir` = 0.4, `lwcr` = 0.1 WHERE `code` = 52239;
UPDATE `naics` SET `trir` = 0.2, `lwcr` = 0.1 WHERE `code` = 523;
UPDATE `naics` SET `trir` = 0.2, `lwcr` = 0.1 WHERE `code` = 5231;
UPDATE `naics` SET `trir` = 0.2, `lwcr` = 0 WHERE `code` = 52311;
UPDATE `naics` SET `trir` = 0.2, `lwcr` = 0 WHERE `code` = 52312;
UPDATE `naics` SET `trir` = 0.2, `lwcr` = 0.1 WHERE `code` = 5239;
UPDATE `naics` SET `trir` = 0.1, `lwcr` = 0.1 WHERE `code` = 52391;
UPDATE `naics` SET `trir` = 0.3, `lwcr` = 0.1 WHERE `code` = 52392;
UPDATE `naics` SET `trir` = 0.1, `lwcr` = 0.1 WHERE `code` = 52393;
UPDATE `naics` SET `trir` = 0.1, `lwcr` = 0 WHERE `code` = 52399;
UPDATE `naics` SET `trir` = 0.9, `lwcr` = 0.3 WHERE `code` = 524;
UPDATE `naics` SET `trir` = 0.9, `lwcr` = 0.3 WHERE `code` = 5241;
UPDATE `naics` SET `trir` = 1, `lwcr` = 0.3 WHERE `code` = 52411;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.3 WHERE `code` = 52412;
UPDATE `naics` SET `trir` = 0.7, `lwcr` = 0.3 WHERE `code` = 52413;
UPDATE `naics` SET `trir` = 0.7, `lwcr` = 0.2 WHERE `code` = 5242;
UPDATE `naics` SET `trir` = 0, `lwcr` = 0.2 WHERE `code` = 52421;
UPDATE `naics` SET `trir` = 1.3, `lwcr` = 0.4 WHERE `code` = 52429;
UPDATE `naics` SET `trir` = 0.7, `lwcr` = 0.3 WHERE `code` = 525;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.4 WHERE `code` = 5251;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.3 WHERE `code` = 52519;
UPDATE `naics` SET `trir` = 0.7, `lwcr` = 0.2 WHERE `code` = 5259;
UPDATE `naics` SET `trir` = 0.4, `lwcr` = 0.2 WHERE `code` = 52591;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.9 WHERE `code` = 53;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 1.7 WHERE `code` = 531;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 2 WHERE `code` = 5311;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 1.8 WHERE `code` = 53111;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 1.7 WHERE `code` = 53112;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 2.1 WHERE `code` = 53113;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.8 WHERE `code` = 5312;
UPDATE `naics` SET `trir` = 3.6, `lwcr` = 1.9 WHERE `code` = 5313;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.1 WHERE `code` = 53131;
UPDATE `naics` SET `trir` = 0.3, `lwcr` = 0.2 WHERE `code` = 53132;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.8 WHERE `code` = 53139;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 2.3 WHERE `code` = 532;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.2 WHERE `code` = 5321;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 1.9 WHERE `code` = 53211;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.7 WHERE `code` = 53212;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.7 WHERE `code` = 5322;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.3 WHERE `code` = 53223;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2.5 WHERE `code` = 53229;
UPDATE `naics` SET `trir` = 5.8, `lwcr` = 3.5 WHERE `code` = 5323;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.5 WHERE `code` = 5324;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 1 WHERE `code` = 53241;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 1.8 WHERE `code` = 53242;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 2.2 WHERE `code` = 53249;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.2 WHERE `code` = 533;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.5 WHERE `code` = 54;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.5 WHERE `code` = 541;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.3 WHERE `code` = 5411;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.2 WHERE `code` = 5412;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.2 WHERE `code` = 54121;
UPDATE `naics` SET `trir` = 0.3, `lwcr` = 0.1 WHERE `code` = 541211;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.2 WHERE `code` = 541219;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.8 WHERE `code` = 5413;
UPDATE `naics` SET `trir` = 0.4, `lwcr` = 0.2 WHERE `code` = 54131;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 2.2 WHERE `code` = 54132;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.5 WHERE `code` = 54133;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.5 WHERE `code` = 54137;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.6 WHERE `code` = 5414;
UPDATE `naics` SET `trir` = 0.4, `lwcr` = 0.2 WHERE `code` = 5415;
UPDATE `naics` SET `trir` = 0.4, `lwcr` = 0.2 WHERE `code` = 54151;
UPDATE `naics` SET `trir` = 0.3, `lwcr` = 0.1 WHERE `code` = 541511;
UPDATE `naics` SET `trir` = 0.3, `lwcr` = 0.2 WHERE `code` = 541512;
UPDATE `naics` SET `trir` = 1.3, `lwcr` = 0.4 WHERE `code` = 541513;
UPDATE `naics` SET `trir` = 0.5, `lwcr` = 0.2 WHERE `code` = 541519;
UPDATE `naics` SET `trir` = 0.7, `lwcr` = 0.2 WHERE `code` = 5416;
UPDATE `naics` SET `trir` = 0.6, `lwcr` = 0.3 WHERE `code` = 54161;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.3 WHERE `code` = 54162;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.1 WHERE `code` = 54169;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.5 WHERE `code` = 5417;
UPDATE `naics` SET `trir` = 1.1, `lwcr` = 0.7 WHERE `code` = 5418;
UPDATE `naics` SET `trir` = 5.5, `lwcr` = 1.9 WHERE `code` = 5419;
UPDATE `naics` SET `trir` = 0.5, `lwcr` = 0.3 WHERE `code` = 54191;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.3 WHERE `code` = 54192;
UPDATE `naics` SET `trir` = 9.4, `lwcr` = 3.4 WHERE `code` = 54194;
UPDATE `naics` SET `trir` = 1.1, `lwcr` = 0.3 WHERE `code` = 54199;
UPDATE `naics` SET `trir` = 1.7, `lwcr` = 0.8 WHERE `code` = 55;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.6 WHERE `code` = 56;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.5 WHERE `code` = 561;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.5 WHERE `code` = 5611;
UPDATE `naics` SET `trir` = 4.7, `lwcr` = 2.6 WHERE `code` = 5612;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.1 WHERE `code` = 5613;
UPDATE `naics` SET `trir` = 0.9, `lwcr` = 0.4 WHERE `code` = 56131;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1 WHERE `code` = 56132;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.6 WHERE `code` = 5614;
UPDATE `naics` SET `trir` = 0.2, `lwcr` = 0.1 WHERE `code` = 56141;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.5 WHERE `code` = 56142;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 0.9 WHERE `code` = 56143;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.3 WHERE `code` = 56144;
UPDATE `naics` SET `trir` = 0.5, `lwcr` = 0.1 WHERE `code` = 56145;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 1.3 WHERE `code` = 56149;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.5 WHERE `code` = 5615;
UPDATE `naics` SET `trir` = 0.4, `lwcr` = 0.2 WHERE `code` = 56151;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.5 WHERE `code` = 56152;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.9 WHERE `code` = 56159;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.1 WHERE `code` = 5616;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 1.1 WHERE `code` = 56161;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 1.1 WHERE `code` = 561612;
UPDATE `naics` SET `trir` = 4.6, `lwcr` = 2.4 WHERE `code` = 561613;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 0.9 WHERE `code` = 56162;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2.4 WHERE `code` = 5617;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.9 WHERE `code` = 56172;
UPDATE `naics` SET `trir` = 5, `lwcr` = 3.3 WHERE `code` = 56173;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.4 WHERE `code` = 56174;
UPDATE `naics` SET `trir` = 0, `lwcr` = 1.5 WHERE `code` = 56179;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.9 WHERE `code` = 5619;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 3.3 WHERE `code` = 562;
UPDATE `naics` SET `trir` = 6.4, `lwcr` = 4.2 WHERE `code` = 5621;
UPDATE `naics` SET `trir` = 6.4, `lwcr` = 4.2 WHERE `code` = 56211;
UPDATE `naics` SET `trir` = 6.1, `lwcr` = 4.1 WHERE `code` = 562111;
UPDATE `naics` SET `trir` = 9.7, `lwcr` = 6 WHERE `code` = 562112;
UPDATE `naics` SET `trir` = 6.4, `lwcr` = 3.1 WHERE `code` = 562119;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 3.2 WHERE `code` = 5622;
UPDATE `naics` SET `trir` = 4.8, `lwcr` = 3.2 WHERE `code` = 56221;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 2.7 WHERE `code` = 562211;
UPDATE `naics` SET `trir` = 5.9, `lwcr` = 3.9 WHERE `code` = 562212;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.2 WHERE `code` = 562213;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.1 WHERE `code` = 5629;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.5 WHERE `code` = 56291;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2.7 WHERE `code` = 56299;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 0.8 WHERE `code` = 61;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 0.8 WHERE `code` = 611;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1 WHERE `code` = 6111;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 0.6 WHERE `code` = 6112;
UPDATE `naics` SET `trir` = 2.2, `lwcr` = 0.9 WHERE `code` = 6113;
UPDATE `naics` SET `trir` = 0.7, `lwcr` = 0.1 WHERE `code` = 6114;
UPDATE `naics` SET `trir` = 1.2, `lwcr` = 0.5 WHERE `code` = 61141;
UPDATE `naics` SET `trir` = 0.8, `lwcr` = 0.2 WHERE `code` = 61143;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 0.8 WHERE `code` = 6115;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.7 WHERE `code` = 6116;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 1.4 WHERE `code` = 61161;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 0.8 WHERE `code` = 61162;
UPDATE `naics` SET `trir` = 0.9, `lwcr` = 0.5 WHERE `code` = 61169;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 0.7 WHERE `code` = 6117;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 2.4 WHERE `code` = 62;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 0.9 WHERE `code` = 621;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.3 WHERE `code` = 6211;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.3 WHERE `code` = 62111;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.3 WHERE `code` = 621111;
UPDATE `naics` SET `trir` = 1.9, `lwcr` = 0.6 WHERE `code` = 621112;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 0.4 WHERE `code` = 6212;
UPDATE `naics` SET `trir` = 1.1, `lwcr` = 0.5 WHERE `code` = 6213;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 1.1 WHERE `code` = 6214;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1 WHERE `code` = 6215;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2 WHERE `code` = 6216;
UPDATE `naics` SET `trir` = 7.9, `lwcr` = 4.3 WHERE `code` = 6219;
UPDATE `naics` SET `trir` = 9.9, `lwcr` = 5.7 WHERE `code` = 62191;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.5 WHERE `code` = 62199;
UPDATE `naics` SET `trir` = 7.3, `lwcr` = 2.9 WHERE `code` = 622;
UPDATE `naics` SET `trir` = 7.3, `lwcr` = 2.8 WHERE `code` = 6221;
UPDATE `naics` SET `trir` = 8.5, `lwcr` = 4.2 WHERE `code` = 6222;
UPDATE `naics` SET `trir` = 6.9, `lwcr` = 2.9 WHERE `code` = 6223;
UPDATE `naics` SET `trir` = 8.4, `lwcr` = 5 WHERE `code` = 623;
UPDATE `naics` SET `trir` = 8.9, `lwcr` = 5.6 WHERE `code` = 6231;
UPDATE `naics` SET `trir` = 7.5, `lwcr` = 3.9 WHERE `code` = 6232;
UPDATE `naics` SET `trir` = 8, `lwcr` = 4.8 WHERE `code` = 6233;
UPDATE `naics` SET `trir` = 8.6, `lwcr` = 3.6 WHERE `code` = 6239;
UPDATE `naics` SET `trir` = 4, `lwcr` = 2 WHERE `code` = 624;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 1.9 WHERE `code` = 6241;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.4 WHERE `code` = 62411;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 2.1 WHERE `code` = 62412;
UPDATE `naics` SET `trir` = 3.3, `lwcr` = 1.8 WHERE `code` = 62419;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 2.4 WHERE `code` = 6242;
UPDATE `naics` SET `trir` = 4, `lwcr` = 3.5 WHERE `code` = 62421;
UPDATE `naics` SET `trir` = 1.6, `lwcr` = 2.2 WHERE `code` = 62422;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.8 WHERE `code` = 62423;
UPDATE `naics` SET `trir` = 6.8, `lwcr` = 3.2 WHERE `code` = 6243;
UPDATE `naics` SET `trir` = 2.8, `lwcr` = 1.7 WHERE `code` = 6244;
UPDATE `naics` SET `trir` = 4.9, `lwcr` = 2.3 WHERE `code` = 71;
UPDATE `naics` SET `trir` = 6.4, `lwcr` = 3 WHERE `code` = 711;
UPDATE `naics` SET `trir` = 8.2, `lwcr` = 2.9 WHERE `code` = 7111;
UPDATE `naics` SET `trir` = 9.7, `lwcr` = 5.2 WHERE `code` = 7112;
UPDATE `naics` SET `trir` = 4.1, `lwcr` = 1.8 WHERE `code` = 711212;
UPDATE `naics` SET `trir` = 3, `lwcr` = 1.9 WHERE `code` = 711219;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 1.6 WHERE `code` = 7113;
UPDATE `naics` SET `trir` = 0.4, `lwcr` = 0.2 WHERE `code` = 7115;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2.4 WHERE `code` = 712;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2.1 WHERE `code` = 713;
UPDATE `naics` SET `trir` = 6.3, `lwcr` = 3.9 WHERE `code` = 7131;
UPDATE `naics` SET `trir` = 6.9, `lwcr` = 4.4 WHERE `code` = 71311;
UPDATE `naics` SET `trir` = 1.7, `lwcr` = 0.6 WHERE `code` = 71312;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2 WHERE `code` = 7132;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 1.8 WHERE `code` = 7139;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2 WHERE `code` = 71391;
UPDATE `naics` SET `trir` = 10.5, `lwcr` = 5.6 WHERE `code` = 71392;
UPDATE `naics` SET `trir` = 4.2, `lwcr` = 1.2 WHERE `code` = 71393;
UPDATE `naics` SET `trir` = 2.7, `lwcr` = 1.2 WHERE `code` = 71394;
UPDATE `naics` SET `trir` = 3.1, `lwcr` = 1.7 WHERE `code` = 71395;
UPDATE `naics` SET `trir` = 3.7, `lwcr` = 1.5 WHERE `code` = 72;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.6 WHERE `code` = 721;
UPDATE `naics` SET `trir` = 5, `lwcr` = 2.6 WHERE `code` = 7211;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 2.7 WHERE `code` = 72111;
UPDATE `naics` SET `trir` = 4.5, `lwcr` = 2.4 WHERE `code` = 72112;
UPDATE `naics` SET `trir` = 5.2, `lwcr` = 1.2 WHERE `code` = 72119;
UPDATE `naics` SET `trir` = 5.6, `lwcr` = 1.7 WHERE `code` = 7212;
UPDATE `naics` SET `trir` = 5.6, `lwcr` = 1.7 WHERE `code` = 72121;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.8 WHERE `code` = 721211;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.2 WHERE `code` = 722;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.1 WHERE `code` = 7221;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.2 WHERE `code` = 7222;
UPDATE `naics` SET `trir` = 3.4, `lwcr` = 1.2 WHERE `code` = 72221;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 1.2 WHERE `code` = 722211;
UPDATE `naics` SET `trir` = 4.4, `lwcr` = 1.9 WHERE `code` = 722212;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.1 WHERE `code` = 7223;
UPDATE `naics` SET `trir` = 1.8, `lwcr` = 0.5 WHERE `code` = 7224;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.4 WHERE `code` = 81;
UPDATE `naics` SET `trir` = 3.8, `lwcr` = 1.8 WHERE `code` = 811;
UPDATE `naics` SET `trir` = 3.9, `lwcr` = 1.8 WHERE `code` = 8111;
UPDATE `naics` SET `trir` = 1.5, `lwcr` = 0.8 WHERE `code` = 8112;
UPDATE `naics` SET `trir` = 5.4, `lwcr` = 2.6 WHERE `code` = 8113;
UPDATE `naics` SET `trir` = 2, `lwcr` = 1.3 WHERE `code` = 8114;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1.4 WHERE `code` = 812;
UPDATE `naics` SET `trir` = 2.1, `lwcr` = 1.2 WHERE `code` = 8122;
UPDATE `naics` SET `trir` = 3.5, `lwcr` = 2.5 WHERE `code` = 8123;
UPDATE `naics` SET `trir` = 2.9, `lwcr` = 1.2 WHERE `code` = 8129;
UPDATE `naics` SET `trir` = 0, `lwcr` = 2 WHERE `code` = 81291;
UPDATE `naics` SET `trir` = 3, `lwcr` = 0.9 WHERE `code` = 81292;
UPDATE `naics` SET `trir` = 2.3, `lwcr` = 1.2 WHERE `code` = 81293;
UPDATE `naics` SET `trir` = 1.4, `lwcr` = 0.4 WHERE `code` = 81299;
UPDATE `naics` SET `trir` = 2.4, `lwcr` = 1 WHERE `code` = 813;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2.7 WHERE `code` = 23;
UPDATE `naics` SET `trir` = 5.1, `lwcr` = 2.7 WHERE `code` = 237;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.1 WHERE `code` = 61;
UPDATE `naics` SET `trir` = 2.6, `lwcr` = 1.1 WHERE `code` = 611;
UPDATE `naics` SET `trir` = 2.5, `lwcr` = 1 WHERE `code` = 6113;
UPDATE `naics` SET `trir` = 10.3, `lwcr` = 5.6 WHERE `code` = 62;
UPDATE `naics` SET `trir` = 11, `lwcr` = 5.5 WHERE `code` = 622;
UPDATE `naics` SET `trir` = 4.3, `lwcr` = 2.2 WHERE `code` = 92;
UPDATE `naics` SET `trir` = 6.2, `lwcr` = 3.3 WHERE `code` = 922;
UPDATE `naics` SET `trir` = 6.2, `lwcr` = 3.3 WHERE `code` = 9221;
UPDATE `naics` SET `trir` = 7.8, `lwcr` = 4.3 WHERE `code` = 92214;
UPDATE `naics` SET `trir` = 13, `lwcr` = 6.1 WHERE `code` = 23;
*/
--
-- PICS-3414 removing spaces from the msgKey in these translations
update app_translation set msgKey = "ContractorEdit.error.BrochureFormat" where msgKey = "ContractorEdit.error.Brochure Format";

-- PICS-3318
update `email_template`
set `body`='Hello <ContactName>,\r\n\r\nThe following insurance certificates have been approved or rejected by <MyName> at <MyCompanyName> for the following reasons:\r\n\r\n#foreach ( $cao in $caoList )\r\n#if ( $cao.status == \"Incomplete\" )\r\n#foreach ( $caow in $cao.caoWorkflow )\r\n#if ( $caow.status == \"Incomplete\" )\r\n-- ${cao.audit.auditType.name} Insurance Certificates have been ${cao.status.button}ed\r\n#if ( $caow.notes.length() > 0 )because ${caow.notes}.\r\n\r\n#end\r\n#end\r\n#end\r\n#end\r\n#end\r\n\r\nPlease correct these issues and re-upload your insurance certificate to your PICS account.\r\nIf you have any specific questions about any operator''s insurance requirements, please review the Insurance Requirements documents in the Forms and Docs section. For questions, please contact ${permissions.name} at ${permissions.email}.\r\n\r\nWhen you renew any policy, please make sure that you upload the new insurance certificate to keep the information up to date.\r\n\r\nHave a great day,\r\nPICS Customer Service',
`updatedBy`=23157,`updateDate`=NOW()
where `id`='132';
--

-- PICS-2600 Allow Audit Types to be hidden from Operators but not contractors
update audit_type set canOperatorView=0 where id=9 or id=232 or id=269 or id=270 or id =272 or id=281;

-- PICS-3746
DELETE from app_translation where msgKey = 'ContractorAccount.city.fieldhelp';

-- PICS-3747
DELETE from app_translation where msgKey = 'ContractorAccount.zip.fieldhelp';

-- PICS-3749
DELETE from app_translation where msgKey = 'User.email.fieldhelp';

-- PICS-3750
DELETE from app_translation where msgKey = 'User.phone.fieldhelp';

-- PICS-3753
UPDATE app_translation SET msgValue = '<p>
	This is the web site of <b>PICS</b>.
</p>
<p>
	Our postal address is <br />
	<b>P.O. Box 51387<br />Irvine, CA 92619-1387</b><br />
	USA
</p>
<p>
	We can be reached via e-mail at <a href="mailto:info@picsauditing.com">info@picsauditing.com</a><br />
	or you can reach us by telephone at {0}
</p>

<h2>DISCLAIMER AND INDEMNITY</h2>

<p>
	As the duly authorized representative of your company that has been delegated the task of filling out the requested
	forms, you agree that your company will defend, indemnify and hold PICS harmless from any claim, loss, liability or
	expense that PICS may incur relating to your company''s participation in the PICS program, including any claim, loss,
	liability or expense made by a third party accessing the information provided by your company. The sole exception to the
	foregoing disclaimer and indemnity shall be any claim, loss, liability or expense that is attributable to the gross
	negligence or willful misconduct of PICS. Without limiting the foregoing, your company agrees that PICS shall, in no event
	or circumstance, be liable for any loss or damage, direct, indirect or consequential, that your company may incur arising
	from or related in any way to the PICS audit and/or the use of the PICS website.
</p>
<p>
	While PICS will endeavor to accurately record the information received from you into your company''s account appearing
	on the PICS website, PICS shall have no responsibility for any claim, loss, liability or expense, direct, indirect or
	consequential, arising out of or related to any errors or omissions in the information contained in the online PICS audit
	for your company. Upon logging in or by request, your company will be provided a completed copy of any audit that PICS
	has performed on your company as it will appear on PICS''s online website. It shall be your responsibility to review the
	information contained in the completed audits for your company and to verify that the information contained in the
	audit form is and remains complete and accurate. You further agree to promptly notify PICS of any errors or omissions in
	the information contained in the PICS website.
</p>
<p>
	Listing on the PICS website does not guarantee acceptance of work from any client. It is understood these audits are for
the purpose of gathering the required documentation by PICS for review by potential clients. PICS is not responsible for
record retention. Unless specifically requested in writing to have your documentation returned, PICS has the right to
dispose of the records.
</p>

<h2>USER AGREEMENT</h2>

<p>
	You will be issued a username and password to access your company''s inputed data. It is your responsibility to keep
	this username and password confidential and private. PICS will not share your username and password data with any
	third party, and regardless of circumstances you cannot share your username and/or password with a third party with
	the sole exception that the third party is contractually acting as your agent to assist you through the PICS process. Any
	violation of this user agreement will result in your account being either temporarily or permanently deactivated.
</p>

<h2>PRIVACY POLICY</h2>

<p>
	PICS'' privacy policy can be accessed here as well as from the payment option page <a href="http://www.picsorganizer.com/PrivacyPolicy.action">(http://www.picsorganizer.com/PrivacyPolicy.action)</a>.
</p>

<h2>CONFIDENTIALITY</h2>
	
<p>
	In addition to the linked privacy policy, PICS recognizes the expected confidentiality and sensitivity of the data that is
	entered through the PICS website.
</p>
<p>
	Sensitive data is defined as the following: your company''s Prequalification Form (PQF), specific Insurance information,
	any and all data gathered during an audit (including but not limited to a Desktop Audit, Office Audit, Field Audit, Integrity
	Management Audit, etc), EMR and OSHA data. Non-sensitive data is defined as your company''s name, description of
	services, which states you operate in and have offices in, contact information of your company''s listed main point of
	contact, your company''s logo.
</p>
<p>
	PICS is a password-protected website. Your company''s Sensitive and Non-sensitive data will be held behind this
	password protection.
</p>
<p>
	Sensitive Data will be shared only with authorized users from operators appearing on your "facility list" which are linked
	to your account. You control which operators are linked to your account and can review any time by logging in to your
	account. At any time you can add or remove any operator from the facility list by either calling in to Customer Service
	({0}) or controlling it using the web interface. Any user from an operator not listed on your facility can not
	access your Sensitive Data. Other Contractors do not have access to your Sensitive Data.
</p>
<p>
	Non-Sensitive Data will be used to assist Operators who are searching for potential bidders. Only Operators listed on the
	PICS Facility list will be able to view this Non-Sensitive Data. In other words, even your Non-Sensitive Data is not public
	data, but is password protected and shared only with PICS consortium operator members.
</p>
<p>
	In an effort to help you gain more work with PICS consortium operator members, authorized users can add your
	contractor account to the approved contractor list of the operator. This generates a notification email to your primary
	user, stating which operator added your company and when. If you do not wish to associate your account with that
	operator, you may remove your account from their approved contractor list by contacting PICS Customer Service, in
	which case the Operator will be notified that your company requested to be removed from the Operator''s approved
	contractor list.
</p>

<h2>REFUND POLICY</h2>

<p>
	Your account will be automatically renewed each 12 months unless you call to cancel your account.  In the event of an operator-requested upgrade, your primary user will be notified via email immediately.  The invoice will be created three days after the upgrade event and the upgrade charge will occur 7 days later. PICS refund policy can be accessed here as well from as the payment options page <a href="http://www.picsorganizer.com/RefundPolicy.action">(http://www.picsorganizer.com/RefundPolicy.action)</a>
</p>
<p>
	Our annual pricing can be seen on the facilities page, which you will be able to review before completing registration.
</p>

<h2>AGREEMENTS OUTSIDE OF THIS DOCUMENT</h2>

<p>
	You agree to the terms and conditions of the contractor agreement as communicated in this document.  Any agreements outside of the terms and conditions contained herein shall be agreed to in writing and signed by both contractor and PICS representatives.  PICS copy of said agreement will be held in the PICS UPLOAD FILES category of the PQF under PICS Contractor Agreement.
</p>' 
WHERE msgKey = 'ContractorAgreement.content'
and locale = 'en';

-- PICS-3987
update accounts
set onsiteServices = 1, 
transportationServices = 1, 
offsiteServices = 1, 
materialSupplier = 1
where type in ('Operator','Corporate')

update accounts
set onsiteServices = 1, 
transportationServices = 1, 
offsiteServices = 1, 
materialSupplier = 0
where id in (22102,22100)

update accounts
set onsiteServices = 1, 
transportationServices = 0, 
offsiteServices = 0, 
materialSupplier = 0
where id in (12400,16272,16273,11720,11738,11739,14128,14130,14129,5922,5910,5909,18684,5908,5907,5906,5949,5905,5904,5903,5830,7928,5902,5829,5921,5920,5901,5900,5899,17354,5962,7901,5898,7902,5947,5946,5897,18689,18686,7460,5896,18679,7903,5919,6115,5895,5894,5891,6121,5918,5892,5893,5890,5886,13964,5889,5888,15380,5885,5883,5882,5941,5881,6112,9319,5878,5960,5959,5940,5916,5877,5876,5875,5874,5873,6122,5871,7462,5879,5870,5872,6423,6422,6424,6425,5938,5869,5937,5867,5868,18693,5866,5865,18695,5864,5863,5862,5861,6127,5936,5957,5860,18697,9423,5926,7464,5880,5915,5934,5913,5859,5856,5855,9415,5857,5854,5933,5853,5958,5956,18700,5955,5852,5851,5850,5849,7908,15406,5932,5954,5953,5951,7905,5931,5848,5846,5847,7466,5950,5930,5844,5843,5929,5842,7454,5841,5928,5840,15381,9420,5839,5838,9416,9418,5837,5912,18701,5836,5925,5835,7457,5834,13962,5833,5924,5832,5911,7458,5831,5147,11781,6040,8769,14138,8281,9394,1937,2852,3534,4440,28,1251,1039,23100)
