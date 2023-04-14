import React, { useEffect, useState } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './book.reducer';

export const BookDetail = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [imageError, setImageError] = useState(false);

  const handleImageError = () => {
    setImageError(true);
  };

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const bookEntity = useAppSelector(state => state.book.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="bookDetailsHeading">
          {bookEntity.id} : {bookEntity.title}
        </h2>
        <dl className="jh-entity-details">
          <dd>
            {!imageError && (
              <img
                style={{ overflow: 'hidden', borderRadius: '5px', boxShadow: '5px 5px 4px rgba(0, 0, 0, 0.1)' }}
                src={bookEntity.imgsrc}
                alt="Book Cover"
                onError={handleImageError}
              />
            )}
          </dd>
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
          <dt>Collections</dt>
          <dd>
            {bookEntity.collections?.map(item => (
              <Button onClick={() => navigate(`/collection/${item.id}`)} color="light" size="sm">
                {item.name}
              </Button>
            ))}
          </dd>
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
        <Button tag={Link} to="/book" onClick={() => navigate(-1)} replace color="dark" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        <Button href={bookEntity.url} replace color="dark" data-cy="entityDetailsBackButton">
          <span className="d-none d-md-inline">Go to Legimi </span>
          <FontAwesomeIcon icon="book" />
        </Button>
      </Col>
    </Row>
  );
};

export default BookDetail;
