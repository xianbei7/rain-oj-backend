# 建表脚本

-- 创建库
create database if not exists rain_oj;

-- 切换库
use rain_oj;

-- 用户表
create table if not exists user
(
    id            bigint auto_increment comment 'id' primary key,
    user_account  varchar(256)                           not null comment '账号',
    user_password varchar(512)                           not null comment '密码',
    union_id      varchar(256)                           null comment '微信开放平台id',
    mp_open_id    varchar(256)                           null comment '公众号openId',
    user_name     varchar(256)                           null comment '用户昵称',
    user_avatar   varchar(1024)                          null comment '用户头像',
    user_profile  varchar(512)                           null comment '用户简介',
    user_role     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint      default 0                 not null comment '是否删除',
    index idx_union_id (union_id)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 题目表
create table if not exists question
(
    id           bigint auto_increment comment 'id' primary key,
    number       int                                not null comment '编号',
    title        varchar(512)                       null comment '标题',
    content      text                               null comment '内容',
    tags         varchar(1024)                      null comment '标签列表（json 数组）',
    type         tinyint                            not null comment '类型',
    difficulty   tinyint                            not null comment '难度',
    answer       text                               null comment '题目答案',
    submit_num   int      default 0                 not null comment '题目提交数',
    accepted_num int      default 0                 not null comment '题目通过数',
    judge_case   text                               null comment '判题用例（json 数组）',
    judge_config text                               null comment '判题配置（json 数组）',
    thumb_num    int      default 0                 not null comment '点赞数',
    favour_num   int      default 0                 not null comment '收藏数',
    user_id      bigint                             not null comment '创建用户 id',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint  default 0                 not null comment '是否删除',
    index idx_user_id (user_id)
) comment '题目' collate = utf8mb4_unicode_ci;

-- 题目提交表（硬删除）
create table if not exists question_submit
(
    id           bigint auto_increment comment 'id' primary key,
    question_id  bigint                             not null comment '题目 id',
    user_id      bigint                             not null comment '提交用户 id',
    language     varchar(10)                        not null comment '编程语言',
    code         text                               not null comment '用户代码',
    judge_info   text                               null comment '判题信息（json 数组）',
    judge_result text                               null comment '判题结果（json对象）',
    status       int      default 0                 not null comment '判题状态（0 - 待判题、1 - 判题中、2  成功、3 - 失败）',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_question_id (question_id),
    index idx_user_id (user_id)
) comment '题目提交';

-- 题目点赞表（硬删除）
create table if not exists question_thumb
(
    id          bigint auto_increment comment 'id' primary key,
    question_id bigint                             not null comment '题目 id',
    user_id     bigint                             not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_question_id (question_id),
    index idx_user_id (user_id)
) comment '题目点赞';

-- 题目收藏表（硬删除）
create table if not exists question_favour
(
    id          bigint auto_increment comment 'id' primary key,
    question_id bigint                             not null comment '题目 id',
    user_id     bigint                             not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_question_id (question_id),
    index idx_user_id (user_id)
) comment '题目收藏';
-- 题目模板表
create table if not exists question_template
(
    question_id   bigint comment '题目 id',
    language      varchar(10) comment '编程语言',
    code_template text                               not null comment '代码模板',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    primary key (question_id, language)
) comment '题目代码模板';
