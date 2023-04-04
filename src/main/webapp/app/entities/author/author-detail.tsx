import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Row, Col, Button } from 'reactstrap';
import { Grid, Typography, Link, createMuiTheme } from '@mui/material';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './author.reducer';

export const AuthorDetail = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const authorEntity = useAppSelector(state => state.author.entity);

  return (
    <Grid container>
      <Grid item md={8}>
        <Typography variant="h2" data-cy="authorDetailsHeading">
          Author {authorEntity.id}
        </Typography>
        <dl className="jh-entity-details">
          <dt>
            <Typography variant="subtitle1" id="name">
              Name
            </Typography>
          </dt>
          <dt>
            <span id="url">Url</span>
          </dt>
          <dt>
            <a href={authorEntity.url} color="link">
              {authorEntity.url}
            </a>{' '}
          </dt>
          {authorEntity.books && (
            <>
              <dt>
                <Typography variant="subtitle1">Books</Typography>
              </dt>
              <dd>
                {authorEntity.books.map(item => (
                  <Button onClick={() => navigate(`/book/${item.id}`)} color="light">
                    {item.title}
                  </Button>
                ))}
              </dd>
            </>
          )}
        </dl>
        <Button component={Link} to="/author" replace color="primary" data-cy="entityDetailsBackButton">
          <span className="d-none d-md-inline">Back</span>
        </Button>
      </Grid>
    </Grid>
  );
};

export default AuthorDetail;
