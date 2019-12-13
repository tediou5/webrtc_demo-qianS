USE db_st_ha

INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xA40, 0x00, 'WJ-admin',     '管理员',   0xA0040, '无锡计量 用户 00' ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xA41, 0x00, 'WJ-office01',  '办公室01', 0xA0041, '无锡计量 演示客户端 01' ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xA42, 0x00, 'WJ-office02',  '办公室02', 0xA0042, '无锡计量 演示客户端 02' ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xA43, 0x00, 'WJ-user01',    '用户01',   0xA0043, '无锡计量 用户 01' ); 
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xA44, 0x00, 'WJ-user02',    '用户02',   0xA0044, '无锡计量 用户 02' ); 

INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xB41, 0x02, 'WJ-lab01',     '实验室 01',   0xB0041, '第二基地实验室' );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xB42, 0x02, 'WJ-lab02',     '实验室 02',   0xB0042, '移动实验室' );
INSERT INTO tb_client(id, flag, name, label, sip_acct_id, dscp)     VALUES (0xB43, 0x01, 'WJ-lab03',     '展厅 01',     0xB0043, '展厅 电视' );


INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA40, 0xB41, 0x0208);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA40, 0xB42, 0x0208);
INSERT INTO tb_client_has(clt_a, clt_b, flag) VALUES (0xA40, 0xB43, 0x0108);




/******************************************************************************
 * DEBUG: show the testing records 
 */

SELECT "INF: [1] Show assgiend SIP accounts " AS "";
SELECT hex(id), sip_id, sip_passwd, sip_domain, hex(flag)  FROM tb_sip_acct WHERE flag>0;


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


