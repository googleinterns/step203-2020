package com.google.step.datamanager;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.step.model.Deal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DealSearchManagerIndex implements DealSearchManager {
  Index index;

  public DealSearchManagerIndex() {
    IndexSpec indexSpec = IndexSpec.newBuilder().setName("Deal").build();
    index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
  }

  @Override
  public List<Long> search(String query, List<Long> tagIds) {
    // replace characters that might cause syntax errors with space character and change all to
    // lowercase
    query = query.replaceAll("[^a-zA-Z0-9-_/]", " ");
    query = query.toLowerCase();
    query = query.trim();

    String tags = tagsToString(tagIds);

    String queryString = "";
    if (!query.isEmpty()) {
      queryString += " description=(" + query + ")";
    }
    if (!tags.isEmpty()) {
      queryString += " tags=(" + tags + ")";
    }

    List<Long> dealIds = new ArrayList<>();
    Results<ScoredDocument> results = index.search(queryString);
    for (ScoredDocument document : results) {
      long id = Long.parseLong(document.getId());
      dealIds.add(id);
    }
    return dealIds;
  }

  @Override
  public void addDeal(Deal deal, List<Long> tagIds) {
    String tags = tagsToString(tagIds);
    Document doc =
        Document.newBuilder()
            .setId(Long.toString(deal.id))
            .addField(Field.newBuilder().setName("description").setText(deal.description))
            .addField(Field.newBuilder().setName("tags").setText(tags))
            .build();
    index.put(doc);
  }

  /** Joins the list of tag IDs in a string */
  private String tagsToString(List<Long> tagIds) {
    List<String> tagIdsString =
        tagIds.stream().map(x -> Long.toString(x)).collect(Collectors.toList());
    return String.join(" ", tagIdsString).trim();
  }

  @Override
  public void removeDeal(long id) {
    index.delete(Long.toString(id));
  }
}
