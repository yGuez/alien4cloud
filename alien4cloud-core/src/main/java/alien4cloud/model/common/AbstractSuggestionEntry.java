package alien4cloud.model.common;

import static alien4cloud.dao.FetchContext.SUMMARY;

import java.util.Set;

import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.query.FetchContext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;

@ESObject
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractSuggestionEntry {

    /**
     * List of values that can be suggested for the property ( for example Windows, Linux, Mac OS etc ...)
     */
    @FetchContext(contexts = { SUMMARY }, include = { false })
    private Set<String> suggestions = Sets.newHashSet();

    public abstract String getId();

    public abstract void setId(String id);

}
