package com.project.api;

import com.project.VO.article.ArticleVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "articleFeignClient", url = "http://localhost:8084")
public interface ArticleFeignClient {

}