create table ACT_APP_DATABASECHANGELOG
(
    ID            varchar(255) not null,
    AUTHOR        varchar(255) not null,
    FILENAME      varchar(255) not null,
    DATEEXECUTED  datetime     not null,
    ORDEREXECUTED int          not null,
    EXECTYPE      varchar(10)  not null,
    MD5SUM        varchar(35)  null,
    DESCRIPTION   varchar(255) null,
    COMMENTS      varchar(255) null,
    TAG           varchar(255) null,
    LIQUIBASE     varchar(20)  null,
    CONTEXTS      varchar(255) null,
    LABELS        varchar(255) null,
    DEPLOYMENT_ID varchar(10)  null
);

create table ACT_APP_DATABASECHANGELOGLOCK
(
    ID          int          not null
        primary key,
    LOCKED      bit          not null,
    LOCKGRANTED datetime     null,
    LOCKEDBY    varchar(255) null
);

create table ACT_APP_DEPLOYMENT
(
    ID_          varchar(255)            not null
        primary key,
    NAME_        varchar(255)            null,
    CATEGORY_    varchar(255)            null,
    KEY_         varchar(255)            null,
    DEPLOY_TIME_ datetime(3)             null,
    TENANT_ID_   varchar(255) default '' null
);

create table ACT_APP_APPDEF
(
    ID_            varchar(255)            not null
        primary key,
    REV_           int                     not null,
    NAME_          varchar(255)            null,
    KEY_           varchar(255)            not null,
    VERSION_       int                     not null,
    CATEGORY_      varchar(255)            null,
    DEPLOYMENT_ID_ varchar(255)            null,
    RESOURCE_NAME_ varchar(4000)           null,
    DESCRIPTION_   varchar(4000)           null,
    TENANT_ID_     varchar(255) default '' null,
    constraint ACT_IDX_APP_DEF_UNIQ
        unique (KEY_, VERSION_, TENANT_ID_),
    constraint ACT_FK_APP_DEF_DPLY
        foreign key (DEPLOYMENT_ID_) references ACT_APP_DEPLOYMENT (ID_)
);

create index ACT_IDX_APP_DEF_DPLY
    on ACT_APP_APPDEF (DEPLOYMENT_ID_);

create table ACT_APP_DEPLOYMENT_RESOURCE
(
    ID_             varchar(255) not null
        primary key,
    NAME_           varchar(255) null,
    DEPLOYMENT_ID_  varchar(255) null,
    RESOURCE_BYTES_ longblob     null,
    constraint ACT_FK_APP_RSRC_DPL
        foreign key (DEPLOYMENT_ID_) references ACT_APP_DEPLOYMENT (ID_)
);

create index ACT_IDX_APP_RSRC_DPL
    on ACT_APP_DEPLOYMENT_RESOURCE (DEPLOYMENT_ID_);

create table ACT_CMMN_DATABASECHANGELOG
(
    ID            varchar(255) not null,
    AUTHOR        varchar(255) not null,
    FILENAME      varchar(255) not null,
    DATEEXECUTED  datetime     not null,
    ORDEREXECUTED int          not null,
    EXECTYPE      varchar(10)  not null,
    MD5SUM        varchar(35)  null,
    DESCRIPTION   varchar(255) null,
    COMMENTS      varchar(255) null,
    TAG           varchar(255) null,
    LIQUIBASE     varchar(20)  null,
    CONTEXTS      varchar(255) null,
    LABELS        varchar(255) null,
    DEPLOYMENT_ID varchar(10)  null
);

create table ACT_CMMN_DATABASECHANGELOGLOCK
(
    ID          int          not null
        primary key,
    LOCKED      bit          not null,
    LOCKGRANTED datetime     null,
    LOCKEDBY    varchar(255) null
);

create table ACT_CMMN_DEPLOYMENT
(
    ID_                   varchar(255)            not null
        primary key,
    NAME_                 varchar(255)            null,
    CATEGORY_             varchar(255)            null,
    KEY_                  varchar(255)            null,
    DEPLOY_TIME_          datetime(3)             null,
    PARENT_DEPLOYMENT_ID_ varchar(255)            null,
    TENANT_ID_            varchar(255) default '' null
);

create table ACT_CMMN_CASEDEF
(
    ID_                     varchar(255)            not null
        primary key,
    REV_                    int                     not null,
    NAME_                   varchar(255)            null,
    KEY_                    varchar(255)            not null,
    VERSION_                int                     not null,
    CATEGORY_               varchar(255)            null,
    DEPLOYMENT_ID_          varchar(255)            null,
    RESOURCE_NAME_          varchar(4000)           null,
    DESCRIPTION_            varchar(4000)           null,
    HAS_GRAPHICAL_NOTATION_ bit                     null,
    TENANT_ID_              varchar(255) default '' null,
    DGRM_RESOURCE_NAME_     varchar(4000)           null,
    HAS_START_FORM_KEY_     bit                     null,
    constraint ACT_IDX_CASE_DEF_UNIQ
        unique (KEY_, VERSION_, TENANT_ID_),
    constraint ACT_FK_CASE_DEF_DPLY
        foreign key (DEPLOYMENT_ID_) references ACT_CMMN_DEPLOYMENT (ID_)
);

create index ACT_IDX_CASE_DEF_DPLY
    on ACT_CMMN_CASEDEF (DEPLOYMENT_ID_);

create table ACT_CMMN_DEPLOYMENT_RESOURCE
(
    ID_             varchar(255) not null
        primary key,
    NAME_           varchar(255) null,
    DEPLOYMENT_ID_  varchar(255) null,
    RESOURCE_BYTES_ longblob     null,
    GENERATED_      bit          null,
    constraint ACT_FK_CMMN_RSRC_DPL
        foreign key (DEPLOYMENT_ID_) references ACT_CMMN_DEPLOYMENT (ID_)
);

create index ACT_IDX_CMMN_RSRC_DPL
    on ACT_CMMN_DEPLOYMENT_RESOURCE (DEPLOYMENT_ID_);

create table ACT_CMMN_HI_CASE_INST
(
    ID_                        varchar(255)            not null
        primary key,
    REV_                       int                     not null,
    BUSINESS_KEY_              varchar(255)            null,
    NAME_                      varchar(255)            null,
    PARENT_ID_                 varchar(255)            null,
    CASE_DEF_ID_               varchar(255)            null,
    STATE_                     varchar(255)            null,
    START_TIME_                datetime(3)             null,
    END_TIME_                  datetime(3)             null,
    START_USER_ID_             varchar(255)            null,
    CALLBACK_ID_               varchar(255)            null,
    CALLBACK_TYPE_             varchar(255)            null,
    TENANT_ID_                 varchar(255) default '' null,
    REFERENCE_ID_              varchar(255)            null,
    REFERENCE_TYPE_            varchar(255)            null,
    LAST_REACTIVATION_TIME_    datetime(3)             null,
    LAST_REACTIVATION_USER_ID_ varchar(255)            null,
    BUSINESS_STATUS_           varchar(255)            null
);

create table ACT_CMMN_HI_MIL_INST
(
    ID_           varchar(255)            not null
        primary key,
    REV_          int                     not null,
    NAME_         varchar(255)            not null,
    TIME_STAMP_   datetime(3)             null,
    CASE_INST_ID_ varchar(255)            not null,
    CASE_DEF_ID_  varchar(255)            not null,
    ELEMENT_ID_   varchar(255)            not null,
    TENANT_ID_    varchar(255) default '' null
);

create table ACT_CMMN_HI_PLAN_ITEM_INST
(
    ID_                    varchar(255)            not null
        primary key,
    REV_                   int                     not null,
    NAME_                  varchar(255)            null,
    STATE_                 varchar(255)            null,
    CASE_DEF_ID_           varchar(255)            null,
    CASE_INST_ID_          varchar(255)            null,
    STAGE_INST_ID_         varchar(255)            null,
    IS_STAGE_              bit                     null,
    ELEMENT_ID_            varchar(255)            null,
    ITEM_DEFINITION_ID_    varchar(255)            null,
    ITEM_DEFINITION_TYPE_  varchar(255)            null,
    CREATE_TIME_           datetime(3)             null,
    LAST_AVAILABLE_TIME_   datetime(3)             null,
    LAST_ENABLED_TIME_     datetime(3)             null,
    LAST_DISABLED_TIME_    datetime(3)             null,
    LAST_STARTED_TIME_     datetime(3)             null,
    LAST_SUSPENDED_TIME_   datetime(3)             null,
    COMPLETED_TIME_        datetime(3)             null,
    OCCURRED_TIME_         datetime(3)             null,
    TERMINATED_TIME_       datetime(3)             null,
    EXIT_TIME_             datetime(3)             null,
    ENDED_TIME_            datetime(3)             null,
    LAST_UPDATED_TIME_     datetime(3)             null,
    START_USER_ID_         varchar(255)            null,
    REFERENCE_ID_          varchar(255)            null,
    REFERENCE_TYPE_        varchar(255)            null,
    TENANT_ID_             varchar(255) default '' null,
    ENTRY_CRITERION_ID_    varchar(255)            null,
    EXIT_CRITERION_ID_     varchar(255)            null,
    SHOW_IN_OVERVIEW_      bit                     null,
    EXTRA_VALUE_           varchar(255)            null,
    DERIVED_CASE_DEF_ID_   varchar(255)            null,
    LAST_UNAVAILABLE_TIME_ datetime(3)             null
);

