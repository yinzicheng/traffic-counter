package org.abc.trafficcounter

import org.abc.trafficcounter.Main.logger
import org.abc.trafficcounter.TrafficCounter.lineToHalfHourlyCar
import org.scalatest.freespec._

import java.io.FileNotFoundException
import java.time.format.DateTimeFormatter.ofPattern
import java.time.{LocalDate, LocalDateTime}
import scala.util.{Failure, Success}

class TrafficCounterTests extends AnyFreeSpec {

  "Smoke Test" - {

    "Smoke Test should not fail" in {
      val args = Array("src/main/resources/data/")
      Main.main(args)
    }
  }

  "Test utility functions in Utils" - {

    "Utils.timestampToDate should convert timestamp to date" in {
      val timestamp: LocalDateTime = LocalDateTime.parse("2021-12-05T15:30:00", ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
      val date = Utils.timestampToDate(timestamp)
      assert(date == LocalDate.of(2021, 12, 5))
    }
  }

  "Test static methods in TrafficCounter companion object" - {

    "lineToHalfHourlyCars should convert a text line to HalfHourlyCar" in {
      val hourlyCar = TrafficCounter.lineToHalfHourlyCar("2021-12-08T23:00:00 11")
      assert(hourlyCar.timestamp == LocalDateTime.parse("2021-12-08T23:00:00"))
      assert(hourlyCar.cars == 11)
    }

    "getHalfHourlyCarsFromFile should return list of HalfHourlyCar from a file" in {
      val testFilePath = "src/main/resources/test-data/test-traffic-input-1.txt"
      val hourlyCarList = TrafficCounter.getHalfHourlyCarsFromFile(testFilePath) match {
        case Success(data) => data
        case Failure(e) => throw e
      }
      assert(hourlyCarList.length == 3)
      assert(hourlyCarList.head.toString == "2021-12-01T05:00 8")
    }
  }


  "Test methods in TrafficCounter companion class" - {

    val dataDir = "src/main/resources/test-data/"
    val counter = TrafficCounter(dataDir)

    "getAllHalfHourlyCars should return all HalfHourlyCar items from all files in the input path" in {
      val halfHourlyCars = counter.getAllHalfHourlyCars()
      assert(halfHourlyCars.length == 8)
      val totalCars = halfHourlyCars.map(_.cars).sum
      assert(totalCars == 27)
    }

    "TrafficCounter should throws exception if input file is unavailable" in {
      val dataDir = "src/main/resources/dummy/"
      assertThrows[FileNotFoundException] {
        TrafficCounter(dataDir)
      }
    }

    "totalCars should return total number of cars from input data" in {
      val total = counter.totalCars
      assert(total == 27)
    }

    "dailyCars should return DailyCar record per day from input data" in {
      val dailyCars = counter.dailyCarsPar()
      assert(dailyCars.head.toString == "2021-12-01 11")
      assert(dailyCars.last.toString == "2021-12-02 16")
    }

    "getTopHalfHourlyCars should return top n records with most cars" in {
      val top1 = counter.getTopHalfHourlyCars(1)
      val top3 = counter.getTopHalfHourlyCars(3)
      assert(top1.head.toString == "2021-12-02T07:30 9")
      assert(top3.head.toString == "2021-12-02T07:30 9")
      assert(top3.last.toString == "2021-12-02T07:00 7")
    }

    "getLeastCarOfContiguousPeriods should return records with n contiguous periods" in {
      val least1 = counter.getLeastCarOfContiguousPeriods(1)
      assert(least1.head.toString == "2021-12-02T06:30 0")

      val least2 = counter.getLeastCarOfContiguousPeriods(2)
      assert(least2.head.toString == "2021-12-02T08:00 0")
      assert(least2.last.toString == "2021-12-02T08:30 0")

      val least3 = counter.getLeastCarOfContiguousPeriods(3)
      assert(least3.head.toString == "2021-12-02T06:30 0")
      assert(least3.last.toString == "2021-12-01T05:30 2")
    }

  }

  "Performance Test" - {
    val inputData = (1 to 10000000)
      .map(_ => lineToHalfHourlyCar("2021-12-08T23:00:00 1"))

    val counter = TrafficCounter(inputData)


    "1: Test code performance with large input data" ignore {
      logger.info(s"\n1. Total cars:\n${counter.totalCars}")
    }

    "2.1: Test code performance with large input data" in {
      logger.info(s"\n2. Daily cars:\n${counter.dailyCars().mkString("\n")}")
    }

    "2.2: Test code performance with large input data" in {
      logger.info(s"\n2. Daily cars:\n${counter.dailyCarsPartition(6).mkString("\n")}")
    }

    "3: Test code performance with large input data" ignore {
      logger.info(s"\n3. Top 3 hourly cars:\n${counter.getTopHalfHourlyCars(3).mkString("\n")}")
    }

    "4: Test code performance with large input data" ignore {
      logger.info(s"\n4. Least Car of Contiguous Periods:\n${counter.getLeastCarOfContiguousPeriods(3).mkString("\n")}")
    }
  }

}
