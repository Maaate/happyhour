package models.google

import play.api.libs.json.Json

case class GoogleAddressResultComponent(name: String, website: Option[String], international_phone_number: Option[String], opening_hours: Option[GoogleAddressOpeningHours], geometry: GoogleAddressGeometryComponent, address_components: List[GoogleAddressComponent], types: List[String])

case class GoogleAddressGeometryComponent(location: GoogleAddressLocationComponent)

case class GoogleAddressLocationComponent(lat: Double, lng: Double)

case class GoogleAddressOpeningHours(weekday_text: List[String])

case class GoogleAddressComponent(long_name: String, short_name: String, types: List[String])


object GoogleAddressResultComponentProtocol {
  implicit lazy val googleAddressOpeningHoursFmt = Json.format[GoogleAddressOpeningHours]
  implicit lazy val googleAddressComponentFmt = Json.format[GoogleAddressComponent]
  implicit lazy val googleAddressLocationComponentFmt = Json.format[GoogleAddressLocationComponent]
  implicit lazy val googleAddressGeometryFmt = Json.format[GoogleAddressGeometryComponent]
  implicit lazy val googleAddressResultFmt = Json.format[GoogleAddressResultComponent]
}


