package cassandra

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.datastax.driver.core.Cluster
import io.github.gatling.cql.Predef.cql

trait CassandraSimulationBase {

  val random = new util.Random
  val feeder = Iterator.continually(
    Map(
      "randomSelect" -> random.nextInt(10000).toString,
      "randomInsert" -> random.nextInt(10000).toString,
      "randomString" -> random.nextString(50000)
    ))

  val gatlingUsers = 50

  val time = LocalDateTime.now().format(DateTimeFormatter.ISO_TIME).replaceAll(":|\\.", "_")

  def table_name: String

  val keyspace = "dice"

  def table = s"$keyspace.${table_name}_$time"

  val cluster = Cluster.builder().addContactPoint("127.0.0.1").build()
  val session = cluster.connect() //Your C* session

  session.execute(
    s"""CREATE KEYSPACE IF NOT EXISTS $keyspace
       |WITH replication =
       |{'class': 'SimpleStrategy', 'replication_factor': '1'}
       |AND durable_writes = false;""".stripMargin)

  session.execute(s"USE $keyspace")
  val cqlConfig = cql.session(session) //Initialize Gatling DSL with your session

}
