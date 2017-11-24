create table CATEGORY (
	id bigserial not null primary key,
	name varchar(25) not null unique
);

create table USERS (
	id bigserial not null primary key,
	created_at date not null,
	name varchar(40) not null,
	email varchar(70) not null unique,
	password varchar(100) not null
);

create table USER_ROLE (
	user_id bigint not null,
	role varchar(30) not null,
	primary key(user_id, role),
	constraint fk_user_roles_user foreign key(user_id) references USERS(id)
);

insert into USERS (created_at, name, email, password) values(current_timestamp, 'Admin', 'adm@domain.com', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=');
insert into USER_ROLE (user_id, role) values((select id from USERS where email = 'adm@domain.com'), 'STANDARD');
insert into USER_ROLE (user_id, role) values((select id from USERS where email = 'adm@domain.com'), 'ADMIN');

create table COURSE (
	id bigserial not null primary key,
	name varchar(25) not null unique,
	url varchar(100) not null,
	description varchar(255),
	category_id	bigint not null,
	constraint fk_course_category foreign key(category_id) references CATEGORY(id)
);

create table REVIEW (
	id bigserial not null primary key,
	rating integer not null,
	comment varchar(200),
	created_at date not null,
	user_id bigint not null,
	course_id bigint not null,
	constraint fk_review_users foreign key(user_id) references USERS(id),
	constraint fk_review_course foreign key(course_id) references COURSE(id)
);