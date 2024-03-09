package com.sprelf.taptimer.Utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * TapTimer - A Timer Widget App
 * Copyright (C) 2016 Dilley, Christopher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Created by Chris on 15.11.2016.
 */

public abstract class JSONUtils
{
    /** Loads a JSON file at the given filepath in the app's file storage.  If no file is found,
     * reads from the same path in Assets directory instead.
     *
     * @param c Context within which to perform the operation.
     * @param filename Filepath to read from.
     * @return The JSON object contained in the assets file
     * @throws JSONException
     */
    public static JSONObject loadJSONFile(Context c, String filename) throws JSONException
    {
        FileInputStream in;
        try
        {
            in = c.openFileInput(filename);
        } catch (FileNotFoundException e)
        {
            Log.d("[Data]", "File not found, searching assets instead...");
            return loadJSONAsset(c, filename);
        }

        try
        {
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                sb.append(line);
            }
            inputStreamReader.close();

            return new JSONObject(sb.toString());

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /** Loads a JSON file at the given filepath in the app's Assets directory.
     *
     * @param c Context within which to perform the operation.
     * @param filename Filepath to read from.
     * @return The JSON object contained in the assets file
     * @throws JSONException
     */
    public static JSONObject loadJSONAsset(Context c, String filename) throws JSONException
    {
        String returnString;
        try
        {

            InputStream is = c.getAssets().open(filename);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte buffer[] = new byte[1024];

            int length = 0;

            while ((length = is.read(buffer)) != -1)
            {
                baos.write(buffer, 0, length);
            }

            is.close();

            returnString = new String(baos.toByteArray(), "UTF-8");

        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        return new JSONObject(returnString);
    }
}
