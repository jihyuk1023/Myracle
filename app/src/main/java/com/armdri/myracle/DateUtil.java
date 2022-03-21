package com.armdri.myracle;

import static java.util.Locale.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    public static class Format{
        public String stringFormat = "yyyy-MM-dd";
        public int YEAR;
        public int MONTH;
        public int DAY_OF_MONTH;

        public Format(int YEAR, int MONTH, int DAY_OF_MONTH){
            this.YEAR = YEAR;
            this.MONTH = MONTH;
            this.DAY_OF_MONTH = DAY_OF_MONTH;
            this.stringFormat = joinToFormat(YEAR, MONTH, DAY_OF_MONTH);
        }

        public Format(){
            // default : 오늘 날짜 저장
            this.YEAR = getDateInt(0);
            this.MONTH = getDateInt(1);
            this.DAY_OF_MONTH = getDateInt(2);
            this.stringFormat = joinToFormat(this.YEAR, this.MONTH, this.DAY_OF_MONTH);
        }

        public Format(String stringFormat){
            this.stringFormat = stringFormat;
            this.YEAR = parseFormat(stringFormat, 0);
            this.MONTH = parseFormat(stringFormat, 1);
            this.DAY_OF_MONTH = parseFormat(stringFormat, 2);
        }

        public Format(Date date){
            Calendar calendar = null;
            calendar.setTime(date);
            this.YEAR = calendar.get(Calendar.YEAR);
            this.MONTH = calendar.get(Calendar.MONTH);
            this.DAY_OF_MONTH = calendar.get(Calendar.DAY_OF_MONTH);
            this.stringFormat = joinToFormat(this.YEAR, this.MONTH, this.DAY_OF_MONTH);
        }

        public static String joinToFormat(int YEAR, int MONTH, int DAY_OF_MONTH){
            return Integer.toString(YEAR)
                    + "-" + SubDateUtil.getMonth(MONTH)
                    + "-" + SubDateUtil.getDay(DAY_OF_MONTH);
        }
    }

    // 현재 날짜와 시간 출력
    public static String getCurrentDate(String format) {
        return getDateToString(format, System.currentTimeMillis());
    }

    public static String getDateToString(String format, long milliTimes){
        setDefault(KOREA);
        SimpleDateFormat sdf = new SimpleDateFormat(format, getDefault());
        return sdf.format(milliTimes);
    }

    // 현재 시간을 "kk:mm"로 표시하는 메소드
    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
        return sdf.format(calendar.getTime());
    }

    public static boolean lockChat(String currentTime, String setTime){
        if(currentTime.substring(0, 2).equals(setTime)) return false;
        else return true;
    }

    public static String getDate(){
        Calendar calendar;
        calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", KOREA);
        return sdf.format(calendar.getTime());
    }

    public static int getDateInt(int type){
        Calendar calendar = Calendar.getInstance();
        if(type == 0)       return calendar.get(Calendar.YEAR);
        else if(type == 1)  return calendar.get(Calendar.MONTH) + 1; // zero based
        else if(type == 2)  return calendar.get(Calendar.DAY_OF_MONTH);

        return -1;
    }

    public static int parseFormat(String format, int type){
        if(type == 0) return Integer.parseInt(format.substring(0, 4));
        else if(type == 1) return Integer.parseInt(format.substring(5, 7));
        else if(type == 2) return Integer.parseInt(format.substring(8, 10));

        return -1;
    }

    public static long getDateDifference(String firstFormat, String secondFormat){
        java.util.Date firstDate = fromStringToDate(firstFormat);
        java.util.Date secondDate = fromStringToDate(secondFormat);

        long diff = secondDate.getTime() - firstDate.getTime();

        TimeUnit time = TimeUnit.DAYS;
        long ret = time.convert(diff, TimeUnit.MILLISECONDS);
        return ret;
    }

    // 날짜 및 시간을 두자리 수로 맞추기
    public static class SubDateUtil {
        public static String getMonth(int month){
            if(month >= 1 && month < 10){
                return "0" + String.valueOf(month);
            } else if(month >= 10 && month < 13){
                return String.valueOf(month);
            } else throw null;
        }

        public static String getDay(int day){
            if(day >= 1 && day < 10){
                return "0" + String.valueOf(day);
            } else if(day >= 10 && day < 32){
                return String.valueOf(day);
            } else throw null;
        }
    }

    // 오늘 날짜 기준으로 이번 달의 시작일 가져오기
    public static String getFirstDateOfCurrentMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    // 설정한 날짜 기준으로 해당 월 시작일 가져오기
    public static String getMonthlyStartDate(String format){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        try {
            java.util.Date date = sdf.parse(format);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return sdf.format(calendar.getTime());
    }

    // 오늘 날짜 기준으로 이번 달의 마지막 날짜 가져오기
    public static String getEndDateOfCurrentMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    // 설정한 날짜 기준으로 해당 월의 마지막 날짜 가져오기
    public static String getMonthlyEndDate(String format){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        try {
            java.util.Date date = sdf.parse(format);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return sdf.format(calendar.getTime());
    }

    // Date -> String 변환기
    public static String fromDateToString(java.util.Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    // String -> Date 변환기
    public static java.util.Date fromStringToDate(String format){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;

        try {
            date = sdf.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // 요일을 반환하는 함수. 일요일 = 1, 월요일 = 2, 화요일 = 3, 토요일 = 7
    public static int getDayOfWeek(String format){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromStringToDate(format));
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    // 다음 날짜 가져오기
    public static String getNextDate(String current){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromStringToDate(current));
        calendar.add(calendar.DATE, +1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    // 이전 날짜 가져오기
    public static String getPreviousDate(String current){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromStringToDate(current));
        calendar.add(calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    // 시작일부터 마지막일까지 매 월의 시작일을 배열에 저장
    public static ArrayList<Format> getMonthlyStartDateOfRange(Format startFormat, Format endFormat){
        ArrayList<Format> months = new ArrayList<>();

        if(startFormat.YEAR < endFormat.YEAR){
            for(int M = startFormat.MONTH; M <= 12; M++) months.add(new Format(startFormat.YEAR, M, 01));

            for(int Y = startFormat.YEAR + 1; Y < endFormat.YEAR ; Y++){
                for(int M = 1; M <= 12; M++) months.add(new Format(Y, M, 01));
            }

            for(int M = 1; M <= endFormat.MONTH; M++) months.add(new Format(endFormat.YEAR, M, 01));

            return months;
        } else if(startFormat.YEAR == endFormat.YEAR){
            if(startFormat.MONTH == endFormat.MONTH){
                months.add(new Format(startFormat.YEAR, startFormat.MONTH, 01));
                return months;
            } else if(startFormat.MONTH < endFormat.MONTH){
                for(int M = startFormat.MONTH; M <= endFormat.MONTH; M++) months.add(new Format(startFormat.YEAR, M, 01));
                return months;
            } else {
                for(int M = endFormat.MONTH; M <= startFormat.MONTH; M++) months.add(new Format(startFormat.YEAR, M, 01));
                return months;
            }
        } else {
            return getMonthlyStartDateOfRange(endFormat, startFormat);
        }
    }

    // 매 달의 일주일 개수 구하기 + 누적합
    public static ArrayList<Integer> getNumbersOfWeeksFromEveryMonths(ArrayList<Format> months, boolean getSum){
        ArrayList<Integer> plot = new ArrayList<>();
        int sum = 0;
        for (Format M : months) {
            // ((각 월의 시작일 offset) + (각 월의 일수)) / 7
            int weeks = getDayOfWeek(M.stringFormat) + parseFormat(getMonthlyEndDate(M.stringFormat), 2);
            weeks /= 7;
            sum += weeks;
            if(getSum) plot.add(sum);
            else       plot.add(weeks);
        }
        return plot;
    }

}
