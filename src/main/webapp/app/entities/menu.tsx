import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/author">
        Author
      </MenuItem>
      <MenuItem icon="asterisk" to="/book">
        Book
      </MenuItem>
      <MenuItem icon="asterisk" to="/cycle">
        Cycle
      </MenuItem>
      <MenuItem icon="asterisk" to="/collection">
        Collection
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
