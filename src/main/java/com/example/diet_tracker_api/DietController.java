package com.example.diet_tracker_api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;

/* Controller in charge of handling all requests coming to /hello.
 */

@Controller
@AllArgsConstructor
@RequestMapping("/hello")
public class DietController {
    /* 
    * Diet Service autowired object. 
    */
    private DietService dietService;

    /*
     * GET /world endpoint.  
    */
    @GetMapping("/world")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.status(HttpStatus.OK).body(dietService.getHelloWorld());

    }

}
