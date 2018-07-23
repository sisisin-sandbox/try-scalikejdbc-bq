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
  val dataset = DatasetId.of("your-gcp-project-id", "your-dataset")

  def findAll(executor: QueryExecutor) = bq {
    select.from(MemberBq in dataset as m)
  }.map(apply(_)).single.run(executor)
}