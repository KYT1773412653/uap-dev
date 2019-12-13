create table uap_msg_info
(
  id        NUMBER(19) not null,
  msg_title  VARCHAR2(128 CHAR) not null,
  msg_content  VARCHAR2(2048 CHAR) not null,
  msg_code  VARCHAR2(64 CHAR) not null,
  tenancy_id NUMBER(19),
  sender_nm VARCHAR2(128 CHAR),
  msg_level     VARCHAR2(16 CHAR) not null,
  create_time NUMBER(19),
  state VARCHAR2(8 CHAR)
);
alter table uap_msg_info
  add primary key (ID);
  
create table uap_msg_receiver
(
  id        NUMBER(19) not null,
  user_id        NUMBER(19) not null,
  msg_id        NUMBER(19) not null,
  state  VARCHAR2(16 CHAR) not null,
  update_time NUMBER(19)
);
alter table uap_msg_receiver
  add primary key (ID);