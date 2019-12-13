INSERT INTO uap_sequence (sequence_name, next_val) VALUES ('userOrgManageId', 10000);
update uap_code set text = 'N/A' where id = 60;
-- alter table uap_user_org_manage add column ORG_PATH_ID varchar(128);

INSERT INTO UAP_REST_API (ID,NAME,URL,tags,APP_ID,STATE) VALUES ('UAP-01-USERORGMANAGE-12','find suborglist','GET:/userorgmanage/find/suborglist/{userId}','UserOrgManage',1,'1');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-01-USERORGMANAGE-12','UAP-01-USERORGMANAGE','find suborglist','','2','2','1',1,'1');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( 'C23BC096-D5C1-497E-BF98-304912D0C41C','UAP-01-USERORGMANAGE-12','UAP-01-USERORGMANAGE-12');
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(305,'UAP-01-USERORGMANAGE-12',1);
UPDATE UAP_REST_API SET URL = 'PUT:/userorgmanage/user/{userId}' WHERE ID = 'UAP-01-USERORGMANAGE-06';

UPDATE UAP_REST_API SET URL = 'POST:/userorgmanage/find/user/page' WHERE ID = 'UAP-01-USERORGMANAGE-01';
INSERT INTO UAP_REST_API (ID,NAME,URL,tags,APP_ID,STATE) VALUES ('UAP-01-USERORGMANAGE-13','find all suborgs','POST:/userorgmanage/org/page','UserOrgManage',1,'1');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-01-USERORGMANAGE-13','UAP-01-USERORGMANAGE','find all unmanaged orgs','','2','2','1',1,'1');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( '7283F182-BB14-43A5-B68A-2D50D2E2AD5A','UAP-01-USERORGMANAGE-13','UAP-01-USERORGMANAGE-13');
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(306,'UAP-01-USERORGMANAGE-13',1);


UPDATE uap_organization SET org_path_id = '1>' WHERE id = 1;
UPDATE uap_organization SET org_path_id = '1>2>' WHERE id = 2;
UPDATE uap_organization SET org_path_id = '1>2>3>' WHERE id = 3;
UPDATE uap_organization SET org_path_id = '1>2>4>' WHERE id = 4;

-- UPDATE uap_rest_api SET url = 'PUT:/bpmcfg/{bpmFormId}' WHERE id = 'UAP-BPM-PROCESS-PUT-10';
-- UPDATE uap_rest_api SET url = 'PUT:/bpmcfg/{bpmFormId}/state' WHERE id = 'UAP-BPM-PROCESS-PUT-11';

UPDATE uap_rest_api SET url = 'POST:/userorgmanage/user/page' WHERE id = 'UAP-01-USERORGMANAGE-01';
UPDATE uap_rest_api SET url = 'GET:/userorgmanage/org/{orgId}' WHERE id = 'UAP-01-USERORGMANAGE-02';
UPDATE uap_rest_api SET url = 'GET:/userorgmanage/suborglist/{userId}' WHERE id = 'UAP-01-USERORGMANAGE-12';

UPDATE uap_rest_api SET STATE = '0' WHERE ID = 'UAP-01-USERORGMANAGE-05';
UPDATE uap_rest_api SET STATE = '0' WHERE ID = 'UAP-01-USERORGMANAGE-02';
UPDATE uap_rest_api SET STATE = '0' WHERE ID = 'UAP-01-USERORGMANAGE-12';

-- 新加菜单
INSERT INTO UAP_REST_API (ID,NAME,URL,tags,APP_ID,STATE) VALUES ('UAP-03-LOG-05','query menulog','POST:/log/menu','Logs',1,'1');
INSERT INTO UAP_REST_API (ID,NAME,URL,tags,APP_ID,STATE) VALUES ('UAP-03-LOG-06','Illegallog','POST:/log/login/illegal','Logs',1,'1');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-03-ILLEGALLOG','UAP-03','illegallog','ui/page/manage/illegallog.html','1','2','1',1,'1');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-03-ILLEGALLOG-01','UAP-03-ILLEGALLOG','query illegallog','','2','2','1',1,'1');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-03-MENULOG','UAP-03','menulog','ui/page/manage/menulog.html','1','2','1',1,'1');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-03-MENULOG-01','UAP-03-MENULOG','query menulog','','2','2','1',1,'1');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( 'DD6E2E4B-45D0-4B76-9515-42EB627DD7F7','UAP-03-ILLEGALLOG-01','UAP-03-LOG-06');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( 'B180063B-1086-4637-8FE7-A1DD5003706A','UAP-03-MENULOG-01','UAP-03-LOG-05');
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(310,'UAP-03-ILLEGALLOG',1);
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(311,'UAP-03-ILLEGALLOG-01',1);
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(312,'UAP-03-MENULOG',1);
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(313,'UAP-03-MENULOG-01',1);

INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-01-USER-19','UAP-01-USER','Query User','','2','2','1',1,'1');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-01-USER-20','UAP-01-USER','Add Org To User','','2','2','1',1,'1');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-01-USER-21','UAP-01-USER','find all suborgs','','2','2','1',1,'1');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( 'C11F8C13-5FC7-480B-A32D-B5BC0A166147','UAP-01-USER-19','UAP-01-USERORGMANAGE-01');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( '7B7DF677-29ED-49F7-A5F4-41583B477E9C','UAP-01-USER-20','UAP-01-USERORGMANAGE-06');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( '94718CEF-800D-42DF-9509-4C5848EF1DD4','UAP-01-USER-21','UAP-01-USERORGMANAGE-13');
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(314,'UAP-01-USER-19',1);
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(315,'UAP-01-USER-20',1);
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(316,'UAP-01-USER-21',1);

INSERT INTO UAP_MENU_DISPLAY (ID,PARENT_ID,NAME,TYPE,RANK_ID,MENU_ID,ICON,app_id,app_name,INSERT_TIME,UPDATE_TIME,STATE) VALUES ('UAP-03-ILLEGALLOG','UAP-03','Illegallog','1',8,'UAP-03-ILLEGALLOG','',1,'统一应用平台',0,0,'1');
INSERT INTO UAP_MENU_DISPLAY (ID,PARENT_ID,NAME,TYPE,RANK_ID,MENU_ID,ICON,app_id,app_name,INSERT_TIME,UPDATE_TIME,STATE) VALUES ('UAP-03-MENULOG','UAP-03','Menulog','1',9,'UAP-03-MENULOG','',1,'统一应用平台',0,0,'1');

-- update UAP_MENU_DISPLAY set state = '2' where id = 'UAP-01-USERORGMANAGE';

INSERT INTO uap_sequence (sequence_name,next_val) VALUES('menuLogId',10000);
INSERT INTO uap_sequence (sequence_name,next_val) VALUES('illegalLogId',10000);


INSERT INTO UAP_REST_API (ID,NAME,URL,tags,APP_ID,STATE,TYPE) VALUES ('UAP-02-ROLE-31','Role users config','PUT:/role/{roleId}/users','RoleUser',1,'1','0');
INSERT INTO UAP_REST_API (ID,NAME,URL,tags,APP_ID,STATE,TYPE) VALUES ('UAP-02-ROLE-32','get all role users','POST:/role/{roleId}/users','RoleUser',1,'1','0');
INSERT INTO UAP_REST_API (ID,NAME,URL,tags,APP_ID,STATE,TYPE) VALUES ('UAP-02-ROLE-33','get role candidate users','POST:/role/{roleId}/candidate','RoleUser',1,'1','0');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-02-ROLE-31','UAP-02-ROLE','Role users config','','2','2','1',1,'1');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-02-ROLE-32','UAP-02-ROLE','get all role users','','2','2','2',1,'1');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-02-ROLE-33','UAP-02-ROLE','get role candidate users','','2','2','2',1,'1');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( '8071B825-D312-40B4-B1B5-4C011349DEB5','UAP-02-ROLE-31','UAP-02-ROLE-31');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( '87F1FF77-F413-47D6-8D92-9738EC858AD6','UAP-02-ROLE-32','UAP-02-ROLE-32');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( '61E2707C-D79E-4F82-99FA-3D0430021BEB','UAP-02-ROLE-33','UAP-02-ROLE-33');
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(307,'UAP-02-ROLE-31',1);
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(308,'UAP-02-ROLE-32',1);
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(309,'UAP-02-ROLE-33',1);
update UAP_REST_API set url = 'POST:/menuDisplay/menu/{menuId}' where id = 'UAP-03-MENUCUSTOM-09';
UPDATE uap_menu SET func_type = '1';
UPDATE uap_role SET type_ext = '0';
UPDATE uap_role SET code = 'uap' WHERE id = 1;
UPDATE uap_role SET code = 'user' WHERE id = 2;


update uap_menu set code = 'USER_MANAGE_UNIT' where id = 'UAP-01-USER-20';

INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-03-TENANCYINFO-05','UAP-03-TENANCYINFO','Eidt Tenancy','','2','2','1',1,'1');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( '4B6AE6F2-18DC-40C7-8704-08997EDD702F','UAP-03-TENANCYINFO-05','UAP-03-TENANCY-05');
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(317,'UAP-03-TENANCYINFO-05',1);


