-- "Add new BrainTree currency Processors"
-- Please run on production
insert into pics_alpha1.app_properties
            (`property`,
             `value`,
             `ticklerDate`)
values ('brainTree.processor_id.zar', 'picszar',NULL),
        ('brainTree.processor_id.nok', 'picsnok',NULL),
        ('brainTree.processor_id.dkk', 'picsdkk',NULL),
        ('brainTree.processor_id.sek', 'picssek',NULL);
