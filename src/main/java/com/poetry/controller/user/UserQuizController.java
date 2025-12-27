package com.poetry.controller.user;

import com.poetry.common.Result;
import com.poetry.dto.response.QuizVO;
import com.poetry.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/quiz")
public class UserQuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping
    public Result<List<QuizVO>> getQuiz(
            @RequestParam(value = "count", defaultValue = "5") int count) {
        count = Math.min(Math.max(count, 1), 20);
        return Result.success(quizService.generateQuiz(count));
    }

    @GetMapping("/single")
    public Result<QuizVO> getSingleQuestion(
            @RequestParam(value = "type", defaultValue = "author") String type) {
        QuizVO question = quizService.generateQuestion(type);
        return Result.success(question);
    }

    @PostMapping("/check")
    public Result<Map<String, Object>> checkAnswer(@RequestBody Map<String, Object> body) {
        Long poemId = body.get("poemId") != null ? Long.valueOf(body.get("poemId").toString()) : null;
        String type = (String) body.getOrDefault("type", "author");
        int userAnswer = Integer.parseInt(body.getOrDefault("userAnswer", "0").toString());
        int correctAnswer = Integer.parseInt(body.getOrDefault("correctAnswer", "0").toString());
        return Result.success(quizService.checkAnswer(poemId, type, userAnswer, correctAnswer));
    }
}
