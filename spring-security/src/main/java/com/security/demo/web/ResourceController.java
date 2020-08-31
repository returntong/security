package com.security.demo.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
public class ResourceController {


    //    @PreAuthorize("hasAuthority('p1')")
    @GetMapping("/getResource")
    public String getResource() {
        return "访问资源";
    }

    @GetMapping("/getResource2")
    @PreAuthorize("hasAuthority('EDIT_PATROL')")
    public String getResource2() {
        return "访问资源2";
    }

    @GetMapping("/getUsername")
    public String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }
}
