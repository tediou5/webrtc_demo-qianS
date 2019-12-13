
/******************************************************************************
 * [Theodore: 2017-12-05] Created
 *
 * DEBUG: show the testing records 
 *
 */

USE db_st_ha


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



