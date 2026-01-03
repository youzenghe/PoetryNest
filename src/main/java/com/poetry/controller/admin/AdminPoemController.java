package com.poetry.controller.admin;

import com.poetry.common.Result;
import com.poetry.dto.request.PoemCreateRequest;
import com.poetry.dto.response.PoemVO;
import com.poetry.service.DataImportService;
import com.poetry.service.PoemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/poems")
public class AdminPoemController {

    @Autowired
    private PoemService poemService;

    @Autowired
    private DataImportService dataImportService;

    @PostMapping
    public Result<PoemVO> create(@Valid @RequestBody PoemCreateRequest request) {
        return Result.success("诗词创建成功", poemService.createPoem(request));
    }

    @PutMapping("/{id}")
    public Result<PoemVO> update(@PathVariable Long id, @Valid @RequestBody PoemCreateRequest request) {
        return Result.success("诗词更新成功", poemService.updatePoem(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        poemService.deletePoem(id);
        return Result.success("诗词删除成功", null);
    }

    @PostMapping("/import")
    public Result<Map<String, Object>> importPoems(
            @RequestParam(value = "dataDir", defaultValue = "./poemsdata/json") String dataDir) {
        int count = dataImportService.importFromJson(dataDir);
        return Result.success("导入完成", Map.of("imported", count));
    }
}
