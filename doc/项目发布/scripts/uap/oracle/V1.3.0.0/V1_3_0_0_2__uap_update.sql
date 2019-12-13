INSERT INTO UAP_REST_API (ID,NAME,URL,tags,APP_ID,STATE) VALUES ('UAP-03-TENANCY-19','Refresh cache','POST:/tenancy/cache','Cache',1,'1');
INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-03-TENANCY-19','UAP-03-TENANCY','Refresh cache','','2','2','1',1,'1');
INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( '5F3B691B-24DA-4932-8F6D-F3B2894702A9','UAP-03-TENANCY-19','UAP-03-TENANCY-19');
INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(323,'UAP-03-TENANCY-19',1);

update UAP_REST_API set url = 'PUT:/task/simpleTask/{jobId}' where id = 'UAP-JOB-TASK-05';
update UAP_REST_API set url = 'PUT:/task/cronTask/{jobId}' where id = 'UAP-JOB-TASK-04';
update UAP_REST_API set url = 'DELETE:/task/{jobId}' where id = 'UAP-JOB-TASK-06';
update UAP_REST_API set url = 'PUT:/task/operation/{jobId}/{action}' where id = 'UAP-JOB-TASK-07';
update UAP_REST_API set url = 'POST:/task/list' where id = 'UAP-JOB-TASK-08';
update UAP_MENU_API set API_ID = 'UAP-JOB-TASK-07' where API_ID = 'UAP-JOB-TASK-09';
update UAP_MENU_API set API_ID = 'UAP-JOB-TASK-07' where API_ID = 'UAP-JOB-TASK-10';
update UAP_REST_API set url = 'POST:/schedule/jobLog' where id = 'UAP-JOB-LOG-DELETE';
update UAP_REST_API set url = 'PUT:/schedule/jobLog/{logId}' where id = 'UAP-JOB-LOG-SAVE';


update uap_rest_api set url = 'POST:/app/bpm/task/claim/{taskId}' where id = 'UAP-BPM-TASK-POST-06';
update uap_rest_api set url = 'POST:/app/bpm/task/unclaim/{taskId}' where id = 'UAP-BPM-TASK-POST-10';

  
insert into UAP_APP (id, code, in_time, name, parent_id,  state, type, up_time, url,scret, ui_url)
VALUES
(4, 'uap-message', NULL, 'uap-message', 1, '1', '1', NULL, 'http://192.168.15.135:8080/uap-msg/', '$2a$10$yYqhrA6eDUNiLYUxWek3H.2lLCcyvZeDg9mxnTOgktT8X57k09092', 'http://192.168.15.135:8080/uap-msg/');

INSERT INTO uap_tenancy_app (id, app_id, tanancy_id, insert_time, update_time) VALUES
	(4, 4, 1, 1, NULL);

insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01', '2', '', '1', NULL, NULL, NULL, 'Message Center', NULL, NULL, NULL, '1', '0', 1564378126647, '', NULL, 4);
  insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-01', '2', '', '1', NULL, NULL, NULL, 'User Message', NULL, 'uap-notification-01', NULL, '1', '1', 1564379068390, 'ui/page/notification/userMsg.html', NULL, 4);
  insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-01-01', '2', '', '1', NULL, NULL, NULL, 'Get msg list', NULL, 'uap-notification-01-01', NULL, '1', '2', 1564378521422, '', NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-01-02', '2', '', '1', NULL, NULL, NULL, 'Update msg state', NULL, 'uap-notification-01-01', NULL, '1', '2', 1564378529704, '', NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-01-03', '2', '', '1', NULL, NULL, NULL, 'Delete msg', NULL, 'uap-notification-01-01', NULL, '1', '2', 1564378535832, '', NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-01-04', '2', '', '1', NULL, NULL, NULL, 'Get msg detail', NULL, 'uap-notification-01-01', NULL, '1', '2', 1564378542546, '', NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-01-05', '2', '', '1', NULL, NULL, NULL, 'Unread msg number', NULL, 'uap-notification-01-01', NULL, '1', '2', 1564378566627, '', NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-01-06', '2', '', '1', NULL, NULL, NULL, 'Send msg', NULL, 'uap-notification-01-01', NULL, '1', '2', 1564378573816, '', NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-02', '2', '', '1', NULL, NULL, NULL, 'Message Type', NULL, 'uap-notification-01', NULL, '1', '1', 1564379080266, 'ui/page/notification/msgType.html', NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-02-01', '2', NULL, '1', NULL, NULL, NULL, 'Get topics list', NULL, 'uap-notification-01-02', NULL, '1', '2', 1563872545723, NULL, NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-02-02', '2', NULL, '1', NULL, NULL, NULL, 'Delete topic', NULL, 'uap-notification-01-02', NULL, '1', '2', 1563872733427, NULL, NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-02-03', '2', NULL, '1', NULL, NULL, NULL, 'Add topic', NULL, 'uap-notification-01-02', NULL, '1', '2', 1563872670718, NULL, NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-02-04', '2', '', '1', NULL, NULL, NULL, 'Add topic receiver', NULL, 'uap-notification-01-02', NULL, '1', '2', 1564392486582, '', NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-02-05', '2', NULL, '1', NULL, NULL, NULL, 'Update message topic', NULL, 'uap-notification-01-02', NULL, '1', '2', 1564392659581, NULL, NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-02-06', '2', NULL, '1', NULL, NULL, NULL, 'View topic', NULL, 'uap-notification-01-02', NULL, '1', '2', 1564447280872, NULL, NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-02-07', '2', NULL, '1', NULL, NULL, NULL, 'View topic receiver', NULL, 'uap-notification-01-02', NULL, '1', '2', 1564449475905, NULL, NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-03', '2', '', '1', NULL, NULL, NULL, 'Messages', NULL, 'uap-notification-01', NULL, '1', '1', 1564378396465, 'ui/page/notification/messages.html', NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-03-01', '2', NULL, '1', NULL, NULL, NULL, 'Get msg list', NULL, 'uap-notification-01-03', NULL, '1', '2', 1564378602475, NULL, NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-03-02', '2', NULL, '1', NULL, NULL, NULL, 'Get msg detail', NULL, 'uap-notification-01-03', NULL, '1', '2', 1564378671464, NULL, NULL, 4);
    insert into UAP_MENU (id, auth_type, code, func_type, icon, insert_time, is_leaf, name, no, parent_id, rank_id, state, type, update_time, url, url_type, app_id)
VALUES
  ('uap-notification-01-04', '2', '', '1', NULL, NULL, NULL, 'Configuration Color', NULL, 'uap-notification-01', NULL, '1', '1', 1564378793275, 'ui/page/notification/configurationColor.html', NULL, 4);
  
insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-01', '', 1561001603806, '1', '', 'POST:/msg/list', '1', 'notification', 1561103278603, 'POST:/msg/list', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-02', '', 1561022990836, '1', '', 'PUT:/msg/state', '1', 'notification', 1561540010937, 'PUT:/msg/state', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-03', '', 1561100751445, '1', '', 'DELETE:/msg/infoList', '1', 'notification', 1561540075791, 'DELETE:/msg/infoList', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-04', '', 1561100821087, '1', '', 'GET:/msg/{msgId}', '1', 'notification', 1563871978378, 'GET:/msg/{msgId}', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-05', '', 1561100876452, '1', '', 'POST:/msg/unReadCount/{state}', '1', 'notification', 1563852781732, 'POST:/msg/unReadCount/{state}', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-06', '', 1563872170753, '1', '', 'POST:/msg/uapMsgInfo', '1', 'notification', 1563872170760, 'POST:/msg/uapMsgInfo', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-07', '', 1563872193965, '1', '', 'POST:/topic/list', '1', 'notification', 1563872193969, 'POST:/topic/list', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-08', '', 1563872221501, '1', '', 'POST:/topic/uapMsgTopic', '1', 'notification', 1563872221507, 'POST:/topic/uapMsgTopic', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-09', '', 1563872247038, '1', '', 'PUT:/topic/state', '1', 'notification', 1563956011756, 'PUT:/topic/state', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-10', '', 1564392368767, '1', '', 'POST:/topic/uapMsgTopicTenancy', '1', 'notification', 1564392368771, 'POST:/topic/uapMsgTopicTenancy', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-11', '', 1564392596497, '1', '', 'PUT:/topic/info', '1', 'notification', 1564392596502, 'PUT:/topic/info', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-12', '', 1564447333716, '1', '', 'GET:/topic/{code}', '1', 'notification', 1564447333756, 'GET:/topic/{code}', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-13', '', 1564449399762, '1', '', 'GET:/topic/tenancy/{code}', '1', 'notification', 1564449399781, 'GET:/topic/tenancy/{code}', 4, '0');
  insert into UAP_REST_API (id, code, insert_time, log_state, log_type, name, state, tags, update_time, url, app_id,type)
