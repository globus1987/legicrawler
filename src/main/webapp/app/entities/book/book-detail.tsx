import React, { useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './book.reducer';

export const BookDetail = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const bookEntity = useAppSelector(state => state.book.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="bookDetailsHeading">Book {bookEntity.id}</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="title">Title</span>
          </dt>
          <dd>{bookEntity.title}</dd>
          <dt>Authors</dt>
          <dd>
            {bookEntity.authors?.map(item => (
              <Button onClick={() => navigate(`/author/${item.id}`)} color="light" size="sm">
                {item.name}
              </Button>
            ))}
          </dd>
          <dt>
            <span id="category">Category</span>
          </dt>
          <dd>{bookEntity.category}</dd>
          {bookEntity.cycle ? (
            <dt>
              <span id="Cycle"> Cycle</span>
            </dt>
          ) : (
            ''
          )}
          {bookEntity.cycle ? (
            <dd>
              <Button onClick={() => navigate(`/cycle/${bookEntity.cycle.id}`)} color="light">
                {bookEntity.cycle.name}
              </Button>
            </dd>
          ) : (
            ''
          )}
          <dt>
            <span id="url">Url</span>
          </dt>
          <dt>
            <a href={bookEntity.url} color="link">
              {bookEntity.url}
            </a>{' '}
          </dt>
          <dd></dd>
          <dt>
            <FontAwesomeIcon style={bookEntity.ebook ? { color: 'green' } : { color: 'red' }} icon={bookEntity.ebook ? 'check' : 'xmark'} />
            <span id="ebook">Ebook</span>
          </dt>

          <dt>
            <FontAwesomeIcon
              style={bookEntity.audiobook ? { color: 'green' } : { color: 'red' }}
              icon={bookEntity.audiobook ? 'check' : 'xmark'}
            />
            <span id="audiobook">Audiobook</span>
          </dt>
          <dd></dd>

          <dt>
            <FontAwesomeIcon
              style={bookEntity.subscription ? { color: 'green' } : { color: 'red' }}
              icon={bookEntity.subscription ? 'check' : 'xmark'}
            />
            <span id="subscription"> Subscription</span>
          </dt>
          <dt>
            <FontAwesomeIcon
              style={bookEntity.kindleSubscription ? { color: 'green' } : { color: 'red' }}
              icon={bookEntity.kindleSubscription ? 'check' : 'xmark'}
            />
            <span id="kindleSubscription"> Kindle Subscription</span>
          </dt>
          <dt>
            <FontAwesomeIcon
              style={bookEntity.libraryPass ? { color: 'green' } : { color: 'red' }}
              icon={bookEntity.libraryPass ? 'check' : 'xmark'}
            />
            <span id="libraryPass"> Library Pass</span>
          </dt>
          <dt>
            <FontAwesomeIcon
              style={bookEntity.librarySubscription ? { color: 'green' } : { color: 'red' }}
              icon={bookEntity.librarySubscription ? 'check' : 'xmark'}
            />
            <span id="librarySubscription"> Library Subscription</span>
          </dt>
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
