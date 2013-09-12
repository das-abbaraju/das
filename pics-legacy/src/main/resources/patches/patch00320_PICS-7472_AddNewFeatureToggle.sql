insert ignore into app_properties (property,`value`)
values ('Toggle.ContractorCampaign', 'return contractor.operatorAccounts.find {it.id == 23531} != null');