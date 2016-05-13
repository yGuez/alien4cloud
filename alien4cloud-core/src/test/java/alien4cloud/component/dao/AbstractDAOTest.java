package alien4cloud.component.dao;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import org.elasticsearch.action.deletebyquery.DeleteByQueryAction;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.mapping.ElasticSearchClient;
import org.elasticsearch.mapping.MappingBuilder;
import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import alien4cloud.dao.ElasticSearchDAO;
import alien4cloud.dao.IGenericSearchDAO;
import alien4cloud.model.application.Application;
import alien4cloud.model.components.IndexedToscaElement;
import alien4cloud.model.topology.Topology;

public abstract class AbstractDAOTest {

    @Resource
    protected ElasticSearchClient esclient;

    @Resource(name = "alien-es-dao")
    protected IGenericSearchDAO dao;

    protected Client nodeClient;

    protected final ObjectMapper jsonMapper = new ObjectMapper();

    @Before
    public void before() throws Exception {
        nodeClient = esclient.getClient();
        clean();
    }

    private GetResponse getDocument(String indexName, String typeName, String id) {
        return nodeClient.prepareGet(indexName, typeName, id).execute().actionGet();
    }

    protected void saveDataToES(IndexedToscaElement... dataTest) throws JsonProcessingException {
        for (IndexedToscaElement datum : dataTest) {
            String typeName = MappingBuilder.indexTypeFromClass(datum.getClass());
            dao.save(datum);
            assertDocumentExist(ElasticSearchDAO.TOSCA_ELEMENT_INDEX, typeName, datum.getId(), true);
        }
        refresh();
    }

    private void assertDocumentExist(String indexName, String typeName, String id, boolean expected) {
        GetResponse response = getDocument(indexName, typeName, id);
        assertEquals(expected, response.isExists());
        assertEquals(expected, !response.isSourceEmpty());
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