create table ACT_CMMN_RU_CASE_INST
(
    ID_                        varchar(255)            not null
        primary key,
    REV_                       int                     not null,
    BUSINESS_KEY_              varchar(255)            null,
    NAME_                      varchar(255)            null,
    PARENT_ID_                 varchar(255)            null,
    CASE_DEF_ID_               varchar(255)            null,
    STATE_                     varchar(255)            null,
    START_TIME_                datetime(3)             null,
    START_USER_ID_             varchar(255)            null,
    CALLBACK_ID_               varchar(255)            null,
    CALLBACK_TYPE_             varchar(255)            null,
    TENANT_ID_                 varchar(255) default '' null,
    LOCK_TIME_                 datetime(3)             null,
    IS_COMPLETEABLE_           bit                     null,
    REFERENCE_ID_              varchar(255)            null,
    REFERENCE_TYPE_            varchar(255)            null,
    LOCK_OWNER_                varchar(255)            null,
    LAST_REACTIVATION_TIME_    datetime(3)             null,
    LAST_REACTIVATION_USER_ID_ varchar(255)            null,
    BUSINESS_STATUS_           varchar(255)            null,
    constraint ACT_FK_CASE_INST_CASE_DEF
        foreign key (CASE_DEF_ID_) references ACT_CMMN_CASEDEF (ID_)
);

create index ACT_IDX_CASE_INST_CASE_DEF
    on ACT_CMMN_RU_CASE_INST (CASE_DEF_ID_);

create index ACT_IDX_CASE_INST_PARENT
    on ACT_CMMN_RU_CASE_INST (PARENT_ID_);

create index ACT_IDX_CASE_INST_REF_ID_
    on ACT_CMMN_RU_CASE_INST (REFERENCE_ID_);

create table ACT_CMMN_RU_MIL_INST
(
    ID_           varchar(255)            not null
        primary key,
    NAME_         varchar(255)            not null,
    TIME_STAMP_   datetime(3)             null,
    CASE_INST_ID_ varchar(255)            not null,
    CASE_DEF_ID_  varchar(255)            not null,
    ELEMENT_ID_   varchar(255)            not null,
    TENANT_ID_    varchar(255) default '' null,
    constraint ACT_FK_MIL_CASE_DEF
        foreign key (CASE_DEF_ID_) references ACT_CMMN_CASEDEF (ID_),
    constraint ACT_FK_MIL_CASE_INST
        foreign key (CASE_INST_ID_) references ACT_CMMN_RU_CASE_INST (ID_)
);

create index ACT_IDX_MIL_CASE_DEF
    on ACT_CMMN_RU_MIL_INST (CASE_DEF_ID_);

create index ACT_IDX_MIL_CASE_INST
    on ACT_CMMN_RU_MIL_INST (CASE_INST_ID_);

create table ACT_CMMN_RU_PLAN_ITEM_INST
(
    ID_                     varchar(255)            not null
        primary key,
    REV_                    int                     not null,
    CASE_DEF_ID_            varchar(255)            null,
    CASE_INST_ID_           varchar(255)            null,
    STAGE_INST_ID_          varchar(255)            null,
    IS_STAGE_               bit                     null,
    ELEMENT_ID_             varchar(255)            null,
    NAME_                   varchar(255)            null,
    STATE_                  varchar(255)            null,
    CREATE_TIME_            datetime(3)             null,
    START_USER_ID_          varchar(255)            null,
    REFERENCE_ID_           varchar(255)            null,
    REFERENCE_TYPE_         varchar(255)            null,
    TENANT_ID_              varchar(255) default '' null,
    ITEM_DEFINITION_ID_     varchar(255)            null,
    ITEM_DEFINITION_TYPE_   varchar(255)            null,
    IS_COMPLETEABLE_        bit                     null,
    IS_COUNT_ENABLED_       bit                     null,
    VAR_COUNT_              int                     null,
    SENTRY_PART_INST_COUNT_ int                     null,
    LAST_AVAILABLE_TIME_    datetime(3)             null,
    LAST_ENABLED_TIME_      datetime(3)             null,
    LAST_DISABLED_TIME_     datetime(3)             null,
    LAST_STARTED_TIME_      datetime(3)             null,
    LAST_SUSPENDED_TIME_    datetime(3)             null,
    COMPLETED_TIME_         datetime(3)             null,
    OCCURRED_TIME_          datetime(3)             null,
    TERMINATED_TIME_        datetime(3)             null,
    EXIT_TIME_              datetime(3)             null,
    ENDED_TIME_             datetime(3)             null,
    ENTRY_CRITERION_ID_     varchar(255)            null,
    EXIT_CRITERION_ID_      varchar(255)            null,
    EXTRA_VALUE_            varchar(255)            null,
    DERIVED_CASE_DEF_ID_    varchar(255)            null,
    LAST_UNAVAILABLE_TIME_  datetime(3)             null,
    constraint ACT_FK_PLAN_ITEM_CASE_DEF
        foreign key (CASE_DEF_ID_) references ACT_CMMN_CASEDEF (ID_),
    constraint ACT_FK_PLAN_ITEM_CASE_INST
        foreign key (CASE_INST_ID_) references ACT_CMMN_RU_CASE_INST (ID_)
);

create index ACT_IDX_PLAN_ITEM_CASE_DEF
    on ACT_CMMN_RU_PLAN_ITEM_INST (CASE_DEF_ID_);

create index ACT_IDX_PLAN_ITEM_CASE_INST
    on ACT_CMMN_RU_PLAN_ITEM_INST (CASE_INST_ID_);

create index ACT_IDX_PLAN_ITEM_STAGE_INST
    on ACT_CMMN_RU_PLAN_ITEM_INST (STAGE_INST_ID_);

create table ACT_CMMN_RU_SENTRY_PART_INST
(
    ID_                varchar(255) not null
        primary key,
    REV_               int          not null,
    CASE_DEF_ID_       varchar(255) null,
    CASE_INST_ID_      varchar(255) null,
    PLAN_ITEM_INST_ID_ varchar(255) null,
    ON_PART_ID_        varchar(255) null,
    IF_PART_ID_        varchar(255) null,
    TIME_STAMP_        datetime(3)  null,
    constraint ACT_FK_SENTRY_CASE_DEF
        foreign key (CASE_DEF_ID_) references ACT_CMMN_CASEDEF (ID_),
    constraint ACT_FK_SENTRY_CASE_INST
        foreign key (CASE_INST_ID_) references ACT_CMMN_RU_CASE_INST (ID_),
    constraint ACT_FK_SENTRY_PLAN_ITEM
        foreign key (PLAN_ITEM_INST_ID_) references ACT_CMMN_RU_PLAN_ITEM_INST (ID_)
);

create index ACT_IDX_SENTRY_CASE_DEF
    on ACT_CMMN_RU_SENTRY_PART_INST (CASE_DEF_ID_);

create index ACT_IDX_SENTRY_CASE_INST
    on ACT_CMMN_RU_SENTRY_PART_INST (CASE_INST_ID_);

create index ACT_IDX_SENTRY_PLAN_ITEM
    on ACT_CMMN_RU_SENTRY_PART_INST (PLAN_ITEM_INST_ID_);

create table ACT_CO_CONTENT_ITEM
(
    ID_                 varchar(255)        not null
        primary key,
    NAME_               varchar(255)        not null,
    MIME_TYPE_          varchar(255)        null,
    TASK_ID_            varchar(255)        null,
    PROC_INST_ID_       varchar(255)        null,
    CONTENT_STORE_ID_   varchar(255)        null,
    CONTENT_STORE_NAME_ varchar(255)        null,
    FIELD_              varchar(400)        null,
    CONTENT_AVAILABLE_  bit    default b'0' null,
    CREATED_            timestamp(6)        null,
    CREATED_BY_         varchar(255)        null,
    LAST_MODIFIED_      timestamp(6)        null,
    LAST_MODIFIED_BY_   varchar(255)        null,
    CONTENT_SIZE_       bigint default 0    null,
    TENANT_ID_          varchar(255)        null,
    SCOPE_ID_           varchar(255)        null,
    SCOPE_TYPE_         varchar(255)        null
);

create index idx_contitem_procid
    on ACT_CO_CONTENT_ITEM (PROC_INST_ID_);

create index idx_contitem_scope
    on ACT_CO_CONTENT_ITEM (SCOPE_ID_, SCOPE_TYPE_);

create index idx_contitem_taskid
    on ACT_CO_CONTENT_ITEM (TASK_ID_);

create table ACT_CO_DATABASECHANGELOG
(
    ID            varchar(255) not null,
    AUTHOR        varchar(255) not null,
    FILENAME      varchar(255) not null,
    DATEEXECUTED  datetime     not null,
    ORDEREXECUTED int          not null,
    EXECTYPE      varchar(10)  not null,
    MD5SUM        varchar(35)  null,
    DESCRIPTION   varchar(255) null,
    COMMENTS      varchar(255) null,
    TAG           varchar(255) null,
    LIQUIBASE     varchar(20)  null,
    CONTEXTS      varchar(255) null,
    LABELS        varchar(255) null,
    DEPLOYMENT_ID varchar(10)  null
);

create table ACT_CO_DATABASECHANGELOGLOCK
(
    ID          int          not null
        primary key,
    LOCKED      bit          not null,
    LOCKGRANTED datetime     null,
    LOCKEDBY    varchar(255) null
);

