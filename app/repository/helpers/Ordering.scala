package repository.helpers

sealed trait Ordering {

  def fieldName: String
  def direction: String = ""

}

case class Ascending(field: String) extends Ordering {
  override def fieldName: String = field

  override def direction: String = "ASC"
}

case class Descending(field: String) extends Ordering {
  override def fieldName: String = field

  override def direction: String = "DESC"
}

