import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import io.gatling.http.Predef._

import java.util.concurrent.TimeUnit
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.FiniteDuration

object SampleSimulation extends Simulation {

    // indeks arraya == client, x(0)(0) = intensywnosc dla klienta o idku 0 dla resource-a o idku 0
    // te arraye ewentualnie mozna latwo przeksztalcic na feedery Map(indeks, Array)
    val clientsAndResourceIntensity = Array(
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
    )

    // indeks arraya = client, x(0)(0) = koszt accessu serwera 0 przez klienta 0
    val clientsAndCosts = Array(
      Array(0, 1, 1, 2, 2),
      Array(0, 1, 1, 2, 2),
      Array(2, 0, 1, 1, 2),
      Array(2, 0, 1, 1, 2),
      Array(2, 0, 1, 1, 2),
      Array(2, 2, 0, 1, 1),
      Array(1, 2, 2, 0, 1),
      Array(1, 2, 2, 0, 1),
      Array(1, 1, 2, 2, 0),
      Array(1, 1, 2, 2, 0),
    )

    var scenarios = new ListBuffer[PopulationBuilder]()

    for (client <- clientsAndResourceIntensity.indices) {
      for (resource <- clientsAndResourceIntensity(client).indices) {
        val intensity = clientsAndResourceIntensity(client)(resource)
        val serverIndex = clientsAndCosts(client).indexOf(0)
        val serverAddress = s"localhost:808${serverIndex}"

        val httpProtocol = http
          .baseUrl(s"http://$serverAddress")
          .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
          .doNotTrackHeader("1")
          .acceptLanguageHeader("en-US,en;q=0.5")
          .acceptEncodingHeader("gzip, deflate")
          .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")


        val scn = scenario(s"Client ${client} attacks server ${serverIndex} at address ${serverAddress} looking for resource ${resource} with intensity ${intensity}").repeat(100) {
          exec(
            http(serverAddress)
              .get(s"/resource/$resource")
          )
        }

        val populationBuilder: PopulationBuilder = scn
          .inject(atOnceUsers(1))
          .throttle(
            reachRps(intensity).in(FiniteDuration(1L, TimeUnit.SECONDS))
          )
          .protocols(httpProtocol)

        scenarios += (populationBuilder)
      }
    }

    setUp(scenarios.toList)
}
