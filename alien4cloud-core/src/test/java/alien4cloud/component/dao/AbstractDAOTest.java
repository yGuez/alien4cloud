package alien4cloud.component.dao;

import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import org.elasticsearch.action.deletebyquery.DeleteByQueryAction;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.mapping.ElasticSearchClient;
import org.junit.After;
import org.junit.Before;

import alien4cloud.dao.ElasticSearchDAO;
import alien4cloud.model.application.Application;
import alien4cloud.model.topology.Topology;

public abstract class AbstractDAOTest {

    @Resource
    protected ElasticSearchClient esclient;

    protected Client nodeClient;

    @Before
    public void before() throws Exception {
        nodeClient = esclient.getClient();
        clean();
    }

    private void refresh(String indexName) {
        nodeClient.admin().indices().prepareRefresh(indexName).execute().actionGet();
    }

    private void clearIndex(String indexName, String... types) throws InterruptedException, ExecutionException {
        new DeleteByQueryRequestBuilder(nodeClient, DeleteByQueryAction.INSTANCE).setIndices(indexName).setTypes(types).setQuery(QueryBuilders.matchAllQuery())
                .execute().actionGet();
    }

    public void refresh() {
        refresh(Application.class.getSimpleName().toLowerCase());
        refresh(ElasticSearchDAO.TOSCA_ELEMENT_INDEX);
        refresh(Topology.class.getSimpleName().toLowerCase());
    }

    @After
    public void clean() throws Exception {
        clearIndex(Application.class.getSimpleName().toLowerCase());
        clearIndex(ElasticSearchDAO.TOSCA_ELEMENT_INDEX);
        clearIndex(Topology.class.getSimpleName().toLowerCase());
        refresh();
    }
}
