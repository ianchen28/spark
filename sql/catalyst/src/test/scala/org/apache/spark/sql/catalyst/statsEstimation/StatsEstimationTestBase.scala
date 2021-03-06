/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.catalyst.statsEstimation

import org.apache.spark.SparkFunSuite
import org.apache.spark.sql.catalyst.{CatalystConf, SimpleCatalystConf}
import org.apache.spark.sql.catalyst.expressions.{Attribute, AttributeMap, AttributeReference}
import org.apache.spark.sql.catalyst.plans.logical.{ColumnStat, LeafNode, LogicalPlan, Statistics}
import org.apache.spark.sql.types.IntegerType


class StatsEstimationTestBase extends SparkFunSuite {

  /** Enable stats estimation based on CBO. */
  protected val conf = SimpleCatalystConf(caseSensitiveAnalysis = true, cboEnabled = true)

  def attr(colName: String): AttributeReference = AttributeReference(colName, IntegerType)()

  /** Convert (column name, column stat) pairs to an AttributeMap based on plan output. */
  def toAttributeMap(colStats: Seq[(String, ColumnStat)], plan: LogicalPlan)
    : AttributeMap[ColumnStat] = {
    val nameToAttr: Map[String, Attribute] = plan.output.map(a => (a.name, a)).toMap
    AttributeMap(colStats.map(kv => nameToAttr(kv._1) -> kv._2))
  }
}

/**
 * This class is used for unit-testing. It's a logical plan whose output and stats are passed in.
 */
protected case class StatsTestPlan(outputList: Seq[Attribute], stats: Statistics) extends LeafNode {
  override def output: Seq[Attribute] = outputList
  override def computeStats(conf: CatalystConf): Statistics = stats
}
