package com.armdri.myracle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


import java.util.ArrayList;

public class CalendarFragment extends Fragment {
    private static String TAG = "CalendarFragment";
    Context context;
    ViewGroup rootView;
    TextView textView;
    ArrayList<Record> res;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);
        context = container.getContext();

        /*
        Button resetButton = (Button) rootView.findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).deleteAllRecordDB();
                ((MainActivity)getActivity()).setUpdateFlag(true);
            }
        });

         */

        textView = (TextView) rootView.findViewById(R.id.show_record_text);

        ((MainActivity)getActivity()).printLogDB(TAG);
        res = ((MainActivity) getActivity()).readAllRecordDB();

        showText(getContinueDay(res));
        fillGrass(res);
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            Log.d(TAG, "체크 1");
            if(((MainActivity)getActivity()).getUpdateFlag()){
                Log.d(TAG, "체크 2");
                ((MainActivity)getActivity()).setUpdateFlag(false);

                ((MainActivity)getActivity()).printLogDB("Hidden Change");
                TableLayout tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);
                tableLayout.removeAllViewsInLayout();

                res = ((MainActivity) getActivity()).readAllRecordDB();

                showText(getContinueDay(res));
                fillGrass(res);
            }
        }
    }

    public void showText(int days){
        textView.setText("최대 " + Integer.toString(days) + "일 연속 Myracle 성공");
    }

    public int getContinueDay(ArrayList<Record> records){
        if(records == null) return 0;

        int count = 0;
        int max = 0;
        for (Record record: records) {
            if (record.color == 0) count = 0;
            else count++;

            max = Math.max(count, max);
        }
        return max;
    }


    @SuppressLint({"ResourceType", "SetTextI18n"})
    public void fillGrass(ArrayList<Record> records){
        if(records == null) return;

        DateUtil.Format startDate = records.get(0).getDateFormat();
        DateUtil.Format endDate = records.get(records.size() - 1).getDateFormat();
        long diff =  DateUtil.getDateDifference(startDate.stringFormat, endDate.stringFormat);
        int offset = DateUtil.getDayOfWeek(startDate.stringFormat); // one base

        diff += offset;
        long length = diff / 7 + 1;
        long height = diff % 7;
        int offset_counter = 0;

        ArrayList<DateUtil.Format> months = DateUtil.getMonthlyStartDateOfRange(startDate, endDate);
        ArrayList<Integer> plotMonths = DateUtil.getNumbersOfWeeksFromEveryMonths(months, true);
        int plotMonthsIdx = 0;

        TableLayout tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);

        int color = 0;
        for(int row = 0; row < 8; row++){
            TableRow tableRow = new TableRow(context);
            if(row >= height) length = diff / 7;

            for(int col = 0; col < length; col++){
                if (row < 7) {
                    // 채팅 남김/안남김 체크
                    if (col == 0 && (++offset_counter < offset)) color = -1;
                    else color = records.get(col*7 + row - offset + 1).color;

                    tableRow.addView(new Rectangle(context, color));
                } else if (row == 7 && col == plotMonths.get(plotMonthsIdx)){
                    // 하단에 M월 출력
                    plotMonthsIdx = Math.min(plotMonthsIdx+1, plotMonths.size()); //인덱스++
                    int MONTH = DateUtil.parseFormat( months.get(plotMonthsIdx).stringFormat, 1 );
                    tableRow.addView(createTextView(context, MONTH));
                } else {
                    // 하단에 M월이 아닌 빈칸 부분 채우기
                    tableRow.addView(new Rectangle(context, -1));
                }

            }
            tableLayout.addView(tableRow);
        }
    }

    public static TextView createTextView(Context context, int month){
        TextView textView = new TextView(context);
        textView.setText(month + "월");
        textView.setTextSize(12);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        // layoutParams.span = 1;
        textView.setLayoutParams(layoutParams);
        return textView;
    }
}
