create database `my_ssm` DEFAULT CHARACTER SET utf8;
use my_ssm;
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `regTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

use my_ssm;
CREATE TABLE `memo` (
    `mid` int(11) NOT NULL AUTO_INCREMENT,
    `content` text DEFAULT NULL,
    `createTime` datetime DEFAULT NULL,
    `sendTime` datetime DEFAULT NULL,
    `uid` int(11),
    `state` int(2),
    PRIMARY KEY (`mid`),
    CONSTRAINT fk_memo_user FOREIGN KEY (uid) REFERENCES user(id)
)ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;