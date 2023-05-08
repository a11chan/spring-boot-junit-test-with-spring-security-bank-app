SET REFERENTIAL_INTEGRITY FALSE; -- 모든 제약조건 해제
truncate table transaction_tb;
truncate table account_tb;
truncate table user_tb;
SET REFERENTIAL_INTEGRITY TRUE;