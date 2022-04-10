package org.abc.trafficcounter

import java.time.format.DateTimeFormatter.ofPattern
import java.time.{LocalDate, LocalDateTime}

object Utils {

  def timestampToDate(timestamp: LocalDateTime, pattern: String = "yyyy-MM-dd"): LocalDate = {
    val dateStr = timestamp.toString.substring(0, 10)
    LocalDate.parse(dateStr, ofPattern(pattern))
  }

}