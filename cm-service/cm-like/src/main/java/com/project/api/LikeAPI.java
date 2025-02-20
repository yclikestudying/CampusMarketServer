package com.project.api;

import com.project.service.LikesService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/likes")
@Slf4j
public class LikeAPI {
    @Resource
    private LikesService likesService;

}
