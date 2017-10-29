package util


import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import play.api.mvc.QueryStringBindable

object Bindables {

  implicit def bindableLocalTime(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[LocalTime] {

    val fmt = DateTimeFormat.forPattern("HH:mm")

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, LocalTime]] = {
      stringBinder.bind(key, params).map(_.right.map(translate))
    }

    override def unbind(key: String, value: LocalTime): String = {
      s"$key=${translate(value)}"
    }

    private def translate(value: LocalTime): String = {
      fmt.print(value)
    }

    private def translate(value: String): LocalTime = {
      fmt.parseLocalTime(value)
    }
  }

  // Generic list binder for base data types
  implicit def listBinder[T](implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[List[T]] {

    val seperator = ","

    override def bind(key: String, params: Map[String, Seq[String]]) = stringBinder.bind(key, params).map(_.right.map(translate))

    override def unbind(key: String, value: List[T]) = s"$key=${translate(value)}"

    private def translate(value: String): List[T] = {
      value.split(s"$seperator").map(_.asInstanceOf[T]).toList
    }

    private def translate(value: List[T]): String = {
      value.mkString(seperator)
    }

  }

}