VALUES
  ('uap-notification-14', '', 1564456698177, '1', '', 'POST:/msg/userList', '1', 'notification', 1564456698184, 'POST:/msg/userList', 4, '0');
  
  
insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('40288fc36c1da5c0016c1e0fb6700000', 'uap-notification-01-01-06', 'uap-notification-06');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('40288fc36c1da5c0016c1e120fe30001', 'uap-notification-01-02-01', 'uap-notification-07');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('40288fc36c1da5c0016c1e1369810002', 'uap-notification-01-02-03', 'uap-notification-08');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('40288fc36c1da5c0016c1e1487a30003', 'uap-notification-01-02-02', 'uap-notification-09');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('40288fc36c3b92c5016c3c3c7b8a0000', 'uap-notification-01-03-01', 'uap-notification-01');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('40288fc36c3b92c5016c3c3caad70001', 'uap-notification-01-03-02', 'uap-notification-04');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('40288fec6c3b95f3016c3d0f67550000', 'uap-notification-01-02-04', 'uap-notification-10');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('40288fec6c3b95f3016c3d1210e30001', 'uap-notification-01-02-05', 'uap-notification-11');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('40288fec6c3d935c016c405452840000', 'uap-notification-01-02-06', 'uap-notification-12');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('40288fec6c4069db016c4074dcd40000', 'uap-notification-01-02-07', 'uap-notification-13');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('40288fec6c40cdb1016c40e3c6180000', 'uap-notification-01-01-01', 'uap-notification-14');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('ff8080816b742a97016b743b9ed80000', 'uap-notification-01-01-02', 'uap-notification-02');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('ff8080816b78ed03016b79026b8c0000', 'uap-notification-01-01-03', 'uap-notification-03');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('ff8080816b78ed03016b79029faf0001', 'uap-notification-01-01-04', 'uap-notification-04');
  insert into UAP_MENU_API (id, menu_id, api_id)
VALUES
  ('ff8080816b78ed03016b7902c60c0002', 'uap-notification-01-01-05', 'uap-notification-05');
  
insert into UAP_MENU_DISPLAY (id, code, insert_time, parent_id, rank_id, name, state, type, update_time, menu_id, app_id, app_name, icon)
VALUES
  ('UAP-notification', '', 1561001379390, 'UAP', 8, 'Message Center', '1', '0', 1564378193157, NULL, NULL, NULL, 'fa fa-bell-o');
  insert into UAP_MENU_DISPLAY (id, code, insert_time, parent_id, rank_id, name, state, type, update_time, menu_id, app_id, app_name, icon)
VALUES
  ('UAP-notification-01', '', 1562914359231, 'UAP-notification', 1, 'User Message', '1', '1', 1564378205345, 'uap-notification-01-01', 4, 'uap-message', '');
  insert into UAP_MENU_DISPLAY (id, code, insert_time, parent_id, rank_id, name, state, type, update_time, menu_id, app_id, app_name, icon)
VALUES
  ('UAP-notification-02', '', 1563872820929, 'UAP-notification', 2, 'Message Type', '1', '1', 1564378282107, 'uap-notification-01-02', 4, 'uap-message', '');
  insert into UAP_MENU_DISPLAY (id, code, insert_time, parent_id, rank_id, name, state, type, update_time, menu_id, app_id, app_name, icon)
VALUES
  ('UAP-notification-03', '', 1564378832853, 'UAP-notification', 3, 'Messages', '1', '1', 1564378859648, 'uap-notification-01-03', 4, 'uap-message', '');
  insert into UAP_MENU_DISPLAY (id, code, insert_time, parent_id, rank_id, name, state, type, update_time, menu_id, app_id, app_name, icon)
