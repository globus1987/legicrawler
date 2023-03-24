import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './book.reducer';

export const BookDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const bookEntity = useAppSelector(state => state.book.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="bookDetailsHeading">Book</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">Id</span>
          </dt>
          <dd>{bookEntity.id}</dd>
          <dt>
            <span id="title">Title</span>
          </dt>
          <dd>{bookEntity.title}</dd>
          <dt>
            <span id="url">Url</span>
          </dt>
          <dt>
            <a href={bookEntity.url} color="link">
              {bookEntity.url}
            </a>{' '}
          </dt>
          <dt>
            <span id="ebook">Ebook</span>
          </dt>
          <dd>{bookEntity.ebook ? 'true' : 'false'}</dd>
          <dt>
            <span id="audiobook">Audiobook</span>
          </dt>
          <dd>{bookEntity.audiobook ? 'true' : 'false'}</dd>
          <dt>
            <span id="category">Category</span>
          </dt>
          <dd>{bookEntity.category}</dd>
          <dt>
            <span id="added">Added</span>
          </dt>
          <dd>{bookEntity.added ? <TextFormat value={bookEntity.added} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="kindleSubscription">Kindle Subscription</span>
          </dt>
          <dd>{bookEntity.kindleSubscription ? 'true' : 'false'}</dd>
          <dt>
            <span id="libraryPass">Library Pass</span>
          </dt>
          <dd>{bookEntity.libraryPass ? 'true' : 'false'}</dd>
          <dt>
            <span id="librarySubscription">Library Subscription</span>
          </dt>
          <dd>{bookEntity.librarySubscription ? 'true' : 'false'}</dd>
          <dt>
            <span id="subscription">Subscription</span>
          </dt>
          <dd>{bookEntity.subscription ? 'true' : 'false'}</dd>
          <dt>Cycle</dt>
          <dd>
            {' '}
            {bookEntity.cycle ? (
              <Button href={`/cycle/${bookEntity.cycle.id}`} color="link">
                {bookEntity.cycle.name}
              </Button>
            ) : (
              ''
            )}
          </dd>
          <dt>Authors</dt>
          <dd>
            {bookEntity.authors?.map(item => (
              <Button tag={Link} to={`/author/${item.id}`} color="black" size="sm">
                {item.name}
              </Button>
            ))}
          </dd>
        </dl>
        <Button tag={Link} to="/book" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/book/${bookEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default BookDetail;