create table ACT_DMN_DATABASECHANGELOG
(
    ID            varchar(255) not null,
    AUTHOR        varchar(255) not null,
    FILENAME      varchar(255) not null,
    DATEEXECUTED  datetime     not null,
    ORDEREXECUTED int          not null,
    EXECTYPE      varchar(10)  not null,
    MD5SUM        varchar(35)  null,
    DESCRIPTION   varchar(255) null,
    COMMENTS      varchar(255) null,
    TAG           varchar(255) null,
    LIQUIBASE     varchar(20)  null,
    CONTEXTS      varchar(255) null,
    LABELS        varchar(255) null,
    DEPLOYMENT_ID varchar(10)  null
);

create table ACT_DMN_DATABASECHANGELOGLOCK
(
    ID          int          not null
        primary key,
    LOCKED      bit          not null,
    LOCKGRANTED datetime     null,
    LOCKEDBY    varchar(255) null
);

create table ACT_DMN_DECISION
(
    ID_            varchar(255) not null
        primary key,
    NAME_          varchar(255) null,
    VERSION_       int          null,
    KEY_           varchar(255) null,
    CATEGORY_      varchar(255) null,
    DEPLOYMENT_ID_ varchar(255) null,
    TENANT_ID_     varchar(255) null,
    RESOURCE_NAME_ varchar(255) null,
    DESCRIPTION_   varchar(255) null,
    DECISION_TYPE_ varchar(255) null,
    constraint ACT_IDX_DMN_DEC_UNIQ
        unique (KEY_, VERSION_, TENANT_ID_)
);

create table ACT_DMN_DEPLOYMENT
(
    ID_                   varchar(255) not null
        primary key,
    NAME_                 varchar(255) null,
    CATEGORY_             varchar(255) null,
    DEPLOY_TIME_          datetime(3)  null,
    TENANT_ID_            varchar(255) null,
    PARENT_DEPLOYMENT_ID_ varchar(255) null
);

create table ACT_DMN_DEPLOYMENT_RESOURCE
(
    ID_             varchar(255) not null
        primary key,
    NAME_           varchar(255) null,
    DEPLOYMENT_ID_  varchar(255) null,
    RESOURCE_BYTES_ longblob     null
);

create table ACT_DMN_HI_DECISION_EXECUTION
(
    ID_                     varchar(255)     not null
        primary key,
    DECISION_DEFINITION_ID_ varchar(255)     null,
    DEPLOYMENT_ID_          varchar(255)     null,
    START_TIME_             datetime(3)      null,
    END_TIME_               datetime(3)      null,
    INSTANCE_ID_            varchar(255)     null,
    EXECUTION_ID_           varchar(255)     null,
    ACTIVITY_ID_            varchar(255)     null,
    FAILED_                 bit default b'0' null,
    TENANT_ID_              varchar(255)     null,
    EXECUTION_JSON_         longtext         null,
    SCOPE_TYPE_             varchar(255)     null
);

create table ACT_EVT_LOG
(
    LOG_NR_       bigint auto_increment
        primary key,
    TYPE_         varchar(64)                               null,
    PROC_DEF_ID_  varchar(64)                               null,
    PROC_INST_ID_ varchar(64)                               null,
    EXECUTION_ID_ varchar(64)                               null,
    TASK_ID_      varchar(64)                               null,
    TIME_STAMP_   timestamp(3) default CURRENT_TIMESTAMP(3) not null,
    USER_ID_      varchar(255)                              null,
    DATA_         longblob                                  null,
    LOCK_OWNER_   varchar(255)                              null,
    LOCK_TIME_    timestamp(3)                              null,
    IS_PROCESSED_ tinyint      default 0                    null
)
    collate = utf8mb3_bin;

create table ACT_FO_DATABASECHANGELOG
(
    ID            varchar(255) not null,
    AUTHOR        varchar(255) not null,
    FILENAME      varchar(255) not null,
    DATEEXECUTED  datetime     not null,
    ORDEREXECUTED int          not null,
    EXECTYPE      varchar(10)  not null,
    MD5SUM        varchar(35)  null,
    DESCRIPTION   varchar(255) null,
    COMMENTS      varchar(255) null,
    TAG           varchar(255) null,
    LIQUIBASE     varchar(20)  null,
    CONTEXTS      varchar(255) null,
    LABELS        varchar(255) null,
    DEPLOYMENT_ID varchar(10)  null
);

create table ACT_FO_DATABASECHANGELOGLOCK
(
    ID          int          not null
        primary key,
    LOCKED      bit          not null,
    LOCKGRANTED datetime     null,
    LOCKEDBY    varchar(255) null
);

create table ACT_FO_FORM_DEFINITION
(
    ID_            varchar(255) not null
        primary key,
    NAME_          varchar(255) null,
    VERSION_       int          null,
    KEY_           varchar(255) null,
    CATEGORY_      varchar(255) null,
    DEPLOYMENT_ID_ varchar(255) null,
    TENANT_ID_     varchar(255) null,
    RESOURCE_NAME_ varchar(255) null,
    DESCRIPTION_   varchar(255) null,
    constraint ACT_IDX_FORM_DEF_UNIQ
        unique (KEY_, VERSION_, TENANT_ID_)
);

create table ACT_FO_FORM_DEPLOYMENT
(
    ID_                   varchar(255) not null
        primary key,
    NAME_                 varchar(255) null,
    CATEGORY_             varchar(255) null,
    DEPLOY_TIME_          datetime(3)  null,
    TENANT_ID_            varchar(255) null,
    PARENT_DEPLOYMENT_ID_ varchar(255) null
);

create table ACT_FO_FORM_INSTANCE
(
    ID_                  varchar(255) not null
        primary key,
    FORM_DEFINITION_ID_  varchar(255) not null,
    TASK_ID_             varchar(255) null,
    PROC_INST_ID_        varchar(255) null,
    PROC_DEF_ID_         varchar(255) null,
    SUBMITTED_DATE_      datetime(3)  null,
    SUBMITTED_BY_        varchar(255) null,
    FORM_VALUES_ID_      varchar(255) null,
    TENANT_ID_           varchar(255) null,
    SCOPE_ID_            varchar(255) null,
    SCOPE_TYPE_          varchar(255) null,
    SCOPE_DEFINITION_ID_ varchar(255) null
);

create index ACT_IDX_FORM_PROC
    on ACT_FO_FORM_INSTANCE (PROC_INST_ID_);

