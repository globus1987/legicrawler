import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Row, Col, Button } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Grid, Typography, Link, createMuiTheme } from '@mui/material';
import { ThemeProvider } from '@mui/material';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './author.reducer';

export const AuthorDetail = () => {
  const theme = createMuiTheme({
    palette: {
      primary: {
        main: '#00897b',
      },
      secondary: {
        main: '#f50057',
      },
    },
    typography: {
      h2: {
        fontSize: '2.5rem',
        fontWeight: 700,
        marginBottom: '2rem',
      },
      subtitle1: {
        fontWeight: 700,
        marginBottom: '0.5rem',
      },
    },
  });

  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const authorEntity = useAppSelector(state => state.author.entity);

  return (
    <ThemeProvider theme={theme}>
      <Grid container>
        <Grid item md={8}>
          <Typography variant="h2" data-cy="authorDetailsHeading">
            Author
          </Typography>
          <dl className="jh-entity-details">
            <dt>
              <Typography variant="subtitle1" id="id">
                Id
              </Typography>
            </dt>
            <dd>{authorEntity.id}</dd>
            <dt>
              <Typography variant="subtitle1" id="name">
                Name
              </Typography>
            </dt>
            <dd>{authorEntity.name}</dd>
            <dt>
              <Typography variant="subtitle1" id="url">
                Url
              </Typography>
            </dt>
            <dd>{authorEntity.url}</dd>
            {authorEntity.books && (
              <>
                <dt>
                  <Typography variant="subtitle1">Books</Typography>
                </dt>
                <dd>
                  {authorEntity.books.map(item => (
                    <dd key={item.id}>
                      <Link href={`/book/${item.id}`}>{item.title}</Link>
                    </dd>
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
    </ThemeProvider>
  );
};

export default AuthorDetail;
