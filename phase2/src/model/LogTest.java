package model;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class LogTest {
  private final String LOG_FILE_NAME = "log";
  private final String LOG_FILE_FULL_NAME = "." + LOG_FILE_NAME + ".log";
  @Rule public TemporaryFolder folder = new TemporaryFolder();
  private Log log;

  @Before
  public void setUp() throws Exception {
    log = new Log(new File(folder.getRoot(), LOG_FILE_NAME));
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testLogFileIsCreated() {
    File expectedResult = new File(folder.getRoot(), LOG_FILE_FULL_NAME);
    assertTrue(expectedResult.exists());
  }

  @Test
  public void moveFile() throws Exception {
    File tempFolder = folder.newFolder("folder");
    log.moveFile(tempFolder.getPath());
    File expectedResult = new File(tempFolder.getPath(), LOG_FILE_FULL_NAME);
    File shouldntExist = new File(folder.getRoot(), LOG_FILE_FULL_NAME);
    assertTrue(expectedResult.exists());
    assertFalse(shouldntExist.exists());
  }

  @Test
  public void updateLog() throws Exception {
    String newName = "newName";
    try {
      log.updateLog("", "", newName);
    } catch (Exception e) {
      e.printStackTrace();
    }
    File expectedResult = new File(folder.getRoot(), ".newName.log");
    File shouldntExist = new File(folder.getRoot(), LOG_FILE_FULL_NAME);
    assertTrue(expectedResult.exists());
    assertFalse(shouldntExist.exists());
  }

  @Test
  public void updateLog1() throws Exception {
    try {
      log.updateLog("", "");
    } catch (Exception e) {
      e.printStackTrace();
    }
    File shouldntExist = new File(folder.getRoot(), ".newName.log");
    File expectedResult = new File(folder.getRoot(), LOG_FILE_FULL_NAME);
    assertTrue(expectedResult.exists());
    assertFalse(shouldntExist.exists());
  }

  @Test
  public void getLog() throws Exception {
    File file = new File(folder.getRoot(), LOG_FILE_FULL_NAME);
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    writer.append("oldname / newname / time");
    writer.close();
    String[] expectedResults = new String[] {"oldname / newname / time"};
    Assert.assertArrayEquals(expectedResults, log.getLog());
  }

  @Test
  public void getColumnWithSpecialEntries() throws Exception {
    File file = new File(folder.getRoot(), LOG_FILE_FULL_NAME);
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    writer.append("!@#$%^&*() / ,.<>;:'\"[]{}\\|? / `~-_=+");
    writer.close();
    String[] expectedResultsColumn0 = new String[] {"!@#$%^&*()"};
    String[] expectedResultsColumn1 = new String[] {",.<>;:'\"[]{}\\|?"};
    String[] expectedResultsColumn2 = new String[] {"`~-_=+"};
    Assert.assertArrayEquals(expectedResultsColumn0, log.getColumn(0));
    Assert.assertArrayEquals(expectedResultsColumn1, log.getColumn(1));
    Assert.assertArrayEquals(expectedResultsColumn2, log.getColumn(2));
  }

  @Test
  public void getColumnWithAlphaEntries() throws Exception {
    File file = new File(folder.getRoot(), LOG_FILE_FULL_NAME);
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    writer.append("oldname / newname / time");
    writer.close();
    String[] expectedResultsColumn0 = new String[] {"oldname"};
    String[] expectedResultsColumn1 = new String[] {"newname"};
    String[] expectedResultsColumn2 = new String[] {"time"};
    Assert.assertArrayEquals(expectedResultsColumn0, log.getColumn(0));
    Assert.assertArrayEquals(expectedResultsColumn1, log.getColumn(1));
    Assert.assertArrayEquals(expectedResultsColumn2, log.getColumn(2));
  }
}
