/**
 * Loads search results onto page
 */
function loadSearchResults() {
  const searchParams = new URLSearchParams(window.location.search);
  if (!searchParams.has('query')) {
    return;
  }
  const query = searchParams.get('query');
  const searchInput = document.getElementById('search-input');
  searchInput.value = query;
  $.ajax({
    url: '/api/search/deals',
    data: {
      query: query,
    },
  }).done((deals) => {
    const resutlsDiv = document.getElementById('results');
    for (const deal of deals) {
      const card = createDealCard(deal);
      resutlsDiv.appendChild(card);
    }
  });
}

loadSearchResults();
