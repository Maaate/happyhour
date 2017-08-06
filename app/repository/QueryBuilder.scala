package repository

import java.sql.Connection

import anorm._
import repository.helpers.Ordering

private[repository] trait QueryBuilder {
  type QueryParam = (String, Seq[NamedParameter])

  /**
    * Small helper to simplify building of where clauses.
    */
  /*def executeQuery[T](query: String, rowParser: ResultSetParser[T], params: Seq[QueryParam], orderBy: Option[Ordering] = None)(implicit conn: Connection) = {
    val combined = params match {
      case Nil => Nil
      case where :: ands => Seq((s" where ${where._1} ", where._2)) ++ ands.map { case (query, params) => (s" and $query ", params) }
    }

    val q = combined.map(_._1).mkString(" ")
    val p = combined.flatMap(_._2)
    val order = orderBy match {
      case Some(x) => s" order by ${x.fieldName} ${x.direction}"
      case _ => ""
    }
    SQL(query + q + order).on(p: _*).as(rowParser)

  }*/

  def executeQuery[T](query: String, rowParser: ResultSetParser[T], params: Seq[String], orderBy: Option[Ordering] = None)(implicit conn: Connection) = {
    val combined = params match {
      case Nil => Nil
      case where :: ands => Seq(s" where ${where} ") ++ ands.map { case query => s" and $query " }
    }

    val q = combined.mkString(" ")
    val order = orderBy match {
      case Some(x) => s" order by ${x.fieldName} ${x.direction}"
      case _ => ""
    }

    SQL(query + q + order).as(rowParser)

  }

}