package com.sec.api.securedapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", authorizationHeader.substring(7));
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> resp = restTemplate.exchange("http://localhost:8080/validate",
                HttpMethod.POST, httpEntity, JsonNode.class);
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            JsonNode body = resp.getBody();
            System.out.println(body.toPrettyString());
            ArrayNode array = (ArrayNode) body.get("authorities");
            List<SimpleGrantedAuthority> auths = new ArrayList<>();
            if(array != null){
                for(int i=0; i < array.size(); i++){
                    auths.add(new SimpleGrantedAuthority(array.get(i).get("authority").asText()));
                }
            }
            User user = new User(body.get("username").asText(), "null", auths);
            UserDetails userDetails = user;

            if (resp.getStatusCode() == HttpStatus.OK) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}