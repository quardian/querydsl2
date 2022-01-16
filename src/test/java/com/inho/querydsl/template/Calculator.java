package com.inho.querydsl.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public Integer calcSum(String filepath) throws IOException {
        LineCallback sumCallback = (line,value) -> value + Integer.valueOf(line) ;
        return lineReadTemplate(filepath,sumCallback, 0);
    }

    public Integer calcMultiply(String filepath) throws IOException {

        LineCallback sumCallback = (line,value) -> value * Integer.valueOf(line) ;
        return lineReadTemplate(filepath,sumCallback, 1);
    }

    public Integer lineReadTemplate(String filepath, LineCallback callback, int initVal) throws IOException
    {
        BufferedReaderCallback sumCallback = (br) -> {
            Integer res = initVal;
            String line = null;
            while( (line = br.readLine()) != null ){
                res = callback.doSomethingWithLine(line, res);
            }
            return res;
        };

        return fileReadTemplate(filepath, sumCallback);
    }


    public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException {

        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(filepath));
            Integer ret = callback.doSomethingWithReader(br);
            return ret;
        }catch (IOException e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            if ( br != null){
                try{br.close();}
                catch (IOException e){}
            }
        }
    }

}
