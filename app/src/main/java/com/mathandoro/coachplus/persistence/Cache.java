package com.mathandoro.coachplus.persistence;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by dominik on 31.03.17.
 */

public class Cache {
    protected Context context;

    public Cache(Context context){
        this.context = context;
    }

    public <T extends Parcelable> void saveList(List<T> list, String listName, CacheContext cacheContext) throws IOException {
        Parcel parcel = Parcel.obtain();
        parcel.writeList(list);
        byte[] marshall = parcel.marshall();
        String path = this.context.getFilesDir() + "/" + cacheContext.getDirectory();
        this.saveFile(path, listName, marshall);
        parcel.recycle();
    }

    public <T extends Parcelable> List<T> readList(String listName, CacheContext cacheContext, Class elementClass) throws IOException {
        String path = this.context.getFilesDir() + "/" + cacheContext.getDirectory();
        byte[] bytes = this.readFile(path, listName);
        Parcel parcel = unmarshall(bytes);
        List<T> result = new ArrayList<>();
        parcel.readList(result, elementClass.getClassLoader());
        parcel.recycle();
        return result;
    }

    public <T extends Parcelable> void saveObject(T object, String objectName, CacheContext cacheContext) throws IOException {
        Parcel parcel = Parcel.obtain();
        parcel.writeValue(object);
        byte[] marshall = parcel.marshall();
        String path = this.context.getFilesDir() + "/" + cacheContext.getDirectory();
        this.saveFile(path, objectName, marshall);
        parcel.recycle();
    }

    public <T extends Parcelable> T readObject(String objectName, CacheContext cacheContext, Class elementClass) throws IOException {
        String path = this.context.getFilesDir() + "/" + cacheContext.getDirectory();
        byte[] bytes = this.readFile(path, objectName);
        Parcel parcel = unmarshall(bytes);
        T result = parcel.readParcelable(elementClass.getClassLoader());
        parcel.recycle();
        return result;
    }

    public void delete(String fileName, CacheContext cacheContext){
        String path = this.context.getFilesDir() + "/" + cacheContext.getDirectory();
        File file = new File(path + "/" + fileName);
        file.delete();
    }

    public boolean exists(String fileName, CacheContext cacheContext){
        String path = this.context.getFilesDir() + "/" + cacheContext.getDirectory();
        File file = new File(path + "/" + fileName);
        return file.exists();
    }

    protected byte[] readFile(String path, String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(new File(path + "/" + fileName));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int bytesRead;
        while((bytesRead = fis.read(b)) != -1) {
            bos.write(b, 0, bytesRead);
        }
        return bos.toByteArray();
    }

    protected Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // This is extremely important!
        return parcel;
    }

    void saveFile(String path, String filename, byte[] data) throws IOException {
        File file = new File(path);
        file.mkdirs();
        FileOutputStream fos = new FileOutputStream(new File(path + "/" + filename));
        fos.write(data);
        fos.close();
    }

}
