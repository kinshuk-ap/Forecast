package com.example.forecast.internal

import java.io.IOException

class NoConnectivityException: IOException()
class LocationPermissionNotGrantedException: IOException()
class DateNotFoundException: Exception()