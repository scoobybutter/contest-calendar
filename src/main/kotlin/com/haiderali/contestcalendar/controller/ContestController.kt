package com.haiderali.contestcalendar.controller

import com.haiderali.contestcalendar.service.ContestService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("contests")
class ContestController(private val contestService: ContestService) {

    @GetMapping("/open")
    fun getOpenContests(@RequestParam(defaultValue = "true") cached: Boolean) =
        contestService.getOpenContests(cached)

    @GetMapping("/upcoming")
    fun getUpcomingContests(@RequestParam(defaultValue = "true") cached: Boolean) =
        contestService.getUpcomingContests(cached)
}
