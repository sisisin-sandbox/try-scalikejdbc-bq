package example

import com.google.cloud.bigquery.BigQueryOptions
import scalikejdbc._
import scalikejdbc.bigquery._

object Hello extends App {
  main()

  def main() = {
    Psql.proc()
    BQ.proc()
  }
}

object BQ {
  def proc() = {
    //    val credentials = GoogleCredentials.fromStream(new FileInputStream("/path/to/key.json"))
    val bigQuery = BigQueryOptions.newBuilder()
      //      .setCredentials(credentials)
      .setProjectId(Conf.gcloud.projectId)
      .build()
      .getService

    val executor = new QueryExecutor(bigQuery, QueryConfig())
    println(MemberBq.findAll(executor).result)
  }
}

object Psql {
  def proc() = {
    GlobalSettings.loggingSQLAndTime = new LoggingSQLAndTimeSettings(
      enabled = true,
      logLevel = 'DEBUG,
      singleLineMode = true
    )
    Class.forName("org.postgresql.Driver")
    ConnectionPool.singleton("jdbc:postgresql://localhost:5432/tsb_db", "tsb_user", "")
    implicit val session = AutoSession

    try {
      query
    } finally {
      sql"drop table members".execute().apply()
    }
  }

  private def query(implicit session: DBSession) = {

    sql"""
create table members (
  id serial not null primary key,
  name varchar(64),
  created_at timestamp not null
)
""".execute.apply()

    // insert initial data
    Seq("Alice", "Bob", "Chris") foreach { name =>
      sql"insert into members (name, created_at) values (${name}, current_timestamp)".update.apply()
    }

    val members = MemberPsql.findAll
    println(members)

    val alice = MemberPsql.findByName("Alice")
    println(alice)
  }
}