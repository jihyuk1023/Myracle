package com.armdri.myracle;

import android.content.Context;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class Nickname {

    public static String makeNickname() {
        String result = null;
        JSONObject jsonObject = null;

        String url = "https://nickname.hwanmoo.kr/?format=json";
        try {
            jsonObject = new RestAPITask(url).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            result = jsonObject.getString("words");

            result = result.replace("[", "");
            result = result.replace("]", "");
            result = result.replace("\"", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String loadNickname(Context context){
        String loadSql = "select _id, nickname from " + NicknameDatabase.TABLE_NOTE + " order by _id desc";
        String ret = null;

        NicknameDatabase nicknameDatabase = NicknameDatabase.getInstance(context);
        if(nicknameDatabase == null) return null;

        Cursor outCursor = nicknameDatabase.rawQuery(loadSql);
        if(outCursor.getCount() == 0) return null;

        outCursor.moveToFirst();
        int _id = outCursor.getInt(0);
        String nickname = outCursor.getString(1);
        ret = nickname;

        return ret;
    }

    public static void saveNickname(String nickname, Context context) {
        String saveSql = "insert into " + NicknameDatabase.TABLE_NOTE + "(nickname) values ("
                + "'" + nickname + "')";
        NicknameDatabase nicknameDatabase = NicknameDatabase.getInstance(context);
        nicknameDatabase.execSQL(saveSql);
    }

    public static void deleteNickname(String nickname, Context context) {
        String deleteSql = "delete from " + NicknameDatabase.TABLE_NOTE + "where "
                + " nickname = '" + nickname + "'";
        NicknameDatabase nicknameDatabase = NicknameDatabase.getInstance(context);
        nicknameDatabase.execSQL(deleteSql);
    }

}