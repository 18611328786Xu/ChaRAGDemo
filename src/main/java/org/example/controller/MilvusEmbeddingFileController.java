package org.example.controller;

import jakarta.annotation.Resource;
import org.example.util.ReadResouceEmbeddingFileUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/milvus")
public class MilvusEmbeddingFileController {


    @Resource
    private ReadResouceEmbeddingFileUtil readResouceEmbeddingFileUtil;

    @RequestMapping("/addMilvusEmbeddingFile")
    public void addMilvusEmbeddingFile() throws Exception{
        readResouceEmbeddingFileUtil.initMilvusClient();
        IO.println("初始化milvus向量数据库success");
    }


}
