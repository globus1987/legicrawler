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
    <span className="brand-title"> Legicrawler</span>
    <span className="navbar-version">{VERSION}</span>
  </NavbarBrand>
);

export const Books = () => (
  <NavItem>
    <NavLink tag={Link} to="/book" className="d-flex align-items-center">
      <span>Books</span>
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
