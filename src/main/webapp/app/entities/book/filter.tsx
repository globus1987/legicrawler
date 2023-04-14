import React, { useEffect } from 'react';
import { Button, Col, FormGroup, Label, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { ButtonGroup } from '@mui/material';

interface BookFilterProps {
  filters: {
    [key: string]: [any, React.Dispatch<React.SetStateAction<any>>];
  };
  handleFilter: () => void;
  handleClear: () => void;
}

const BookFilter = ({ filters, handleFilter, handleClear }: BookFilterProps) => {
  const { title, author, cycle, collection, added } = filters;

  const [filterTitle, setFilterTitle] = title;
  const [filterAuthor, setFilterAuthor] = author;
  const [filterCycle, setFilterCycle] = cycle;
  const [filterCollection, setFilterCollection] = collection;
  const [filterAdded, setFilterAdded] = added;

  useEffect(() => {
    handleFilter();
  }, [filterTitle]);

  const handleKeyDown = (e, callback) => {
    if (e.keyCode === 13) {
      e.preventDefault();
      callback();
    }
  };

  const handleClearClick = () => {
    window.localStorage.removeItem('filter.added');
    setFilterAdded(undefined);
  };

  return (
    <ValidatedForm onSubmit={handleFilter}>
      <Row>
        <Col md="6">
          <ValidatedField
            type="text"
            name="title"
            label="Title"
            value={filterTitle}
            onChange={e => setFilterTitle(e.target.value)}
            onKeyDown={e => handleKeyDown(e, handleFilter)}
            onBlur={handleFilter}
          />
        </Col>
        <Col md="6">
          <ValidatedField
            type="text"
            name="author"
            label="Author"
            value={filterAuthor}
            onChange={e => setFilterAuthor(e.target.value)}
            onKeyDown={e => handleKeyDown(e, handleFilter)}
            onBlur={handleFilter}
          />
        </Col>
      </Row>
      <Row>
        <Col md="6">
          <ValidatedField
            type="text"
            name="collection"
            label="Collection"
            value={filterCollection}
            onChange={e => setFilterCollection(e.target.value)}
            onKeyDown={e => handleKeyDown(e, handleFilter)}
            onBlur={handleFilter}
          />
        </Col>
        <Col md="6">
          <ValidatedField
            type="text"
            name="cycle"
            label="Cycle"
            value={filterCycle}
            onChange={e => setFilterCycle(e.target.value)}
            onKeyDown={e => handleKeyDown(e, handleFilter)}
            onBlur={handleFilter}
          />
        </Col>
      </Row>
      <Row>
        <Col md="6">
          <FormGroup>
            <Label for="added">Added</Label>
            <DatePicker
              name="added"
              onBlur={handleFilter}
              selected={filterAdded}
              onChange={date => setFilterAdded(date)}
              isClearable={true}
              onClear={handleClearClick}
              dateFormat="dd/MM/yyyy" // specify date format
              label="Added"
              showYearDropdown
              scrollableYearDropdown // add these props to allow selecting year from dropdown
            />{' '}
          </FormGroup>
        </Col>
        <Col md="6">
          <ButtonGroup style={{ verticalAlign: 'bottom' }} className="btn-container">
            <Button onClick={handleFilter} className="me-2" color="success">
              <FontAwesomeIcon icon="search" />
              &nbsp; Search
            </Button>
            <Button onClick={handleClear} className="me-2" color="danger">
              <FontAwesomeIcon icon="trash" />
              &nbsp; Clear
            </Button>
          </ButtonGroup>
        </Col>
      </Row>
    </ValidatedForm>
  );
};

export default BookFilter;
