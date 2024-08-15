insert into users (email, password, nickname, gender, birth_date, registration) values ('qwerty', '$2a$10$6AKFWn/et64TZj7lI3LdmuzK/qSiLJrp0b6GEiYz4AxAhPvf9cRM6', '지나가는여행자', 1, '2024-05-29', 0);
insert into users (email, password, nickname, gender, birth_date, registration) values ('qwerty2', '1234', '누워있는개백수', 0, '1990-01-11', 0);
insert into users (email, password, nickname, gender, birth_date, registration) values ('qwerty3', '1234', '코딩하는개발자', 1, '2022-02-22', 0);
insert into users (email, password, nickname, gender, birth_date, registration) values ('qwerty4', '1234', '야근하는직장인', 0, '2033-03-01', 0);
insert into users (email, password, nickname, gender, birth_date, registration) values ('qwerty5', '1234', '노래하는아이돌', 1, '2044-04-01', 0);

insert into roles (role, user_id) values (0, 1);
# insert into role_model (role_name) value ('ROLE_USER');
# insert into role_model (role_name) value ('ROLE_ADMIN');
#
# insert into users_role (users_id, role_id) value (1, 1);