UPDATE uap_menu_display SET name = 'Organization Management' WHERE (id = 'UAP-01');
UPDATE uap_menu_display SET name = 'Organizations' WHERE (id = 'UAP-01-ORG');
UPDATE uap_menu_display SET name = 'Base Organizations' WHERE (id = 'UAP-01-ORGBASE');
UPDATE uap_menu_display SET name = 'Management Unit' WHERE (id = 'UAP-01-USERORGMANAGE');
UPDATE uap_menu_display SET name = 'Roles' WHERE (id = 'UAP-02-ROLE');
UPDATE uap_menu_display SET name = 'Calendar' WHERE (id = 'UAP-03-CALENDAR');
UPDATE uap_menu_display SET name = 'System Codes' WHERE (id = 'UAP-03-CODE');
UPDATE uap_menu_display SET name = 'Illegal Login Logs' WHERE (id = 'UAP-03-ILLEGALLOG');
UPDATE uap_menu_display SET name = 'Privilege Management' WHERE (id = 'UAP-02');
UPDATE uap_menu_display SET name = 'Organizational Roles' WHERE (id = 'UAP-02-ORGROLE');
UPDATE uap_menu_display SET name = 'System Setup' WHERE (id = 'UAP-03');
UPDATE uap_menu_display SET name = 'API Logs' WHERE (id = 'UAP-03-LOG');
UPDATE uap_menu_display SET name = 'Menu Customization' WHERE (id = 'UAP-03-MENUCUSTOM');
UPDATE uap_menu_display SET name = 'Menu Logs' WHERE (id = 'UAP-03-MENULOG');
UPDATE uap_menu_display SET name = 'Multi-tenant' WHERE (id = 'UAP-03-TENANCY');
UPDATE uap_menu_display SET name = 'Tenant Info' WHERE (id = 'UAP-03-TENANCYINFO');
UPDATE uap_menu_display SET name = 'APIs' WHERE (id = 'UAP-04-API');
UPDATE uap_menu_display SET name = 'Application Settings' WHERE (id = 'UAP-04');
UPDATE uap_menu_display SET name = 'Login Logs' WHERE (id = 'UAP-03-TOKENHISTORY');
UPDATE uap_menu_display SET name = 'Applications' WHERE (id = 'UAP-04-APP');
UPDATE uap_menu_display SET name = 'Menus' WHERE (id = 'UAP-04-MENU');
UPDATE uap_menu_display SET name = 'Workflow Management' WHERE (id = 'UAP-BPM-01');
UPDATE uap_menu_display SET name = 'Page Configuration' WHERE (id = 'UAP-BPM-01-CONFIG');
UPDATE uap_menu_display SET name = 'Dynamic Field' WHERE (id = 'UAP-BPM-01-DYNAMIC');
UPDATE uap_menu_display SET name = 'Process Instance' WHERE (id = 'UAP-BPM-01-INSTANCES');
UPDATE uap_menu_display SET name = 'Process Design' WHERE (id = 'UAP-BPM-01-MODEL');
UPDATE uap_menu_display SET name = 'Deployed Process' WHERE (id = 'UAP-BPM-01-PROCESS');
UPDATE uap_menu_display SET name = 'Process Task' WHERE (id = 'UAP-BPM-01-TASK-01');
UPDATE uap_menu_display SET name = 'My Task' WHERE (id = 'UAP-BPM-02');
UPDATE uap_menu_display SET name = 'Historical Task' WHERE (id = 'UAP-BPM-02-TASK-03');
UPDATE uap_menu_display SET name = 'My Processes' WHERE (id = 'UAP-BPM-02-TASK-04');
UPDATE uap_menu_display SET name = 'Timing Task' WHERE (id = 'UAP-JOB');
UPDATE uap_menu_display SET name = 'Curent Task' WHERE (id = 'UAP-BPM-02-TASK-05');
UPDATE uap_menu_display SET name = 'Executive History' WHERE (id = 'UAP-JOB-LOG');
UPDATE uap_menu_display SET name = 'Users' WHERE (id = 'UAP-01-USER');
UPDATE uap_menu_display SET name = 'Task Configuration' WHERE (id = 'UAP-JOB-TASK');

