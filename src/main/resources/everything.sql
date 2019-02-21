-- 创建数据库
-- create database if not exists everything;
-- 创建数据库表
drop table if exists file_index;
create table if not exists file_index(
  name varchar(256) not null comment '文件名称',
  path varchar(1024) not null comment '文件路径',
  depth int not null comment '文件路径深度',
  file_type varchar(32) not null comment '文件类型'
);

-- insert into file_index(name, path, depth, file_type) values (?,?,?,?)
-- insert into file_index(name, path, depth, file_type) values ('简历.ppt','D:\\a\\test\\简历.ppt',3,'DOC');
