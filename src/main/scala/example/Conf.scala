package example

import com.typesafe.config.ConfigFactory

sealed case class DB()

sealed case class Gcloud(val projectId: String, val datasetId: String)

object Conf {
  private[this] val conf = ConfigFactory.defaultOverrides
    .withFallback(ConfigFactory.load("default"))
    .withFallback(ConfigFactory.load)
  val gcloud = Gcloud(conf.getString("gcloud.projectId"), conf.getString("gcloud.datasetId"))
}
