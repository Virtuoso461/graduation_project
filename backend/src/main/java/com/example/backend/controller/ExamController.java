package com.example.backend.controller;

import com.example.backend.dto.ExamStatsDTO;
import com.example.backend.entity.Exam;
import com.example.backend.entity.ExamResult;
import com.example.backend.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "考试管理", description = "提供考试相关的API，包括考试的创建、修改、删除、查询和提交等功能")
public class ExamController {

    private final ExamService examService;

    @Autowired
    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @Operation(summary = "创建考试", description = "创建新的考试，包含考试标题、描述、时间限制和考试题目等信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "考试创建成功"),
        @ApiResponse(responseCode = "400", description = "请求数据无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        try {
            Exam createdExam = examService.createExam(exam);
            return new ResponseEntity<>(createdExam, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "更新考试", description = "更新现有考试的信息，包括标题、描述、时间限制和考试题目等")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "考试更新成功"),
        @ApiResponse(responseCode = "404", description = "考试不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(
            @Parameter(description = "考试ID") @PathVariable("id") Long id,
            @RequestBody Exam exam) {
        try {
            Exam updatedExam = examService.updateExam(id, exam);
            if (updatedExam != null) {
                return new ResponseEntity<>(updatedExam, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "删除考试", description = "根据ID删除指定的考试")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "考试删除成功"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteExam(
            @Parameter(description = "考试ID") @PathVariable("id") Long id) {
        try {
            examService.deleteExam(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "获取考试详情", description = "根据ID获取指定考试的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取考试信息"),
        @ApiResponse(responseCode = "404", description = "考试不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(
            @Parameter(description = "考试ID") @PathVariable("id") Long id) {
        try {
            Exam exam = examService.getExamById(id);
            if (exam != null) {
                return new ResponseEntity<>(exam, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "获取考试列表", description = "获取考试列表，可以根据创建者邮箱进行过滤")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取考试列表"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllExams(
            @Parameter(description = "创建者邮箱，可选") @RequestParam(required = false) String email) {
        try {
            List<Exam> exams;

            if (email != null && !email.isEmpty()) {
                // 根据创建者邮箱获取考试
                exams = examService.getExamsByCreator(email);
            } else {
                // 获取所有可参加的考试
                exams = examService.getAvailableExams();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("exams", exams);
            response.put("total", exams.size());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "提交考试结果", description = "提交用户完成的考试结果，包含答案和分数信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "考试结果提交成功"),
        @ApiResponse(responseCode = "400", description = "请求数据无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/{id}/submit")
    public ResponseEntity<ExamResult> submitExamResult(
            @Parameter(description = "考试ID") @PathVariable("id") Long examId,
            @RequestBody ExamResult examResult) {
        try {
            // 确保设置了考试ID
            examResult.setExamId(examId);

            ExamResult savedResult = examService.submitExamResult(examResult);
            return new ResponseEntity<>(savedResult, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "获取已完成考试", description = "获取用户已完成的考试列表及结果")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取已完成考试列表"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/completed")
    public ResponseEntity<List<ExamResult>> getCompletedExams(
            @Parameter(description = "用户邮箱") @RequestParam("email") String email) {
        try {
            List<ExamResult> completedExams = examService.getCompletedExams(email);
            return new ResponseEntity<>(completedExams, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "获取考试统计信息", description = "获取用户的考试统计信息，包括完成数量、平均分数等")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取考试统计信息"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getExamStats(
            @Parameter(description = "用户邮箱") @RequestParam("email") String email) {
        try {
            Map<String, Object> stats = examService.getExamStats(email);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "按日期范围获取考试结果", description = "根据日期范围获取用户的考试结果")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取考试结果"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/results")
    public ResponseEntity<List<ExamResult>> getExamResultsByDateRange(
            @Parameter(description = "用户邮箱") @RequestParam("email") String email,
            @Parameter(description = "开始日期，格式：YYYY-MM-DD") @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @Parameter(description = "结束日期，格式：YYYY-MM-DD") @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        try {
            List<ExamResult> results = examService.getExamResultsByDateRange(email, startDate, endDate);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "获取特定考试结果", description = "获取用户对特定考试的结果")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取考试结果"),
        @ApiResponse(responseCode = "404", description = "考试结果不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{id}/result")
    public ResponseEntity<ExamResult> getExamResult(
            @Parameter(description = "考试ID") @PathVariable("id") Long examId,
            @Parameter(description = "用户邮箱") @RequestParam("email") String userEmail) {
        try {
            ExamResult result = examService.getExamResult(examId, userEmail);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}