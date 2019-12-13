/*
 * [Theodore: 2016-09-04] Created from db_create_cn.sql
 * 
 * [Theodore: 2016-11-10] Deprecated! use db_init_test.sql
 * 
 */

USE db_st_ha


/*
 * -----------------------------------------------------------------------------
 * Insert some initial testing data
 *
 * -----------------------------------------------------------------------------
 */


/*
 * -----------------------------------------------------------------------------
 * TABLE 'tb_sip_acct'
 * -----------------------------------------------------------------------------
 *
 * [Theodore: 2016-09-04] use perl script to create SIP account SQL;
 *
 * NAMING RULES:
 * (1) SIP ID Namex:
 *     'stu' --> FamboA, User App
 *     'stg' --> FamboX, Gateway App
 *
 * (2) Record ID for FREE & TEST SIP account:
 *     ----------------------------------------------------------------
 *        Type          Record ID               SIP ID (Name)
 *     ------------     ------------        ---------------------------
 *     Free User        0x01xxxx            stuFxxxx@sip.teclub.cn
 *     Free Gateway     0x02xxxx            stgFxxxx@sip.teclub.cn
 *
 *     Test User        0x0Axx              stuxx@sip.teclub.cn
 *     Test Gateway     0x0Bxx              stgxx@sip.teclub.cn 
 *     ----------------------------------------------------------------
 *
 */



/* [Theodore: 2016-09-04] for future use 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0x01,     0x81, 'root', 'Super User',   0x01, 'Super user of the system');
UPDATE tb_client SET passwd='abcD_1234' WHERE name='root';
*/

/* Clients: users and gateways */
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA00, 0x81, 'user00', 'Mancook',    0xA00, 'Baby sitter');
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA01, 0x81, 'user01', '卫青',       0xA01, 'Grand General');   
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA02, 0x81, 'user02', '刘平阳',     0xA02, 'Princess');   
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA03, 0x81, 'user03', '霍去病',     0xA03, 'No.1 Duke');     

INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA04, 0x81, 'user04', '主父偃',     0xA04, 'Justice Officer');
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA05, 0x81, 'user05', '韩安国',     0xA05, 'Prime Minister');
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA06, 0x81, 'user06', '窦婴',       0xA06, 'Bad Luck');
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA07, 0x81, 'user07', '周亚夫',     0xA07, 'Warrior');

INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA08, 0x81, 'user08', '刘彻',       0xA08, 'Emperor' );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA09, 0x81, 'user09', '卫子夫',     0xA09, 'Queue' );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA0A, 0x81, 'user0A', '刘据',       0xA0A, 'Prince' );             
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA0B, 0x81, 'user0B', '刘卫长',     0xA0B, 'Daugher' );        

INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA0C, 0x81, 'user0C', '王聪宝',     0xA0C, '大宝 小米手机' );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA0D, 0x81, 'user0D', '大宝',       0xA0D, '大宝 三星手机' );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA0E, 0x81, 'user0E', 'Uncle 3',    0xA0E, 'Family Relative' );  
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp) VALUES (0xA0F, 0x81, 'user0F', 'Uncle 4',    0xA0F, 'Family Relative' ); 


INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB00,  0x41, 'gw00', 'Cook House',    0xB00 );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB01,  0x41, 'gw01', '纬地路1401',    0xB01 );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB02,  0x41, 'gw02', '工作室',        0xB02 );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB03,  0x41, 'gw03', '三星平板',      0xB03 );

INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB04,  0x41, 'gw04', '孙氏家',        0xB04 );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB05,  0x41, 'gw05', 'HTC-3G 手机',   0xB05 ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB06,  0x41, 'gw06', '索尼手机',      0xB06 ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB07,  0x41, 'gw07', '电视盒',        0xB07 ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB08,  0x41, 'gw08', '青旅1号',       0xB08 ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB09,  0x41, 'gw09', '青旅2号',       0xB09 ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB0A,  0x41, 'gw0A', '青旅3号',       0xB0A ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB0B,  0x41, 'gw0B', '青旅4号',       0xB0B );

INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB0C,  0x41, 'gw0C', '燕郊家',        0xB0C );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB0D,  0x41, 'gw0D', '清盒1号',       0xB0D );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB0E,  0x41, 'gw0E', '房车 03',       0xB0E );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id) VALUES (0xB0F,  0x41, 'gw0F', '房车 04',       0xB0F );


UPDATE tb_client SET passwd='abcD1234'  WHERE name='user00';
UPDATE tb_client SET passwd='abcD1234'  WHERE name='user01';
UPDATE tb_client SET passwd='abcD1234'  WHERE name='user02';
UPDATE tb_client SET passwd='abcD1234'  WHERE name='user03';

UPDATE tb_client SET icon_ts=1  WHERE name='user02';
UPDATE tb_client SET icon_ts=1  WHERE name='user09';
UPDATE tb_client SET icon_ts=1  WHERE name='user0B';



/* update SIP accounts, set the ASSIGNED bit  */
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xA00;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xA01;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xA02;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xA03;

UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xA04;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xA05;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xA06;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xA07;

UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xA0C;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xA0D;


UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xB00;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xB01;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xB02;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xB03;

UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xB04;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xB05;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xB06;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xB07;

UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xBB000C;
UPDATE tb_sip_acct SET flag=0x01 WHERE id=0xBB000D;




/* client-relationship 
	
 [user]
 Mancook: 	--> GW: (all)
 		    --> user: Weiqin Cao, Zifu Yu, Qubin Cao

 guilin: 	--> GW: cook house, oasis 1401, Sun House
 			--> user: Mancook, Zifu Yu, Qubin Cao
  
 wenyan:	--> GW: oasis 1401, Yu305601, Sun House
 			--> user: guilni

 sun min:	--> GW: sun house
 			--> user: sun kuangting

 sun k.t.	--> GW: sun house
 			--> user: sun min


 [GW]
 cook house:		--> GW: oasis 1401, Yu305601, Wood House, Sun House, ... (all GWs)
 					--> user: cook, guilin
 oasis1401:			... ...
 Yu305601:			... ...
 Wood House:		... ...

 sun house:			--> GW: cook house, oaais 1401, Yu305601, Wood House
 					--> user: mancook, Weiqin Cao(guilin), sun min, sun k.t.

 CK Lab 1:			... ...
 CK Lab 2:			... ...
 CK Lab 3:			... ...
 */


/*****************************************************************/
/* user friends */
/*****************************************************************/

/* user00: Mancook */
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB00, 0x06);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB01, 0x06);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB02, 0x06);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB03, 0x06);
/* 
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xB00, 0xA00, 0x05); 
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xB01, 0xA00, 0x05);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xB02, 0xA00, 0x05);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xB03, 0xA00, 0x05);
*/


INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB04);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB05);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB06);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB07);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB08);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB09);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB0A);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB0B);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB0C);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB0D);


INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xA02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xA03);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xA0C);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xA0D);



/* user01: Weiqin  */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB03);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB04);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB05);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB06);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB07);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB08);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB09);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB0A);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xB0B);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xA02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xA03);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xA0C);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA01, 0xA0D);

/* user02: Liu Pingyang  */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA02, 0xB00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA02, 0xB01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA02, 0xB02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA02, 0xB04);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA02, 0xB05);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA02, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA02, 0xA03);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA02, 0xA08);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA02, 0xA09);


/* user03:  ??? */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA03, 0xB00);

/* user04:  Zhu Fuyan */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA04, 0xB00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA04, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA04, 0xA05);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA04, 0xB04);

/* user05: Han Anguo */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA05, 0xB00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA05, 0xA04);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA05, 0xB04);



/* user0c: WangCongbao */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA0C, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA0C, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA0C, 0xA0D);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA0C, 0xB0C);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA0C, 0xB0D);

