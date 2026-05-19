package com.doomguard.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
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
public final class DailyReportDao_Impl implements DailyReportDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyReportEntity> __insertionAdapterOfDailyReportEntity;

  public DailyReportDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyReportEntity = new EntityInsertionAdapter<DailyReportEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_reports` (`dayEpochDay`,`doomScore`,`riskName`,`socialMediaMinutes`,`longestSessionMinutes`,`nightUsage`,`mostUsedAppLabel`,`aiText`,`savedAt`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyReportEntity entity) {
        statement.bindLong(1, entity.getDayEpochDay());
        statement.bindLong(2, entity.getDoomScore());
        statement.bindString(3, entity.getRiskName());
        statement.bindLong(4, entity.getSocialMediaMinutes());
        statement.bindLong(5, entity.getLongestSessionMinutes());
        final int _tmp = entity.getNightUsage() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindString(7, entity.getMostUsedAppLabel());
        statement.bindString(8, entity.getAiText());
        statement.bindLong(9, entity.getSavedAt());
      }
    };
  }

  @Override
  public Object upsert(final DailyReportEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyReportEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailyReportEntity>> observeReports() {
    final String _sql = "SELECT * FROM daily_reports ORDER BY dayEpochDay DESC LIMIT 30";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_reports"}, new Callable<List<DailyReportEntity>>() {
      @Override
      @NonNull
      public List<DailyReportEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDayEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dayEpochDay");
          final int _cursorIndexOfDoomScore = CursorUtil.getColumnIndexOrThrow(_cursor, "doomScore");
          final int _cursorIndexOfRiskName = CursorUtil.getColumnIndexOrThrow(_cursor, "riskName");
          final int _cursorIndexOfSocialMediaMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "socialMediaMinutes");
          final int _cursorIndexOfLongestSessionMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "longestSessionMinutes");
          final int _cursorIndexOfNightUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "nightUsage");
          final int _cursorIndexOfMostUsedAppLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "mostUsedAppLabel");
          final int _cursorIndexOfAiText = CursorUtil.getColumnIndexOrThrow(_cursor, "aiText");
          final int _cursorIndexOfSavedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "savedAt");
          final List<DailyReportEntity> _result = new ArrayList<DailyReportEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyReportEntity _item;
            final long _tmpDayEpochDay;
            _tmpDayEpochDay = _cursor.getLong(_cursorIndexOfDayEpochDay);
            final int _tmpDoomScore;
            _tmpDoomScore = _cursor.getInt(_cursorIndexOfDoomScore);
            final String _tmpRiskName;
            _tmpRiskName = _cursor.getString(_cursorIndexOfRiskName);
            final long _tmpSocialMediaMinutes;
            _tmpSocialMediaMinutes = _cursor.getLong(_cursorIndexOfSocialMediaMinutes);
            final long _tmpLongestSessionMinutes;
            _tmpLongestSessionMinutes = _cursor.getLong(_cursorIndexOfLongestSessionMinutes);
            final boolean _tmpNightUsage;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfNightUsage);
            _tmpNightUsage = _tmp != 0;
            final String _tmpMostUsedAppLabel;
            _tmpMostUsedAppLabel = _cursor.getString(_cursorIndexOfMostUsedAppLabel);
            final String _tmpAiText;
            _tmpAiText = _cursor.getString(_cursorIndexOfAiText);
            final long _tmpSavedAt;
            _tmpSavedAt = _cursor.getLong(_cursorIndexOfSavedAt);
            _item = new DailyReportEntity(_tmpDayEpochDay,_tmpDoomScore,_tmpRiskName,_tmpSocialMediaMinutes,_tmpLongestSessionMinutes,_tmpNightUsage,_tmpMostUsedAppLabel,_tmpAiText,_tmpSavedAt);
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
