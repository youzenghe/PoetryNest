package com.poetry.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poetry.common.CacheClean;
import com.poetry.common.PageResult;
import com.poetry.dto.request.PoemCreateRequest;
import com.poetry.dto.response.PoemVO;
import com.poetry.entity.Poem;
import com.poetry.mapper.PoemMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PoemService {

    @Autowired
    private PoemMapper poemMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(value = "poems", key = "'search:' + #query + ':' + #pageNum + ':' + #pageSize")
    public PageResult<PoemVO> search(String query, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Poem> poems;
        if (query == null || query.trim().isEmpty()) {
            poems = poemMapper.findAll();
        } else {
            poems = poemMapper.search(query.trim());
        }
        PageInfo<Poem> pageInfo = new PageInfo<>(poems);
        List<PoemVO> voList = pageInfo.getList().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(voList, pageInfo.getTotal(), pageNum, pageSize);
    }

    @Cacheable(value = "poemDetail", key = "#id")
    public PoemVO getDetail(Long id) {
        Poem poem = poemMapper.findById(id);
        if (poem == null) {
            throw new IllegalArgumentException("诗词不存在");
        }
        PoemVO vo = toVO(poem);
        // Related poems by same author
        List<Poem> related = poemMapper.findByAuthor(poem.getAuthor());
        vo.setRelatedPoems(related.stream()
                .filter(p -> !p.getId().equals(id))
                .limit(5)
                .map(this::toVO)
                .collect(Collectors.toList()));
        return vo;
    }

    public PoemVO getRandomPoem() {
        Poem poem = poemMapper.findOneRandom();
        return poem != null ? toVO(poem) : null;
    }

    public List<PoemVO> getRandomPoems(int num) {
        return poemMapper.findRandom(num).stream().map(this::toVO).collect(Collectors.toList());
    }

    public String exportPoem(Long id, String format) {
        Poem poem = poemMapper.findById(id);
        if (poem == null) {
            throw new IllegalArgumentException("诗词不存在");
        }

        if ("json".equals(format)) {
            try {
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("id", poem.getId());
                data.put("title", poem.getTitle());
                data.put("author", poem.getAuthor());
                data.put("content", poem.getParagraphs());
                return objectMapper.writeValueAsString(data);
            } catch (Exception e) {
                throw new RuntimeException("JSON序列化失败", e);
            }
        }
        // default txt
        return String.format("《%s》\n作者：%s\n\n%s", poem.getTitle(), poem.getAuthor(), poem.getParagraphs());
    }

    public PoemVO getShareData() {
        Poem poem = poemMapper.findOneRandom();
        return poem != null ? toVO(poem) : null;
    }

    public Poem findById(Long id) {
        return poemMapper.findById(id);
    }

    // Admin methods
    @CacheClean(cacheNames = {"poems", "poemDetail"})
    public PoemVO createPoem(PoemCreateRequest request) {
        Poem poem = new Poem();
        poem.setTitle(request.getTitle());
        poem.setAuthor(request.getAuthor());
        poem.setParagraphs(request.getParagraphs());
        poemMapper.insertOne(poem);
        return toVO(poemMapper.findById(poem.getId()));
    }

    @CacheClean(cacheNames = {"poems", "poemDetail"})
    public PoemVO updatePoem(Long id, PoemCreateRequest request) {
        Poem poem = poemMapper.findById(id);
        if (poem == null) {
            throw new IllegalArgumentException("诗词不存在");
        }
        poem.setTitle(request.getTitle());
        poem.setAuthor(request.getAuthor());
        poem.setParagraphs(request.getParagraphs());
        poemMapper.update(poem);
        return toVO(poemMapper.findById(id));
    }

    @CacheClean(cacheNames = {"poems", "poemDetail"})
    public void deletePoem(Long id) {
        Poem poem = poemMapper.findById(id);
        if (poem == null) {
            throw new IllegalArgumentException("诗词不存在");
        }
        poemMapper.deleteById(id);
    }

    private PoemVO toVO(Poem poem) {
        PoemVO vo = new PoemVO();
        BeanUtils.copyProperties(poem, vo);
        return vo;
    }
}
