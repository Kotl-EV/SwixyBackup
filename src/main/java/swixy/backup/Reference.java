// 
// Decompiled by Procyon v0.5.36
// 

package swixy.backup;

import java.io.InputStream;
import com.google.common.base.Throwables;
import java.util.Properties;

public class Reference
{
    public static final String MOD_ID = "SwixyBackup";
    public static final String MOD_NAME = "SwixyBackup";
    public static final String VERSION;
    
    static {
        final Properties prop = new Properties();
        try {
            final InputStream stream = Reference.class.getResourceAsStream("reference.properties");
            prop.load(stream);
            stream.close();
        }
        catch (Exception e) {
            Throwables.propagate((Throwable)e);
        }
        VERSION = prop.getProperty("version");
    }
}
