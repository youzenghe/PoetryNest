package com.poetry.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poetry.entity.Poem;
import com.poetry.mapper.PoemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataImportService {

    private static final Logger log = LoggerFactory.getLogger(DataImportService.class);
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private PoemMapper poemMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public int importFromJson(String dataDir) {
        File dir = new File(dataDir);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("数据目录不存在: " + dataDir);
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json") && !name.contains("DS_Store"));
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("目录中没有JSON文件");
        }

        int totalImported = 0;
        List<Poem> batch = new ArrayList<>(BATCH_SIZE);

        for (File file : files) {
            log.info("正在处理>> {}", file.getName());
            try {
                List<Map<String, Object>> jsonPoems = objectMapper.readValue(file,
                        new TypeReference<List<Map<String, Object>>>() {});

                for (Map<String, Object> jp : jsonPoems) {
                    String title = (String) jp.get("title");
                    String author = (String) jp.get("author");
                    @SuppressWarnings("unchecked")
                    List<String> paragraphsList = (List<String>) jp.get("paragraphs");
                    String paragraphs = String.join("", paragraphsList);

                    if (title != null && author != null && !paragraphs.isEmpty()) {
                        Poem poem = new Poem();
                        poem.setTitle(title);
                        poem.setAuthor(author);
                        poem.setParagraphs(paragraphs);
                        batch.add(poem);

                        if (batch.size() >= BATCH_SIZE) {
                            poemMapper.batchInsert(batch);
                            totalImported += batch.size();
                            batch.clear();
                        }
                    }
                }
            } catch (IOException e) {
                log.error("处理文件失败: {}", file.getName(), e);
            }
        }

        // Insert remaining
        if (!batch.isEmpty()) {
            poemMapper.batchInsert(batch);
            totalImported += batch.size();
        }

        log.info("导入完成，共导入 {} 首诗词", totalImported);
        return totalImported;
    }
}
