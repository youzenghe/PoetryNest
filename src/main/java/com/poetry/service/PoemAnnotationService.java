package com.poetry.service;

import com.poetry.dto.request.AnnotationRequest;
import com.poetry.dto.response.PoemAnnotationVO;
import com.poetry.entity.PoemAnnotation;
import com.poetry.mapper.PoemAnnotationMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PoemAnnotationService {

    @Autowired
    private PoemAnnotationMapper annotationMapper;

    public List<PoemAnnotationVO> getByPoemId(Long poemId) {
        return annotationMapper.findByPoemId(poemId).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    public PoemAnnotationVO getById(Long id) {
        PoemAnnotation annotation = annotationMapper.findById(id);
        if (annotation == null) {
            throw new IllegalArgumentException("注解不存在");
        }
        return toVO(annotation);
    }

    public PoemAnnotationVO create(AnnotationRequest request, Long adminId) {
        PoemAnnotation annotation = new PoemAnnotation();
        annotation.setPoemId(request.getPoemId());
        annotation.setTitle(request.getTitle());
        annotation.setContent(request.getContent());
        annotation.setAuthorId(adminId);
        annotationMapper.insert(annotation);
        return toVO(annotationMapper.findById(annotation.getId()));
    }

    public PoemAnnotationVO update(Long id, AnnotationRequest request, Long adminId) {
        PoemAnnotation annotation = annotationMapper.findById(id);
        if (annotation == null) {
            throw new IllegalArgumentException("注解不存在");
        }
        annotation.setTitle(request.getTitle());
        annotation.setContent(request.getContent());
        annotationMapper.update(annotation);
        return toVO(annotationMapper.findById(id));
    }

    public void delete(Long id) {
        PoemAnnotation annotation = annotationMapper.findById(id);
        if (annotation == null) {
            throw new IllegalArgumentException("注解不存在");
        }
        annotationMapper.deleteById(id);
    }

    private PoemAnnotationVO toVO(PoemAnnotation annotation) {
        PoemAnnotationVO vo = new PoemAnnotationVO();
        BeanUtils.copyProperties(annotation, vo);
        return vo;
    }
}
