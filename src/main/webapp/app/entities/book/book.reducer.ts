import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { createEntitySlice, EntityState, IQueryParams, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { defaultValue, IBook, IBookStat } from 'app/shared/model/book.model';

const initialState: EntityState<IBook> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/books';

// Actions

export const getEntities = createAsyncThunk(
  'book/fetch_entity_list',
  async ({ page, size, sort, filterTitle, filterAuthor, filterCycle, filterCollection, added }: IQueryParams) => {
    const requestUrl = `${apiUrl}${
      sort
        ? `?filterTitle=${filterTitle}&filterAuthor=${filterAuthor}&filterCycle=${filterCycle}&filterCollection=${filterCollection}&added=${added}&page=${page}&size=${size}&sort=${sort}&`
        : '?'
    }cacheBuster=${new Date().getTime()}`;
    return axios.get<IBook[]>(requestUrl);
  }
);

export const reload = async () => {
  const requestUrl = `${apiUrl}/reload`;
  axios.get<IBook[]>(requestUrl);
};

export const reloadCycles = async () => {
  const requestUrl = `${apiUrl}/reloadCycles`;
  axios.get<IBook[]>(requestUrl);
};

export const reloadCollections = async () => {
  const requestUrl = `${apiUrl}/reloadCollections`;
  axios.get<IBook[]>(requestUrl);
};

export const reloadAuthors = async () => {
  const requestUrl = `${apiUrl}/reloadAuthors`;
  axios.get<IBook[]>(requestUrl);
};

export const getEntity = createAsyncThunk(
  'book/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IBook>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'book/create_entity',
  async (entity: IBook, thunkAPI) => {
    const result = await axios.post<IBook>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'book/update_entity',
  async (entity: IBook, thunkAPI) => {
    const result = await axios.put<IBook>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'book/partial_update_entity',
  async (entity: IBook, thunkAPI) => {
    const result = await axios.patch<IBook>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'book/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IBook>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

// slice

export const BookSlice = createEntitySlice({
  name: 'book',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = BookSlice.actions;

// Reducer
export default BookSlice.reducer;
