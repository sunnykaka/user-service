package repositories.common

import java.sql.Timestamp

import org.joda.time.DateTime

import scala.slick.jdbc.{GetResult => GR, PositionedParameters, SetParameter, PositionedResult}


/**
 * Created by liubin on 15-3-11.
 */
object SlickImplicits {

  /* --- jodatime support --- */
  implicit object getJodaResult extends GR[DateTime] {
    def apply(rs: PositionedResult) = fromSqlType(next(rs))
  }

  implicit object getJodaOptionResult extends GR[Option[DateTime]] {
    def apply(rs: PositionedResult) = nextOption(rs).map(fromSqlType)
  }

  implicit object setJodaParameter extends SetParameter[DateTime] {
    def apply(d: DateTime, p: PositionedParameters) {
      set(p, toSqlType(d))
    }
  }

  implicit object setJodaOptionParameter extends SetParameter[Option[DateTime]] {
    def apply(d: Option[DateTime], p: PositionedParameters) {
      setOption(p, d.map(toSqlType))
    }
  }

  private def next(rs: PositionedResult): Timestamp = rs.nextTimestamp()
  private def nextOption(rs: PositionedResult): Option[Timestamp] = rs.nextTimestampOption()

  private def set(rs: PositionedParameters, d: Timestamp): Unit = rs.setTimestamp(d)
  private def setOption(rs: PositionedParameters, d: Option[Timestamp]): Unit = rs.setTimestampOption(d)

  private def fromSqlType(t: java.sql.Timestamp): DateTime =
    if (t == null) null else new DateTime(t.getTime)

  private def toSqlType(t: DateTime): java.sql.Timestamp =
    if (t == null) null else new java.sql.Timestamp(t.getMillis)


  /* --- Seq[Long] param --- */
  implicit val setSeqLongParameter = SetParameter { (v: Seq[Long], pp) =>
    v.foreach(pp.setLong)
  }

}
