/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer.script;

import be.ordina.unitils.db.maintainer.script.IncrementalFileScriptSource;
import be.ordina.unitils.db.maintainer.VersionScriptPair;
import be.ordina.unitils.util.PropertiesUtils;
import be.ordina.unitils.testing.util.ReflectionAssert;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.util.Properties;
import java.util.List;

/**
 * @author Filip Neven
 */
public class IncrementalFileScriptSourceTest extends TestCase {

    private static final String DBCHANGE_FILE_DIRECTORY = System.getProperty("java.io.tmpdir") + "/FileScriptSourceTest";

    private static final String DBCHANGE_FILE1_CLASSPATH = "/be/ordina/unitils/db/maintainer/script/001_script.sql";

    private static final String DBCHANGE_FILE2_CLASSPATH = "/be/ordina/unitils/db/maintainer/script/002_script.sql";

    private static final String DBCHANGE_FILE1_FILESYSTEM = DBCHANGE_FILE_DIRECTORY + "/001_script.sql";

    private static final String DBCHANGE_FILE2_FILESYSTEM = DBCHANGE_FILE_DIRECTORY + "/002_script.sql";

    private static final String[][] scriptSourceProperties = {
            {"dbMaintainer.fileScriptSource.dir", DBCHANGE_FILE_DIRECTORY},
            {"dbMaintainer.fileScriptSource.fileExtension", "sql"}
    };

    private ScriptSource incrementalFileScriptSource;

    protected void setUp() throws Exception {
        super.setUp();

        // Clean up test directory
        File testDir = new File(DBCHANGE_FILE_DIRECTORY);
        FileUtils.deleteDirectory(testDir);
        testDir.mkdirs();

        // Copy test files
        copyFile(DBCHANGE_FILE1_CLASSPATH, DBCHANGE_FILE1_FILESYSTEM);
        copyFile(DBCHANGE_FILE2_CLASSPATH, DBCHANGE_FILE2_FILESYSTEM);

        // Initialize FileScriptSourceObject
        Properties testProperties = PropertiesUtils.asProperties(scriptSourceProperties);
        incrementalFileScriptSource = new IncrementalFileScriptSource();
        incrementalFileScriptSource.init(testProperties);
    }

    private void copyFile(String fileInClassPath, String systemPath) throws Exception {
        //todo if file not found => NullPointer exception --> fix with proper checks
        InputStream is = getClass().getResourceAsStream(fileInClassPath);
        OutputStream os = new FileOutputStream(systemPath);
        IOUtils.copy(is, os);
        is.close();
        os.close();
    }

    public void testGetNextDbChange() {
        List<VersionScriptPair> script = incrementalFileScriptSource.getScripts(0L); // Should load script1.sql
        assertEquals(new Long(1L), script.get(0).getVersion());
        assertEquals("Contents of script 1", script.get(0).getScripts().get(0));
        assertEquals(new Long(2L), script.get(1).getVersion());
        assertEquals("Contents of script 2", script.get(1).getScripts().get(0));
    }

    public void testGetNextDbChange_noMoreChanges() {
        List<VersionScriptPair> script = incrementalFileScriptSource.getScripts(2L); // There is no script2.sql, should return null
        assertTrue(script.isEmpty());
    }

}
