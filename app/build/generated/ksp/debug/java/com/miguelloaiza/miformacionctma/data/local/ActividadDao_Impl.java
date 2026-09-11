package com.miguelloaiza.miformacionctma.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ActividadDao_Impl implements ActividadDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ActividadEntity> __insertionAdapterOfActividadEntity;

  private final EntityDeletionOrUpdateAdapter<ActividadEntity> __deletionAdapterOfActividadEntity;

  private final SharedSQLiteStatement __preparedStmtOfEliminarPorId;

  public ActividadDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfActividadEntity = new EntityInsertionAdapter<ActividadEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `actividades` (`id`,`titulo`,`descripcion`,`progreso`,`diasRestantes`,`prioridad`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ActividadEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitulo());
        if (entity.getDescripcion() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDescripcion());
        }
        statement.bindLong(4, entity.getProgreso());
        statement.bindLong(5, entity.getDiasRestantes());
        statement.bindString(6, entity.getPrioridad());
      }
    };
    this.__deletionAdapterOfActividadEntity = new EntityDeletionOrUpdateAdapter<ActividadEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `actividades` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ActividadEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfEliminarPorId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM actividades WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertar(final ActividadEntity actividad,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfActividadEntity.insert(actividad);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object eliminar(final ActividadEntity actividad,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfActividadEntity.handle(actividad);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object eliminarPorId(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfEliminarPorId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfEliminarPorId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ActividadEntity>> obtenerTodas() {
    final String _sql = "SELECT * FROM actividades ORDER BY diasRestantes ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"actividades"}, new Callable<List<ActividadEntity>>() {
      @Override
      @NonNull
      public List<ActividadEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "titulo");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final int _cursorIndexOfProgreso = CursorUtil.getColumnIndexOrThrow(_cursor, "progreso");
          final int _cursorIndexOfDiasRestantes = CursorUtil.getColumnIndexOrThrow(_cursor, "diasRestantes");
          final int _cursorIndexOfPrioridad = CursorUtil.getColumnIndexOrThrow(_cursor, "prioridad");
          final List<ActividadEntity> _result = new ArrayList<ActividadEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ActividadEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitulo;
            _tmpTitulo = _cursor.getString(_cursorIndexOfTitulo);
            final String _tmpDescripcion;
            if (_cursor.isNull(_cursorIndexOfDescripcion)) {
              _tmpDescripcion = null;
            } else {
              _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            }
            final int _tmpProgreso;
            _tmpProgreso = _cursor.getInt(_cursorIndexOfProgreso);
            final int _tmpDiasRestantes;
            _tmpDiasRestantes = _cursor.getInt(_cursorIndexOfDiasRestantes);
            final String _tmpPrioridad;
            _tmpPrioridad = _cursor.getString(_cursorIndexOfPrioridad);
            _item = new ActividadEntity(_tmpId,_tmpTitulo,_tmpDescripcion,_tmpProgreso,_tmpDiasRestantes,_tmpPrioridad);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
