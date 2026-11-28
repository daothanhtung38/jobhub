package com.jubhub.controller;

import com.jubhub.common.ApiResponse;
import com.jubhub.model.dto.response.SkillResponse;
import com.jubhub.service.interfaces.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Skills", description = "Tìm kiếm danh mục kỹ năng")
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @Operation(summary = "Tìm kiếm kỹ năng", description = "Tìm kiếm kỹ năng có phân trang")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<SkillResponse>>> searchSkill(
            @RequestParam(
                    value = "keyword",
                    defaultValue = ""
            ) String keyword,

            @RequestParam(
                    value = "page",
                    defaultValue = "0"
            ) int page,

            @RequestParam(
                    value = "size",
                    defaultValue = "10"
            ) int size
    ) {
        Page<SkillResponse> responses = skillService.searchSkills(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("Tìm kiếm kỹ năng thành công", responses));
    }

}
