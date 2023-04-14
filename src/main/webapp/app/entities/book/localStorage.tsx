import React, { useState, useEffect } from 'react';

const useLocalStorageState = (key, defaultValue) => {
  const [state, setState] = useState(() => {
    const savedValue = window.localStorage.getItem(key);
    return savedValue !== null ? (key === 'filter.added' ? new Date(savedValue) : savedValue) : defaultValue;
  });

  useEffect(() => {
    if (state === null) window.localStorage.removeItem(key);
    if (key !== 'filter.added' || (state !== undefined && state !== null)) window.localStorage.setItem(key, state);
  }, [key, state]);

  return [state, setState];
};

export default useLocalStorageState;
