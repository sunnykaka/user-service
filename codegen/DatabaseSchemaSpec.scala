//import models.daos.DBTableDefinitions._
//import org.junit.runner._
//import org.specs2.mutable._
//import org.specs2.runner._
//import play.api.db.slick.Config.driver.simple._
//import play.api.test._
//
///**
// * Add your spec here.
// * You can mock out a whole application including requests, plugins etc.
// * For more information, consult the wiki.
// */
//@RunWith(classOf[JUnitRunner])
//class DatabaseSchemaSpec extends Specification {
//
//  "DatabaseSchema" should {
//
//    "print database schema" in new WithApplication{
//
//        val ddls = slickUsers.ddl ++ slickLoginInfos.ddl ++ slickUserLoginInfos.ddl ++
//          slickPasswordInfos.ddl
//
//        ddls.createStatements.foreach(println)
//
//        ddls.dropStatements.foreach(println)
//    }
//
//  }
//}
