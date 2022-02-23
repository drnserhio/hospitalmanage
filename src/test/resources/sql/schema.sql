create table AnalyzeICDDate (id bigint not null, date_add_analyze datetime, icd_id varchar(255), primary key (id)) engine=MyISAM;
create table hibernate_sequence (next_val bigint) engine=MyISAM;
insert into hibernate_sequence values ( 1 );
insert into hibernate_sequence values ( 1 );
insert into hibernate_sequence values ( 1 );
insert into hibernate_sequence values ( 1 );
insert into hibernate_sequence values ( 1 );
create table ICD (id bigint not null, code varchar(255), language varchar(255), value varchar(255), primary key (id)) engine=MyISAM;
create table Treatment (id bigint not null, date_create datetime, treatment longtext, primary key (id)) engine=MyISAM;
create table User (id bigint not null, QRCODE varchar(255), address varchar(255), age integer not null, authorities tinyblob, email varchar(255), firstname varchar(255), hospiztalization bit, infoAboutComplaint varchar(255), infoAboutSick varchar(255), infoDiagnosis varchar(255), isActive bit, isNotLocked bit, joindDate datetime, lastLoginDate datetime, lastLoginDateDisplay datetime, lastname varchar(255), online bit, password varchar(255), patronomic varchar(255), profileImageUrl varchar(255), role varchar(255), timeToVisitAt datetime, userId varchar(255), username varchar(255), primary key (id)) engine=MyISAM;
create table users_diagnosis (user_id bigint not null, diagnos_id bigint not null, primary key (user_id, diagnos_id)) engine=MyISAM;
create table users_treatments (user_id bigint not null, treatment_id bigint not null) engine=MyISAM;
create table users_videos (user_id bigint not null, video_id bigint not null, primary key (user_id, video_id)) engine=MyISAM;
create table Video (id bigint not null, create_date datetime, name_file varchar(255), primary key (id)) engine=MyISAM;
alter table users_diagnosis add foreign key (diagnos_id) references AnalyzeICDDate (id);
alter table users_diagnosis add foreign key (user_id) references User (id);
alter table users_treatments add  foreign key (treatment_id) references Treatment (id);
alter table users_treatments add foreign key (user_id) references User (id);
alter table users_videos add foreign key (video_id) references Video (id);
alter table users_videos add foreign key (user_id) references User (id);
