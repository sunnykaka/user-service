/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2015-3-18 10:02:21                           */
/*==============================================================*/


drop table if exists user;

drop table if exists user_login_info;

drop table if exists user_password_info;

/*==============================================================*/
/* Table: user                                                  */
/*==============================================================*/
create table user
(
   id                   bigint not null auto_increment,
   username             varchar(64),
   email                varchar(64) default '',
   gender               varchar(8),
   birthday             timestamp,
   primary key (id)
);

/*==============================================================*/
/* Table: user_login_info                                       */
/*==============================================================*/
create table user_login_info
(
   id                   bigint not null auto_increment,
   provider_id          varchar(255) not null,
   provider_key         varchar(255) not null default '',
   user_id              bigint not null,
   primary key (id)
);

/*==============================================================*/
/* Table: user_password_info                                    */
/*==============================================================*/
create table user_password_info
(
   id                   bigint not null auto_increment,
   hasher               varchar(255) not null,
   password             varchar(255) not null default '',
   salt                 varchar(255) default '',
   user_login_info_id   bigint not null,
   primary key (id)
);

alter table user_login_info add constraint FK_Reference_2 foreign key (user_id)
      references user (id) on delete restrict on update restrict;

alter table user_password_info add constraint FK_Reference_1 foreign key (user_login_info_id)
      references user_login_info (id) on delete restrict on update restrict;