/* user0d: Daobao */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA0D, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA0D, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA0D, 0xA0C);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA0D, 0xB0C);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA0D, 0xB0D);



/*****************************************************************/
/* Gateway Friends */
/*****************************************************************/

/* GW: cook house */
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xB00, 0xA00, 0x05); 
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA03);

/* 
 * [Theodore: 2015-09-18]  DO NOT add too much friends! There is a limit
 * network-packet. This is a BUG! 
 *
 * [Theodore: 2015-12-31] Now the network packet max size is 60KB. The above bug is fixed!
 *
 */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA04);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA05);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA06);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA07);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA08);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA09);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA0A);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA0B);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA0C);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA0D);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA0E);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xA0F);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB03);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB04);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB05);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB06);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB07);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB08);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB09);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB0A);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB0B);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB0C);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB0D);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB0E);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB00, 0xB0F);



/* GW: oasis 1401 */
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xB01, 0xA00, 0x05);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB01, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB01, 0xA02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB01, 0xA03);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB01, 0xB00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB01, 0xB02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB01, 0xB03);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB01, 0xB04);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB01, 0xB0C);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB01, 0xB0D);


/* GW: Yu 305601 */
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xB02, 0xA00, 0x05);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB02, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB02, 0xA02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB02, 0xB00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB02, 0xB01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB02, 0xB03);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB02, 0xB04);


/* GW: wood house */
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xB03, 0xA00, 0x05);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB03, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB03, 0xA02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB03, 0xA03);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB03, 0xA04);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB03, 0xA05);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB03, 0xB00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB03, 0xB01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB03, 0xB02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB03, 0xB04);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB03, 0xB05);


/* GW: Sun House */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB04, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB04, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB04, 0xA02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB04, 0xA04);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB04, 0xA05);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB04, 0xB00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB04, 0xB01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB04, 0xB02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB04, 0xB03);



/* GW: HTC 3G */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB05, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB05, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB05, 0xA02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB05, 0xB00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB05, 0xB02);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB05, 0xB03);


/* GW: Sony Phone */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB06, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB06, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB06, 0xB00);

/* GW: Skyworth TV-Box */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB07, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB07, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB07, 0xB00);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB08, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB08, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB08, 0xB00);


/* GW: Yanjiao */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0C, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0C, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0C, 0xA0C);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0C, 0xA0D);

INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0C, 0xB01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0C, 0xB0D);


/* GW: FAMBO No.1 */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0D, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0D, 0xA01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0D, 0xA0C);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0D, 0xA0D);


INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0D, 0xB01);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB0D, 0xB0C);




/* 
 * [Theodore: 2016-08-xx] For Wuxi Jiliang
 * 
 */

/*
INSERT INTO tb_sip_acct(id, sip_id) VALUES (0xAAC000,  'stu0100');
INSERT INTO tb_sip_acct(id, sip_id) VALUES (0xAAC001,  'stu0101');
INSERT INTO tb_sip_acct(id, sip_id) VALUES (0xAAC002,  'stu0102');
INSERT INTO tb_sip_acct(id, sip_id) VALUES (0xAAC003,  'stu0103');


INSERT INTO tb_sip_acct(id, sip_id) VALUES (0xBBC000,  'stg0100');
INSERT INTO tb_sip_acct(id, sip_id) VALUES (0xBBC001,  'stg0101');
INSERT INTO tb_sip_acct(id, sip_id) VALUES (0xBBC002,  'stg0102');
INSERT INTO tb_sip_acct(id, sip_id) VALUES (0xBBC003,  'stg0103');
*/

INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xA40, 0x81, 'WJ-user00',    '用户00',   0xA40, '无锡计量 用户 00' ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xA41, 0x81, 'WJ-office01',  '办公室01', 0xA41, '无锡计量 演示客户端 01' ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xA42, 0x81, 'WJ-office02',  '办公室02', 0xA42, '无锡计量 演示客户端 02' ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xA43, 0x81, 'WJ-user03',    '用户03',   0xA43, '无锡计量 用户 03' ); 

INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xB41, 0x41, 'WJ-lab01',     '实验室 01',0xB41, '第二基地实验室' );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xB42, 0x41, 'WJ-lab02',     '实验室 02',0xB42, '移动实验室' );

/* WJ-lab01 */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB41, 0xB42);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB41, 0xA40);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB41, 0xA41);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB41, 0xA42);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB41, 0xA43);

/* WJ-lab02 */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB42, 0xB41);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB42, 0xA40);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB42, 0xA41);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB42, 0xA42);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB42, 0xA43);


/* WJ-user00 */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA40, 0xB41);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA40, 0xB42);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA40, 0xA43);


/* WJ-user03 */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA43, 0xB41);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA43, 0xB42);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA43, 0xA40);



/* WJ-office01 */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA41, 0xB41);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA41, 0xB42);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA41, 0xA42);

/* WJ-office02 */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA42, 0xB41);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA42, 0xB42);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA42, 0xA41);


/* user00(mancook) --> WJ-lab01, WJ-lab02 */
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB41, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB41);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xB42, 0xA00);
INSERT INTO tb_client_has(clt_a, clt_b) VALUES (0xA00, 0xB42);


/*
INSERT INTO tb_message(flag, clt_a, clt_b) VALUES (0x01, 0xA04, 0xB01);
*/



/* 
 * TODO:
 * 1) Check if used SIP in tb_client has assigned bit set in tb_sip_acct;
 * 2) A gateway has ZERO or ONLY ONE admin owner;
 *
 */


/* 
 * DEBUG: show the testing records 
 *
 */
SELECT "INF: [1] Show assgiend SIP accounts " AS "";
SELECT hex(id), sip_id, sip_passwd, sip_domain, hex(flag)  FROM tb_sip_acct WHERE flag>0;

/*
SELECT "INF: S.T. Client Relationship " AS "";
SELECT hex(id), hex(clt_a), hex(clt_b), hex(flag)                    FROM tb_client_has;
*/


SELECT "INF: [2] Show all clients and counts" AS "";
SELECT hex(id), name, label, phone, mac_addr, hex(sip_acct_id), hex(flag)    FROM tb_client;
SELECT 
    ( SELECT count(*) FROM tb_client  )                 AS "Client Num",
    ( SELECT count(*) FROM tb_sip_acct WHERE flag>0)    AS "Assigned SIP",
    ( SELECT count(*) FROM tb_sip_acct)                 AS "Total SIP" ,
    ( SELECT count(*) FROM tb_client_has)               AS "Relations" 
    FROM dual;



SELECT "INF: [3] Make sure: flag of a used SIP account is set" AS "";
SET @rank=0;
SELECT @rank:=@rank+1 as 'No.', hex(id), sip_id, hex(flag) FROM tb_sip_acct 
WHERE id IN ( SELECT sip_acct_id FROM tb_client);


UPDATE tb_sip_acct SET flag=0x01 WHERE id IN(
    SELECT sip_acct_id FROM tb_client
);


SELECT "" AS "";
SELECT "==== After Correction ====" AS "";
SET @rank=0;
SELECT @rank:=@rank+1 as 'No.', hex(id), sip_id, hex(flag) FROM tb_sip_acct WHERE id IN (
    SELECT sip_acct_id FROM tb_client
);

/*
 * For correct reference of SIP accounts, following number should same!
 */
SELECT 
    (   SELECT count(*) FROM tb_client
    ) AS "Clients", 
    (   SELECT count(*) FROM tb_sip_acct 
        WHERE flag>0
    ) AS "Assinged SIP",
    (   SELECT count(*) FROM tb_sip_acct 
        WHERE id IN ( SELECT sip_acct_id FROM tb_client)
    ) AS "Used SIP", 
    (   SELECT count(*) FROM tb_sip_acct 
        WHERE id IN ( SELECT sip_acct_id FROM tb_client) AND flag>0
    ) AS "Used and Assigned SIP"
    FROM dual;




