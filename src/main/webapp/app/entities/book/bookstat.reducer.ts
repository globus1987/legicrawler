import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { defaultValue, IBookStat } from 'app/shared/model/book.model';
import { getEntities } from 'app/entities/book/book.reducer';

const initialState: EntityState<IBookStat> = {
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

export const getBookStats = createAsyncThunk('bookstat/fetch_entity_list', async () => {
  const requestUrl = `${apiUrl}/bookStats`;
  console.log(3);
  return axios.get<IBookStat[]>(requestUrl);
});

export const createEntity = createAsyncThunk(
  'bookstat/create_entity',
  async (entity: IBookStat, thunkAPI) => {
    const result = await axios.post<IBookStat>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'bookstat/update_entity',
  async (entity: IBookStat, thunkAPI) => {
    const result = await axios.put<IBookStat>(`${apiUrl}/${entity.added}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);
export const getEntity = createAsyncThunk(
  'bookstat/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IBookStat>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);
export const partialUpdateEntity = createAsyncThunk(
  'bookstat/partial_update_entity',
  async (entity: IBookStat, thunkAPI) => {
    const result = await axios.patch<IBookStat>(`${apiUrl}/${entity.added}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'bookstat/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IBookStat>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);
// slice

export const BookStatSlice = createEntitySlice({
  name: 'bookstat',
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

export const { reset } = BookStatSlice.actions;

// Reducer
export default BookStatSlice.reducer;
