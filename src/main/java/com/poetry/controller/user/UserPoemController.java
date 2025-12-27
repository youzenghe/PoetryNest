package com.poetry.controller.user;

import com.poetry.common.PageResult;
import com.poetry.common.Result;
import com.poetry.dto.response.PoemAnnotationVO;
import com.poetry.dto.response.PoemVO;
import com.poetry.service.PoemAnnotationService;
import com.poetry.service.PoemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/user/poems")
public class UserPoemController {

    @Autowired
    private PoemService poemService;

    @Autowired
    private PoemAnnotationService annotationService;

    @GetMapping
    public Result<PageResult<PoemVO>> search(
            @RequestParam(value = "q", required = false, defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return Result.success(poemService.search(query, page, size));
    }

    @GetMapping("/{id}")
    public Result<PoemVO> detail(@PathVariable Long id) {
        return Result.success(poemService.getDetail(id));
    }

    @GetMapping("/random")
    public Result<PoemVO> random() {
        return Result.success(poemService.getRandomPoem());
    }

    @GetMapping("/random/list")
    public Result<List<PoemVO>> randomList(@RequestParam(value = "num", defaultValue = "5") int num) {
        return Result.success(poemService.getRandomPoems(num));
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> export(@PathVariable Long id,
                                          @RequestParam(value = "format", defaultValue = "txt") String format) {
        String content = poemService.exportPoem(id, format);
        String contentType = "json".equals(format) ? "application/json" : "text/plain";
        String ext = "json".equals(format) ? ".json" : ".txt";
        String filename = URLEncoder.encode("poem" + id + ext, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType + ";charset=UTF-8"))
                .body(content.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/share")
    public Result<PoemVO> share() {
        return Result.success(poemService.getShareData());
    }

    @GetMapping("/{id}/annotations")
    public Result<List<PoemAnnotationVO>> getAnnotations(@PathVariable Long id) {
        return Result.success(annotationService.getByPoemId(id));
    }
}
