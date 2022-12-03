package swixy.backup.util;

import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;

public class ZipCompression
{
    public static void zipFolder(final String srcFolder, final String destZipFile) throws Exception {
        zipFolder(srcFolder, destZipFile, -1);
    }

    public static void zipFolder(final String srcFolder, final String destZipFile, final int compression) throws Exception {
        final FileOutputStream fos = new FileOutputStream(destZipFile);
        final ZipOutputStream zip = new ZipOutputStream(fos);
        zip.setLevel(compression);
        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
    }

    public static void addFileToZip(final String path, final String srcFile, final ZipOutputStream zip) throws Exception {
        final File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zip);
        }
        else {
            final byte[] buf = new byte[1024];
            final FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
            in.close();
        }
    }

    public static void addFolderToZip(final String path, final String srcFolder, final ZipOutputStream zip) throws Exception {
        final File folder = new File(srcFolder);
        for (final String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            }
            else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
            }
        }
    }
}
