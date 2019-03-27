package automatic.phonerecorder.callrecorder.model;

import java.io.File;

@SuppressWarnings("rawtypes")
public class MyPair implements Comparable {
    public long t;
    public File f;

    public MyPair(File file) {
        f = file;
        t = file.lastModified();
    }

    public int compareTo(Object o) {
        long u = ((MyPair) o).t;
        return t < u ? -1 : t == u ? 0 : 1;
    }
};