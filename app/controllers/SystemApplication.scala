package controllers

import java.net.URL

import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTimeConstants
import org.jsoup.Jsoup
import play.api.mvc.{Action, Controller}

import scala.collection.JavaConverters._

class SystemApplication extends Controller {

  def scrapHappiestHour = Action {


    scrape("sydney", 50, "NSW")
    scrape("melbourne", 75, "VIC")
    scrape("perth", 7, "WA")
    scrape("adelaide", 11, "SA")
    scrape("brisbane", 4, "QLD")
    scrape("hobart", 3, "TAS")
    scrape("auckland", 6, "AK")

    Ok("finished stealing")

    //val post = new HttpPost("http://localhost:8080/stocks/saveJsonStock")

  }

  private def scrape (city: String, pageNumbers: Int, stateString: String) {
    val pubMap = scala.collection.mutable.HashMap.empty[String, PubImport]
    var finalPromotions = new scala.collection.mutable.ListBuffer[PromotionImport]()

    val GoogleMapsRegex = ".*google.maps.LatLng\\(([-\\d\\.]*) , ([-\\d\\.]*)".r


    for (page <- 1 to pageNumbers) {
      System.out.println(s"Page number: $page")

      val src = Jsoup.parse(new URL(s"https://www.thehappiesthour.com/specials/$city/?page=$page"), 5000)
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

            val pub = PubImport(title, url, pubAddress, pubSuburb, stateString, latLng.get._1, latLng.get._2, pubUrl)
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


              val dealTypes = scala.collection.mutable.ListBuffer.empty[Long]
              val dealTypesContainer = promotion.select("td:nth-child(5) span i")
              if (dealTypesContainer.hasClass("icon-beer")) {
                dealTypes += 1
              }
              if (dealTypesContainer.hasClass("icon-wine")) {
                dealTypes += 2
              }
              if (dealTypesContainer.hasClass("icon-spirit")) {
                dealTypes += 3
              }
              if (dealTypesContainer.hasClass("icon-food")) {
                dealTypes += 4
              }
              if (dealTypesContainer.hasClass("icon-cocktail")) {
                dealTypes += 5
              }

              finalPromotions += PromotionImport(pub, days, dealTimesString(0), dealTimesString(1), dealString, dealTypes)
            }
          } catch {
            case e: Exception => System.out.println(e.getMessage)
          }
        }
      }
    }

    /*val dbPubResults = for (pub <- pubMap.values) yield {
      val toBeSaved = Pub(None, pub.title, pub.address, pub.suburb, pub.state, "Australia", BigDecimal(pub.lng), BigDecimal(pub.lat), 1L, if (StringUtils.isEmpty(pub.companyUrl)) None else Some(pub.companyUrl), true)
      println("Trying to save Pub: " + toBeSaved)
      (PubDao.create(toBeSaved), pub)
    }

    for (promotion <- finalPromotions) {
      val pubForSaving = dbPubResults.filter(_._2.equals(promotion.pub)).head._1
      for (day <- promotion.day) {
        val dealType = if (promotion.dealTypes.contains(1) || promotion.dealTypes.contains(2) || promotion.dealTypes.contains(3) || promotion.dealTypes.contains(5)) {
          1
        } else {
          2
        }
        //println(s"start time: ${promotion.start}, cleansed start time: ${cleanseDate(setDefaultStartTime(promotion.start))}")
        val startTime = new LocalTime(cleanseDate(setDefaultStartTime(promotion.start)))
        val endTime = new LocalTime(cleanseDate(promotion.finish))
        val toBeSaved = Promotion(None, pubForSaving.id.get, dealType, startTime, endTime, startTime.isAfter(endTime),promotion.deal, Some(day), None)
        println("Trying to save Promotion: " + toBeSaved)
        val saved = PromotionDao.create(toBeSaved)
        PromotionDao.save(saved, promotion.dealTypes.toList)
      }
    }*/
  }

  private def cleanseDate(date: String) = {

    val pattern = "(\\d+)(\\d\\d)".r

    val cleaned = date.toLowerCase()
      .replaceAll(";", ":")
      .replaceAll("24:", "0:")
      .replaceAll("::",":")
      .replaceAll("l", ":")
      .replaceAll("am","")
      .replaceAll("pm","")
      .trim

    pattern.replaceAllIn(cleaned, "$1:$2")
  }

  private def setDefaultStartTime(start: String) = {
    if (StringUtils.isEmpty(start)) {
      "17:00"
    } else {
      start
    }
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
                           dealTypes: scala.collection.mutable.ListBuffer[Long])
