package org.abc.trafficcounter

import TrafficCounter._
import Utils._

import com.typesafe.scalalogging.Logger

import java.io.File
import java.time.format.DateTimeFormatter.ofPattern
import java.time.{LocalDate, LocalDateTime}
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

case class HalfHourlyCar(timestamp: LocalDateTime, cars: Int) {
  override def toString = s"$timestamp $cars"
}

case class DailyCar(date: LocalDate, cars: Int) {
  override def toString = s"$date $cars"
}


object Utils {

  def timestampToDate(timestamp: LocalDateTime, pattern: String = "yyyy-MM-dd"): LocalDate = {
    val dateStr = timestamp.toString.substring(0, 10)
    LocalDate.parse(dateStr, ofPattern(pattern))
  }

}

object TrafficCounter {

  val logger: Logger = Logger(getClass.getName)

  def apply(dataDir: String): TrafficCounter = {
    new TrafficCounter(dataDir)
  }

  /**
   * read input file and convert to List of HalfHourlyCar
   *
   * @param filePath Input file path
   * @return
   */
  def getHalfHourlyCarsFromFile(filePath: String): Try[List[HalfHourlyCar]] = {
    Try {
      Using(Source.fromFile(filePath)) { source =>
        source.getLines()
          .filterNot(_.trim.isEmpty)
          .map(line => lineToHalfHourlyCar(line))
          .toList
      }.get
    }
  }

  def lineToHalfHourlyCar(line: String, pattern: String = "yyyy-MM-dd'T'HH:mm:ss"): HalfHourlyCar = {
    val Array(timestampStr, cars) = line.split("\\s+")
    val timestamp: LocalDateTime = LocalDateTime.parse(timestampStr, ofPattern(pattern))
    HalfHourlyCar(timestamp, cars.toInt)
  }

}


class TrafficCounter(dataDir: String) {

  val allHalfHourlyCars: Vector[HalfHourlyCar] = getAllHalfHourlyCars()

  def getAllHalfHourlyCars(fileExt: String = "txt"): Vector[HalfHourlyCar] = {
    val inputDir = new File(dataDir)
    if (inputDir.exists()) {
      inputDir.listFiles()
        .map(_.getAbsolutePath)
        .filter(_.endsWith(fileExt))
        .map { filePath =>
          getHalfHourlyCarsFromFile(filePath) match {
            case Success(items) => items
            case Failure(e) =>
              logger.error(s"Failed to read data from file $filePath", e.getMessage)
              throw e
          }
        }.foldLeft[Vector[HalfHourlyCar]](Vector()) { (agg, item) =>
        agg.appendedAll(item)
      }
    } else {
      throw new RuntimeException(s"Input data directory $dataDir doesn't exist...")
    }
  }

  /**
   * Get total number of cars from input source
   *
   * @return total number of cars
   */
  def totalCars: Int = {
    allHalfHourlyCars
      .map(_.cars)
      .sum
  }


  /**
   * Get list of daily cars ordering by date
   *
   * @return
   */
  def dailyCars: List[DailyCar] = {
    allHalfHourlyCars
      .map(item => DailyCar(timestampToDate(item.timestamp), item.cars))
      .groupMapReduce(_.date)(_.cars)(_ + _)
      .toList
      .map { case (date, cars) => DailyCar(date, cars) }
      .sortBy(_.date)
  }


  /**
   * Get top N half Hours with most cars
   *
   * @param n Top N
   * @return The top 3 half hours with most cars, in the same format as the input file
   */
  def getTopHalfHourlyCars(n: Int = 3): Vector[HalfHourlyCar] = {
    allHalfHourlyCars
      .sortBy(_.cars)(Ordering[Int].reverse)
      .take(n)
  }


  /**
   * Get the records with the least cars during n contiguous periods
   *
   * @param numOfPeriod number of Contiguous periods (half overly)
   */
  def getLeastCarOfContiguousPeriods(numOfPeriod: Int = 3): Seq[HalfHourlyCar] = {
    val length = allHalfHourlyCars.length
    val listOfItems = for (from <- 0 until length - 2)
      yield allHalfHourlyCars.slice(from, from + numOfPeriod)

    listOfItems.minBy(list => list.map(_.cars).sum).sortBy(_.cars)
  }


}