create index ACT_IDX_FORM_SCOPE
    on ACT_FO_FORM_INSTANCE (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_FORM_TASK
    on ACT_FO_FORM_INSTANCE (TASK_ID_);

create table ACT_FO_FORM_RESOURCE
(
    ID_             varchar(255) not null
        primary key,
    NAME_           varchar(255) null,
    DEPLOYMENT_ID_  varchar(255) null,
    RESOURCE_BYTES_ longblob     null
);

create table ACT_GE_PROPERTY
(
    NAME_  varchar(64)  not null
        primary key,
    VALUE_ varchar(300) null,
    REV_   int          null
)
    collate = utf8mb3_bin;

create table ACT_HI_ACTINST
(
    ID_                varchar(64)             not null
        primary key,
    REV_               int          default 1  null,
    PROC_DEF_ID_       varchar(64)             not null,
    PROC_INST_ID_      varchar(64)             not null,
    EXECUTION_ID_      varchar(64)             not null,
    ACT_ID_            varchar(255)            not null,
    TASK_ID_           varchar(64)             null,
    CALL_PROC_INST_ID_ varchar(64)             null,
    ACT_NAME_          varchar(255)            null,
    ACT_TYPE_          varchar(255)            not null,
    ASSIGNEE_          varchar(255)            null,
    START_TIME_        datetime(3)             not null,
    END_TIME_          datetime(3)             null,
    TRANSACTION_ORDER_ int                     null,
    DURATION_          bigint                  null,
    DELETE_REASON_     varchar(4000)           null,
    TENANT_ID_         varchar(255) default '' null
)
    collate = utf8mb3_bin;

create index ACT_IDX_HI_ACT_INST_END
    on ACT_HI_ACTINST (END_TIME_);

create index ACT_IDX_HI_ACT_INST_EXEC
    on ACT_HI_ACTINST (EXECUTION_ID_, ACT_ID_);

create index ACT_IDX_HI_ACT_INST_PROCINST
    on ACT_HI_ACTINST (PROC_INST_ID_, ACT_ID_);

create index ACT_IDX_HI_ACT_INST_START
    on ACT_HI_ACTINST (START_TIME_);

create table ACT_HI_ATTACHMENT
(
    ID_           varchar(64)   not null
        primary key,
    REV_          int           null,
    USER_ID_      varchar(255)  null,
    NAME_         varchar(255)  null,
    DESCRIPTION_  varchar(4000) null,
    TYPE_         varchar(255)  null,
    TASK_ID_      varchar(64)   null,
    PROC_INST_ID_ varchar(64)   null,
    URL_          varchar(4000) null,
    CONTENT_ID_   varchar(64)   null,
    TIME_         datetime(3)   null
)
    collate = utf8mb3_bin;

create table ACT_HI_COMMENT
(
    ID_           varchar(64)   not null
        primary key,
    TYPE_         varchar(255)  null,
    TIME_         datetime(3)   not null,
    USER_ID_      varchar(255)  null,
    TASK_ID_      varchar(64)   null,
    PROC_INST_ID_ varchar(64)   null,
    ACTION_       varchar(255)  null,
    MESSAGE_      varchar(4000) null,
    FULL_MSG_     longblob      null
)
    collate = utf8mb3_bin;

create table ACT_HI_DETAIL
(
    ID_           varchar(64)   not null
        primary key,
    TYPE_         varchar(255)  not null,
    PROC_INST_ID_ varchar(64)   null,
    EXECUTION_ID_ varchar(64)   null,
    TASK_ID_      varchar(64)   null,
    ACT_INST_ID_  varchar(64)   null,
    NAME_         varchar(255)  not null,
    VAR_TYPE_     varchar(255)  null,
    REV_          int           null,
    TIME_         datetime(3)   not null,
    BYTEARRAY_ID_ varchar(64)   null,
    DOUBLE_       double        null,
    LONG_         bigint        null,
    TEXT_         varchar(4000) null,
    TEXT2_        varchar(4000) null
)
    collate = utf8mb3_bin;

create index ACT_IDX_HI_DETAIL_ACT_INST
    on ACT_HI_DETAIL (ACT_INST_ID_);

create index ACT_IDX_HI_DETAIL_NAME
    on ACT_HI_DETAIL (NAME_);

create index ACT_IDX_HI_DETAIL_PROC_INST
    on ACT_HI_DETAIL (PROC_INST_ID_);

create index ACT_IDX_HI_DETAIL_TASK_ID
    on ACT_HI_DETAIL (TASK_ID_);

create index ACT_IDX_HI_DETAIL_TIME
    on ACT_HI_DETAIL (TIME_);

create table ACT_HI_ENTITYLINK
(
    ID_                      varchar(64)  not null
        primary key,
    LINK_TYPE_               varchar(255) null,
    CREATE_TIME_             datetime(3)  null,
    SCOPE_ID_                varchar(255) null,
    SUB_SCOPE_ID_            varchar(255) null,
    SCOPE_TYPE_              varchar(255) null,
    SCOPE_DEFINITION_ID_     varchar(255) null,
    PARENT_ELEMENT_ID_       varchar(255) null,
    REF_SCOPE_ID_            varchar(255) null,
    REF_SCOPE_TYPE_          varchar(255) null,
    REF_SCOPE_DEFINITION_ID_ varchar(255) null,
    ROOT_SCOPE_ID_           varchar(255) null,
    ROOT_SCOPE_TYPE_         varchar(255) null,
    HIERARCHY_TYPE_          varchar(255) null
)
    collate = utf8mb3_bin;

create index ACT_IDX_HI_ENT_LNK_REF_SCOPE
    on ACT_HI_ENTITYLINK (REF_SCOPE_ID_, REF_SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_HI_ENT_LNK_ROOT_SCOPE
    on ACT_HI_ENTITYLINK (ROOT_SCOPE_ID_, ROOT_SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_HI_ENT_LNK_SCOPE
    on ACT_HI_ENTITYLINK (SCOPE_ID_, SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_HI_ENT_LNK_SCOPE_DEF
    on ACT_HI_ENTITYLINK (SCOPE_DEFINITION_ID_, SCOPE_TYPE_, LINK_TYPE_);

create table ACT_HI_IDENTITYLINK
(
    ID_                  varchar(64)  not null
        primary key,
    GROUP_ID_            varchar(255) null,
    TYPE_                varchar(255) null,
    USER_ID_             varchar(255) null,
    TASK_ID_             varchar(64)  null,
    CREATE_TIME_         datetime(3)  null,
    PROC_INST_ID_        varchar(64)  null,
    SCOPE_ID_            varchar(255) null,
    SUB_SCOPE_ID_        varchar(255) null,
    SCOPE_TYPE_          varchar(255) null,
    SCOPE_DEFINITION_ID_ varchar(255) null
)
    collate = utf8mb3_bin;

create index ACT_IDX_HI_IDENT_LNK_PROCINST
    on ACT_HI_IDENTITYLINK (PROC_INST_ID_);

create index ACT_IDX_HI_IDENT_LNK_SCOPE
    on ACT_HI_IDENTITYLINK (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_IDENT_LNK_SCOPE_DEF
    on ACT_HI_IDENTITYLINK (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_IDENT_LNK_SUB_SCOPE
    on ACT_HI_IDENTITYLINK (SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_IDENT_LNK_TASK
    on ACT_HI_IDENTITYLINK (TASK_ID_);

create index ACT_IDX_HI_IDENT_LNK_USER
    on ACT_HI_IDENTITYLINK (USER_ID_);

create table ACT_HI_PROCINST
(
    ID_                        varchar(64)             not null
        primary key,
    REV_                       int          default 1  null,
    PROC_INST_ID_              varchar(64)             not null,
    BUSINESS_KEY_              varchar(255)            null,
    PROC_DEF_ID_               varchar(64)             not null,
    START_TIME_                datetime(3)             not null,
    END_TIME_                  datetime(3)             null,
    DURATION_                  bigint                  null,
    START_USER_ID_             varchar(255)            null,
    START_ACT_ID_              varchar(255)            null,
    END_ACT_ID_                varchar(255)            null,
    SUPER_PROCESS_INSTANCE_ID_ varchar(64)             null,
    DELETE_REASON_             varchar(4000)           null,
    TENANT_ID_                 varchar(255) default '' null,
    NAME_                      varchar(255)            null,
    CALLBACK_ID_               varchar(255)            null,
    CALLBACK_TYPE_             varchar(255)            null,
    REFERENCE_ID_              varchar(255)            null,
    REFERENCE_TYPE_            varchar(255)            null,
    PROPAGATED_STAGE_INST_ID_  varchar(255)            null,
    BUSINESS_STATUS_           varchar(255)            null,
    constraint PROC_INST_ID_
        unique (PROC_INST_ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_HI_PRO_INST_END
    on ACT_HI_PROCINST (END_TIME_);

create index ACT_IDX_HI_PRO_I_BUSKEY
    on ACT_HI_PROCINST (BUSINESS_KEY_);

create table ACT_HI_TASKINST
(
    ID_                       varchar(64)             not null
        primary key,
    REV_                      int          default 1  null,
    PROC_DEF_ID_              varchar(64)             null,
    TASK_DEF_ID_              varchar(64)             null,
    TASK_DEF_KEY_             varchar(255)            null,
    PROC_INST_ID_             varchar(64)             null,
    EXECUTION_ID_             varchar(64)             null,
    SCOPE_ID_                 varchar(255)            null,
    SUB_SCOPE_ID_             varchar(255)            null,
    SCOPE_TYPE_               varchar(255)            null,
    SCOPE_DEFINITION_ID_      varchar(255)            null,
    PROPAGATED_STAGE_INST_ID_ varchar(255)            null,
    NAME_                     varchar(255)            null,
    PARENT_TASK_ID_           varchar(64)             null,
    DESCRIPTION_              varchar(4000)           null,
    OWNER_                    varchar(255)            null,
    ASSIGNEE_                 varchar(255)            null,
    START_TIME_               datetime(3)             not null,
    CLAIM_TIME_               datetime(3)             null,
    END_TIME_                 datetime(3)             null,
    DURATION_                 bigint                  null,
    DELETE_REASON_            varchar(4000)           null,
    PRIORITY_                 int                     null,
    DUE_DATE_                 datetime(3)             null,
    FORM_KEY_                 varchar(255)            null,
    CATEGORY_                 varchar(255)            null,
    TENANT_ID_                varchar(255) default '' null,
    LAST_UPDATED_TIME_        datetime(3)             null
)
    collate = utf8mb3_bin;

create index ACT_IDX_HI_TASK_INST_PROCINST
    on ACT_HI_TASKINST (PROC_INST_ID_);

create index ACT_IDX_HI_TASK_SCOPE
    on ACT_HI_TASKINST (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_TASK_SCOPE_DEF
    on ACT_HI_TASKINST (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_TASK_SUB_SCOPE
    on ACT_HI_TASKINST (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table ACT_HI_TSK_LOG
(
    ID_                  bigint auto_increment
        primary key,
    TYPE_                varchar(64)             null,
    TASK_ID_             varchar(64)             not null,
    TIME_STAMP_          timestamp(3)            not null,
    USER_ID_             varchar(255)            null,
    DATA_                varchar(4000)           null,
    EXECUTION_ID_        varchar(64)             null,
    PROC_INST_ID_        varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    SCOPE_ID_            varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    TENANT_ID_           varchar(255) default '' null
)
    collate = utf8mb3_bin;

create table ACT_HI_VARINST
(
    ID_                varchar(64)   not null
        primary key,
    REV_               int default 1 null,
    PROC_INST_ID_      varchar(64)   null,
    EXECUTION_ID_      varchar(64)   null,
    TASK_ID_           varchar(64)   null,
    NAME_              varchar(255)  not null,
    VAR_TYPE_          varchar(100)  null,
    SCOPE_ID_          varchar(255)  null,
    SUB_SCOPE_ID_      varchar(255)  null,
    SCOPE_TYPE_        varchar(255)  null,
    BYTEARRAY_ID_      varchar(64)   null,
    DOUBLE_            double        null,
    LONG_              bigint        null,
    TEXT_              varchar(4000) null,
    TEXT2_             varchar(4000) null,
    CREATE_TIME_       datetime(3)   null,
    LAST_UPDATED_TIME_ datetime(3)   null
)
    collate = utf8mb3_bin;

create index ACT_IDX_HI_PROCVAR_EXE
    on ACT_HI_VARINST (EXECUTION_ID_);

create index ACT_IDX_HI_PROCVAR_NAME_TYPE
    on ACT_HI_VARINST (NAME_, VAR_TYPE_);

create index ACT_IDX_HI_PROCVAR_PROC_INST
    on ACT_HI_VARINST (PROC_INST_ID_);

create index ACT_IDX_HI_PROCVAR_TASK_ID
    on ACT_HI_VARINST (TASK_ID_);

create index ACT_IDX_HI_VAR_SCOPE_ID_TYPE
    on ACT_HI_VARINST (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_VAR_SUB_ID_TYPE
    on ACT_HI_VARINST (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table ACT_ID_BYTEARRAY
(
    ID_    varchar(64)  not null
        primary key,
    REV_   int          null,
    NAME_  varchar(255) null,
    BYTES_ longblob     null
)
    collate = utf8mb3_bin;

create table ACT_ID_GROUP
(
    ID_   varchar(64)  not null
        primary key,
    REV_  int          null,
    NAME_ varchar(255) null,
    TYPE_ varchar(255) null
)
    collate = utf8mb3_bin;

create table ACT_ID_INFO
(
    ID_        varchar(64)  not null
        primary key,
    REV_       int          null,
    USER_ID_   varchar(64)  null,
    TYPE_      varchar(64)  null,
    KEY_       varchar(255) null,
    VALUE_     varchar(255) null,
    PASSWORD_  longblob     null,
    PARENT_ID_ varchar(255) null
)
    collate = utf8mb3_bin;

create table ACT_ID_PRIV
(
    ID_   varchar(64)  not null
        primary key,
    NAME_ varchar(255) not null,
    constraint ACT_UNIQ_PRIV_NAME
        unique (NAME_)
)
    collate = utf8mb3_bin;

create table ACT_ID_PRIV_MAPPING
(
    ID_       varchar(64)  not null
        primary key,
    PRIV_ID_  varchar(64)  not null,
    USER_ID_  varchar(255) null,
    GROUP_ID_ varchar(255) null,
    constraint ACT_FK_PRIV_MAPPING
        foreign key (PRIV_ID_) references ACT_ID_PRIV (ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_PRIV_GROUP
    on ACT_ID_PRIV_MAPPING (GROUP_ID_);

create index ACT_IDX_PRIV_USER
    on ACT_ID_PRIV_MAPPING (USER_ID_);

create table ACT_ID_PROPERTY
(
    NAME_  varchar(64)  not null
        primary key,
    VALUE_ varchar(300) null,
    REV_   int          null
)
    collate = utf8mb3_bin;

create table ACT_ID_TOKEN
(
    ID_          varchar(64)   not null
        primary key,
    REV_         int           null,
    TOKEN_VALUE_ varchar(255)  null,
    TOKEN_DATE_  timestamp(3)  null,
    IP_ADDRESS_  varchar(255)  null,
    USER_AGENT_  varchar(255)  null,
    USER_ID_     varchar(255)  null,
    TOKEN_DATA_  varchar(2000) null
)
    collate = utf8mb3_bin;

create table ACT_ID_USER
(
    ID_           varchar(64)             not null
        primary key,
    REV_          int                     null,
    FIRST_        varchar(255)            null,
    LAST_         varchar(255)            null,
    DISPLAY_NAME_ varchar(255)            null,
    EMAIL_        varchar(255)            null,
    PWD_          varchar(255)            null,
    PICTURE_ID_   varchar(64)             null,
    TENANT_ID_    varchar(255) default '' null
)
    collate = utf8mb3_bin;

create table ACT_ID_MEMBERSHIP
(
    USER_ID_  varchar(64) not null,
    GROUP_ID_ varchar(64) not null,
    primary key (USER_ID_, GROUP_ID_),
    constraint ACT_FK_MEMB_GROUP
        foreign key (GROUP_ID_) references ACT_ID_GROUP (ID_),
    constraint ACT_FK_MEMB_USER
        foreign key (USER_ID_) references ACT_ID_USER (ID_)
)
    collate = utf8mb3_bin;

create table ACT_RE_DEPLOYMENT
(
    ID_                   varchar(64)             not null
        primary key,
    NAME_                 varchar(255)            null,
    CATEGORY_             varchar(255)            null,
    KEY_                  varchar(255)            null,
    TENANT_ID_            varchar(255) default '' null,
    DEPLOY_TIME_          timestamp(3)            null,
    DERIVED_FROM_         varchar(64)             null,
    DERIVED_FROM_ROOT_    varchar(64)             null,
    PARENT_DEPLOYMENT_ID_ varchar(255)            null,
    ENGINE_VERSION_       varchar(255)            null
)
    collate = utf8mb3_bin;

create table ACT_GE_BYTEARRAY
(
    ID_            varchar(64)  not null
        primary key,
    REV_           int          null,
    NAME_          varchar(255) null,
    DEPLOYMENT_ID_ varchar(64)  null,
    BYTES_         longblob     null,
    GENERATED_     tinyint      null,
    constraint ACT_FK_BYTEARR_DEPL
        foreign key (DEPLOYMENT_ID_) references ACT_RE_DEPLOYMENT (ID_)
)
    collate = utf8mb3_bin;

create table ACT_RE_MODEL
(
    ID_                           varchar(64)             not null
        primary key,
    REV_                          int                     null,
    NAME_                         varchar(255)            null,
    KEY_                          varchar(255)            null,
    CATEGORY_                     varchar(255)            null,
    CREATE_TIME_                  timestamp(3)            null,
    LAST_UPDATE_TIME_             timestamp(3)            null,
    VERSION_                      int                     null,
    META_INFO_                    varchar(4000)           null,
    DEPLOYMENT_ID_                varchar(64)             null,
    EDITOR_SOURCE_VALUE_ID_       varchar(64)             null,
    EDITOR_SOURCE_EXTRA_VALUE_ID_ varchar(64)             null,
    TENANT_ID_                    varchar(255) default '' null,
    constraint ACT_FK_MODEL_DEPLOYMENT
        foreign key (DEPLOYMENT_ID_) references ACT_RE_DEPLOYMENT (ID_),
    constraint ACT_FK_MODEL_SOURCE
        foreign key (EDITOR_SOURCE_VALUE_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_MODEL_SOURCE_EXTRA
        foreign key (EDITOR_SOURCE_EXTRA_VALUE_ID_) references ACT_GE_BYTEARRAY (ID_)
)
    collate = utf8mb3_bin;

create table ACT_RE_PROCDEF
(
    ID_                     varchar(64)             not null
        primary key,
    REV_                    int                     null,
    CATEGORY_               varchar(255)            null,
    NAME_                   varchar(255)            null,
    KEY_                    varchar(255)            not null,
    VERSION_                int                     not null,
    DEPLOYMENT_ID_          varchar(64)             null,
    RESOURCE_NAME_          varchar(4000)           null,
    DGRM_RESOURCE_NAME_     varchar(4000)           null,
    DESCRIPTION_            varchar(4000)           null,
    HAS_START_FORM_KEY_     tinyint                 null,
    HAS_GRAPHICAL_NOTATION_ tinyint                 null,
    SUSPENSION_STATE_       int                     null,
    TENANT_ID_              varchar(255) default '' null,
    ENGINE_VERSION_         varchar(255)            null,
    DERIVED_FROM_           varchar(64)             null,
    DERIVED_FROM_ROOT_      varchar(64)             null,
    DERIVED_VERSION_        int          default 0  not null,
    constraint ACT_UNIQ_PROCDEF
        unique (KEY_, VERSION_, DERIVED_VERSION_, TENANT_ID_)
)
    collate = utf8mb3_bin;

create table ACT_PROCDEF_INFO
(
    ID_           varchar(64) not null
        primary key,
    PROC_DEF_ID_  varchar(64) not null,
    REV_          int         null,
    INFO_JSON_ID_ varchar(64) null,
    constraint ACT_UNIQ_INFO_PROCDEF
        unique (PROC_DEF_ID_),
    constraint ACT_FK_INFO_JSON_BA
        foreign key (INFO_JSON_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_INFO_PROCDEF
        foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_INFO_PROCDEF
    on ACT_PROCDEF_INFO (PROC_DEF_ID_);

create table ACT_RU_ACTINST
(
    ID_                varchar(64)             not null
        primary key,
    REV_               int          default 1  null,
    PROC_DEF_ID_       varchar(64)             not null,
    PROC_INST_ID_      varchar(64)             not null,
    EXECUTION_ID_      varchar(64)             not null,
    ACT_ID_            varchar(255)            not null,
    TASK_ID_           varchar(64)             null,
    CALL_PROC_INST_ID_ varchar(64)             null,
    ACT_NAME_          varchar(255)            null,
    ACT_TYPE_          varchar(255)            not null,
    ASSIGNEE_          varchar(255)            null,
    START_TIME_        datetime(3)             not null,
    END_TIME_          datetime(3)             null,
    DURATION_          bigint                  null,
    TRANSACTION_ORDER_ int                     null,
    DELETE_REASON_     varchar(4000)           null,
    TENANT_ID_         varchar(255) default '' null
)
    collate = utf8mb3_bin;

create index ACT_IDX_RU_ACTI_END
    on ACT_RU_ACTINST (END_TIME_);

create index ACT_IDX_RU_ACTI_EXEC
    on ACT_RU_ACTINST (EXECUTION_ID_);

create index ACT_IDX_RU_ACTI_EXEC_ACT
    on ACT_RU_ACTINST (EXECUTION_ID_, ACT_ID_);

create index ACT_IDX_RU_ACTI_PROC
    on ACT_RU_ACTINST (PROC_INST_ID_);

create index ACT_IDX_RU_ACTI_PROC_ACT
    on ACT_RU_ACTINST (PROC_INST_ID_, ACT_ID_);

create index ACT_IDX_RU_ACTI_START
    on ACT_RU_ACTINST (START_TIME_);

create index ACT_IDX_RU_ACTI_TASK
    on ACT_RU_ACTINST (TASK_ID_);

create table ACT_RU_ENTITYLINK
(
    ID_                      varchar(64)  not null
        primary key,
    REV_                     int          null,
    CREATE_TIME_             datetime(3)  null,
    LINK_TYPE_               varchar(255) null,
    SCOPE_ID_                varchar(255) null,
    SUB_SCOPE_ID_            varchar(255) null,
    SCOPE_TYPE_              varchar(255) null,
    SCOPE_DEFINITION_ID_     varchar(255) null,
    PARENT_ELEMENT_ID_       varchar(255) null,
    REF_SCOPE_ID_            varchar(255) null,
    REF_SCOPE_TYPE_          varchar(255) null,
    REF_SCOPE_DEFINITION_ID_ varchar(255) null,
    ROOT_SCOPE_ID_           varchar(255) null,
    ROOT_SCOPE_TYPE_         varchar(255) null,
    HIERARCHY_TYPE_          varchar(255) null
)
    collate = utf8mb3_bin;

create index ACT_IDX_ENT_LNK_REF_SCOPE
    on ACT_RU_ENTITYLINK (REF_SCOPE_ID_, REF_SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_ENT_LNK_ROOT_SCOPE
    on ACT_RU_ENTITYLINK (ROOT_SCOPE_ID_, ROOT_SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_ENT_LNK_SCOPE
    on ACT_RU_ENTITYLINK (SCOPE_ID_, SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_ENT_LNK_SCOPE_DEF
    on ACT_RU_ENTITYLINK (SCOPE_DEFINITION_ID_, SCOPE_TYPE_, LINK_TYPE_);

create table ACT_RU_EXECUTION
(
    ID_                        varchar(64)             not null
        primary key,
    REV_                       int                     null,
    PROC_INST_ID_              varchar(64)             null,
    BUSINESS_KEY_              varchar(255)            null,
    PARENT_ID_                 varchar(64)             null,
    PROC_DEF_ID_               varchar(64)             null,
    SUPER_EXEC_                varchar(64)             null,
    ROOT_PROC_INST_ID_         varchar(64)             null,
    ACT_ID_                    varchar(255)            null,
    IS_ACTIVE_                 tinyint                 null,
    IS_CONCURRENT_             tinyint                 null,
    IS_SCOPE_                  tinyint                 null,
    IS_EVENT_SCOPE_            tinyint                 null,
    IS_MI_ROOT_                tinyint                 null,
    SUSPENSION_STATE_          int                     null,
    CACHED_ENT_STATE_          int                     null,
    TENANT_ID_                 varchar(255) default '' null,
    NAME_                      varchar(255)            null,
    START_ACT_ID_              varchar(255)            null,
    START_TIME_                datetime(3)             null,
    START_USER_ID_             varchar(255)            null,
    LOCK_TIME_                 timestamp(3)            null,
    LOCK_OWNER_                varchar(255)            null,
    IS_COUNT_ENABLED_          tinyint                 null,
    EVT_SUBSCR_COUNT_          int                     null,
    TASK_COUNT_                int                     null,
    JOB_COUNT_                 int                     null,
    TIMER_JOB_COUNT_           int                     null,
    SUSP_JOB_COUNT_            int                     null,
    DEADLETTER_JOB_COUNT_      int                     null,
    EXTERNAL_WORKER_JOB_COUNT_ int                     null,
    VAR_COUNT_                 int                     null,
    ID_LINK_COUNT_             int                     null,
    CALLBACK_ID_               varchar(255)            null,
    CALLBACK_TYPE_             varchar(255)            null,
    REFERENCE_ID_              varchar(255)            null,
    REFERENCE_TYPE_            varchar(255)            null,
    PROPAGATED_STAGE_INST_ID_  varchar(255)            null,
    BUSINESS_STATUS_           varchar(255)            null,
    constraint ACT_FK_EXE_PARENT
        foreign key (PARENT_ID_) references ACT_RU_EXECUTION (ID_)
            on delete cascade,
    constraint ACT_FK_EXE_PROCDEF
        foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_),
    constraint ACT_FK_EXE_PROCINST
        foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_)
            on update cascade on delete cascade,
    constraint ACT_FK_EXE_SUPER
        foreign key (SUPER_EXEC_) references ACT_RU_EXECUTION (ID_)
            on delete cascade
)
    collate = utf8mb3_bin;

create table ACT_RU_DEADLETTER_JOB
(
    ID_                  varchar(64)             not null
        primary key,
    REV_                 int                     null,
    CATEGORY_            varchar(255)            null,
    TYPE_                varchar(255)            not null,
    EXCLUSIVE_           tinyint(1)              null,
    EXECUTION_ID_        varchar(64)             null,
    PROCESS_INSTANCE_ID_ varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    ELEMENT_ID_          varchar(255)            null,
    ELEMENT_NAME_        varchar(255)            null,
    SCOPE_ID_            varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    CORRELATION_ID_      varchar(255)            null,
    EXCEPTION_STACK_ID_  varchar(64)             null,
    EXCEPTION_MSG_       varchar(4000)           null,
    DUEDATE_             timestamp(3)            null,
    REPEAT_              varchar(255)            null,
    HANDLER_TYPE_        varchar(255)            null,
    HANDLER_CFG_         varchar(4000)           null,
    CUSTOM_VALUES_ID_    varchar(64)             null,
    CREATE_TIME_         timestamp(3)            null,
    TENANT_ID_           varchar(255) default '' null,
    constraint ACT_FK_DEADLETTER_JOB_CUSTOM_VALUES
        foreign key (CUSTOM_VALUES_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_DEADLETTER_JOB_EXCEPTION
        foreign key (EXCEPTION_STACK_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_DEADLETTER_JOB_EXECUTION
        foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE
        foreign key (PROCESS_INSTANCE_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_DEADLETTER_JOB_PROC_DEF
        foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_DEADLETTER_JOB_CORRELATION_ID
    on ACT_RU_DEADLETTER_JOB (CORRELATION_ID_);

create index ACT_IDX_DEADLETTER_JOB_CUSTOM_VALUES_ID
    on ACT_RU_DEADLETTER_JOB (CUSTOM_VALUES_ID_);

create index ACT_IDX_DEADLETTER_JOB_EXCEPTION_STACK_ID
    on ACT_RU_DEADLETTER_JOB (EXCEPTION_STACK_ID_);

create index ACT_IDX_DJOB_SCOPE
    on ACT_RU_DEADLETTER_JOB (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_DJOB_SCOPE_DEF
    on ACT_RU_DEADLETTER_JOB (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_DJOB_SUB_SCOPE
    on ACT_RU_DEADLETTER_JOB (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table ACT_RU_EVENT_SUBSCR
(
    ID_                  varchar(64)                               not null
        primary key,
    REV_                 int                                       null,
    EVENT_TYPE_          varchar(255)                              not null,
    EVENT_NAME_          varchar(255)                              null,
    EXECUTION_ID_        varchar(64)                               null,
    PROC_INST_ID_        varchar(64)                               null,
    ACTIVITY_ID_         varchar(64)                               null,
    CONFIGURATION_       varchar(255)                              null,
    CREATED_             timestamp(3) default CURRENT_TIMESTAMP(3) not null,
    PROC_DEF_ID_         varchar(64)                               null,
    SUB_SCOPE_ID_        varchar(64)                               null,
    SCOPE_ID_            varchar(64)                               null,
    SCOPE_DEFINITION_ID_ varchar(64)                               null,
    SCOPE_TYPE_          varchar(64)                               null,
    TENANT_ID_           varchar(255) default ''                   null,
    constraint ACT_FK_EVENT_EXEC
        foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_EVENT_SUBSCR_CONFIG_
    on ACT_RU_EVENT_SUBSCR (CONFIGURATION_);

create index ACT_IDC_EXEC_ROOT
    on ACT_RU_EXECUTION (ROOT_PROC_INST_ID_);

create index ACT_IDX_EXEC_BUSKEY
    on ACT_RU_EXECUTION (BUSINESS_KEY_);

create index ACT_IDX_EXEC_REF_ID_
    on ACT_RU_EXECUTION (REFERENCE_ID_);

create table ACT_RU_EXTERNAL_JOB
(
    ID_                  varchar(64)             not null
        primary key,
    REV_                 int                     null,
    CATEGORY_            varchar(255)            null,
    TYPE_                varchar(255)            not null,
    LOCK_EXP_TIME_       timestamp(3)            null,
    LOCK_OWNER_          varchar(255)            null,
    EXCLUSIVE_           tinyint(1)              null,
    EXECUTION_ID_        varchar(64)             null,
    PROCESS_INSTANCE_ID_ varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    ELEMENT_ID_          varchar(255)            null,
    ELEMENT_NAME_        varchar(255)            null,
    SCOPE_ID_            varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    CORRELATION_ID_      varchar(255)            null,
    RETRIES_             int                     null,
    EXCEPTION_STACK_ID_  varchar(64)             null,
    EXCEPTION_MSG_       varchar(4000)           null,
    DUEDATE_             timestamp(3)            null,
    REPEAT_              varchar(255)            null,
    HANDLER_TYPE_        varchar(255)            null,
    HANDLER_CFG_         varchar(4000)           null,
    CUSTOM_VALUES_ID_    varchar(64)             null,
    CREATE_TIME_         timestamp(3)            null,
    TENANT_ID_           varchar(255) default '' null,
    constraint ACT_FK_EXTERNAL_JOB_CUSTOM_VALUES
        foreign key (CUSTOM_VALUES_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_EXTERNAL_JOB_EXCEPTION
        foreign key (EXCEPTION_STACK_ID_) references ACT_GE_BYTEARRAY (ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_EJOB_SCOPE
    on ACT_RU_EXTERNAL_JOB (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_EJOB_SCOPE_DEF
    on ACT_RU_EXTERNAL_JOB (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_EJOB_SUB_SCOPE
    on ACT_RU_EXTERNAL_JOB (SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_EXTERNAL_JOB_CORRELATION_ID
    on ACT_RU_EXTERNAL_JOB (CORRELATION_ID_);

create index ACT_IDX_EXTERNAL_JOB_CUSTOM_VALUES_ID
    on ACT_RU_EXTERNAL_JOB (CUSTOM_VALUES_ID_);

create index ACT_IDX_EXTERNAL_JOB_EXCEPTION_STACK_ID
    on ACT_RU_EXTERNAL_JOB (EXCEPTION_STACK_ID_);

create table ACT_RU_HISTORY_JOB
(
    ID_                 varchar(64)             not null
        primary key,
    REV_                int                     null,
    LOCK_EXP_TIME_      timestamp(3)            null,
    LOCK_OWNER_         varchar(255)            null,
    RETRIES_            int                     null,
    EXCEPTION_STACK_ID_ varchar(64)             null,
    EXCEPTION_MSG_      varchar(4000)           null,
    HANDLER_TYPE_       varchar(255)            null,
    HANDLER_CFG_        varchar(4000)           null,
    CUSTOM_VALUES_ID_   varchar(64)             null,
    ADV_HANDLER_CFG_ID_ varchar(64)             null,
    CREATE_TIME_        timestamp(3)            null,
    SCOPE_TYPE_         varchar(255)            null,
    TENANT_ID_          varchar(255) default '' null
)
    collate = utf8mb3_bin;

create table ACT_RU_JOB
(
    ID_                  varchar(64)             not null
        primary key,
    REV_                 int                     null,
    CATEGORY_            varchar(255)            null,
    TYPE_                varchar(255)            not null,
    LOCK_EXP_TIME_       timestamp(3)            null,
    LOCK_OWNER_          varchar(255)            null,
    EXCLUSIVE_           tinyint(1)              null,
    EXECUTION_ID_        varchar(64)             null,
    PROCESS_INSTANCE_ID_ varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    ELEMENT_ID_          varchar(255)            null,
    ELEMENT_NAME_        varchar(255)            null,
    SCOPE_ID_            varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    CORRELATION_ID_      varchar(255)            null,
    RETRIES_             int                     null,
    EXCEPTION_STACK_ID_  varchar(64)             null,
    EXCEPTION_MSG_       varchar(4000)           null,
    DUEDATE_             timestamp(3)            null,
    REPEAT_              varchar(255)            null,
    HANDLER_TYPE_        varchar(255)            null,
    HANDLER_CFG_         varchar(4000)           null,
    CUSTOM_VALUES_ID_    varchar(64)             null,
    CREATE_TIME_         timestamp(3)            null,
    TENANT_ID_           varchar(255) default '' null,
    constraint ACT_FK_JOB_CUSTOM_VALUES
        foreign key (CUSTOM_VALUES_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_JOB_EXCEPTION
        foreign key (EXCEPTION_STACK_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_JOB_EXECUTION
        foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_JOB_PROCESS_INSTANCE
        foreign key (PROCESS_INSTANCE_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_JOB_PROC_DEF
        foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_JOB_CORRELATION_ID
    on ACT_RU_JOB (CORRELATION_ID_);

create index ACT_IDX_JOB_CUSTOM_VALUES_ID
    on ACT_RU_JOB (CUSTOM_VALUES_ID_);

create index ACT_IDX_JOB_EXCEPTION_STACK_ID
    on ACT_RU_JOB (EXCEPTION_STACK_ID_);

create index ACT_IDX_JOB_SCOPE
    on ACT_RU_JOB (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_JOB_SCOPE_DEF
    on ACT_RU_JOB (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_JOB_SUB_SCOPE
    on ACT_RU_JOB (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table ACT_RU_SUSPENDED_JOB
(
    ID_                  varchar(64)             not null
        primary key,
    REV_                 int                     null,
    CATEGORY_            varchar(255)            null,
    TYPE_                varchar(255)            not null,
    EXCLUSIVE_           tinyint(1)              null,
    EXECUTION_ID_        varchar(64)             null,
    PROCESS_INSTANCE_ID_ varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    ELEMENT_ID_          varchar(255)            null,
    ELEMENT_NAME_        varchar(255)            null,
    SCOPE_ID_            varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    CORRELATION_ID_      varchar(255)            null,
    RETRIES_             int                     null,
    EXCEPTION_STACK_ID_  varchar(64)             null,
    EXCEPTION_MSG_       varchar(4000)           null,
    DUEDATE_             timestamp(3)            null,
    REPEAT_              varchar(255)            null,
    HANDLER_TYPE_        varchar(255)            null,
    HANDLER_CFG_         varchar(4000)           null,
    CUSTOM_VALUES_ID_    varchar(64)             null,
    CREATE_TIME_         timestamp(3)            null,
    TENANT_ID_           varchar(255) default '' null,
    constraint ACT_FK_SUSPENDED_JOB_CUSTOM_VALUES
        foreign key (CUSTOM_VALUES_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_SUSPENDED_JOB_EXCEPTION
        foreign key (EXCEPTION_STACK_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_SUSPENDED_JOB_EXECUTION
        foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE
        foreign key (PROCESS_INSTANCE_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_SUSPENDED_JOB_PROC_DEF
        foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_SJOB_SCOPE
    on ACT_RU_SUSPENDED_JOB (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_SJOB_SCOPE_DEF
    on ACT_RU_SUSPENDED_JOB (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_SJOB_SUB_SCOPE
    on ACT_RU_SUSPENDED_JOB (SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_SUSPENDED_JOB_CORRELATION_ID
    on ACT_RU_SUSPENDED_JOB (CORRELATION_ID_);

create index ACT_IDX_SUSPENDED_JOB_CUSTOM_VALUES_ID
    on ACT_RU_SUSPENDED_JOB (CUSTOM_VALUES_ID_);

create index ACT_IDX_SUSPENDED_JOB_EXCEPTION_STACK_ID
    on ACT_RU_SUSPENDED_JOB (EXCEPTION_STACK_ID_);

create table ACT_RU_TASK
(
    ID_                       varchar(64)             not null
        primary key,
    REV_                      int                     null,
    EXECUTION_ID_             varchar(64)             null,
    PROC_INST_ID_             varchar(64)             null,
    PROC_DEF_ID_              varchar(64)             null,
    TASK_DEF_ID_              varchar(64)             null,
    SCOPE_ID_                 varchar(255)            null,
    SUB_SCOPE_ID_             varchar(255)            null,
    SCOPE_TYPE_               varchar(255)            null,
    SCOPE_DEFINITION_ID_      varchar(255)            null,
    PROPAGATED_STAGE_INST_ID_ varchar(255)            null,
    NAME_                     varchar(255)            null,
    PARENT_TASK_ID_           varchar(64)             null,
    DESCRIPTION_              varchar(4000)           null,
    TASK_DEF_KEY_             varchar(255)            null,
    OWNER_                    varchar(255)            null,
    ASSIGNEE_                 varchar(255)            null,
    DELEGATION_               varchar(64)             null,
    PRIORITY_                 int                     null,
    CREATE_TIME_              timestamp(3)            null,
    DUE_DATE_                 datetime(3)             null,
    CATEGORY_                 varchar(255)            null,
    SUSPENSION_STATE_         int                     null,
    TENANT_ID_                varchar(255) default '' null,
    FORM_KEY_                 varchar(255)            null,
    CLAIM_TIME_               datetime(3)             null,
    IS_COUNT_ENABLED_         tinyint                 null,
    VAR_COUNT_                int                     null,
    ID_LINK_COUNT_            int                     null,
    SUB_TASK_COUNT_           int                     null,
    constraint ACT_FK_TASK_EXE
        foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_TASK_PROCDEF
        foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_),
    constraint ACT_FK_TASK_PROCINST
        foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_)
)
    collate = utf8mb3_bin;

create table ACT_RU_IDENTITYLINK
(
    ID_                  varchar(64)  not null
        primary key,
    REV_                 int          null,
    GROUP_ID_            varchar(255) null,
    TYPE_                varchar(255) null,
    USER_ID_             varchar(255) null,
    TASK_ID_             varchar(64)  null,
    PROC_INST_ID_        varchar(64)  null,
    PROC_DEF_ID_         varchar(64)  null,
    SCOPE_ID_            varchar(255) null,
    SUB_SCOPE_ID_        varchar(255) null,
    SCOPE_TYPE_          varchar(255) null,
    SCOPE_DEFINITION_ID_ varchar(255) null,
    constraint ACT_FK_ATHRZ_PROCEDEF
        foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_),
    constraint ACT_FK_IDL_PROCINST
        foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_TSKASS_TASK
        foreign key (TASK_ID_) references ACT_RU_TASK (ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_ATHRZ_PROCEDEF
    on ACT_RU_IDENTITYLINK (PROC_DEF_ID_);

create index ACT_IDX_IDENT_LNK_GROUP
    on ACT_RU_IDENTITYLINK (GROUP_ID_);

create index ACT_IDX_IDENT_LNK_SCOPE
    on ACT_RU_IDENTITYLINK (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_IDENT_LNK_SCOPE_DEF
    on ACT_RU_IDENTITYLINK (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_IDENT_LNK_SUB_SCOPE
    on ACT_RU_IDENTITYLINK (SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_IDENT_LNK_USER
    on ACT_RU_IDENTITYLINK (USER_ID_);

create index ACT_IDX_TASK_CREATE
    on ACT_RU_TASK (CREATE_TIME_);

create index ACT_IDX_TASK_SCOPE
    on ACT_RU_TASK (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_TASK_SCOPE_DEF
    on ACT_RU_TASK (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_TASK_SUB_SCOPE
    on ACT_RU_TASK (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table ACT_RU_TIMER_JOB
(
    ID_                  varchar(64)             not null
        primary key,
    REV_                 int                     null,
    CATEGORY_            varchar(255)            null,
    TYPE_                varchar(255)            not null,
    LOCK_EXP_TIME_       timestamp(3)            null,
    LOCK_OWNER_          varchar(255)            null,
    EXCLUSIVE_           tinyint(1)              null,
    EXECUTION_ID_        varchar(64)             null,
    PROCESS_INSTANCE_ID_ varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    ELEMENT_ID_          varchar(255)            null,
    ELEMENT_NAME_        varchar(255)            null,
    SCOPE_ID_            varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    CORRELATION_ID_      varchar(255)            null,
    RETRIES_             int                     null,
    EXCEPTION_STACK_ID_  varchar(64)             null,
    EXCEPTION_MSG_       varchar(4000)           null,
    DUEDATE_             timestamp(3)            null,
    REPEAT_              varchar(255)            null,
    HANDLER_TYPE_        varchar(255)            null,
    HANDLER_CFG_         varchar(4000)           null,
    CUSTOM_VALUES_ID_    varchar(64)             null,
    CREATE_TIME_         timestamp(3)            null,
    TENANT_ID_           varchar(255) default '' null,
    constraint ACT_FK_TIMER_JOB_CUSTOM_VALUES
        foreign key (CUSTOM_VALUES_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_TIMER_JOB_EXCEPTION
        foreign key (EXCEPTION_STACK_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_TIMER_JOB_EXECUTION
        foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_TIMER_JOB_PROCESS_INSTANCE
        foreign key (PROCESS_INSTANCE_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_TIMER_JOB_PROC_DEF
        foreign key (PROC_DEF_ID_) references ACT_RE_PROCDEF (ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_TIMER_JOB_CORRELATION_ID
    on ACT_RU_TIMER_JOB (CORRELATION_ID_);

create index ACT_IDX_TIMER_JOB_CUSTOM_VALUES_ID
    on ACT_RU_TIMER_JOB (CUSTOM_VALUES_ID_);

create index ACT_IDX_TIMER_JOB_DUEDATE
    on ACT_RU_TIMER_JOB (DUEDATE_);

create index ACT_IDX_TIMER_JOB_EXCEPTION_STACK_ID
    on ACT_RU_TIMER_JOB (EXCEPTION_STACK_ID_);

create index ACT_IDX_TJOB_SCOPE
    on ACT_RU_TIMER_JOB (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_TJOB_SCOPE_DEF
    on ACT_RU_TIMER_JOB (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_TJOB_SUB_SCOPE
    on ACT_RU_TIMER_JOB (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table ACT_RU_VARIABLE
(
    ID_           varchar(64)   not null
        primary key,
    REV_          int           null,
    TYPE_         varchar(255)  not null,
    NAME_         varchar(255)  not null,
    EXECUTION_ID_ varchar(64)   null,
    PROC_INST_ID_ varchar(64)   null,
    TASK_ID_      varchar(64)   null,
    SCOPE_ID_     varchar(255)  null,
    SUB_SCOPE_ID_ varchar(255)  null,
    SCOPE_TYPE_   varchar(255)  null,
    BYTEARRAY_ID_ varchar(64)   null,
    DOUBLE_       double        null,
    LONG_         bigint        null,
    TEXT_         varchar(4000) null,
    TEXT2_        varchar(4000) null,
    constraint ACT_FK_VAR_BYTEARRAY
        foreign key (BYTEARRAY_ID_) references ACT_GE_BYTEARRAY (ID_),
    constraint ACT_FK_VAR_EXE
        foreign key (EXECUTION_ID_) references ACT_RU_EXECUTION (ID_),
    constraint ACT_FK_VAR_PROCINST
        foreign key (PROC_INST_ID_) references ACT_RU_EXECUTION (ID_)
)
    collate = utf8mb3_bin;

create index ACT_IDX_RU_VAR_SCOPE_ID_TYPE
    on ACT_RU_VARIABLE (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_RU_VAR_SUB_ID_TYPE
    on ACT_RU_VARIABLE (SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_VARIABLE_TASK_ID
    on ACT_RU_VARIABLE (TASK_ID_);

create table FLW_CHANNEL_DEFINITION
(
    ID_             varchar(255) not null
        primary key,
    NAME_           varchar(255) null,
    VERSION_        int          null,
    KEY_            varchar(255) null,
    CATEGORY_       varchar(255) null,
    DEPLOYMENT_ID_  varchar(255) null,
    CREATE_TIME_    datetime(3)  null,
    TENANT_ID_      varchar(255) null,
    RESOURCE_NAME_  varchar(255) null,
    DESCRIPTION_    varchar(255) null,
    TYPE_           varchar(255) null,
    IMPLEMENTATION_ varchar(255) null,
    constraint ACT_IDX_CHANNEL_DEF_UNIQ
        unique (KEY_, VERSION_, TENANT_ID_)
);

create table FLW_EVENT_DEFINITION
(
    ID_            varchar(255) not null
        primary key,
    NAME_          varchar(255) null,
    VERSION_       int          null,
    KEY_           varchar(255) null,
    CATEGORY_      varchar(255) null,
    DEPLOYMENT_ID_ varchar(255) null,
    TENANT_ID_     varchar(255) null,
    RESOURCE_NAME_ varchar(255) null,
    DESCRIPTION_   varchar(255) null,
    constraint ACT_IDX_EVENT_DEF_UNIQ
        unique (KEY_, VERSION_, TENANT_ID_)
);

create table FLW_EVENT_DEPLOYMENT
(
    ID_                   varchar(255) not null
        primary key,
    NAME_                 varchar(255) null,
    CATEGORY_             varchar(255) null,
    DEPLOY_TIME_          datetime(3)  null,
    TENANT_ID_            varchar(255) null,
    PARENT_DEPLOYMENT_ID_ varchar(255) null
);

create table FLW_EVENT_RESOURCE
(
    ID_             varchar(255) not null
        primary key,
    NAME_           varchar(255) null,
    DEPLOYMENT_ID_  varchar(255) null,
    RESOURCE_BYTES_ longblob     null
);

create table FLW_EV_DATABASECHANGELOG
(
    ID            varchar(255) not null,
    AUTHOR        varchar(255) not null,
    FILENAME      varchar(255) not null,
    DATEEXECUTED  datetime     not null,
    ORDEREXECUTED int          not null,
    EXECTYPE      varchar(10)  not null,
    MD5SUM        varchar(35)  null,
    DESCRIPTION   varchar(255) null,
    COMMENTS      varchar(255) null,
    TAG           varchar(255) null,
    LIQUIBASE     varchar(20)  null,
    CONTEXTS      varchar(255) null,
    LABELS        varchar(255) null,
    DEPLOYMENT_ID varchar(10)  null
);

create table FLW_EV_DATABASECHANGELOGLOCK
(
    ID          int          not null
        primary key,
    LOCKED      bit          not null,
    LOCKGRANTED datetime     null,
    LOCKEDBY    varchar(255) null
);

create table FLW_RU_BATCH
(
    ID_            varchar(64)             not null
        primary key,
    REV_           int                     null,
    TYPE_          varchar(64)             not null,
    SEARCH_KEY_    varchar(255)            null,
    SEARCH_KEY2_   varchar(255)            null,
    CREATE_TIME_   datetime(3)             not null,
    COMPLETE_TIME_ datetime(3)             null,
    STATUS_        varchar(255)            null,
    BATCH_DOC_ID_  varchar(64)             null,
    TENANT_ID_     varchar(255) default '' null
)
    collate = utf8mb3_bin;

create table FLW_RU_BATCH_PART
(
    ID_            varchar(64)             not null
        primary key,
    REV_           int                     null,
    BATCH_ID_      varchar(64)             null,
    TYPE_          varchar(64)             not null,
    SCOPE_ID_      varchar(64)             null,
    SUB_SCOPE_ID_  varchar(64)             null,
    SCOPE_TYPE_    varchar(64)             null,
    SEARCH_KEY_    varchar(255)            null,
    SEARCH_KEY2_   varchar(255)            null,
    CREATE_TIME_   datetime(3)             not null,
    COMPLETE_TIME_ datetime(3)             null,
    STATUS_        varchar(255)            null,
    RESULT_DOC_ID_ varchar(64)             null,
    TENANT_ID_     varchar(255) default '' null,
    constraint FLW_FK_BATCH_PART_PARENT
        foreign key (BATCH_ID_) references FLW_RU_BATCH (ID_)
)
    collate = utf8mb3_bin;

create index FLW_IDX_BATCH_PART
    on FLW_RU_BATCH_PART (BATCH_ID_);

create table t_snow_flake_worker_id
(
    id       int auto_increment
        primary key,
    next_id  int         not null,
    app_name varchar(50) not null,
    constraint t_snow_flake_worker_id_app_name_uindex
        unique (app_name)
);

