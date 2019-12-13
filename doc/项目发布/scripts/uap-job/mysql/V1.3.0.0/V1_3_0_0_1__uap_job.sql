
-- INSERT INTO UAP_MENU (ID,PARENT_ID,NAME,URL,TYPE,AUTH_TYPE,FUNC_TYPE,APP_ID,STATE ) VALUES  ('UAP-JOB-AUTH','UAP-JOB-TASK','Schedule Authorization','','2','0','1',3,'1');
-- INSERT INTO UAP_MENU_API (ID,MENU_ID,API_ID) VALUES ( '6E48BDDD-B477-45C0-8FC3-6FAB80441C09','UAP-JOB-AUTH','UAP-LOGIN-POST-05');
-- INSERT INTO UAP_ROLE_MENU (ID,menu_id,role_id) VALUES(322,'UAP-JOB-AUTH',1);

alter table uap_job add column allow_parallel bigint(10);
create table uap_job_runtime
(
   job_id                  bigint(20)           not null,
   log_id                  bigint(20)        not null,
   insert_time             bigint(20)            not null,
   update_time             bigint(20),
   state          varchar(10),
   PRIMARY KEY (log_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- INSERT INTO uap_sequence (sequence_name, next_val) VALUES ('jobRuntimeId', 10000);

