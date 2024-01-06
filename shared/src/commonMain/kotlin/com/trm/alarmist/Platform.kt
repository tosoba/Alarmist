package com.trm.alarmist

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform
