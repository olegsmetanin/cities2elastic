package sw.util


import scala.io.Source
import java.io.PrintWriter

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.action.admin.indices.delete.{DeleteIndexRequest}
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query.FilterBuilders.geoBoundingBoxFilter
import org.elasticsearch.index.query.QueryBuilders._

object UniqueCityLocations extends App {

  args.toList match {

    case "unique" :: inputfile :: outputfile :: Nil => {

      def normalize(s: String) = {
        s.toFloat.toString
      }

      // Country,City,AccentCity,Region,Population,Latitude,Longitude
      val file = Source.fromFile(inputfile)

      val locations = for (
        (line, id) <- file.getLines.zipWithIndex if id > 0;
        p <- {
          val cols = line.split(",")
          val population = cols(4)
          if (population != "") {
            val lat = normalize(cols(5))
            val lon = normalize(cols(6))
            Some(lat, lon, population)
          } else None
        }

      ) yield p

      val uniqloc = locations.toList.distinct.sortWith(_._3.toLong > _._3.toLong)

      val crlf = System.getProperty("line.separator")

      val wfile = new PrintWriter(outputfile)

      uniqloc.map(s =>
        wfile.write(s._1 + "," + s._2 + "," + s._3 + crlf)
      )

      wfile.close
    }

    case "import" :: inputfile :: Nil => {

      val client = new TransportClient

      client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300))

      // delete index
      client.admin().indices().delete(new DeleteIndexRequest("cities")).actionGet()
      // create index
      client.admin().indices().prepareCreate("cities").addMapping("poi",
        """
{
  "poi": {
    "properties": {
      "population": {"type": "string"},
      "location": {"type": "geo_point"}
    }
  }
}
        """
      )
        .execute()
        .actionGet()

      // Latitude,Longitude,Population
      val file = Source.fromFile(inputfile)

      for (
        (line, id) <- file.getLines.zipWithIndex
      ) {
        val ids = id.toString
        val cols = line.split(",")
        val lat = cols(0)
        val lon = cols(1)
        val population = cols(2)

        client.prepareIndex("cities", "poi", ids)
          .setSource(
          s"""
{
  "population": $population,
  "location": {
    "lat":$lat,
    "lon":$lon
  }
}
      """
        )
          .execute()
          .actionGet()

      }
    }

    case "count" :: Nil => {
      val client = new TransportClient

      client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300))

      val response = client.prepareSearch("cities").setTypes("poi")
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setQuery(filteredQuery(matchAllQuery(), geoBoundingBoxFilter("location").topLeft(42.4766667, 1.4).bottomRight(42.4566667, 1.6)))
        .setSize(100)
        .addField("")
        .execute()
        .actionGet()


      println(
        response.getHits.getHits.map(h =>
          h.getId
        ) mkString (",")
      )
    }
    case _ => println("No input file defined")
  }


}
