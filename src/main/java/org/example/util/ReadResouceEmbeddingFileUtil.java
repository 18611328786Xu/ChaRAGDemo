package org.example.util;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.aliyuncs.IAcsClient;
import io.milvus.client.MilvusClient;
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.highlevel.collection.ListCollectionsParam;
import io.milvus.param.highlevel.collection.response.ListCollectionsResponse;
import io.milvus.param.index.CreateIndexParam;
import org.example.config.MilvusConfig;
import org.springframework.stereotype.Component;
import io.milvus.client.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/***
 * 读取resource文件夹下的文件，并向量化到数据库中
 */
@Component
public class ReadResouceEmbeddingFileUtil {


    private final MilvusConfig milvusConfig; //数据库配置类

    private MilvusClient milvusClient; //milvus客户端


    //初始化 Milvus客户端
    public ReadResouceEmbeddingFileUtil(MilvusConfig milvusConfig) {
        this.milvusConfig = milvusConfig;
//        initMilvusClient();
    }


    /***
     * 初始化milvus客户端
     */
    public void initMilvusClient() throws Exception {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(milvusConfig.getMilvusHost()) // Milvus 地址（如 127.0.0.1）
                .withPort(milvusConfig.getMilvusPort()) // Milvus 端口（如 19530）
                .withConnectTimeout(5000, TimeUnit.MILLISECONDS)            // 连接超时 5 秒（2.6.3 支持）
                .withKeepAliveTime(30000, TimeUnit.MILLISECONDS)            // 长连接保活 30 秒（2.6.3 支持）
                .build();
        // ========== 初始化客户端：2.6.3 直接 new MilvusClient ==========
        this.milvusClient = new MilvusServiceClient(connectParam);

        // 3. 核心修正：替换 isHealthy()，用 listCollections() 校验连接
        R<ListCollectionsResponse> listResult = milvusClient.listCollections(ListCollectionsParam.newBuilder().build());
        if (listResult.getStatus() != 0) {
            throw new RuntimeException("Milvus 2.6.3 连接失败：" + listResult.getMessage());
        }
        createMilvusCollectionIfNotExist();

    }

    /**
     * 创建Milvus集合（2.3.5版本兼容）
     */
    private void createMilvusCollectionIfNotExist() throws Exception {
        try {
            // 检查集合是否存在
            if (milvusClient.hasCollection(HasCollectionParam.newBuilder()
                    .withCollectionName(milvusConfig.getCollectionName()).build()).getData()) {
                return;
            }

            // 定义主键ID字段
            FieldType idField = FieldType.newBuilder()
                    .withName("id")
                    .withDataType(DataType.Int64)
                    .withPrimaryKey(true)
                    .withAutoID(false)
                    .build();

            // 定义向量字段（维度匹配DashScope模型，如768）
            FieldType vectorField = FieldType.newBuilder()
                    .withName("vector")
                    .withDataType(DataType.FloatVector)
                    .withDimension(milvusConfig.getDimension())
                    .build();

            // 定义原文文本字段
            FieldType textField = FieldType.newBuilder()
                    .withName("text")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(4096)
                    .build();
            List<FieldType> fieldTypes = Arrays.asList(idField, vectorField, textField);
            // 创建集合
            milvusClient.createCollection(CreateCollectionParam.newBuilder()
                    .withCollectionName(milvusConfig.getCollectionName())
                    .withFieldTypes(fieldTypes)
                    .withShardsNum(1)
                    .build());

            // 创建向量索引（2.3.5版本兼容IVF_FLAT索引）
            milvusClient.createIndex(CreateIndexParam.newBuilder()
                    .withCollectionName(milvusConfig.getCollectionName())
                    .withFieldName("vector")
                    .withIndexType(IndexType.IVF_FLAT)
                    .withMetricType(MetricType.COSINE)
                    .withExtraParam("{\"nlist\": 1024}")
                    .build());

        } catch (Exception e) {
            throw new RuntimeException("Milvus 2.3.5 集合创建失败", e);
        }
    }


}
