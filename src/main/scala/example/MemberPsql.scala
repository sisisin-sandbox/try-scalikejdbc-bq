package example

import java.time._

import com.google.cloud.bigquery.DatasetId
import scalikejdbc._
import scalikejdbc.bigquery._

case class MemberEntity(id: Long, name: Option[String], createdAt: ZonedDateTime)

trait MemberProvider extends SQLSyntaxSupport[MemberEntity] {
  override val tableName = "members"
  val m = syntax("m")

  def apply(rs: WrappedResultSet): MemberEntity = MemberEntity(
    id = rs.get(m.resultName.id),
    name = rs.stringOpt(m.resultName.name),
    createdAt = rs.zonedDateTime(m.resultName.createdAt))
}

object MemberPsql extends MemberProvider {
  def findAll(implicit session: DBSession) = withSQL {
    select.from(MemberPsql as m)
  }.map { rs => MemberPsql(rs) }.list.apply()

  def findByName(name: String)(implicit session: DBSession) = {
    withSQL {
      select.from(MemberPsql as m).where.eq(m.name, name)
    }.map { rs =>
      println(rs.toMap)
      MemberPsql(rs)
    }.single.apply()
  }
}

object MemberBq extends MemberProvider {
  val dataset = DatasetId.of(Conf.gcloud.projectId, Conf.gcloud.datasetId)

  def findAll(executor: QueryExecutor) = {

    val b = bq {
      val s = selectFrom(MemberBq in dataset as m)
      println(s.sql.value)
      s
    }
    new BqSQL(select(m.*).from(MemberBq in dataset as m).sql)
      .map(apply(_)).single.run(executor)
  }
}