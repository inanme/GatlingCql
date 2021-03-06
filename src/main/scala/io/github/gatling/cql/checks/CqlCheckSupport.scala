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
package io.github.gatling.cql.checks

import io.gatling.core.check._
import io.gatling.core.session.{Expression, RichExpression}

import io.github.gatling.cql.checks.CqlCheckBuilders._
import io.github.gatling.cql.response.CqlResponse

trait CqlCheckSupport {
  val exhausted = CqlCheckBuilder.Exhausted
  val applied = CqlCheckBuilder.Applied

  val executionInfo = CqlCheckBuilder.ExecutionInfo
  val resultSet = CqlCheckBuilder.ResultSet

  /**
   * Get the number of all rows returned by the CQL statement.
   * Note that this statement implicitly fetches <b>all</b> rows from the result set!
   */
  val rowCount = CqlCheckBuilder.RowCount

  /**
   * Get a column by name returned by the CQL statement.
   * Note that this statement implicitly fetches <b>all</b> rows from the result set!
   */
  def columnValue(columnName: Expression[String]) =
    new DefaultMultipleFindCheckBuilder[CqlCheck, CqlResponse, CqlResponse, Any](ResponseExtender, PassThroughResponsePreparer) {
      def findExtractor(occurrence: Int) = columnName.map(new SingleColumnValueExtractor(_, occurrence))
      def findAllExtractor = columnName.map(new MultipleColumnValueExtractor(_))
      def countExtractor = columnName.map(new CountColumnValueExtractor(_))
    }

}

