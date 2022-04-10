package org.abc.trafficcounter

import com.typesafe.scalalogging.Logger

object Main {

  val logger: Logger = Logger(getClass.getName)

  /**
   * Entrypoint
   *
   * @param args sample args:  [src/main/resources/data/]
   */
  def main(args: Array[String]): Unit = {
    logger.info(s"Main Parameters: ${args.mkString(" ")}")

    val dataDir = if (args.isEmpty) "src/main/resources/data/" else args(0)
    val counter = TrafficCounter(dataDir)

    logger.info(s"\n1. Total cars:\n${counter.totalCars}")
    logger.info(s"\n2. Daily cars:\n${counter.dailyCars.mkString("\n")}")
    logger.info(s"\n3. Top 3 hourly cars:\n${counter.getTopHalfHourlyCars(3).mkString("\n")}")
    logger.info(s"\n4. Least Car of Contiguous Periods:\n${counter.getLeastCarOfContiguousPeriods(3).mkString("\n")}")
  }

}
