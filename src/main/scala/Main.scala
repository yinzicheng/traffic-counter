package org.abc.trafficcounter

import com.typesafe.scalalogging.Logger


object Main {

  val logger: Logger = Logger(getClass.getName)

  def main(args: Array[String]): Unit = {
    val dataDir = "src/main/resources/data/"
    val counter = TrafficCounter(dataDir)
    logger.info(s"\n1. Total cars:\n${counter.totalCars}")
    logger.info(s"\n2. Daily cars:\n${counter.dailyCars.mkString("\n")}")
    logger.info(s"\n3. Top 3 hourly cars:\n${counter.getTopHalfHourlyCars(3).mkString("\n")}")
    logger.info(s"\n4. Least Car of Contiguous Periods:\n${counter.getLeastCarOfContiguousPeriods(3).mkString("\n")}")
  }

}
