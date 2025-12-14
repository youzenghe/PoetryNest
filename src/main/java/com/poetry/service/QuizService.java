package com.poetry.service;

import com.poetry.dto.response.QuizVO;
import com.poetry.entity.Poem;
import com.poetry.mapper.PoemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private PoemMapper poemMapper;

    private final Random random = new Random();

    /**
     * 生成一组随机题目
     */
    public List<QuizVO> generateQuiz(int count) {
        List<QuizVO> questions = new ArrayList<>();
        // 题目类型随机混合
        String[] types = {"author", "title", "fill_blank", "match"};

        for (int i = 0; i < count; i++) {
            String type = types[random.nextInt(types.length)];
            QuizVO question = generateQuestion(type);
            if (question != null) {
                question.setQuestionId((long) (i + 1));
                questions.add(question);
            }
        }
        return questions;
    }

    /**
     * 生成单道题目
     */
    public QuizVO generateQuestion(String type) {
        switch (type) {
            case "author":
                return generateAuthorQuestion();
            case "title":
                return generateTitleQuestion();
            case "fill_blank":
                return generateFillBlankQuestion();
            case "match":
                return generateMatchQuestion();
            default:
                return generateAuthorQuestion();
        }
    }

    /**
     * 题型1: 根据诗句猜作者
     */
    private QuizVO generateAuthorQuestion() {
        List<Poem> poems = poemMapper.findRandom(4);
        if (poems.size() < 4) return null;

        Poem target = poems.get(0);
        String excerpt = extractExcerpt(target.getParagraphs());

        List<String> options = poems.stream()
                .map(Poem::getAuthor)
                .distinct()
                .collect(Collectors.toList());

        // 确保有4个不同选项
        if (options.size() < 4) {
            List<Poem> more = poemMapper.findRandom(10);
            for (Poem p : more) {
                if (!options.contains(p.getAuthor())) options.add(p.getAuthor());
                if (options.size() >= 4) break;
            }
        }
        if (options.size() > 4) options = options.subList(0, 4);

        int correctIndex = options.indexOf(target.getAuthor());
        if (correctIndex < 0) {
            options.set(0, target.getAuthor());
            correctIndex = 0;
        }
        // 打乱选项（保持追踪正确答案）
        List<String> shuffled = new ArrayList<>(options);
        Collections.shuffle(shuffled);
        correctIndex = shuffled.indexOf(target.getAuthor());

        QuizVO vo = new QuizVO();
        vo.setType("author");
        vo.setQuestion("「" + excerpt + "」这句诗的作者是谁？");
        vo.setOptions(shuffled);
        vo.setCorrectIndex(correctIndex);
        vo.setExplanation("此句出自" + target.getAuthor() + "的《" + target.getTitle() + "》");
        vo.setPoemId(target.getId());
        vo.setPoemTitle(target.getTitle());
        vo.setPoemAuthor(target.getAuthor());
        return vo;
    }

    /**
     * 题型2: 根据作者猜诗名
     */
    private QuizVO generateTitleQuestion() {
        List<Poem> poems = poemMapper.findRandom(4);
        if (poems.size() < 4) return null;

        Poem target = poems.get(0);
        String excerpt = extractExcerpt(target.getParagraphs());

        List<String> options = poems.stream()
                .map(Poem::getTitle)
                .distinct()
                .collect(Collectors.toList());

        if (options.size() < 4) {
            List<Poem> more = poemMapper.findRandom(10);
            for (Poem p : more) {
                if (!options.contains(p.getTitle())) options.add(p.getTitle());
                if (options.size() >= 4) break;
            }
        }
        if (options.size() > 4) options = options.subList(0, 4);

        int correctIndex = options.indexOf(target.getTitle());
        if (correctIndex < 0) {
            options.set(0, target.getTitle());
            correctIndex = 0;
        }
        List<String> shuffled = new ArrayList<>(options);
        Collections.shuffle(shuffled);
        correctIndex = shuffled.indexOf(target.getTitle());

        QuizVO vo = new QuizVO();
        vo.setType("title");
        vo.setQuestion(target.getAuthor() + "写的「" + excerpt + "」出自哪首诗？");
        vo.setOptions(shuffled);
        vo.setCorrectIndex(correctIndex);
        vo.setExplanation("此句出自《" + target.getTitle() + "》");
        vo.setPoemId(target.getId());
        vo.setPoemTitle(target.getTitle());
        vo.setPoemAuthor(target.getAuthor());
        return vo;
    }

    /**
     * 题型3: 填空题 — 补全诗句
     */
    private QuizVO generateFillBlankQuestion() {
        List<Poem> poems = poemMapper.findRandom(5);
        Poem target = null;
        String[] sentences = null;

        for (Poem p : poems) {
            String text = p.getParagraphs();
            if (text == null) continue;
            // 按句号、逗号分句
            String[] parts = text.split("[。，！？；、,]");
            List<String> valid = Arrays.stream(parts)
                    .filter(s -> s.trim().length() >= 3 && s.trim().length() <= 20)
                    .collect(Collectors.toList());
            if (valid.size() >= 4) {
                target = p;
                sentences = valid.toArray(new String[0]);
                break;
            }
        }

        if (target == null || sentences == null) return generateAuthorQuestion();

        int blankIdx = random.nextInt(Math.min(sentences.length, 4));
        String answer = sentences[blankIdx].trim();

        // 构造题目文字：将目标句替换为____
        String questionText = target.getParagraphs().replace(answer, "____");
        if (questionText.equals(target.getParagraphs())) {
            return generateAuthorQuestion();
        }

        // 构造干扰项
        List<String> options = new ArrayList<>();
        options.add(answer);

        // 从其他诗中取干扰句
        for (Poem p : poems) {
            if (p.getId().equals(target.getId())) continue;
            String text = p.getParagraphs();
            if (text == null) continue;
            String[] parts = text.split("[。，！？；、,]");
            for (String part : parts) {
                String trimmed = part.trim();
                if (trimmed.length() >= 3 && trimmed.length() <= 20 && !options.contains(trimmed)) {
                    options.add(trimmed);
                    if (options.size() >= 4) break;
                }
            }
            if (options.size() >= 4) break;
        }

        // 补足选项
        while (options.size() < 4) {
            options.add("无此句");
        }
        if (options.size() > 4) options = new ArrayList<>(options.subList(0, 4));

        Collections.shuffle(options);
        int correctIndex = options.indexOf(answer);

        QuizVO vo = new QuizVO();
        vo.setType("fill_blank");
        vo.setQuestion("请补全诗句：" + truncate(questionText, 80));
        vo.setOptions(options);
        vo.setCorrectIndex(correctIndex);
        vo.setExplanation("正确答案为「" + answer + "」，出自" + target.getAuthor() + "《" + target.getTitle() + "》");
        vo.setPoemId(target.getId());
        vo.setPoemTitle(target.getTitle());
        vo.setPoemAuthor(target.getAuthor());
        return vo;
    }

    /**
     * 题型4: 诗句与诗名配对
     */
    private QuizVO generateMatchQuestion() {
        List<Poem> poems = poemMapper.findRandom(4);
        if (poems.size() < 4) return null;

        Poem target = poems.get(0);
        String excerpt = extractExcerpt(target.getParagraphs());

        List<String> options = new ArrayList<>();
        for (Poem p : poems) {
            String option = "《" + p.getTitle() + "》— " + p.getAuthor();
            if (!options.contains(option)) options.add(option);
        }
        while (options.size() < 4) {
            options.add("《未知》— 佚名");
        }
        if (options.size() > 4) options = new ArrayList<>(options.subList(0, 4));

        String correctOption = "《" + target.getTitle() + "》— " + target.getAuthor();
        Collections.shuffle(options);
        int correctIndex = options.indexOf(correctOption);

        QuizVO vo = new QuizVO();
        vo.setType("match");
        vo.setQuestion("「" + excerpt + "」出自以下哪部作品？");
        vo.setOptions(options);
        vo.setCorrectIndex(correctIndex);
        vo.setExplanation("出自" + target.getAuthor() + "《" + target.getTitle() + "》");
        vo.setPoemId(target.getId());
        vo.setPoemTitle(target.getTitle());
        vo.setPoemAuthor(target.getAuthor());
        return vo;
    }

    /**
     * 验证答案
     */
    public Map<String, Object> checkAnswer(Long poemId, String type, int userAnswer, int correctAnswer) {
        boolean isCorrect = userAnswer == correctAnswer;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("correct", isCorrect);
        result.put("userAnswer", userAnswer);
        result.put("correctAnswer", correctAnswer);
        result.put("message", isCorrect ? "回答正确！" : "回答错误，再接再厉！");
        return result;
    }

    /**
     * 从诗词正文中提取一句作为题面
     */
    private String extractExcerpt(String paragraphs) {
        if (paragraphs == null || paragraphs.isEmpty()) return "";
        String[] sentences = paragraphs.split("[。！？；]");
        List<String> valid = Arrays.stream(sentences)
                .map(String::trim)
                .filter(s -> s.length() >= 4 && s.length() <= 30)
                .collect(Collectors.toList());
        if (valid.isEmpty()) {
            return truncate(paragraphs, 30);
        }
        return valid.get(random.nextInt(valid.size()));
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }
}
