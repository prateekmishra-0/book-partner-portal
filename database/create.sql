DROP DATABASE IF EXISTS pubs;

CREATE DATABASE IF NOT EXISTS pubs;

USE pubs;



drop table if exists authors;
create table authors (
                         au_id char(11) not null,
                         au_lname varchar(40) not null,
                         au_fname varchar(20) not null,
                         phone char(12) not null default 'UNKNOWN',
                         address varchar(40) null,
                         city varchar(20) null,
                         state char(2) null,
                         zip char(5) null,
                         contract int not null,
                         is_active boolean not null default true,
                         check  (au_id REGEXP '^[0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9][0-9][0-9]$'),
 check (zip REGEXP '^[0-9][0-9][0-9][0-9][0-9]$'),
  constraint UPKCL_auidind
    primary key (au_id)

);

drop table if exists publishers;
create table publishers (
                            pub_id char(4) not null,
                            pub_name varchar(40) null,
                            city varchar(20) null,
                            state char(2) null,
                            country varchar(30) null default 'USA',
                            is_active boolean not null default true,
                            constraint UPKCL_pubind
                                primary key (pub_id),
                            check ((
                                pub_id in (
                                           '1389', '0736', '0877', '1622', '1756'
                                    )
                                    or pub_id REGEXP '^99[0-9][0-9]$'
                                ))
);

drop table if exists titles;
create table titles (
                        title_id varchar(10) not null,
                        title varchar(80) not null,
                        type char(12) not null default 'UNDECIDED',
                        pub_id char(4) null,
                        price double null,
                        advance double null,
                        royalty int null,
                        ytd_sales int null,
                        notes varchar(200) null,
                        pubdate DATETIME NOT NULL,
                        is_active boolean not null default true,
                        constraint UPKCL_titleidind
                            primary key (title_id),
                        foreign key (pub_id)
                            references publishers (pub_id)
);

drop table if exists titleauthor;
create table titleauthor (
                             au_id varchar(11) not null,
                             title_id varchar(10) not null,
                             au_ord tinyint null,
                             royaltyper int null,
                             foreign key (au_id)
                                 references authors (au_id),
                             foreign key (title_id)
                                 references titles (title_id),
                             constraint UPKCL_taind
                                 primary key (au_id, title_id)
);

drop table if exists stores;
create table stores (
                        stor_id char(4) not null,
                        stor_name varchar(40) null,
                        stor_address varchar(40) null,
                        city varchar(20) null,
                        state char(2) null,
                        zip char(5) null,
                        is_active boolean not null default true,
                        constraint UPK_storeid
                            primary key (stor_id)
);

drop table if exists sales;
create table sales (
                       stor_id char(4) not null,
                       ord_num varchar(20) not null,
                       ord_date timestamp not null,
                       qty smallint not null,
                       payterms varchar(12) not null,
                       title_id varchar(10) not null,
                       foreign key (stor_id)
                           references stores (stor_id),
                       foreign key (title_id)
                           references titles (title_id),
                       constraint UPKCL_sales
                           primary key (stor_id, ord_num, title_id)
);

drop table if exists roysched;
create table roysched (
                          roysched_id int primary key auto_increment,
                          title_id varchar(10),
                          lorange int null,
                          hirange int null,
                          royalty int null,
                          foreign key (title_id)
                              references titles (title_id)
);

drop table if exists discounts;
create table discounts (
                           discounttype varchar(40) not null,
                           stor_id char(4) null,
                           lowqty smallint null,
                           highqty smallint null,
                           discount decimal(4, 2) not null,
                           foreign key (stor_id)
                               references stores (stor_id)
);

drop table if exists jobs;
create table jobs (
                      job_id smallint not null auto_increment,
                      job_desc varchar(50) not null default 'New Position - title not formalized yet',
                      min_lvl int not null,
                      max_lvl int not null,
                      primary key (job_id),
                      check (min_lvl >= 10),
                      check (max_lvl <= 250)
);

drop table if exists employee;
create table employee (
                          emp_id varchar(10) not null,
                          fname varchar(20) not null,
                          minit char(1) null,
                          lname varchar(30) not null,
                          job_id smallint not null default 1,
                          job_lvl int default 10,
                          pub_id char(4) not null default '9952',
                          hire_date timestamp not null default current_timestamp(),
                          is_active boolean not null default true,
                          constraint PK_emp_id
                              primary key (emp_id),
                          constraint CK_emp_id
                              check (
                                  emp_id REGEXP '^[A-Z][A-Z][A-Z][1-9][0-9][0-9][0-9][0-9][FM]$'
                                  or emp_id REGEXP '^[A-Z]-[A-Z][1-9][0-9][0-9][0-9][0-9][FM]$'
),
  foreign key (job_id)
  references jobs (job_id),
  foreign key (pub_id)
  references publishers (pub_id)
);