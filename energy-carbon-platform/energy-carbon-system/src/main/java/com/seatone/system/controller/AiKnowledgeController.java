package com.seatone.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seatone.common.result.PageResult;
import com.seatone.common.result.Result;
import com.seatone.system.dto.AiKnowledgeDTO;
import com.seatone.system.entity.AiKnowledgeDoc;
import com.seatone.system.service.AiKnowledgeDocService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "AI知识库管理")
@RestController
@RequestMapping("/ai/knowledge")
public class AiKnowledgeController {

    @Autowired
    private AiKnowledgeDocService knowledgeDocService;

    @ApiOperation("分页查询文档列表")
    @GetMapping("/list")
    public Result<PageResult<AiKnowledgeDTO>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("文档类型") @RequestParam(required = false) String docType,
            @ApiParam("上传用户") @RequestParam(required = false) String uploadUser) {
        Page<AiKnowledgeDoc> page = new Page<>(current, size);
        LambdaQueryWrapper<AiKnowledgeDoc> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeDoc::getDeleted, 0);
        if (docType != null && !docType.isEmpty()) {
            wrapper.eq(AiKnowledgeDoc::getDocType, docType);
        }
        if (uploadUser != null && !uploadUser.isEmpty()) {
            wrapper.eq(AiKnowledgeDoc::getUploadUser, uploadUser);
        }
        wrapper.orderByDesc(AiKnowledgeDoc::getCreateTime);
        Page<AiKnowledgeDoc> resultPage = knowledgeDocService.page(page, wrapper);
        List<AiKnowledgeDTO> records = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(PageResult.of(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal(), records));
    }

    @ApiOperation("上传文档")
    @PostMapping("/upload")
    public Result<AiKnowledgeDTO> upload(
            @ApiParam("文档名称") @RequestParam String docName,
            @ApiParam("文档类型") @RequestParam String docType,
            @ApiParam("上传用户") @RequestParam(required = false) String uploadUser,
            @ApiParam("文档摘要") @RequestParam(required = false) String summary,
            @ApiParam("文件") @RequestParam("file") MultipartFile file) {
        AiKnowledgeDoc doc = new AiKnowledgeDoc();
        doc.setDocName(docName);
        doc.setDocType(docType);
        doc.setUploadUser(uploadUser);
        doc.setSummary(summary);
        if (file != null && !file.isEmpty()) {
            doc.setFilePath("/uploads/knowledge/" + file.getOriginalFilename());
            doc.setFileSize(file.getSize());
        }
        doc.setUploadTime(LocalDateTime.now());
        doc.setStatus(1);
        doc.setDeleted(0);
        knowledgeDocService.save(doc);
        return Result.ok(convertToDTO(doc));
    }

    @ApiOperation("文档检索")
    @GetMapping("/search")
    public Result<List<AiKnowledgeDTO>> search(
            @ApiParam("关键词") @RequestParam String keyword) {
        LambdaQueryWrapper<AiKnowledgeDoc> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeDoc::getDeleted, 0);
        wrapper.and(w -> w.like(AiKnowledgeDoc::getDocName, keyword)
                .or().like(AiKnowledgeDoc::getSummary, keyword));
        wrapper.orderByDesc(AiKnowledgeDoc::getCreateTime);
        List<AiKnowledgeDoc> docs = knowledgeDocService.list(wrapper);
        List<AiKnowledgeDTO> records = docs.stream().map(this::convertToDTO).collect(Collectors.toList());
        return Result.ok(records);
    }

    @ApiOperation("获取文档详情")
    @GetMapping("/{id}")
    public Result<AiKnowledgeDTO> getById(@ApiParam("文档ID") @PathVariable Long id) {
        AiKnowledgeDoc entity = knowledgeDocService.getById(id);
        if (entity == null) {
            return Result.error("文档不存在");
        }
        return Result.ok(convertToDTO(entity));
    }

    @ApiOperation("删除文档")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@ApiParam("文档ID") @PathVariable Long id) {
        AiKnowledgeDoc doc = new AiKnowledgeDoc();
        doc.setId(id);
        doc.setDeleted(1);
        knowledgeDocService.updateById(doc);
        return Result.ok();
    }

    private AiKnowledgeDTO convertToDTO(AiKnowledgeDoc entity) {
        AiKnowledgeDTO dto = new AiKnowledgeDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
