import React from 'react';

import { NavItem, NavLink, NavbarBrand } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';

export const BrandIcon = props => (
  <div {...props} className="brand-icon">
    <img src="content/images/logo-jhipster.svg" alt="Logo" />
  </div>
);

export const Brand = () => (
  <NavbarBrand tag={Link} to="/" className="brand-logo">
    <BrandIcon />
    <span className="brand-title">legicrawler</span>
    <span className="navbar-version">{VERSION}</span>
  </NavbarBrand>
);

export const Home = () => (
  <NavItem>
    <NavLink tag={Link} to="/" className="d-flex align-items-center">
      <FontAwesomeIcon icon="home" />
      <span>Home</span>
    </NavLink>
  </NavItem>
);

export const Authors = () => (
  <NavItem>
    <NavLink tag={Link} to="/author" className="d-flex align-items-center">
      <span>Authors</span>
    </NavLink>
  </NavItem>
);

export const Books = () => (
  <NavItem>
    <NavLink tag={Link} to="/book" className="d-flex align-items-center">
      <span>Books</span>
    </NavLink>
  </NavItem>
);

export const Cycles = () => (
  <NavItem>
    <NavLink tag={Link} to="/cycle" className="d-flex align-items-center">
      <span>Cycles</span>
    </NavLink>
  </NavItem>
);

export const Collections = () => (
  <NavItem>
    <NavLink tag={Link} to="/collection" className="d-flex align-items-center">
      <span>Collections</span>
    </NavLink>
  </NavItem>
);

let reloadBooks = async () => {
  // await axios.get("api/books/reload")
};
export const Reload = () => (
  <NavItem>
    <NavLink tag={Link} to="api/books/reload" className="d-flex align-items-center">
      <FontAwesomeIcon icon="right-left" />
      <span>Reload</span>
    </NavLink>
  </NavItem>
);
