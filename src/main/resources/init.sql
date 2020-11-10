create database if not exists `my_ssm` DEFAULT CHARACTER SET utf8;
use my_ssm;
drop table memo;
drop table roles_user;
drop table user;
drop table roles;
CREATE TABLE if not exists `user`
(
    `id`       int(11) NOT NULL AUTO_INCREMENT,
    `username` varchar(64)  DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    `email`    varchar(64)  DEFAULT NULL,
    `regTime`  datetime     DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 21
  DEFAULT CHARSET = utf8;

CREATE TABLE if not exists `memo`
(
    `mid`        int(11) NOT NULL AUTO_INCREMENT,
    `content`    text     DEFAULT NULL,
    `createTime` datetime DEFAULT NULL,
    `sendTime`   datetime DEFAULT NULL,
    `uid`        int(11),
    `state`      int(2),
    PRIMARY KEY (`mid`),
    CONSTRAINT fk_memo_user FOREIGN KEY (uid) REFERENCES user (id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 21
  DEFAULT CHARSET = utf8;

create TABLE if not exists `roles`
(
    id   int(11) NOT NULL AUTO_INCREMENT,
    name varchar(32) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 21
  DEFAULT CHARSET = utf8;

create TABLE IF NOT EXISTS `roles_user`
(
    id int(11) NOT NULL AUTO_INCREMENT,
    `rid` int DEFAULT 2,
    `uid` int DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT fk_ru_u FOREIGN KEY (uid) REFERENCES user (id),
    CONSTRAINT fk_ru_r FOREIGN KEY (rid) REFERENCES roles (id)
)ENGINE = InnoDB
 AUTO_INCREMENT = 21
 DEFAULT CHARSET = utf8;

insert into roles (id, name) values (1, '管理员');
insert into roles (id, name) values (2, '用户');

select * from roles;
select * from user;
select * from roles_user;

