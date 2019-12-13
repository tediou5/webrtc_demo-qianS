/*
 * ==== LOGS =====
 * [Theodore: 2014-12-04] Created
 * [Theodore: 2019-01-25] changed to work with MySQL 8;
 * 
 */

DROP DATABASE IF EXISTS db_st_ha;
CREATE DATABASE db_st_ha DEFAULT CHARSET UTF8 COLLATE UTF8_GENERAL_CI;

DROP USER 'momo123'@'localhost' ;
CREATE USER 'momo123'@'localhost' IDENTIFIED BY 'abcd123';
GRANT ALL ON db_st_ha.* TO 'momo123'@'localhost';


/* GRANT ALL ON db_st_ha.* TO momo@'%'  IDENTIFIED BY 'asdf123' ; */

USE db_st_ha


/**
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * FILEDS SPECIFICATION
 * ~~~~~~~~~~~~~~~~~~~~
 *
 * ID Space
 *      0x0000 ~ 0xFFFF             For Testing, 64K
 *      0x10000 ~ 0x7FFFF           For Production Gateways, 512K
 *      0x80000 ~ 0xFFFFF           For Free Registration,   512K
 *
 *
 */
CREATE TABLE `tb_client`(
	`id`			BIGINT UNSIGNED		NOT NULL AUTO_INCREMENT, 
	`name`			VARCHAR(32)			NOT NULL UNIQUE,
	`label`			VARCHAR(32),
	`dscp`			VARCHAR(128)	    NOT NULL DEFAULT '<no-description>',
	`flag`          INT UNSIGNED        NOT NULL DEFAULT 0x00,
    `icon_ts`       BIGINT UNSIGNED		NOT NULL DEFAULT 0,

	`phone`			VARCHAR(32)			UNIQUE,  	/* for user */
	`mac_addr`		VARCHAR(32)			UNIQUE,		/* for gateway */
	`public_ip`		VARCHAR(32),
	`public_port`	INT					DEFAULT '0',
	`sip_acct_id`   INT UNSIGNED		NOT NULL UNIQUE,

    /* Fields NOT sent to client */
	`passwd`		VARCHAR(32)			NOT NULL DEFAULT 'abcd1234',
	`local_ip`		VARCHAR(32),		
	`local_port`	INT					DEFAULT '0',

    `create_time`   TIMESTAMP           DEFAULT CURRENT_TIMESTAMP,
	`last_login`	TIMESTAMP           DEFAULT '2000-01-01 00:00:01' ,
	`last_logoff`	TIMESTAMP           DEFAULT '2000-01-01 00:00:01' ,
	`online_time`	INT UNSIGNED        NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`)
)ENGINE=INNODB DEFAULT CHARSET=UTF8;


/* Free registration ID starts from 0x80-0000 (8*65536)  */
ALTER TABLE tb_client AUTO_INCREMENT=524288;



CREATE TABLE `tb_sip_acct`(
	`id`			    INT UNSIGNED		NOT NULL AUTO_INCREMENT,
	`sip_id`			VARCHAR(32)			NOT NULL UNIQUE,
	`sip_passwd`		VARCHAR(32)			NOT NULL DEFAULT 'abcd1234',
	`sip_domain`        VARCHAR(64)	        NOT NULL DEFAULT 'sip.teclub.cn',
	`dscp`			    VARCHAR(128)	    NOT NULL DEFAULT '<no-dscp>',
	`flag`              INT UNSIGNED        NOT NULL default 0x00,
	PRIMARY KEY (`id`)
)ENGINE=INNODB DEFAULT CHARSET=UTF8;


CREATE TABLE `tb_client_has`(
	`id`			BIGINT UNSIGNED		NOT NULL AUTO_INCREMENT,
	`clt_a`			BIGINT UNSIGNED		,
	`clt_b`			BIGINT UNSIGNED		,
	`flag`          INT UNSIGNED        NOT NULL default 0x00,
	PRIMARY KEY (`id`)
)ENGINE=INNODB DEFAULT CHARSET=UTF8;



CREATE TABLE `tb_message`(
	`id`			BIGINT UNSIGNED		NOT NULL AUTO_INCREMENT,
	`flag`          INT UNSIGNED        NOT NULL DEFAULT 0x00,
	`data_len`      INT UNSIGNED        NOT NULL,
	`clt_a`			BIGINT UNSIGNED		NOT NULL,
	`clt_b`			BIGINT UNSIGNED		NOT NULL,
    `start_time`    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `end_time`      TIMESTAMP           DEFAULT '2000-01-01 00:00:01' ,
	`data`			BLOB, 
	PRIMARY KEY (`id`)
)ENGINE=INNODB DEFAULT CHARSET=UTF8;




/*
 * -----------------------------------------------------------------------------
 * TO Insert some initial testing data, 
 * Exeucute the initial SQL script.
 *
 *
 * -----------------------------------------------------------------------------
 *
 */