UPDATE uap_menu SET name = 'Organization Management' WHERE (id = 'UAP-01');
UPDATE uap_menu SET name = 'Organizations' WHERE (id = 'UAP-01-ORG');
UPDATE uap_menu SET name = 'Base Organizations' WHERE (id = 'UAP-01-ORGBASE');
UPDATE uap_menu SET name = 'Management Unit' WHERE (id = 'UAP-01-USERORGMANAGE');
UPDATE uap_menu SET name = 'Roles' WHERE (id = 'UAP-02-ROLE');
UPDATE uap_menu SET name = 'Calendar' WHERE (id = 'UAP-03-CALENDAR');
UPDATE uap_menu SET name = 'System Codes' WHERE (id = 'UAP-03-CODE');
UPDATE uap_menu SET name = 'Illegal Login Logs' WHERE (id = 'UAP-03-ILLEGALLOG');
UPDATE uap_menu SET name = 'Privilege Management' WHERE (id = 'UAP-02');
UPDATE uap_menu SET name = 'Organizational Roles' WHERE (id = 'UAP-02-ORGROLE');
UPDATE uap_menu SET name = 'System Setup' WHERE (id = 'UAP-03');
UPDATE uap_menu SET name = 'API Logs' WHERE (id = 'UAP-03-LOG');
UPDATE uap_menu SET name = 'Menu Customization' WHERE (id = 'UAP-03-MENUCUSTOM');
UPDATE uap_menu SET name = 'Menu Logs' WHERE (id = 'UAP-03-MENULOG');
UPDATE uap_menu SET name = 'Multi-tenant' WHERE (id = 'UAP-03-TENANCY');
UPDATE uap_menu SET name = 'Tenant Info' WHERE (id = 'UAP-03-TENANCYINFO');
UPDATE uap_menu SET name = 'APIs' WHERE (id = 'UAP-04-API');
UPDATE uap_menu SET name = 'Application Settings' WHERE (id = 'UAP-04');
UPDATE uap_menu SET name = 'Login Logs' WHERE (id = 'UAP-03-TOKENHISTORY');
UPDATE uap_menu SET name = 'Applications' WHERE (id = 'UAP-04-APP');
UPDATE uap_menu SET name = 'Menus' WHERE (id = 'UAP-04-MENU');
UPDATE uap_menu SET name = 'Workflow Management' WHERE (id = 'UAP-BPM-01');
UPDATE uap_menu SET name = 'Page Configuration' WHERE (id = 'UAP-BPM-01-CONFIG');
UPDATE uap_menu SET name = 'Dynamic Field' WHERE (id = 'UAP-BPM-01-DYNAMIC');
UPDATE uap_menu SET name = 'Process Instance' WHERE (id = 'UAP-BPM-01-INSTANCES');
UPDATE uap_menu SET name = 'Process Design' WHERE (id = 'UAP-BPM-01-MODEL');
UPDATE uap_menu SET name = 'Deployed Process' WHERE (id = 'UAP-BPM-01-PROCESS');
UPDATE uap_menu SET name = 'Process Task' WHERE (id = 'UAP-BPM-01-TASK-01');
UPDATE uap_menu SET name = 'My Task' WHERE (id = 'UAP-BPM-02');
UPDATE uap_menu SET name = 'Historical Task' WHERE (id = 'UAP-BPM-02-TASK-03');
UPDATE uap_menu SET name = 'My Processes' WHERE (id = 'UAP-BPM-02-TASK-04');
UPDATE uap_menu SET name = 'Timing Task' WHERE (id = 'UAP-JOB');
UPDATE uap_menu SET name = 'Curent Task' WHERE (id = 'UAP-BPM-02-TASK-05');
UPDATE uap_menu SET name = 'Executive History' WHERE (id = 'UAP-JOB-LOG');
UPDATE uap_menu SET name = 'Users' WHERE (id = 'UAP-01-USER');
UPDATE uap_menu SET name = 'Task Configuration' WHERE (id = 'UAP-JOB-TASK');

INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-03-TENANCY-18','UAP-03-TENANCY','Get DisplayList','','2','2','1',1,'1');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( 'ff8080816b4e624a016b53a65dab0000','UAP-03-TENANCY-18','UAP-03-MENUCUSTOM-08');
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(319,'UAP-03-TENANCY-18',1);

UPDATE uap_multi_tenancy SET no = 'reserved' WHERE id != 1;
UPDATE uap_multi_tenancy SET no = 'UAP' WHERE id = 1;

INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-01-USER-22','UAP-01-USER','Delete Management Unit','','2','2','1',1,'1');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( '5B58B10A-263E-4690-9D1A-C430DE79BB20','UAP-01-USER-22','UAP-01-USERORGMANAGE-07');
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(318,'UAP-01-USER-22',1);

update UAP_MENU_DISPLAY set state = '2' where id = 'UAP-02-ORGROLE';
update UAP_MENU_DISPLAY set state = '0' where id = 'UAP-03-CALENDAR';

INSERT INTO uap_rest_api (id,code,insert_time,log_state,log_type,name,state,tags,update_time,url,app_id,type) VALUES ('uap-GLOBAL-LICFLAG','',1562121330767,'1','','GET:/licFlag','1','Authority',1562121330792,'GET:/licFlag',1,'0');
INSERT INTO uap_menu_api (id,menu_id,api_id) VALUES ('40288ff76bb5b2ca016bb5b9dcdf0000','UAP-GLOBAL-LOGIN-SUBMIT','uap-GLOBAL-LICFLAG');