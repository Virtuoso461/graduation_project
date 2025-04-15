package com.example.backend.controller;

import com.example.backend.dto.ExamStatsDTO;
import com.example.backend.entity.Exam;
import com.example.backend.entity.ExamResult;
import com.example.backend.service.ExamService;
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
public class ExamController {
    
    private final ExamService examService;
    
    @Autowired
    public ExamController(ExamService examService) {
        this.examService = examService;
    }
    
    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        try {
            Exam createdExam = examService.createExam(exam);
            return new ResponseEntity<>(createdExam, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable("id") Long id, @RequestBody Exam exam) {
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
    
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteExam(@PathVariable("id") Long id) {
        try {
            examService.deleteExam(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable("id") Long id) {
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
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllExams(@RequestParam(required = false) String email) {
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
    
    @PostMapping("/{id}/submit")
    public ResponseEntity<ExamResult> submitExamResult(@PathVariable("id") Long examId, 
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
    
    @GetMapping("/completed")
    public ResponseEntity<List<ExamResult>> getCompletedExams(@RequestParam("email") String email) {
        try {
            List<ExamResult> completedExams = examService.getCompletedExams(email);
            return new ResponseEntity<>(completedExams, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getExamStats(@RequestParam("email") String email) {
        try {
            Map<String, Object> stats = examService.getExamStats(email);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/results")
    public ResponseEntity<List<ExamResult>> getExamResultsByDateRange(
            @RequestParam("email") String email,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        try {
            List<ExamResult> results = examService.getExamResultsByDateRange(email, startDate, endDate);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}/result")
    public ResponseEntity<ExamResult> getExamResult(
            @PathVariable("id") Long examId,
            @RequestParam("email") String userEmail) {
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