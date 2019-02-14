/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 GatlingCql developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package cassandra

import com.datastax.driver.core.{Cluster, ConsistencyLevel}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.github.gatling.cql.Predef._

import scala.concurrent.duration.DurationInt

class CassandraSTSimulation extends Simulation with CassandraSimulationBase {
  val table_name = "size"

  session.execute(
    s"""create table if not exists $table(pk text primary key, cl text)
       |    with default_time_to_live = 60
       |    and gc_grace_seconds = 60""".stripMargin)

  val prepared = session.prepare(
    s"""INSERT INTO $table(pk, cl)VALUES(?, ?)""")

  val scn = scenario("Two statements").repeat(1) { //Name your scenario
    feed(feeder)
      .exec(cql("prepared INSERT")
        .execute(prepared)
        .withParams("${randomInsert}", "${randomString}")
        .consistencyLevel(ConsistencyLevel.LOCAL_ONE)
      )
      .exec(cql("simple SELECT")
        .execute(s"SELECT * FROM $table WHERE pk = '$${randomInsert}'")
        .check(rowCount.is(1)))
  }

  setUp(scn.inject(rampUsersPerSec(10) to gatlingUsers during (20 minutes)))
    .protocols(cqlConfig)

  after(cluster.close())
}
