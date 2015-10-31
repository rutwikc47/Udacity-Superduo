package barqsoft.footballscores.service;
//
//

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.database.DatabaseContract;

public final class WidgetService extends RemoteViewsService{


    private interface DataQuery {
        String[] PROJECTION = {

                DatabaseContract.scores_table.HOME_COL,
                DatabaseContract.scores_table.AWAY_COL,
                DatabaseContract.scores_table.HOME_GOALS_COL,
                DatabaseContract.scores_table.AWAY_GOALS_COL,
                DatabaseContract.scores_table.MATCH_ID
        };

        public static final int COL_HOME = 0;
        public static final int COL_AWAY = 1;
        public static final int COL_HOME_GOALS = 2;
        public static final int COL_AWAY_GOALS = 3;

    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor mCursor = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {

                if (mCursor != null) {
                    mCursor.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                Uri uri = DatabaseContract.scores_table.buildScoreWithDate();
                String formatString = getString(R.string.datefmt);
                SimpleDateFormat format = new SimpleDateFormat(formatString);
                String todayDate = format.format(new Date());

                mCursor = getContentResolver().query(uri,
                        DataQuery.PROJECTION,
                        null,
                        new String[]{todayDate},
                        null);
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {
                if (mCursor != null) {
                    mCursor.close();
                    mCursor = null;
                }

            }

            @Override
            public int getCount() {
                return mCursor == null ? 0 : mCursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        mCursor == null || !mCursor.moveToPosition(position)) {
                    return null;
                }
                final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_item);

                String homeTeamName = mCursor.getString(DataQuery.COL_HOME);
                String awayTeamName = mCursor.getString(DataQuery.COL_AWAY);
                String score = Utilities.getScores(
                        mCursor.getInt(DataQuery.COL_HOME_GOALS), mCursor.getInt(DataQuery.COL_AWAY_GOALS));

                views.setTextViewText(R.id.home_team_name, homeTeamName);
                views.setTextViewText(R.id.away_team_name, awayTeamName);
                views.setTextViewText(R.id.score_tv, score);

                return views;

            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_item);

            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {

                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}















