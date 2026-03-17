package org.example.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/***
 * 向量化的配置：
 * 1. 首先声明对应向量数据库的配置类
 */
@Configuration
@Data
public class MilvusConfig {


    // Milvus配置
    @Value("${milvus.host}")
    private String milvusHost;        // Milvus地址（如：127.0.0.1）
    @Value("${milvus.port}")
    private int milvusPort;           // Milvus端口（默认19530）
    @Value("${milvus.collection-name}")
    private String collectionName;    // 向量集合名
    @Value("${milvus.dimension}")
    private int dimension;            // 向量维度（百炼模型通常为768/1536，需和模型匹配）




}
