package com.sec.api.securedapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BDDApi {

    @GetMapping("/hitApi")
    public String testGetAPI(@RequestParam boolean success) throws Exception{
        if(success){
            return "successful";
        }
        return "failed";
    }
}
