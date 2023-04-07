import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICycle } from 'app/shared/model/cycle.model';
import { getAllEntities as getCycles } from 'app/entities/cycle/cycle.reducer';
import { ICollection } from 'app/shared/model/collection.model';
import { getEntities as getCollections } from 'app/entities/collection/collection.reducer';
import { IAuthor } from 'app/shared/model/author.model';
import { getEntities as getAuthors } from 'app/entities/author/author.reducer';
import { IBook } from 'app/shared/model/book.model';
import { getEntity, updateEntity, createEntity, reset } from './book.reducer';

export const BookUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const cycles = useAppSelector(state => state.cycle.entities);
  const collections = useAppSelector(state => state.collection.entities);
  const authors = useAppSelector(state => state.author.entities);
  const bookEntity = useAppSelector(state => state.book.entity);
  const loading = useAppSelector(state => state.book.loading);
  const updating = useAppSelector(state => state.book.updating);
  const updateSuccess = useAppSelector(state => state.book.updateSuccess);

  const handleClose = () => {
    navigate('/book' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCycles({}));
    dispatch(getCollections({}));
    dispatch(getAuthors({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...bookEntity,
      ...values,
      cycle: cycles.find(it => it.id.toString() === values.cycle.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...bookEntity,
          cycle: bookEntity?.cycle?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="LegicrawlerApp.book.home.createOrEditLabel" data-cy="BookCreateUpdateHeading">
            Create or edit a Book
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="book-id" label="Id" validate={{ required: true }} /> : null}
              <ValidatedField label="Title" id="book-title" name="title" data-cy="title" type="text" />
              <ValidatedField
                label="Url"
                id="book-url"
                name="url"
                data-cy="url"
                type="text"
                validate={{
                  maxLength: { value: 5000, message: 'This field cannot be longer than 5000 characters.' },
                }}
              />
              <ValidatedField label="Ebook" id="book-ebook" name="ebook" data-cy="ebook" check type="checkbox" />
              <ValidatedField label="Audiobook" id="book-audiobook" name="audiobook" data-cy="audiobook" check type="checkbox" />
              <ValidatedField label="Category" id="book-category" name="category" data-cy="category" type="text" />
              <ValidatedField label="Added" id="book-added" name="added" data-cy="added" type="date" />
              <ValidatedField
                label="Kindle Subscription"
                id="book-kindleSubscription"
                name="kindleSubscription"
                data-cy="kindleSubscription"
                check
                type="checkbox"
              />
              <ValidatedField label="Library Pass" id="book-libraryPass" name="libraryPass" data-cy="libraryPass" check type="checkbox" />
              <ValidatedField
                label="Library Subscription"
                id="book-librarySubscription"
                name="librarySubscription"
                data-cy="librarySubscription"
                check
                type="checkbox"
              />
              <ValidatedField
                label="Subscription"
                id="book-subscription"
                name="subscription"
                data-cy="subscription"
                check
                type="checkbox"
              />
              <ValidatedField id="book-cycle" name="cycle" data-cy="cycle" label="Cycle" type="select">
                <option value="" key="0" />
                {cycles
                  ? cycles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/book" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default BookUpdate;
