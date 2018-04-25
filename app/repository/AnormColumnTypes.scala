package repository

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

import anorm.{RowParser, SqlParser}
import org.joda.time.{DateTime, LocalTime}

trait AnormColumnTypes {

  def bigDecimal(columnName: String): RowParser[BigDecimal] =
    SqlParser.double(columnName).map(f => BigDecimal(f))

  // Map string backed UUID
  def uuidFromString(columnName: String): RowParser[UUID] =
    SqlParser.str(columnName).map(UUID.fromString)

  def localTime(columnName: String): RowParser[LocalTime] =
    SqlParser.date(columnName).map(d => LocalTime.fromDateFields(d))

  def dateTime(columnName: String): RowParser[DateTime] =
    SqlParser.date(columnName).map(d => new DateTime(d))

  def localDateTime(columnName: String): RowParser[LocalDateTime] =
    SqlParser.date(columnName).map(d => d.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDateTime)
}
