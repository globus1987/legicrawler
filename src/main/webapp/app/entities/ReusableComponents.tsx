import React from 'react';
import PropTypes from 'prop-types';
import { Button, Card, CardBody, CardImg, CardTitle } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link, useNavigate } from 'react-router-dom';
import { IBook } from 'app/shared/model/book.model';

const GoToLegimiButton = ({ href }) => {
  return (
    <Button href={href} color="dark" data-cy="entityDetailsBackButton">
      <span className="d-none d-md-inline">Go to Legimi </span>
      <FontAwesomeIcon icon="book" />
    </Button>
  );
};

GoToLegimiButton.propTypes = {
  href: PropTypes.string.isRequired,
};

export const GoBack = ({ to }) => {
  const navigate = useNavigate();

  return (
    <Button tag={Link} to={to} onClick={() => navigate(-1)} replace color="dark" data-cy="entityDetailsBackButton">
      <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
    </Button>
  );
};

interface IBookCardProps {
  book: IBook;
}

export const BookCard = ({ book }: IBookCardProps) => {
  return (
    <Card
      key={book.id}
      className="mb-3"
      style={{
        flex: 'wrap',
        backgroundColor: '#bdbdbd',
        overflow: 'hidden',
        borderRadius: '5px',
        boxShadow: '5px 5px 4px rgba(0, 0, 0, 0.1)',
      }}
    >
      <a className="mx-auto" href={`/book/${book.id}`}>
        <CardImg className="mx-auto" top style={{ width: '140px', height: '200px' }} src={book.imgsrc} alt={book.title} />
      </a>
      <CardBody className="text-center">
        <CardTitle style={{ fontWeight: 'bold' }}>{book.title}</CardTitle>
      </CardBody>
    </Card>
  );
};

export const DetailHeader = ({ id, name }) => {
  return (
    <h1 className="text-center" data-cy="cycleDetailsHeading">
      {id} : {name}
    </h1>
  );
};

export const YesNoMark = ({ condition }) => {
  return <FontAwesomeIcon style={condition ? { color: 'green' } : { color: 'red' }} icon={condition ? 'check' : 'xmark'} />;
};

export const RedirectToEntity = ({ url, name }) => {
  const navigate = useNavigate();
  return (
    <dd>
      <Button
        onClick={() => navigate(url)}
        color="light"
        onAuxClick={event => {
          if (event.button === 1) {
            // Check if middle mouse button is clicked
            event.preventDefault(); // Prevent default click behavior
            window.open(url, '_blank'); // Open link in new tab
          }
        }}
      >
        {name}
      </Button>
    </dd>
  );
};

RedirectToEntity.DetailHeader = {
  url: PropTypes.string.isRequired,
  name: PropTypes.bool.isRequired,
};

YesNoMark.DetailHeader = {
  condition: PropTypes.bool.isRequired,
};

GoBack.DetailHeader = {
  id: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
};

GoBack.propTypes = {
  to: PropTypes.string.isRequired,
};
BookCard.propTypes = {
  book: PropTypes.object.isRequired,
};
GoToLegimiButton.propTypes = {
  href: PropTypes.string.isRequired,
};

export default GoToLegimiButton;
