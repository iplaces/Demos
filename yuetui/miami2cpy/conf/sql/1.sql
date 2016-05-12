CREATE TABLE goods (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id      BIGINT NOT NULL,
  cat_id        BIGINT NOT NULL DEFAULT 0,
  name          VARCHAR(63)  NOT NULL,
  price         INT       NOT NULL
  COMMENT '价格，单位为分',
  sale_price    INT  NOT NULL DEFAULT 0,
  description   VARCHAR(255) NOT NULL DEFAULT '',
  icon          VARCHAR(255) NOT NULL DEFAULT '',
  stock         INT NOT NULL DEFAULT 0,
  sales         INT NOT NULL DEFAULT 0,
  state         INT NOT NULL DEFAULT 0
    COMMENT '状态， 0为上架，1为下架, 2为删除',
  create_time   BIGINT NOT NULL
)
  ENGINE = InnoDB, DEFAULT CHARSET = utf8, AUTO_INCREMENT = 1000001;

CREATE TABLE categories (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(63)  NOT NULL,
  icon          VARCHAR(255) NOT NULL DEFAULT '',
  store_id      BIGINT NOT NULL DEFAULT 0
    COMMENT '系统分类，admin的固定uid;侧边栏，store的uid',
  rank          INT NOT NULL DEFAULT 0
)
  ENGINE = InnoDB, DEFAULT CHARSET = utf8, AUTO_INCREMENT = 2000001;

CREATE Table stores (
  id            BIGINT PRIMARY KEY,
  name          VARCHAR(63)  NOT NULL,
  description   VARCHAR(255) NOT NULL DEFAULT '',
  contact       VARCHAR(255) NOT NULL DEFAULT '',
  address       VARCHAR(255) NOT NULL DEFAULT '',
  icon          VARCHAR(255) NOT NULL DEFAULT '',
  open_from     BIGINT NOT NULL DEFAULT 0
  COMMENT '一天内第几分钟开始营业，0-24*60',
  open_to       BIGINT NOT NULL DEFAULT 1440
  COMMENT  '一天内第几分钟结束营业',
  base_price    INT       NOT NULL DEFAULT 0
  COMMENT '起送价，单位为分',
  pack_fee      INT      NOT NULL DEFAULT 0
  COMMENT '配送费，单位为分',
  cat_id        BIGINT NOT NULL DEFAULT 0,
  sales         INT NOT NULL DEFAULT 0,
  comments      INT NOT NULL DEFAULT 0,
  grades        FLOAT NOT NULL DEFAULT 0,
  cost_time    INT    NOT NULL DEFAULT 60
    COMMENT  '运输时间，单位为分钟',
  state         INT NOT NULL DEFAULT 0
    COMMENT  '状态，-1为超级管理员禁用，-2为删除状态，0为待业状态，1为开店状态',
  create_time   BIGINT NOT NULL,
  modified_time BIGINT NOT NULL
)
  ENGINE = InnoDB, DEFAULT CHARSET = utf8;

CREATE Table orders (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id      BIGINT NOT NULL,
  customer_id   BIGINT NOT NULL,
  recipient     VARCHAR(63)  NOT NULL
    COMMENT  '收件人',
  address       VARCHAR(255) NOT NULL DEFAULT '',
  contact       VARCHAR(255) NOT NULL DEFAULT ''
    COMMENT '联系电话',
  remark        VARCHAR(255) NOT NULL DEFAULT ''
    COMMENT  '订单备注',
  pack_fee      INT      NOT NULL
    COMMENT '配送费，单位为分',
  total_fee     INT  NOT NULL
    COMMENT '订单总价，单位为分',
  pay_status    INT NOT NULL
    COMMENT '付款方式',
  state         INT  NOT NULL DEFAULT 0
    COMMENT '订单状态，0为未付款，1为支付成功、等待接单，2为等待发货，3商家确认送达，4订单成功，5成功并已评价，6为支付失败，
    7为订单撤销，8为商家拒绝接单，9为客户申请退款，999其他。5、4为订单成功状态。',
  trade_no      VARCHAR(255) NOT NULL DEFAULT 0,
  arrive_time   BIGINT NOT NULL DEFAULT 0
   COMMENT '预期送达时间',
  create_time   BIGINT NOT NULL
)
  ENGINE = InnoDB, DEFAULT CHARSET = utf8, AUTO_INCREMENT = 4000001;

CREATE TABLE  order_goods (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id      BIGINT NOT NULL,
  good_id       BIGINT NOT NULL,
  num           INT NOT NULL
)
  ENGINE = InnoDB, DEFAULT CHARSET = utf8;

CREATE TABLE  comments(
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id      BIGINT NOT NULL,
  trans_time    INT NOT NULL DEFAULT 0 NULL COMMENT '送餐时间',
  dish_grade    INT NOT NULL COMMENT '菜品评分打分',
  store_id      BIGINT NOT NULL,
  create_time   BIGINT NOT NULL
)
  ENGINE = InnoDB, DEFAULT CHARSET = utf8;

CREATE TABLE  refunds(
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id      BIGINT NOT NULL,
  amount        INT NOT NULL COMMENT '退款金额',
  customer_desp    VARCHAR(255) NOT NULL DEFAULT '' COMMENT '客户申请退款原因',
  store_desp       VARCHAR(255) NOT NULL DEFAULT '' COMMENT '商家拒绝退款原因',
  state       INT NOT NULL DEFAULT 0
  COMMENT '退款状态，0为客户申请退款，1为商家同意，退款处理中，2为商家拒绝退款，3为退款完成',
  timestamp   BIGINT NOT NULL
)
  ENGINE = InnoDB, DEFAULT CHARSET = utf8;


CREATE TABLE  secure_key(
  appid          VARCHAR(255) NOT NULL,
  secure_key     VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'secureKey'
)
  ENGINE = InnoDB, DEFAULT CHARSET = utf8;

