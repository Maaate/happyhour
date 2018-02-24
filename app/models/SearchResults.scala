package models

case class SearchResult(pubId: Long,
                        name: String,
                        latitude: BigDecimal,
                        longitude: BigDecimal,
                        distance: BigDecimal,
                        address: String,
                        promotions: List[Promotion])
