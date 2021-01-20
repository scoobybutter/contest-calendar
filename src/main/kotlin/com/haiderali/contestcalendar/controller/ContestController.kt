package com.haiderali.contestcalendar.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ContestController {

    @GetMapping("/hello-world")
    fun helloWorld() = "hello world"
}
