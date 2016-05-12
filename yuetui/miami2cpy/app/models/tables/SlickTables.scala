package models.tables

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object SlickTables extends {
  val profile = slick.driver.MySQLDriver
} with SlickTables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait SlickTables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(tCategories.schema, tComments.schema, tGoods.schema, tOrderGoods.schema, tOrders.schema, tRefunds.schema, tSecureKey.schema, tStores.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tCategories
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(63,true)
   *  @param icon Database column icon SqlType(VARCHAR), Length(255,true), Default()
   *  @param storeId Database column store_id SqlType(BIGINT), Default(0)
   *  @param rank Database column rank SqlType(INT), Default(0) */
  case class rCategories(id: Long, name: String, icon: String = "", storeId: Long = 0L, rank: Int = 0)
  /** GetResult implicit for fetching rCategories objects using plain SQL queries */
  implicit def GetResultrCategories(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rCategories] = GR{
    prs => import prs._
    rCategories.tupled((<<[Long], <<[String], <<[String], <<[Long], <<[Int]))
  }
  /** Table description of table categories. Objects of this class serve as prototypes for rows in queries. */
  class tCategories(_tableTag: Tag) extends Table[rCategories](_tableTag, "categories") {
    def * = (id, name, icon, storeId, rank) <> (rCategories.tupled, rCategories.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(icon), Rep.Some(storeId), Rep.Some(rank)).shaped.<>({r=>import r._; _1.map(_=> rCategories.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(63,true) */
    val name: Rep[String] = column[String]("name", O.Length(63,varying=true))
    /** Database column icon SqlType(VARCHAR), Length(255,true), Default() */
    val icon: Rep[String] = column[String]("icon", O.Length(255,varying=true), O.Default(""))
    /** Database column store_id SqlType(BIGINT), Default(0) */
    val storeId: Rep[Long] = column[Long]("store_id", O.Default(0L))
    /** Database column rank SqlType(INT), Default(0) */
    val rank: Rep[Int] = column[Int]("rank", O.Default(0))
  }
  /** Collection-like TableQuery object for table tCategories */
  lazy val tCategories = new TableQuery(tag => new tCategories(tag))

  /** Entity class storing rows of table tComments
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param orderId Database column order_id SqlType(BIGINT)
   *  @param transTime Database column trans_time SqlType(INT), Default(0)
   *  @param dishGrade Database column dish_grade SqlType(INT)
   *  @param storeId Database column store_id SqlType(BIGINT)
   *  @param createTime Database column create_time SqlType(BIGINT) */
  case class rComments(id: Long, orderId: Long, transTime: Int = 0, dishGrade: Int, storeId: Long, createTime: Long)
  /** GetResult implicit for fetching rComments objects using plain SQL queries */
  implicit def GetResultrComments(implicit e0: GR[Long], e1: GR[Int]): GR[rComments] = GR{
    prs => import prs._
    rComments.tupled((<<[Long], <<[Long], <<[Int], <<[Int], <<[Long], <<[Long]))
  }
  /** Table description of table comments. Objects of this class serve as prototypes for rows in queries. */
  class tComments(_tableTag: Tag) extends Table[rComments](_tableTag, "comments") {
    def * = (id, orderId, transTime, dishGrade, storeId, createTime) <> (rComments.tupled, rComments.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(orderId), Rep.Some(transTime), Rep.Some(dishGrade), Rep.Some(storeId), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rComments.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column order_id SqlType(BIGINT) */
    val orderId: Rep[Long] = column[Long]("order_id")
    /** Database column trans_time SqlType(INT), Default(0) */
    val transTime: Rep[Int] = column[Int]("trans_time", O.Default(0))
    /** Database column dish_grade SqlType(INT) */
    val dishGrade: Rep[Int] = column[Int]("dish_grade")
    /** Database column store_id SqlType(BIGINT) */
    val storeId: Rep[Long] = column[Long]("store_id")
    /** Database column create_time SqlType(BIGINT) */
    val createTime: Rep[Long] = column[Long]("create_time")
  }
  /** Collection-like TableQuery object for table tComments */
  lazy val tComments = new TableQuery(tag => new tComments(tag))

  /** Entity class storing rows of table tGoods
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param storeId Database column store_id SqlType(BIGINT)
   *  @param catId Database column cat_id SqlType(BIGINT), Default(0)
   *  @param name Database column name SqlType(VARCHAR), Length(63,true)
   *  @param price Database column price SqlType(INT)
   *  @param salePrice Database column sale_price SqlType(INT), Default(0)
   *  @param description Database column description SqlType(VARCHAR), Length(255,true), Default()
   *  @param icon Database column icon SqlType(VARCHAR), Length(255,true), Default()
   *  @param stock Database column stock SqlType(INT), Default(0)
   *  @param sales Database column sales SqlType(INT), Default(0)
   *  @param state Database column state SqlType(INT), Default(0)
   *  @param createTime Database column create_time SqlType(BIGINT) */
  case class rGoods(id: Long, storeId: Long, catId: Long = 0L, name: String, price: Int, salePrice: Int = 0, description: String = "", icon: String = "", stock: Int = 0, sales: Int = 0, state: Int = 0, createTime: Long)
  /** GetResult implicit for fetching rGoods objects using plain SQL queries */
  implicit def GetResultrGoods(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rGoods] = GR{
    prs => import prs._
    rGoods.tupled((<<[Long], <<[Long], <<[Long], <<[String], <<[Int], <<[Int], <<[String], <<[String], <<[Int], <<[Int], <<[Int], <<[Long]))
  }
  /** Table description of table goods. Objects of this class serve as prototypes for rows in queries. */
  class tGoods(_tableTag: Tag) extends Table[rGoods](_tableTag, "goods") {
    def * = (id, storeId, catId, name, price, salePrice, description, icon, stock, sales, state, createTime) <> (rGoods.tupled, rGoods.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(storeId), Rep.Some(catId), Rep.Some(name), Rep.Some(price), Rep.Some(salePrice), Rep.Some(description), Rep.Some(icon), Rep.Some(stock), Rep.Some(sales), Rep.Some(state), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rGoods.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column store_id SqlType(BIGINT) */
    val storeId: Rep[Long] = column[Long]("store_id")
    /** Database column cat_id SqlType(BIGINT), Default(0) */
    val catId: Rep[Long] = column[Long]("cat_id", O.Default(0L))
    /** Database column name SqlType(VARCHAR), Length(63,true) */
    val name: Rep[String] = column[String]("name", O.Length(63,varying=true))
    /** Database column price SqlType(INT) */
    val price: Rep[Int] = column[Int]("price")
    /** Database column sale_price SqlType(INT), Default(0) */
    val salePrice: Rep[Int] = column[Int]("sale_price", O.Default(0))
    /** Database column description SqlType(VARCHAR), Length(255,true), Default() */
    val description: Rep[String] = column[String]("description", O.Length(255,varying=true), O.Default(""))
    /** Database column icon SqlType(VARCHAR), Length(255,true), Default() */
    val icon: Rep[String] = column[String]("icon", O.Length(255,varying=true), O.Default(""))
    /** Database column stock SqlType(INT), Default(0) */
    val stock: Rep[Int] = column[Int]("stock", O.Default(0))
    /** Database column sales SqlType(INT), Default(0) */
    val sales: Rep[Int] = column[Int]("sales", O.Default(0))
    /** Database column state SqlType(INT), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column create_time SqlType(BIGINT) */
    val createTime: Rep[Long] = column[Long]("create_time")
  }
  /** Collection-like TableQuery object for table tGoods */
  lazy val tGoods = new TableQuery(tag => new tGoods(tag))

  /** Entity class storing rows of table tOrderGoods
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param orderId Database column order_id SqlType(BIGINT)
   *  @param goodId Database column good_id SqlType(BIGINT)
   *  @param num Database column num SqlType(INT) */
  case class rOrderGoods(id: Long, orderId: Long, goodId: Long, num: Int)
  /** GetResult implicit for fetching rOrderGoods objects using plain SQL queries */
  implicit def GetResultrOrderGoods(implicit e0: GR[Long], e1: GR[Int]): GR[rOrderGoods] = GR{
    prs => import prs._
    rOrderGoods.tupled((<<[Long], <<[Long], <<[Long], <<[Int]))
  }
  /** Table description of table order_goods. Objects of this class serve as prototypes for rows in queries. */
  class tOrderGoods(_tableTag: Tag) extends Table[rOrderGoods](_tableTag, "order_goods") {
    def * = (id, orderId, goodId, num) <> (rOrderGoods.tupled, rOrderGoods.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(orderId), Rep.Some(goodId), Rep.Some(num)).shaped.<>({r=>import r._; _1.map(_=> rOrderGoods.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column order_id SqlType(BIGINT) */
    val orderId: Rep[Long] = column[Long]("order_id")
    /** Database column good_id SqlType(BIGINT) */
    val goodId: Rep[Long] = column[Long]("good_id")
    /** Database column num SqlType(INT) */
    val num: Rep[Int] = column[Int]("num")
  }
  /** Collection-like TableQuery object for table tOrderGoods */
  lazy val tOrderGoods = new TableQuery(tag => new tOrderGoods(tag))

  /** Entity class storing rows of table tOrders
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param storeId Database column store_id SqlType(BIGINT)
   *  @param customerId Database column customer_id SqlType(BIGINT)
   *  @param recipient Database column recipient SqlType(VARCHAR), Length(63,true)
   *  @param address Database column address SqlType(VARCHAR), Length(255,true), Default()
   *  @param contact Database column contact SqlType(VARCHAR), Length(255,true), Default()
   *  @param remark Database column remark SqlType(VARCHAR), Length(255,true), Default()
   *  @param packFee Database column pack_fee SqlType(INT)
   *  @param totalFee Database column total_fee SqlType(INT)
   *  @param payStatus Database column pay_status SqlType(INT)
   *  @param state Database column state SqlType(INT), Default(0)
   *  @param tradeNo Database column trade_no SqlType(VARCHAR), Length(255,true), Default()
   *  @param arriveTime Database column arrive_time SqlType(BIGINT), Default(0)
   *  @param createTime Database column create_time SqlType(BIGINT) */
  case class rOrders(id: Long, storeId: Long, customerId: Long, recipient: String, address: String = "", contact: String = "", remark: String = "", packFee: Int, totalFee: Int, payStatus: Int, state: Int = 0, tradeNo: String = "", arriveTime: Long = 0L, createTime: Long)
  /** GetResult implicit for fetching rOrders objects using plain SQL queries */
  implicit def GetResultrOrders(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rOrders] = GR{
    prs => import prs._
    rOrders.tupled((<<[Long], <<[Long], <<[Long], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[Int], <<[Int], <<[Int], <<[String], <<[Long], <<[Long]))
  }
  /** Table description of table orders. Objects of this class serve as prototypes for rows in queries. */
  class tOrders(_tableTag: Tag) extends Table[rOrders](_tableTag, "orders") {
    def * = (id, storeId, customerId, recipient, address, contact, remark, packFee, totalFee, payStatus, state, tradeNo, arriveTime, createTime) <> (rOrders.tupled, rOrders.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(storeId), Rep.Some(customerId), Rep.Some(recipient), Rep.Some(address), Rep.Some(contact), Rep.Some(remark), Rep.Some(packFee), Rep.Some(totalFee), Rep.Some(payStatus), Rep.Some(state), Rep.Some(tradeNo), Rep.Some(arriveTime), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rOrders.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column store_id SqlType(BIGINT) */
    val storeId: Rep[Long] = column[Long]("store_id")
    /** Database column customer_id SqlType(BIGINT) */
    val customerId: Rep[Long] = column[Long]("customer_id")
    /** Database column recipient SqlType(VARCHAR), Length(63,true) */
    val recipient: Rep[String] = column[String]("recipient", O.Length(63,varying=true))
    /** Database column address SqlType(VARCHAR), Length(255,true), Default() */
    val address: Rep[String] = column[String]("address", O.Length(255,varying=true), O.Default(""))
    /** Database column contact SqlType(VARCHAR), Length(255,true), Default() */
    val contact: Rep[String] = column[String]("contact", O.Length(255,varying=true), O.Default(""))
    /** Database column remark SqlType(VARCHAR), Length(255,true), Default() */
    val remark: Rep[String] = column[String]("remark", O.Length(255,varying=true), O.Default(""))
    /** Database column pack_fee SqlType(INT) */
    val packFee: Rep[Int] = column[Int]("pack_fee")
    /** Database column total_fee SqlType(INT) */
    val totalFee: Rep[Int] = column[Int]("total_fee")
    /** Database column pay_status SqlType(INT) */
    val payStatus: Rep[Int] = column[Int]("pay_status")
    /** Database column state SqlType(INT), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column trade_no SqlType(VARCHAR), Length(255,true), Default() */
    val tradeNo: Rep[String] = column[String]("trade_no", O.Length(255,varying=true), O.Default(""))
    /** Database column arrive_time SqlType(BIGINT), Default(0) */
    val arriveTime: Rep[Long] = column[Long]("arrive_time", O.Default(0L))
    /** Database column create_time SqlType(BIGINT) */
    val createTime: Rep[Long] = column[Long]("create_time")
  }
  /** Collection-like TableQuery object for table tOrders */
  lazy val tOrders = new TableQuery(tag => new tOrders(tag))

  /** Entity class storing rows of table tRefunds
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param orderId Database column order_id SqlType(BIGINT)
   *  @param amount Database column amount SqlType(INT)
   *  @param customerDesp Database column customer_desp SqlType(VARCHAR), Length(255,true), Default()
   *  @param storeDesp Database column store_desp SqlType(VARCHAR), Length(255,true), Default()
   *  @param state Database column state SqlType(INT), Default(0)
   *  @param timestamp Database column timestamp SqlType(BIGINT) */
  case class rRefunds(id: Long, orderId: Long, amount: Int, customerDesp: String = "", storeDesp: String = "", state: Int = 0, timestamp: Long)
  /** GetResult implicit for fetching rRefunds objects using plain SQL queries */
  implicit def GetResultrRefunds(implicit e0: GR[Long], e1: GR[Int], e2: GR[String]): GR[rRefunds] = GR{
    prs => import prs._
    rRefunds.tupled((<<[Long], <<[Long], <<[Int], <<[String], <<[String], <<[Int], <<[Long]))
  }
  /** Table description of table refunds. Objects of this class serve as prototypes for rows in queries. */
  class tRefunds(_tableTag: Tag) extends Table[rRefunds](_tableTag, "refunds") {
    def * = (id, orderId, amount, customerDesp, storeDesp, state, timestamp) <> (rRefunds.tupled, rRefunds.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(orderId), Rep.Some(amount), Rep.Some(customerDesp), Rep.Some(storeDesp), Rep.Some(state), Rep.Some(timestamp)).shaped.<>({r=>import r._; _1.map(_=> rRefunds.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column order_id SqlType(BIGINT) */
    val orderId: Rep[Long] = column[Long]("order_id")
    /** Database column amount SqlType(INT) */
    val amount: Rep[Int] = column[Int]("amount")
    /** Database column customer_desp SqlType(VARCHAR), Length(255,true), Default() */
    val customerDesp: Rep[String] = column[String]("customer_desp", O.Length(255,varying=true), O.Default(""))
    /** Database column store_desp SqlType(VARCHAR), Length(255,true), Default() */
    val storeDesp: Rep[String] = column[String]("store_desp", O.Length(255,varying=true), O.Default(""))
    /** Database column state SqlType(INT), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column timestamp SqlType(BIGINT) */
    val timestamp: Rep[Long] = column[Long]("timestamp")
  }
  /** Collection-like TableQuery object for table tRefunds */
  lazy val tRefunds = new TableQuery(tag => new tRefunds(tag))

  /** Entity class storing rows of table tSecureKey
   *  @param appid Database column appid SqlType(VARCHAR), Length(255,true)
   *  @param secureKey Database column secure_key SqlType(VARCHAR), Length(255,true), Default() */
  case class rSecureKey(appid: String, secureKey: String = "")
  /** GetResult implicit for fetching rSecureKey objects using plain SQL queries */
  implicit def GetResultrSecureKey(implicit e0: GR[String]): GR[rSecureKey] = GR{
    prs => import prs._
    rSecureKey.tupled((<<[String], <<[String]))
  }
  /** Table description of table secure_key. Objects of this class serve as prototypes for rows in queries. */
  class tSecureKey(_tableTag: Tag) extends Table[rSecureKey](_tableTag, "secure_key") {
    def * = (appid, secureKey) <> (rSecureKey.tupled, rSecureKey.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(appid), Rep.Some(secureKey)).shaped.<>({r=>import r._; _1.map(_=> rSecureKey.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column appid SqlType(VARCHAR), Length(255,true) */
    val appid: Rep[String] = column[String]("appid", O.Length(255,varying=true))
    /** Database column secure_key SqlType(VARCHAR), Length(255,true), Default() */
    val secureKey: Rep[String] = column[String]("secure_key", O.Length(255,varying=true), O.Default(""))
  }
  /** Collection-like TableQuery object for table tSecureKey */
  lazy val tSecureKey = new TableQuery(tag => new tSecureKey(tag))

  /** Entity class storing rows of table tStores
   *  @param id Database column id SqlType(BIGINT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(63,true)
   *  @param description Database column description SqlType(VARCHAR), Length(255,true), Default()
   *  @param contact Database column contact SqlType(VARCHAR), Length(255,true), Default()
   *  @param address Database column address SqlType(VARCHAR), Length(255,true), Default()
   *  @param icon Database column icon SqlType(VARCHAR), Length(255,true), Default()
   *  @param openFrom Database column open_from SqlType(BIGINT)
   *  @param openTo Database column open_to SqlType(BIGINT)
   *  @param basePrice Database column base_price SqlType(INT)
   *  @param packFee Database column pack_fee SqlType(INT)
   *  @param catId Database column cat_id SqlType(BIGINT), Default(0)
   *  @param sales Database column sales SqlType(INT), Default(0)
   *  @param comments Database column comments SqlType(INT), Default(0)
   *  @param grades Database column grades SqlType(FLOAT), Default(0.0)
   *  @param costTime Database column cost_time SqlType(INT)
   *  @param state Database column state SqlType(INT), Default(0)
   *  @param createTime Database column create_time SqlType(BIGINT)
   *  @param modifiedTime Database column modified_time SqlType(BIGINT) */
  case class rStores(id: Long, name: String, description: String = "", contact: String = "", address: String = "", icon: String = "", openFrom: Long, openTo: Long, basePrice: Int, packFee: Int, catId: Long = 0L, sales: Int = 0, comments: Int = 0, grades: Float = 0.0F, costTime: Int, state: Int = 0, createTime: Long, modifiedTime: Long)
  /** GetResult implicit for fetching rStores objects using plain SQL queries */
  implicit def GetResultrStores(implicit e0: GR[Long], e1: GR[String], e2: GR[Int], e3: GR[Float]): GR[rStores] = GR{
    prs => import prs._
    rStores.tupled((<<[Long], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Long], <<[Long], <<[Int], <<[Int], <<[Long], <<[Int], <<[Int], <<[Float], <<[Int], <<[Int], <<[Long], <<[Long]))
  }
  /** Table description of table stores. Objects of this class serve as prototypes for rows in queries. */
  class tStores(_tableTag: Tag) extends Table[rStores](_tableTag, "stores") {
    def * = (id, name, description, contact, address, icon, openFrom, openTo, basePrice, packFee, catId, sales, comments, grades, costTime, state, createTime, modifiedTime) <> (rStores.tupled, rStores.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(description), Rep.Some(contact), Rep.Some(address), Rep.Some(icon), Rep.Some(openFrom), Rep.Some(openTo), Rep.Some(basePrice), Rep.Some(packFee), Rep.Some(catId), Rep.Some(sales), Rep.Some(comments), Rep.Some(grades), Rep.Some(costTime), Rep.Some(state), Rep.Some(createTime), Rep.Some(modifiedTime)).shaped.<>({r=>import r._; _1.map(_=> rStores.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(63,true) */
    val name: Rep[String] = column[String]("name", O.Length(63,varying=true))
    /** Database column description SqlType(VARCHAR), Length(255,true), Default() */
    val description: Rep[String] = column[String]("description", O.Length(255,varying=true), O.Default(""))
    /** Database column contact SqlType(VARCHAR), Length(255,true), Default() */
    val contact: Rep[String] = column[String]("contact", O.Length(255,varying=true), O.Default(""))
    /** Database column address SqlType(VARCHAR), Length(255,true), Default() */
    val address: Rep[String] = column[String]("address", O.Length(255,varying=true), O.Default(""))
    /** Database column icon SqlType(VARCHAR), Length(255,true), Default() */
    val icon: Rep[String] = column[String]("icon", O.Length(255,varying=true), O.Default(""))
    /** Database column open_from SqlType(BIGINT) */
    val openFrom: Rep[Long] = column[Long]("open_from")
    /** Database column open_to SqlType(BIGINT) */
    val openTo: Rep[Long] = column[Long]("open_to")
    /** Database column base_price SqlType(INT) */
    val basePrice: Rep[Int] = column[Int]("base_price")
    /** Database column pack_fee SqlType(INT) */
    val packFee: Rep[Int] = column[Int]("pack_fee")
    /** Database column cat_id SqlType(BIGINT), Default(0) */
    val catId: Rep[Long] = column[Long]("cat_id", O.Default(0L))
    /** Database column sales SqlType(INT), Default(0) */
    val sales: Rep[Int] = column[Int]("sales", O.Default(0))
    /** Database column comments SqlType(INT), Default(0) */
    val comments: Rep[Int] = column[Int]("comments", O.Default(0))
    /** Database column grades SqlType(FLOAT), Default(0.0) */
    val grades: Rep[Float] = column[Float]("grades", O.Default(0.0F))
    /** Database column cost_time SqlType(INT) */
    val costTime: Rep[Int] = column[Int]("cost_time")
    /** Database column state SqlType(INT), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column create_time SqlType(BIGINT) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column modified_time SqlType(BIGINT) */
    val modifiedTime: Rep[Long] = column[Long]("modified_time")
  }
  /** Collection-like TableQuery object for table tStores */
  lazy val tStores = new TableQuery(tag => new tStores(tag))
}
