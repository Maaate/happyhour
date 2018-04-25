package forms

import java.time.LocalDateTime
import java.util.UUID

import models.Pub
import play.api.data.Form
import play.api.data.Forms._


object PubForm {

  private val defaultUUID = UUID.fromString("9b309e02-5bd2-4f19-a944-fe958aecbdb8")


  def pubForm: Form[Pub] = Form(mapping(
    "id" -> default(uuid, UUID.randomUUID()),
    "googleId" -> optional(text),
    "name" -> text,
    "address" -> text,
    "addressSuburb" -> text,
    "addressState" -> text,
    "addressCountry" -> text,
    "longitude" -> bigDecimal,
    "latitude" -> bigDecimal,
    "accountId" -> default(uuid, defaultUUID),
    "website" -> optional(text),
    "phoneNumber" -> optional(text),
    "hours" -> optional(text),
    "enabled" ->default(boolean, true)
  )({ (id, googleId, name, address, addressSuburb, addressState, addressCountry, longitude, latitude, accountId, website, phoneNumber, hours, enabled) =>
    Pub(id, LocalDateTime.now(), googleId, name, address, addressSuburb, addressState, addressCountry, longitude, latitude, accountId, website, phoneNumber, hours, Some(LocalDateTime.now()), enabled, Set())
  })({
    case pub:Pub =>
      Some(pub.id, pub.googleId, pub.name, pub.address, pub.addressSuburb, pub.addressState, pub.addressCountry, pub.longitude, pub.latitude, pub.accountId, pub.website, pub.phoneNumber, pub.hoursOpenString, pub.enabled)
  }))
}
