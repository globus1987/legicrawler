import author from 'app/entities/author/author.reducer';
import book from 'app/entities/book/book.reducer';
import cycle from 'app/entities/cycle/cycle.reducer';
import collection from 'app/entities/collection/collection.reducer';
import statistics from 'app/entities/statistics/statistics.reducer';
import history from 'app/entities/history/history.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  author,
  book,
  cycle,
  collection,
  statistics,
  history,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
