USE db_st_ha


/*
 * -----------------------------------------------------------------------------
 * TABLE 'tb_sip_acct'
 * -----------------------------------------------------------------------------
 *
 * [Theodore: 2016-09-04] use perl script to create SIP account SQL;
 *
 * NAMING RULES:
 * (1) SIP ID Namex:
 *     'stu' --> FamboA,  User App
 *     'stg' --> FamboM/G, Monitor/Gateway App
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


/* Clients: users and gateways */
INSERT INTO tb_client(id, flag, name, label, dscp) VALUES (0xA00, 0x00, 'user00', 'Mancook',   'Baby sitter');
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA01, 0x00, 'user01', '卫青',        'Grand General');
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA02, 0x00, 'user02', '刘平阳',  'Princess');
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA03, 0x00, 'user03', '霍去病',  'No.1 Duke');

INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA04, 0x00, 'user04', '主父偃', 'Justice Officer');
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA05, 0x00, 'user05', '韩安国', 'Prime Minister');
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA06, 0x00, 'user06', '窦婴',  'Bad Luck');
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA07, 0x00, 'user07', '周亚夫',  'Warrior');

INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA08, 0x00, 'user08', '刘彻', 'Emperor' );
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA09, 0x00, 'user09', '卫子夫', 'Queue' );
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA0A, 0x00, 'user0A', '刘据',  'Prince' );
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA0B, 0x00, 'user0B', '刘卫长',  'Daugher' );

INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA0C, 0x00, 'user0C', '王聪宝', '大宝 小米手机' );
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA0D, 0x00, 'user0D', '大宝',  '大宝 三星手机' );
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA0E, 0x00, 'user0E', 'Uncle 3',  'Family Relative' );
INSERT INTO tb_client(id, flag, name, label,  dscp) VALUES (0xA0F, 0x00, 'user0F', 'Uncle 4', 'Family Relative' );


INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB00,  0x01, 'gw00', 'Cook House');
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB01,  0x01, 'gw01', '纬地路1401');
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB02,  0x01, 'gw02', '工作室' );
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB03,  0x01, 'gw03', '电视盒' );

INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB04,  0x02, 'gw04', '孙氏家');
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB05,  0x02, 'gw05', 'HTC-3G 手机');
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB06,  0x02, 'gw06', '索尼手机');
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB07,  0x02, 'gw07', '三星平板');

INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB08,  0x01, 'gw08', '青旅1号');
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB09,  0x01, 'gw09', '青旅2号');
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB0A,  0x01, 'gw0A', '青旅3号');
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB0B,  0x01, 'gw0B', '青旅4号');

INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB0C,  0x01, 'gw0C', '燕郊家');
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB0D,  0x01, 'gw0D', '清盒1号');
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB0E,  0x01, 'gw0E', '房车 03');
INSERT INTO tb_client(id, flag, name, label ) VALUES (0xB0F,  0x01, 'gw0F', '房车 04');


/* 
 * Initial User in Production 
 *
 * IDs > 0xFFFF are used for free registration
 * 
 * 

  INSERT INTO tb_client(flag, name, label, ) VALUES (0x01,   'gw0000', 'GW 0000',    0x020000);
 */
INSERT INTO tb_client(flag, name, label, ) VALUES (0x00, 'user0000', 'USER 0000',  0x010000);


UPDATE tb_client SET passwd='abcD1234'  WHERE name='user00';
UPDATE tb_client SET passwd='abcD1234'  WHERE name='user01';
UPDATE tb_client SET passwd='abcD1234'  WHERE name='user02';
UPDATE tb_client SET passwd='abcD1234'  WHERE name='user03';


UPDATE tb_client SET icon_ts=2  WHERE name='user01';
UPDATE tb_client SET icon_ts=1  WHERE name='user02';
UPDATE tb_client SET icon_ts=2  WHERE name='user03';

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




/*******************************************************************************
 * Friendship 
 */

/* user00: Mancook */
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB00, 0x0108);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB01, 0x0108);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB02, 0x0108);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB03, 0x0108);


INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB04, 0x0208);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB05, 0x0208);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB06, 0x0208);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA00, 0xB07, 0x0208);


/* user0C is admin */
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA0C, 0xB0C, 0x0108);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA0C, 0xB0D, 0x0108);

/* user0D is NOT admin */
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA0D, 0xB0C, 0x0100);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA0D, 0xB0D, 0x0100);


INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xB00, 0xB0C, 0x0101);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xB00, 0xB0D, 0x0101);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xB0C, 0xB0D, 0x0101);


/******************************************************************************
 * DEBUG: show the testing records 
 */

SELECT "INF: [1] Show assgiend SIP accounts " AS "";
SELECT hex(id), sip_id, sip_passwd, sip_domain, hex(flag)  FROM tb_sip_acct WHERE flag>0;


SELECT "INF: [2] Show all clients and counts" AS "";
SELECT hex(id), name, label, phone, mac_addr, hex(), hex(flag)    FROM tb_client;
SELECT 
    ( SELECT count(*) FROM tb_client  )                 AS "Client Num",
    ( SELECT count(*) FROM tb_sip_acct WHERE flag>0)    AS "Assigned SIP",
    ( SELECT count(*) FROM tb_sip_acct)                 AS "Total SIP" ,
    ( SELECT count(*) FROM tb_client_has)               AS "Relations" 
    FROM dual;



SELECT "INF: [3] Make sure: flag of a used SIP account is set" AS "";
SET @rank=0;
SELECT @rank:=@rank+1 as 'No.', hex(id), sip_id, hex(flag) FROM tb_sip_acct 
WHERE id IN ( SELECT  FROM tb_client);


UPDATE tb_sip_acct SET flag=0x01 WHERE id IN(
    SELECT  FROM tb_client
);


SELECT "" AS "";
SELECT "==== After Correction ====" AS "";
SET @rank=0;
SELECT @rank:=@rank+1 as 'No.', hex(id), sip_id, hex(flag) FROM tb_sip_acct WHERE id IN (
    SELECT  FROM tb_client
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
        WHERE id IN ( SELECT  FROM tb_client)
    ) AS "Used SIP", 
    (   SELECT count(*) FROM tb_sip_acct 
        WHERE id IN ( SELECT  FROM tb_client) AND flag>0
    ) AS "Used and Assigned SIP"
    FROM dual;