VALUES
  ('UAP-notification-04', '', 1564378895700, 'UAP-notification', 4, 'Configuration Color', '1', '1', 1564973779096, 'uap-notification-01-04', 4, 'uap-message', '');
  
  
insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (400, 'uap-notification-01-01', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (401, 'uap-notification-01-01-01', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (402, 'uap-notification-01-01-02', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (403, 'uap-notification-01-01-03', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (404, 'uap-notification-01-01-04', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (405, 'uap-notification-01-01-05', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (406, 'uap-notification-01-01-06', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (407, 'uap-notification-01-02', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (408, 'uap-notification-01-02-01', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (409, 'uap-notification-01-02-02', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (410, 'uap-notification-01-02-03', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (411, 'uap-notification-01-02-04', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (412, 'uap-notification-01-02-05', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (413, 'uap-notification-01-02-06', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (414, 'uap-notification-01-02-07', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (415, 'uap-notification-01-03', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (416, 'uap-notification-01-03-01', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (417, 'uap-notification-01-03-02', 1);
  insert into UAP_ROLE_MENU (id, menu_id, role_id)
VALUES
  (418, 'uap-notification-01-04', 1);
  
insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (90, 'UAP_MESSAGE', NULL, '1', 'Message-encoding', '0', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (91, 'UAP_MESSAGE_STATE', 'UAP_MESSAGE', '1', 'Message state', '1', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (92, 'UAP_MESSAGE_LEVEL', 'UAP_MESSAGE', '1', 'Message level', '1', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (93, 'UAP_TYPE_STATE', 'UAP_MESSAGE', '1', 'Message type state', '1', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (94, 'UAP_TYPE_RECEIVER_TYPE', 'UAP_MESSAGE', '1', 'Message receiver type', '1', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (95, '0', 'UAP_MESSAGE_STATE', '1', 'Unread', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (96, '1', 'UAP_MESSAGE_STATE', '1', 'Read', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (97, '1', 'UAP_MESSAGE_LEVEL', '1', 'Urgent,#F46D6D', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (98, '2', 'UAP_MESSAGE_LEVEL', '1', 'Important,#F8B550', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (99, '3', 'UAP_MESSAGE_LEVEL', '1', 'General,#B3B3B4', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (100, '99', 'UAP_TYPE_STATE', '1', 'All', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (101, '0', 'UAP_TYPE_STATE', '1', 'Disabled', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (102, '1', 'UAP_TYPE_STATE', '1', 'Enabled', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (103, '1', 'UAP_TYPE_RECEIVER_TYPE', '1', 'Tenant', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (104, '2', 'UAP_TYPE_RECEIVER_TYPE', '1', 'Role', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (105, '3', 'UAP_TYPE_RECEIVER_TYPE', '1', 'User', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (106, '5', 'UAP_MESSAGE_LEVEL', '1', 'No,#6CCFE3', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (107, '4', 'UAP_MESSAGE_LEVEL', '1', 'Green,#17D914', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (108, '6', 'UAP_MESSAGE_LEVEL', '1', 'Miss,#302E2E', '2', 4);
  insert into UAP_CODE (id, code, parent_code, state, text, type, app_id)
VALUES
  (109, '7', 'UAP_MESSAGE_LEVEL', '1', 'DO CARE,#CE11E7', '2', 4);
  
-- 后面的改动放在本文档最后
update UAP_REST_API set log_state = '0' where log_state is null;
update UAP_REST_API set type = '0' where type is null;


update uap_rest_api set url = 'POST:/app/bpm/task/claim/{taskId}' where id = 'UAP-BPM-TASK-POST-06';
update uap_rest_api set url = 'POST:/app/bpm/task/unclaim/{taskId}' where id = 'UAP-BPM-TASK-POST-10';

UPDATE uap_rest_api SET url = 'PUT:/bpmcfg/{bpmFormId}' WHERE id = 'UAP-BPM-PROCESS-PUT-10';
UPDATE uap_rest_api SET url = 'PUT:/bpmcfg/{bpmFormId}/state' WHERE id = 'UAP-BPM-PROCESS-PUT-11';
