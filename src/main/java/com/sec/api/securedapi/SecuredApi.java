package com.sec.api.securedapi;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class SecuredApi {

    //@Secured("hasAuthority('ROLE_USER')")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("user")
    public List<String> list1(){
        return Stream.of("user1", "user2").collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("admin")
    public List<String> list2(){
        return Stream.of("user1", "admin1").collect(Collectors.toList());
    }

    @GetMapping("principal")
    public Principal list(Principal principal){
        return principal;
    }
}
