package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.article.ArticleVO;
import com.project.domain.Article;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService extends IService<Article> {
    /**
     * 查询用户所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    List<ArticleVO> queryArticleByUserId(Long id);

    /**
     * 查询关注用户所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    List<ArticleVO> queryArticleOfAttention(Long userId);

    /**
     * 查询校园所有动态信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - list 集合
     */
    List<ArticleVO> queryArticleOfSchool(Long userId);

    /**
     * 查询用户动态数量
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - count 动态数量
     */
    Integer queryArticleCount(Long userId);

    /**
     * 根据动态id删除动态以及相关信息
     * 请求数据:
     * - userId 用户id
     * 响应数据:
     * - count 动态数量
     */
    boolean deleteArticleByArticleId(Long articleId);

    /**
     * 上传校园动态
     * 请求数据:
     * - file 图片二进制数据
     * - text 文本内容
     * - count 图片数量
     */
    boolean uploadArticle(List<MultipartFile> files, String text);
}
