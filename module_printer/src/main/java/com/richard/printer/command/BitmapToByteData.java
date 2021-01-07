//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.richard.printer.command;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public class BitmapToByteData {

    public enum AlignType {
        Left,
        Center,
        Right;

        AlignType() {
        }
    }

    public enum BmpType {
        Dithering,
        Threshold,
        Grey;

        BmpType() {
        }
    }

    public static byte[] rasterBmpToSendData(int m, Bitmap mBitmap, BitmapToByteData.BmpType bmpType, BitmapToByteData.AlignType alignType, int pageWidth) {
        Bitmap bitmap;
        switch (bmpType) {
            case Threshold:
                bitmap = convertGreyImgByFloyd(mBitmap);
                break;
            case Grey:
                bitmap = getGreyBitmap(mBitmap);
                break;
            case Dithering:
            default:
                bitmap = convertGreyImg(mBitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        byte[] data = getbmpdata(pixels, width, height);
        int n = (width + 7) / 8;
        byte xL = (byte) (n % 256);
        byte xH = (byte) (n / 256);
        int x = (height + 23) / 24;
        List<Byte> list = new ArrayList();
        byte[] head = new byte[]{29, 118, 48, (byte) m, xL, xH, 24, 0};
        int mL = 0;
        int mH = 0;

        if (width >= pageWidth) {
            alignType = BitmapToByteData.AlignType.Left;
        }

        switch (alignType) {
            case Left:
                mL = 0;
                mH = 0;
                break;
            case Center:
                mL = (pageWidth - width) / 2 % 256;
                mH = (pageWidth - width) / 2 / 256;
                break;
            case Right:
                mL = (pageWidth - width) % 256;
                mH = (pageWidth - width) / 256;
        }

        byte[] aligndata = PrinterCmd.setAbsolutePrintPosition(mL, mH);

        for (int i = 0; i < x; ++i) {
            byte[] newdata;
            if (i == x - 1) {
                if (height % 24 == 0) {
                    head[6] = 24;
                    newdata = new byte[n * 24];
                    System.arraycopy(data, 24 * i * n, newdata, 0, 24 * n);
                } else {
                    head[6] = (byte) (height % 24);
                    newdata = new byte[height % 24 * n];
                    System.arraycopy(data, 24 * i * n, newdata, 0, height % 24 * n);
                }
            } else {
                newdata = new byte[n * 24];
                System.arraycopy(data, 24 * i * n, newdata, 0, 24 * n);
            }

            byte b;
            int var22;
            int var23;
            byte[] var24;
            if (alignType != BitmapToByteData.AlignType.Left) {
                var24 = aligndata;
                var23 = aligndata.length;

                for (var22 = 0; var22 < var23; ++var22) {
                    b = var24[var22];
                    list.add(b);
                }
            }

            var24 = head;
            var23 = head.length;

            for (var22 = 0; var22 < var23; ++var22) {
                b = var24[var22];
                list.add(b);
            }

            var24 = newdata;
            var23 = newdata.length;

            for (var22 = 0; var22 < var23; ++var22) {
                b = var24[var22];
                list.add(b);
            }
        }

        byte[] byteData = new byte[list.size()];

        for (int i = 0; i < byteData.length; ++i) {
            byteData[i] = (Byte) list.get(i);
        }

        return byteData;
    }

    public static byte[] rasterBmpToSendData(int m, Bitmap mBitmap, BitmapToByteData.BmpType bmpType) {
        Bitmap bitmap = toGrayscale(mBitmap);
        switch (bmpType) {
            case Threshold:
                bitmap = convertGreyImgByFloyd(bitmap);
                break;
            case Dithering:
            default:
                bitmap = convertGreyImg(bitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] data = getbmpdata(pixels, width, height);
        int n = (width + 7) / 8;
        byte xL = (byte) (n % 256);
        byte xH = (byte) (n / 256);
        int x = (height + 23) / 24;
        List<Byte> list = new ArrayList();
        byte[] head = new byte[]{29, 118, 48, (byte) m, xL, xH, 24, 0};

        for (int i = 0; i < x; ++i) {
            byte[] newdata;
            if (i == x - 1) {
                if (height % 24 == 0) {
                    head[6] = 24;
                    newdata = new byte[n * 24];
                    System.arraycopy(data, 24 * i * n, newdata, 0, 24 * n);
                } else {
                    head[6] = (byte) (height % 24);
                    newdata = new byte[height % 24 * n];
                    System.arraycopy(data, 24 * i * n, newdata, 0, height % 24 * n);
                }
            } else {
                newdata = new byte[n * 24];
                System.arraycopy(data, 24 * i * n, newdata, 0, 24 * n);
            }

            byte[] var19 = head;
            int var18 = head.length;

            byte b;
            int var17;
            for (var17 = 0; var17 < var18; ++var17) {
                b = var19[var17];
                list.add(b);
            }

            var19 = newdata;
            var18 = newdata.length;

            for (var17 = 0; var17 < var18; ++var17) {
                b = var19[var17];
                list.add(b);
            }
        }

        byte[] byteData = new byte[list.size()];

        for (int i = 0; i < byteData.length; ++i) {
            byteData[i] = (Byte) list.get(i);
        }

        return byteData;
    }

    public static byte[] flashBmpToSendData(Bitmap mBitmap, BitmapToByteData.BmpType bmpType) {
        Bitmap bitmap = convertBmp(mBitmap);
        bitmap = toGrayscale(bitmap);

        switch (bmpType) {
            case Threshold:
                bitmap = convertGreyImgByFloyd(bitmap);
                break;
            case Dithering:
            default:
                bitmap = convertGreyImg(bitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int n = (width + 7) / 8;
        int h = (height + 7) / 8;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] data = getbmpdata(pixels, width, height);
        byte xL = (byte) (n % 256);
        byte xH = (byte) (n / 256);
        byte yL = (byte) (h % 256);
        byte yH = (byte) (h / 256);
        byte[] head = new byte[]{xL, xH, yL, yH};
        data = byteMerger(head, data);
        return data;
    }

    public static byte[] downLoadBmpToSendTSCdownloadcommand(Bitmap mBitmap) {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int[] pixels = new int[width * height];
        mBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] send = new byte[width * height];

        for (int i = 0; i < pixels.length; ++i) {
            send[i] = (byte) pixels[i];
        }

        byte[] data = getbmpdataTsc(pixels, width, height);
        return data;
    }

    public static byte[] downLoadBmpToSendTSCData(Bitmap mBitmap, BitmapToByteData.BmpType bmpType) {
        Bitmap bitmap = toGrayscale(mBitmap);

        switch (bmpType) {
            case Threshold:
                bitmap = convertGreyImgByFloyd(bitmap);
                break;
            case Dithering:
            default:
                bitmap = convertGreyImg(bitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int n = (width + 7) / 8;
        int h = (height + 7) / 8;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] data = getbmpdataTsc(pixels, width, height);
        return data;
    }

    public static byte[] downLoadBmpToSendData(Bitmap mBitmap, BitmapToByteData.BmpType bmpType) {
        Bitmap bitmap = convertBmp(mBitmap);
        bitmap = toGrayscale(bitmap);
        switch (bmpType) {
            case Threshold:
                bitmap = convertGreyImgByFloyd(bitmap);
                break;
            case Dithering:
            default:
                bitmap = convertGreyImg(bitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int n = (width + 7) / 8;
        int h = (height + 7) / 8;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] data = getbmpdata(pixels, width, height);
        byte[] head = new byte[]{(byte) n, (byte) h};
        data = byteMerger(head, data);
        return data;
    }

    private static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0F);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0.0F, 0.0F, paint);
        return bmpGrayscale;
    }

    private static Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = new int[width * height];
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        double redSum = 0.0D;
        double total = (double) (width * height);

        int m;
        int i;
        int j;
        int grey;
        for (m = 0; m < height; ++m) {
            for (i = 0; i < width; ++i) {
                j = pixels[width * m + i];
                grey = (j & 16711680) >> 16;
                redSum += (double) grey;
            }
        }

        m = (int) (redSum / total);

        for (i = 0; i < height; ++i) {
            for (j = 0; j < width; ++j) {
                grey = pixels[width * i + j];
                int alpha1 = -16777216;
                int red = (grey & 16711680) >> 16;
                int green = (grey & '\uff00') >> 8;
                int blue = grey & 255;
                if (red >= m) {
                    blue = 255;
                    green = 255;
                    red = 255;
                } else {
                    blue = 0;
                    green = 0;
                    red = 0;
                }

                grey = alpha1 | red << 16 | green << 8 | blue;
                pixels[width * i + j] = grey;
            }
        }

        Bitmap mBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
        mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return mBitmap;
    }

    private static Bitmap convertGreyImgByFloyd(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = new int[width * height];
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] gray = new int[height * width];

        int e;
        int i;
        int j;
        int g;
        for (e = 0; e < height; ++e) {
            for (i = 0; i < width; ++i) {
                j = pixels[width * e + i];
                g = (j & 16711680) >> 16;
                gray[width * e + i] = g;
            }
        }

        for (i = 0; i < height; ++i) {
            for (j = 0; j < width; ++j) {
                g = gray[width * i + j];
                if (g >= 128) {
                    pixels[width * i + j] = -1;
                    e = g - 255;
                } else {
                    pixels[width * i + j] = -16777216;
                    e = g - 0;
                }

                if (j < width - 1 && i < height - 1) {
                    gray[width * i + j + 1] += 3 * e / 8;
                    gray[width * (i + 1) + j] += 3 * e / 8;
                    gray[width * (i + 1) + j + 1] += e / 4;
                } else if (j == width - 1 && i < height - 1) {
                    gray[width * (i + 1) + j] += 3 * e / 8;
                } else if (j < width - 1 && i == height - 1) {
                    gray[width * i + j + 1] += e / 4;
                }
            }
        }

        Bitmap mBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
        mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return mBitmap;
    }

    public static Bitmap getGreyBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        } else {
            long stime = System.currentTimeMillis();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            int[] gray = new int[height * width];

            int e;
            int i;
            int j;
            int g;
            for (e = 0; e < height; ++e) {
                for (i = 0; i < width; ++i) {
                    j = pixels[width * e + i];
                    g = (j & 16711680) >> 16;
                    gray[width * e + i] = g;
                }
            }

            for (i = 0; i < height; ++i) {
                for (j = 0; j < width; ++j) {
                    g = gray[width * i + j];
                    if (g >= 128) {
                        pixels[width * i + j] = -1;
                        e = g - 255;
                    } else {
                        pixels[width * i + j] = -16777216;
                        e = g - 0;
                    }

                    if (j < width - 1 && i < height - 1) {
                        gray[width * i + j + 1] += 3 * e / 8;
                        gray[width * (i + 1) + j] += 3 * e / 8;
                        gray[width * (i + 1) + j + 1] += e / 4;
                    } else if (j == width - 1 && i < height - 1) {
                        gray[width * (i + 1) + j] += 3 * e / 8;
                    } else if (j < width - 1 && i == height - 1) {
                        gray[width * i + j + 1] += e / 4;
                    }
                }
            }

            Bitmap mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return mBitmap;
        }
    }

    private static byte[] bagetbmpdata(int[] b, int w, int m) {
        int nH = w / 256;
        int nL = w % 256;
        byte[] head = new byte[]{27, 42, (byte) m, (byte) nL, (byte) nH};
        byte[] end = new byte[]{27, 74, 16};
        byte mask = 1;
        byte[] perdata = new byte[w];

        int x;
        for (x = 0; x < w; ++x) {
            for (int y = 0; y < 8; ++y) {
                if ((b[y * w + x] & 16711680) >> 16 != 0) {
                    perdata[x] |= (byte) (mask << 7 - y);
                }
            }
        }

        for (x = 0; x < perdata.length; ++x) {
            perdata[x] = (byte) (~perdata[x]);
        }

        byte[] data = byteMerger(head, perdata);
        data = byteMerger(data, end);
        return data;
    }

    public static byte[] baBmpToSendData(int m, Bitmap mBitmap, BitmapToByteData.BmpType bmpType) {
        Bitmap bitmap = toGrayscale(mBitmap);
        switch (bmpType) {
            case Threshold:
                bitmap = convertGreyImgByFloyd(bitmap);
                break;
            case Dithering:
            default:
                bitmap = convertGreyImg(bitmap);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int n = (height + 7) / 8;
        ArrayList<Byte> list = new ArrayList();

        for (int i = 0; i < n; ++i) {
            int[] perPix = new int[width * 8];

            for (int j = 0; j < perPix.length; ++j) {
                if (j + 8 * i * width < pixels.length - 1) {
                    perPix[j] = pixels[j + 8 * i * width];
                } else {
                    perPix[j] = -1;
                }
            }

            byte[] data = bagetbmpdata(perPix, width, m);
            byte[] var15 = data;
            int var14 = data.length;

            for (int var13 = 0; var13 < var14; ++var13) {
                byte b = var15[var13];
                list.add(b);
            }
        }

        byte[] newdata = new byte[list.size()];

        for (int i = 0; i < newdata.length; ++i) {
            newdata[i] = (Byte) list.get(i);
        }

        return newdata;
    }

    private static byte[] getbmpdata(int[] b, int w, int h) {
        int n = (w + 7) / 8;
        byte[] data = new byte[n * h];
        byte mask = 1;

        int y;
        for (y = 0; y < h; ++y) {
            for (int x = 0; x < n * 8; ++x) {
                if (x < w) {
                    if ((b[y * w + x] & 16711680) >> 16 != 0) {
                        data[y * n + x / 8] |= (byte) (mask << 7 - x % 8);
                    }
                } else if (x >= w) {
                    data[y * n + x / 8] |= (byte) (mask << 7 - x % 8);
                }
            }
        }

        for (y = 0; y < data.length; ++y) {
            data[y] = (byte) (~data[y]);
        }

        return data;
    }

    private static byte[] getbmpdataTsc(int[] b, int w, int h) {
        int n = (w + 7) / 8;
        byte[] data = new byte[n * h];
        byte mask = 1;

        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < n * 8; ++x) {
                if (x < w) {
                    if ((b[y * w + x] & 16711680) >> 16 != 0) {
                        data[y * n + x / 8] |= (byte) (mask << 7 - x % 8);
                    }
                } else if (x >= w) {
                    data[y * n + x / 8] |= (byte) (mask << 7 - x % 8);
                }
            }
        }

        return data;
    }

    private static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    private static Bitmap convertBmp(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Bitmap convertBmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas cv = new Canvas(convertBmp);
        Matrix matrix = new Matrix();
        matrix.postScale(-1.0F, 1.0F);
        matrix.postRotate(-90.0F);
        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
        cv.drawBitmap(newBmp, new Rect(0, 0, newBmp.getWidth(), newBmp.getHeight()), new Rect(0, 0, w, h), (Paint) null);
        return convertBmp;
    }
}
