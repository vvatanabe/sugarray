package net.ultramagnetic.sugarray.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
    private static final String TAG = FileUtils.class.getCanonicalName();

    /**
     * インスタンス化を禁止する
     */
    private FileUtils() {
    }

    private static final Object lock = new Object();

    /**
     * アセットファイルをコピーする
     *
     * @param context
     * @param iFileName 対象のアセットファイル
     * @param oFilePath コピー先のパス
     */
    public static void copyAssetsFile(Context context, String iFileName,
                                      String oFilePath) {

        if (iFileName == null || oFilePath == null) {
            Logger.i(TAG, "invailed argment at copyAssetsFile().");
            return;
        }

        // 出力先ファイルを削除しておく(同名のディレクトリーが存在する場合のため)
        removeFile(new File(oFilePath));

        // ファイル出力
        InputStream in = null;
        try {
            in = context.getResources().getAssets().open(iFileName);
            writeStreamToFile(oFilePath, in);
        } catch (IOException e) {
            Logger.i(TAG, e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Logger.i(TAG, e.getMessage());
                }
            }
        }
    }

    /**
     * InputStreamをファイルに出力する
     *
     * @param filePath
     * @param in
     * @return
     */
    public static boolean writeStreamToFile(String filePath, InputStream in) {

        if (TextUtils.isEmpty(filePath) || in == null) {
            return false;
        }

        File oFile = new File(filePath);
        File oDir = oFile.getParentFile();
        if (!oDir.exists()) {
            oDir.mkdirs();
        }

		/* File出力 */

        boolean ret = false;
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(oFile, false));
            byte[] bytes = new byte[1024];
            int readByte = 0;
            while ((readByte = in.read(bytes)) != -1) {
                out.write(bytes, 0, readByte);
            }
            ret = true;
        } catch (FileNotFoundException e) {
            Logger.e(TAG, e.getMessage());
            ;
        } catch (IOException e) {
            Logger.e(TAG, e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Logger.e(TAG, e.getMessage());
                }
            }
        }
        return ret;
    }

    /**
     * assetsフォルダ内の対象ファイルを取得しStringで返す。
     *
     * @param context  コンテキスト
     * @param fileName assetsフォルダ内の対象ファイル名
     * @throws java.io.IOException
     */
    public static String readAssetsFileStr(Context context, String fileName) {

        String result = "";

        if (fileName == null) {
            //
            System.out.println("fileName is null at readAssetsFileStr().");
            return result;
        }

        AssetManager assetManager = context.getResources().getAssets();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            char[] chars = new char[1024];
            int readNum;
            StringBuilder builder = new StringBuilder();
            while ((readNum = in.read(chars)) != -1) {
                builder.append(chars, 0, readNum);
            }
            result = builder.toString();
        } catch (IOException e) {
            System.out.println("file io error. " + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println("reader close error. " + e.getMessage());
            }
        }

        return result;
    }

    /**
     * ファイルを読み込みStringで返す
     */
    public static String readFileToString(String filePath) {
        return readFileToString(new File(filePath));
    }

    /**
     * ファイルを読み込みStringで返す
     */
    public static String readFileToString(File file) {
        if (isNotFile(file)) {
            System.out.println("file is not exists at readFileString().");
            return null;
        }
        String result = "";
        if (file.exists()) {
            synchronized (lock) {
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new FileReader(file));
                    char[] chars = new char[1024];
                    int readNum;
                    StringBuilder builder = new StringBuilder();
                    while ((readNum = in.read(chars)) != -1) {
                        builder.append(chars, 0, readNum);
                    }
                    result = builder.toString();
                } catch (IOException e) {
                    System.out.println("file io error. " + e.getMessage());
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        System.out.println("reader close error. "
                                + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("file not found. " + file.toString());
        }
        return result;
    }

    /**
     * ファイルを読み込みByte配列で返す
     *
     * @param filePath
     * @return
     */
    public static byte[] readFileToBytes(String filePath) {
        return readFileToBytes(new File(filePath));
    }

    /**
     * ファイルを読み込みByte配列で返す
     *
     * @param file
     * @return
     */
    public static byte[] readFileToBytes(File file) {
        return readFileToString(file).getBytes();
    }

    /**
     * @param filePath
     * @param string
     * @return
     */
    public static boolean writeStringToFile(String filePath, String string) {

        // ディレクトリ作成
        File file = new File(filePath);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // キャッシュファイル作成
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(file));
            osw.write(string);
        } catch (IOException e) {
            System.out.println("file writer error: " + e.getMessage());
            return false;

        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
            } catch (IOException e) {
                System.out.println("close file writer error." + e.getMessage());
            }
        }
        return true;
    }

    /**
     * 親子全て削除
     */
    public static boolean removeFile(File file) {

        if (file == null || !file.exists() || !file.delete()) {
            return false;
        }

        return true;
    }

    /**
     * ディレクトリー以下のファイルを全て削除する。<br>
     * ディレクトリー自身は削除しない<br>
     * 一つでも削除に失敗したらfalseを返すが、残りのファイルも削除を試みる。<br>
     * 引数のファイルオブジェクトが存在しないかディレクトリーでない場合は何もしないでfalseを返す。
     *
     * @param file 子供を削除するファイルオブジェクト(ディレクトリー)
     * @return 削除に成功したらtrueを返す
     */
    public static boolean removeAllChildrens(File file) {

        if (file == null || !file.exists() || !file.isDirectory()) {
            return false;
        }

        File[] childrens = file.listFiles();
        if (childrens == null || childrens.length <= 0) {
            return false;
        }

        boolean ret = true;
        for (File children : childrens) {
            if (!removeFile(children)) {
                ret = false;
            }
        }

        return ret;
    }

    /**
     * 対象のディレクトリ内で、除外したファイルの残りを取得する
     *
     * @param directory        対象のディレクトリ
     * @param excludeFileNames 除外対象のリスト
     * @return
     */
    public static List<File> remainsOfExcludedFilesInDir(File directory,
                                                         final String excludeFileName) {
        if (directory == null || excludeFileName == null) {
            return new ArrayList<File>();
        }
        List<String> excludeFileNames = new ArrayList<String>();
        excludeFileNames.add(excludeFileName);
        return remainsOfExcludedFilesInDir(directory, excludeFileName);
    }

    /**
     * 対象のディレクトリ内で、除外したファイルの残りを取得する
     *
     * @param directory        対象のディレクトリ
     * @param excludeFileNames 除外対象のリスト
     * @return
     */
    public static List<File> remainsOfExcludedFilesInDir(File directory,
                                                         final List<String> excludeFileNames) {

        if (directory == null || excludeFileNames == null) {
            return new ArrayList<File>();
        }

        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {

                if (excludeFileNames.contains(file.getName())) {
                    // 除外対象と一致
                    return false;
                }
                return true;
            }
        });

        if (files == null || files.length <= 0) {
            return new ArrayList<File>();
        }

        return Arrays.asList(files);
    }

    /**
     * 指定したディレクトリ[dirPath]から、正規表現[pattern]にマッチしたファイルのリストを返します。<br>
     * Example.<br>
     * File[] files = getMatchesFiles("C:/example/", "*.java");
     *
     * @param dirPath 検索対象のディレクトリを表すパス
     * @param pattern 検索対象のファイル名[正規表現]
     * @return 検索にマッチしたファイルオブジェクト
     */
    public static List<File> getMatchesFiles(String dirPath, String pattern) {

        List<File> result = new ArrayList<File>();
        if (dirPath == null || pattern == null) {
            return result;
        }

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return result;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.getName().matches(pattern)) {
                result.add(file);
            }
        }

        return result;
    }

    /**
     * fileの引数が存在して、かつファイルであればtrueを返す。
     *
     * @param path
     * @return
     */
    public static boolean isFile(File filePath) {
        return filePath != null && filePath.exists() && filePath.isFile();
    }

    /**
     * fileの引数が存在して、かつファイルであればfalseを返す。
     *
     * @param path
     * @return
     */
    public static boolean isNotFile(File filePath) {
        return !isFile(filePath);
    }

    /**
     * fileの引数が存在して、かつファイルであればtrueを返す。
     *
     * @param path
     * @return
     */
    public static boolean isFile(String filePath) {
        return isFile(new File(filePath != null ? filePath : ""));
    }

    /**
     * fileの引数が存在して、かつファイルであればtrueを返す。
     *
     * @param path
     * @return
     */
    public static boolean isNotFile(String filePath) {
        return !isFile(filePath);
    }

    /**
     * dirPathが存在して、かつディレクトリであればtrueを返す。
     *
     * @param dirPath
     * @return
     */
    public static boolean isDirectory(File dirPath) {
        return dirPath != null && dirPath.exists() && dirPath.isDirectory();
    }

    /**
     * dirPathが存在して、かつディレクトリであればfalseを返す。
     *
     * @param dirPath
     * @return
     */
    public static boolean isNotDirectory(File dirPath) {
        return !isDirectory(dirPath);
    }

    /**
     * dirPathが存在して、かつディレクトリであればtrueを返す。
     *
     * @param path
     * @return
     */
    public static boolean isDirectory(String dirPath) {
        return isDirectory(new File(dirPath != null ? dirPath : ""));
    }

    /**
     * dirPathが存在して、かつディレクトリであればtrueを返す。
     *
     * @param path
     * @return
     */
    public static boolean isNotDirectory(String dirPath) {
        return !isDirectory(dirPath);
    }

    /**
     * ファイル名から拡張子を返します。<br>
     * 拡張子を取得できない場合はnullを返却する
     *
     * @param fileName ファイル名
     * @return ファイルの拡張子
     */
    public static String getSuffix(String fileName) {
        if (fileName == null)
            return null;
        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            return fileName.substring(point + 1);
        }
        return fileName;
    }

    /**
     * ファイル名から拡張子を返します。<br>
     * 拡張子を取得できない場合はnullを返却する
     *
     * @param fileName ファイル名
     * @return ファイルの拡張子
     */
    public static String getSuffix(File fileName) {
        return fileName == null ? null : getSuffix(fileName.toString());
    }

}
