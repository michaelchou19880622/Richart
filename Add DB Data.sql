-- use database
use [Richart Customer Connect]

-- create empty rows
insert into BCS_SYSTEM_CONFIG values
('.cht.cdn.api.token', NULL, GETDATE(), ''),
('.line.point.am.start.time', NULL, GETDATE(), ''),
('.line.point.pm.start.time', NULL, GETDATE(), ''),
('.mgm.trigger.seconds', NULL, GETDATE(), '')

-- set value into rows
update BCS_SYSTEM_CONFIG set VALUE = 'eb82e8f5bb29de7cbf5d6a70bb6622695dca54bc' where CONFIG_ID = '.cht.cdn.api.token'
update BCS_SYSTEM_CONFIG set VALUE = '09:00:00' where CONFIG_ID = '.line.point.am.start.time'
update BCS_SYSTEM_CONFIG set VALUE = '15:00:00' where CONFIG_ID = '.line.point.pm.start.time'
update BCS_SYSTEM_CONFIG set VALUE = '50' where CONFIG_ID = '.mgm.trigger.seconds'

-- show result
select * from BCS_SYSTEM_CONFIG
where CONFIG_ID in ('.cht.cdn.api.token','.line.point.am.start.time','.line.point.pm.start.time','.mgm.trigger.seconds')

