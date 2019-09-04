package com.shuyu.gsyvideoplayer.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class MergeFile {
    public static final int BUFSIZE = 1024 * 8;

    public static void mergeFiles(String outFile, ArrayList<String> files) {

        FileChannel outChannel = null;

       // System.out.println("Merge " + Arrays.toString(files) + " into " + outFile);

        try {

            outChannel = new FileOutputStream(outFile).getChannel();

            for(String f : files){

                Charset charset=Charset.forName("utf-8");

                CharsetDecoder chdecoder=charset.newDecoder();

                CharsetEncoder chencoder=charset.newEncoder();

                FileChannel fc = new FileInputStream(f).getChannel();

                ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);

                CharBuffer charBuffer=chdecoder.decode(bb);

                ByteBuffer nbuBuffer=chencoder.encode(charBuffer);

                while(fc.read(nbuBuffer) != -1){



                    bb.flip();

                    nbuBuffer.flip();

                    outChannel.write(nbuBuffer);

                    bb.clear();

                    nbuBuffer.clear();

                }

                fc.close();

            }

            System.out.println("Merged!! ");

        } catch (IOException ioe) {

            ioe.printStackTrace();

        } finally {

            try {if (outChannel != null) {outChannel.close();}} catch (IOException ignore) {}

        }

    }


}
