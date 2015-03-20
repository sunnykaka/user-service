package repositories.common

import com.github.tototoshi.slick.GenericJodaSupport

import scala.slick.jdbc.{GetResult => GR, PositionedParameters, SetParameter, PositionedResult}


/**
 * util class for jodatime support and other implicits
 * Created by liubin on 15-3-11.
 */
object SlickImplicits extends GenericJodaSupport(scala.slick.driver.MySQLDriver) {

  /* --- Seq[Long] param --- */
  implicit val setSeqLongParameter = SetParameter { (v: Seq[Long], pp) =>
    v.foreach(pp.setLong)
  }

}
