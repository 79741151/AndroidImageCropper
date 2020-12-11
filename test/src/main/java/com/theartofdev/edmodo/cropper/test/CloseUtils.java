package com.theartofdev.edmodo.cropper.test;

import java.io.Closeable;
import java.io.IOException;

/**
 * @创建者 魏震
 * @创建时间 2020/12/7 14:43
 * @描述 ...........
 */
class CloseUtils {
    /**
     *   IO流关闭工具类
     */
    public static void closeIO(Closeable... io) {
        for (Closeable temp : io) {
            try {
                if (null != temp)
                    temp.close();
            } catch (IOException e) {
                System.out.println("" + e.getMessage());
            }
        }
    }

    public static <T extends Closeable> void closeAll(T... io) {
        for (Closeable temp : io) {
            try {
                if (null != temp)
                    temp.close();
            } catch (IOException e) {
                System.out.println("" + e.getMessage());
            }
        }
    }

}

