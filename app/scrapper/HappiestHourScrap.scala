import java.net.URL
import java.util.UUID

import models.Pub
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTimeConstants
import org.jsoup.Jsoup
import scala.collection.JavaConverters._


object HappiestHourScrap {
  def main(args: Array[String]) {

    val pubMap = scala.collection.mutable.HashMap.empty[String, PubImport]
    val finalPromotions = scala.collection.mutable.ListBuffer

    val GoogleMapsRegex = ".*google.maps.LatLng\\(([-\\d\\.]*) , ([-\\d\\.]*)".r


    //val post = new HttpPost("http://localhost:8080/stocks/saveJsonStock")

    for (page <- 1 to 1) {
      System.out.println(s"Page number: $page")

      val src = Jsoup.parse(new URL(s"https://www.thehappiesthour.com/specials/sydney/?page=$page"), 5000)
      for {
        tr <- src.select(".table-venue tbody tr").asScala
      } {
        val title = tr.select("td strong a").text()


        val url = "https://www.thehappiesthour.com" + tr.select("td strong a").attr("href")

        if (!pubMap.contains(url)) {
          System.out.println(s"Finding information for $title")
          try {
            val pubSrc = Jsoup.parse(new URL(url), 5000)

            val pubAddress = pubSrc.select("li[itemprop=streetAddress]").text()
            val pubSuburb = pubSrc.select("li a[itemprop=addressLocality]").text()
            val pubUrl = pubSrc.select("li.url").text()

            val gmapsScript = pubSrc.select("script").get(pubSrc.select("script").size() - 2).html()

            val latLng = GoogleMapsRegex.findFirstIn(gmapsScript) flatMap {
              case GoogleMapsRegex(lat, lng) => {
                Some(lat, lng)
              }
              case _ => None
            }

            val pub = PubImport(title, url, pubAddress, pubSuburb, "NSW", latLng.get._1, latLng.get._2, pubUrl)
            pubMap.put(url, pub)


            for {promotion <- pubSrc.select(".table-venue tbody tr").asScala} {
              val dayString = promotion.select("td:nth-child(1)").first().text()
              //val dayString = pubSrc.select(".table-venue tbody tr td strong").text()
              val days = scala.collection.mutable.ListBuffer.empty[Int]
              var foundOneDay = false
              if (dayString.contains("Mon")) {
                days += DateTimeConstants.MONDAY
                foundOneDay = true
              }
              if (dayString.contains("Tue")) {
                days += DateTimeConstants.TUESDAY
                foundOneDay = true
              }
              if (dayString.contains("Wed")) {
                days += DateTimeConstants.WEDNESDAY
                foundOneDay = true
              }
              if (dayString.contains("Thu")) {
                days += DateTimeConstants.THURSDAY
                foundOneDay = true
              }
              if (dayString.contains("Fri")) {
                days += DateTimeConstants.FRIDAY
                foundOneDay = true
              }
              if (dayString.contains("Sat")) {
                days += DateTimeConstants.SATURDAY
                foundOneDay = true
              }
              if (dayString.contains("Sun")) {
                days += DateTimeConstants.SUNDAY
                foundOneDay = true
              }
              if (dayString.contains("All Week")) {
                foundOneDay = true
                days += DateTimeConstants.MONDAY
                days += DateTimeConstants.TUESDAY
                days += DateTimeConstants.WEDNESDAY
                days += DateTimeConstants.THURSDAY
                days += DateTimeConstants.FRIDAY
                days += DateTimeConstants.SATURDAY
                days += DateTimeConstants.SUNDAY

              }
              if (!foundOneDay) {
                DateTimeConstants.DECEMBER
              }

              val dealTimesString = promotion.select("td:nth-child(2)").first().text().split("-")

              if (dealTimesString.size != 2) {
                val p = 0
              }

              val dealString = promotion.select("td:nth-child(3)").first().text()


              val dealTypes = scala.collection.mutable.ListBuffer.empty[String]
              val dealTypesContainer = promotion.select("td:nth-child(5) span i")
              if (dealTypesContainer.hasClass("icon-beer")) {
                dealTypes += "Beer"
              }
              if (dealTypesContainer.hasClass("icon-wine")) {
                dealTypes += "Wine"
              }
              if (dealTypesContainer.hasClass("icon-spirit")) {
                dealTypes += "Spirits"
              }
              if (dealTypesContainer.hasClass("icon-food")) {
                dealTypes += "Food"
              }
              if (dealTypesContainer.hasClass("icon-cocktail")) {
                dealTypes += "Cocktails"
              }


              val p = PromotionImport(pub, days, dealTimesString(0), dealTimesString(1), dealString, dealTypes)


              val i = 0

            }

            if (StringUtils.isEmpty(pubAddress)) {
              val y = 0
            }
          } catch {
            case e: Exception => System.out.println(e.getMessage)
          }
        }

        val x = 0
      }
    }

    val z = 0

    for (pub <- pubMap.values) {
      //Pub(UUID.randomUUID(), pub.title, pub.address, pub.suburb, pub.state, "Australia", BigDecimal(pub.lng), BigDecimal(pub.lat), UUID.fromString("9b309e02-5bd2-4f19-a944-fe958aecbdb8"), if (StringUtils.isEmpty(pub.companyUrl)) None else Some(pub.companyUrl), true)


    }
    System.out.println(pubMap.size)

  }
}

case class PubImport(title: String,
                     pageUrl: String,
                     address: String,
                     suburb: String,
                     state: String,
                     lat: String,
                     lng: String,
                     companyUrl: String)

case class PromotionImport(pub: PubImport,
                           day: scala.collection.mutable.ListBuffer[Int],
                           start: String,
                           finish: String,
                           deal: String,
                           dealTypes: scala.collection.mutable.ListBuffer[String])