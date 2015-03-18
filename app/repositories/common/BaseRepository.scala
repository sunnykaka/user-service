package repositories.common

import java.sql.SQLException

import models.common.Entity

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.{GetResult => GR, SetParameter => SP, Invoker, StaticQuery}
import repositories.common.SlickImplicits._

import scala.slick.jdbc.StaticQuery.interpolation


/**
 * Created by liubin on 15-3-10.
 */
trait BaseRepository[E <: Entity] {

  def tableName: String

  def findAll()(implicit session: Session, getEntityResult: GR[E]): List[E] = sql"select * from #$tableName".as[E].list

  def deleteAll()(implicit session: Session): Long = sqlu"delete from #$tableName".first

  def findById(id: Long)(implicit session: Session, getEntityResult: GR[E]): Option[E] = {
    StaticQuery.query[Long, E](s"select * from $tableName where id = ? ").apply(id).firstOption
  }

  def findByIds(ids: Seq[Long])(implicit session: Session, getEntityResult: GR[E]): Seq[E] = {

    ids match {
      case Nil => Nil
      case _ => StaticQuery.query[Seq[Long], E](s"select * from $tableName where id in (" +
        ids.map(_ => "?,").mkString.dropRight(1) + ")").apply(ids).list
    }

  }

  def deleteById(id: Long)(implicit session: Session): Long = sqlu"delete from #$tableName where id = $id".first


  def save(entity: E)(implicit session: Session): Long = {
    entity.id.fold(insert(entity))(id => {
      update(entity)
      id
    })
  }

  def update(entity: E)(implicit session: Session): Int

  def insert(entity: E)(implicit session: Session): Long


  implicit class StaticQueryImprovements(val sq: StaticQuery[Unit, AnyVal]) {
    /**
     *
     * @param expectedNum 如果为None,不校验返回值,如果不填,校验返回值为1,否则校验提供的数量
     * @param session
     * @return
     */
    def executeUpdate(expectedNum: Option[Int] = Some(1))(implicit session: Session): Int = {
      sq.apply(()).firstOption match {
        case Some(num) if expectedNum.isDefined && num == expectedNum.get => expectedNum.get
        case Some(num) if expectedNum.isEmpty => num.asInstanceOf[Int]
        case result => throw new SQLException(s"Error during update in table: $tableName, expectedNum: $expectedNum, result: $result")
      }
    }

    def executeInsert()(implicit session: Session): Long = {
      sq.apply(()).firstOption match {
        case Some(num) if num == 1 => sql"SELECT LAST_INSERT_ID()".as[Long].first
        case result => throw new SQLException(s"Error during insert in table: $tableName, result: $result")
      }
    }

  }

}
