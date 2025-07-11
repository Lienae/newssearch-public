package com.tjeoun.newssearch.config;

import com.tjeoun.newssearch.document.NewsDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticSearchInitializer implements ApplicationRunner {
    private final ElasticsearchOperations elasticsearchOperations;

//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        IndexOperations indexOps = elasticsearchOperations.indexOps(NewsDocument.class);
//        if(indexOps.exists()){
//            System.out.println("deleting existing index" + indexOps.getIndexCoordinates().getIndexName());
//            indexOps.delete();
//        }
//
//        System.out.println("creating new index" + indexOps.getIndexCoordinates().getIndexName());
//        indexOps.create();
//
//        indexOps.putMapping();
//        System.out.println("index initialization complete");
//    }
        @Override
        public void run(ApplicationArguments args) {
            IndexOperations indexOps = elasticsearchOperations.indexOps(NewsDocument.class);
            if (!indexOps.exists()) {
                System.out.println("Creating index: " + indexOps.getIndexCoordinates().getIndexName());
                indexOps.create();
                indexOps.putMapping();
                System.out.println("Index initialization complete");
            } else {
                System.out.println("Index already exists: " + indexOps.getIndexCoordinates().getIndexName());
            }
        }


}
